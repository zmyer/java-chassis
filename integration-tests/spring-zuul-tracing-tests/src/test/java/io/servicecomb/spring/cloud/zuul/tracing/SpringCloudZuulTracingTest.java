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

package io.servicecomb.spring.cloud.zuul.tracing;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import io.servicecomb.tests.tracing.TracingTestBase;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TracedZuulMain.class, webEnvironment = RANDOM_PORT)
public class SpringCloudZuulTracingTest extends TracingTestBase {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @After
  public void tearDown() throws Exception {
    appender.clear();
  }

  @Test
  public void tracesCallsReceivedFromZuulToCalledService() throws InterruptedException {
    ResponseEntity<String> responseEntity = testRestTemplate.getForEntity("/dummy/rest/blah", String.class);

    assertThat(responseEntity.getStatusCode(), is(OK));
    assertThat(responseEntity.getBody(), is("blah"));

    TimeUnit.MILLISECONDS.sleep(1000);

    Collection<String> tracingMessages = appender.pollLogs(".*\\[\\w+/\\w+/\\w*\\]\\s+INFO.*(logged tracing|/blah).*");
    assertThat(tracingMessages.size(), greaterThanOrEqualTo(2));

    assertThatSpansReceivedByZipkin(tracingMessages, "/dummy/rest/blah", "/blah");
  }

  @Test
  public void tracesFailedCallsReceivedByZuul() throws InterruptedException {
    ResponseEntity<String> responseEntity = testRestTemplate.getForEntity("/dummy/rest/oops", String.class);

    assertThat(responseEntity.getStatusCode(), is(INTERNAL_SERVER_ERROR));

    TimeUnit.MILLISECONDS.sleep(1000);

    Collection<String> tracingMessages = appender.pollLogs(".*\\[\\w+/\\w+/\\w*\\]\\s+INFO.*(logged tracing|/oops).*");
    assertThat(tracingMessages.size(), greaterThanOrEqualTo(2));

    assertThatSpansReceivedByZipkin(tracingMessages, "/dummy/rest/oops", "500", "/oops", "590");
  }
}
