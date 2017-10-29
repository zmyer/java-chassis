/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.tracing.zipkin;

import static com.seanyinx.github.unit.scaffolding.AssertUtils.expectFailing;
import static io.servicecomb.tracing.zipkin.ZipkinTracingAdviser.CALL_PATH;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Test;

import brave.Span;
import brave.Tracer.SpanInScope;
import brave.Tracing;
import brave.internal.StrictCurrentTraceContext;
import io.servicecomb.tracing.zipkin.ZipkinTracingAdviser.ThrowableSupplier;

public class ZipkinTracingAdviserTest {
  private static final int nThreads = 10;

  private final String spanName = "some span";

  private final String path = this.getClass().getCanonicalName();

  private final String expected = "supplied";

  private final RuntimeException error = new RuntimeException("oops");

  private final ThrowableSupplier<String> supplier = () -> expected;

  private final Map<Long, Queue<zipkin.Span>> traces = new ConcurrentHashMap<>();

  private final Tracing tracing = Tracing.newBuilder()
      .currentTraceContext(new StrictCurrentTraceContext())
      .reporter(e -> traces.computeIfAbsent(e.traceId, id -> new ConcurrentLinkedDeque<>()).add(e))
      .build();

  private final ZipkinTracingAdviser tracingAdviser = new ZipkinTracingAdviser(tracing.tracer());

  @After
  public void tearDown() throws Exception {
    tracing.close();
  }

  @Test
  public void startsNewRootSpan() throws Throwable {
    String result = tracingAdviser.invoke(spanName, path, supplier);

    assertThat(result, is(expected));
    await().atMost(2, SECONDS).until(() -> !traces.isEmpty());

    zipkin.Span span = traces.values().iterator().next().poll();
    assertThat(span.name, is(spanName));
    assertThat(tracedValues(span), contains(this.getClass().getCanonicalName()));
  }

  @Test
  public void includesExceptionInTags() throws Throwable {
    try {
      tracingAdviser.invoke(spanName, path, () -> {
        throw error;
      });

      expectFailing(RuntimeException.class);
    } catch (RuntimeException ignored) {
    }

    await().atMost(2, SECONDS).until(() -> !traces.isEmpty());

    zipkin.Span span = traces.values().iterator().next().poll();
    assertThat(span.name, is(spanName));
    assertThat(tracedValues(span), containsInAnyOrder(this.getClass().getCanonicalName(), "RuntimeException: oops"));
  }

  @Test
  public void startsNewChildSpan() throws Exception {
    CyclicBarrier cyclicBarrier = new CyclicBarrier(nThreads);

    CompletableFuture<?>[] futures = new CompletableFuture[nThreads];
    for (int i = 0; i < nThreads; i++) {
      futures[i] = CompletableFuture.runAsync(() -> {
        Span currentSpan = tracing.tracer().newTrace().start();

        waitTillAllAreReady(cyclicBarrier);

        try (SpanInScope spanInScope = tracing.tracer().withSpanInScope(currentSpan)) {
          assertThat(tracingAdviser.invoke(spanName, path, supplier), is(expected));
        } catch (Throwable throwable) {
          fail(throwable.getMessage());
        } finally {
          currentSpan.finish();
        }
      }, Executors.newFixedThreadPool(nThreads));
    }

    CompletableFuture.allOf(futures).join();

    assertThat(traces.size(), is(nThreads));

    for (Queue<zipkin.Span> queue : traces.values()) {
      zipkin.Span child = queue.poll();
      assertThat(child.name, is(spanName));

      zipkin.Span parent = queue.poll();
      assertThat(child.parentId, is(parent.id));
      assertThat(child.traceId, is(parent.traceId));
      assertThat(tracedValues(child), contains(this.getClass().getCanonicalName()));
    }
  }

  private void waitTillAllAreReady(CyclicBarrier cyclicBarrier) {
    try {
      cyclicBarrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      throw new RuntimeException(e);
    }
  }

  private List<String> tracedValues(zipkin.Span spans) {
    return spans.binaryAnnotations.stream()
        .filter(span -> CALL_PATH.equals(span.key) || "error".equals(span.key))
        .filter(span -> span.value != null)
        .map(annotation -> new String(annotation.value))
        .distinct()
        .collect(Collectors.toList());
  }
}