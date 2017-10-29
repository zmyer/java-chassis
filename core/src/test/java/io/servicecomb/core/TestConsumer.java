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

package io.servicecomb.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import io.servicecomb.core.definition.OperationMeta;
import io.servicecomb.core.definition.SchemaMeta;
import io.servicecomb.core.provider.consumer.ConsumerProviderManager;
import io.servicecomb.core.provider.consumer.InvokerUtils;
import io.servicecomb.core.provider.consumer.SyncResponseExecutor;
import io.servicecomb.foundation.common.RegisterManager;
import io.servicecomb.swagger.invocation.AsyncResponse;
import io.servicecomb.swagger.invocation.Response;

public class TestConsumer {

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  public void testConsumerProviderManager() {
    ConsumerProviderManager oConsumerProviderManager = new ConsumerProviderManager();
    RegisterManager oRegisterManager = new RegisterManager("cse consumer provider manager");
    oRegisterManager.register("cse.references.cse consumer provider manager",
        "cse consumer provider manager");
    boolean validAssert = true;
    try {
      oConsumerProviderManager.getReferenceConfig("consumer provider manager");
    } catch (Exception e) {
      Assert.assertNotEquals(null, e);
      validAssert = false;
    } catch (Throwable ee) {
      Assert.assertNotEquals(null, ee);
      validAssert = false;
    }
    Assert.assertFalse(validAssert);
  }

  @Test
  public void testReferenceConfig() throws InterruptedException {
    Map<String, String> oMap = new ConcurrentHashMap<>();
    oMap.put("test1", "value1");
    RegisterManager<String, String> oManager = new RegisterManager<>("test");
    oManager.register("test1", "value1");

    SyncResponseExecutor oExecutor = new SyncResponseExecutor();
    oExecutor.execute(new Runnable() {

      @Override
      public void run() {
        oExecutor.setResponse(Response.succResp("success"));
      }
    });
    Assert.assertEquals(true, oExecutor.waitResponse().isSuccessed());
  }

  @Test
  public void testInvokerUtils() {
    Invocation oInvocation = Mockito.mock(Invocation.class);
    OperationMeta oOperationMeta = Mockito.mock(OperationMeta.class);
    Mockito.when(oOperationMeta.isSync()).thenReturn(false);
    Mockito.when(oInvocation.getOperationMeta()).thenReturn(oOperationMeta);
    InvokerUtils.reactiveInvoke(oInvocation, Mockito.mock(AsyncResponse.class));
    boolean validReactiveInvoke = true;
    try {
      InvokerUtils.reactiveInvoke(null, null);
    } catch (Exception e) {
      Assert.assertEquals(java.lang.NullPointerException.class, e.getClass());
      validReactiveInvoke = false;
    }
    Assert.assertFalse(validReactiveInvoke);
    boolean validInvokeIsNull = true;
    try {
      InvokerUtils.invoke(null);
    } catch (Exception e) {
      Assert.assertEquals(java.lang.NullPointerException.class, e.getClass());
      validInvokeIsNull = false;
    }
    Assert.assertFalse(validInvokeIsNull);
    boolean validInvoke = true;
    try {
      InvokerUtils.invoke(oInvocation);
    } catch (Exception e) {
      Assert.assertEquals(java.lang.NullPointerException.class, e.getClass());
      validInvoke = false;
    }
    Assert.assertFalse(validInvoke);
  }

  @Test
  public void testInvocation() {
    OperationMeta oOperationMeta = Mockito.mock(OperationMeta.class);
    SchemaMeta oSchemaMeta = Mockito.mock(SchemaMeta.class);
    AsyncResponse asyncResp = Mockito.mock(AsyncResponse.class);
    List<Handler> oHandlerList = new ArrayList<>();

    Mockito.when(oSchemaMeta.getProviderHandlerChain()).thenReturn(oHandlerList);
    Mockito.when(oSchemaMeta.getMicroserviceName()).thenReturn("TMK");
    Mockito.when(oOperationMeta.getSchemaMeta()).thenReturn(oSchemaMeta);
    Endpoint oEndpoint = Mockito.mock(Endpoint.class);
    Transport oTransport = Mockito.mock(Transport.class);
    Mockito.when(oEndpoint.getTransport()).thenReturn(oTransport);
    Mockito.when(oOperationMeta.getOperationId()).thenReturn("TMK");

    Invocation oInvocation = new Invocation(oEndpoint, oOperationMeta, null);
    Assert.assertNotNull(oInvocation.getTransport());
    Assert.assertNotNull(oInvocation.getInvocationType());
    oInvocation.setResponseExecutor(Mockito.mock(Executor.class));
    Assert.assertNotNull(oInvocation.getResponseExecutor());
    Assert.assertNotNull(oInvocation.getSchemaMeta());
    Assert.assertNotNull(oInvocation.getOperationMeta());
    Assert.assertNull(oInvocation.getArgs());
    Assert.assertNotNull(oInvocation.getEndpoint());
    oInvocation.setEndpoint(null);
    Map<String, String> map = oInvocation.getContext();
    Assert.assertNotNull(map);
    String str = oInvocation.getSchemaId();
    Assert.assertEquals(null, str);
    String str1 = oInvocation.getMicroserviceName();
    Assert.assertEquals("TMK", str1);
    Map<String, Object> mapp = oInvocation.getHandlerContext();
    Assert.assertNotNull(mapp);
    Assert.assertEquals(true, oInvocation.getHandlerIndex() >= 0);
    oInvocation.setHandlerIndex(8);
    Assert.assertEquals("TMK", oInvocation.getOperationName());
    Assert.assertEquals("TMK", oInvocation.getMicroserviceName());

    boolean validAssert;

    try {

      validAssert = true;

      oInvocation.next(asyncResp);
    } catch (Exception e) {
      validAssert = false;
    }
    Assert.assertFalse(validAssert);
  }
}
