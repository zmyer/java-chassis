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

package io.servicecomb.serviceregistry.client.http;

import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import io.servicecomb.foundation.common.net.IpPort;
import io.servicecomb.foundation.vertx.AsyncResultCallback;
import io.servicecomb.serviceregistry.api.registry.Microservice;
import io.servicecomb.serviceregistry.api.registry.MicroserviceFactory;
import io.servicecomb.serviceregistry.cache.InstanceCacheManager;
import io.servicecomb.serviceregistry.client.Endpoints;
import io.servicecomb.serviceregistry.client.IpPortManager;
import io.servicecomb.serviceregistry.config.ServiceRegistryConfig;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

public class TestClienthttp {
  @SuppressWarnings("unchecked")
  @Test
  public void testServiceRegistryClientImpl(@Mocked IpPortManager manager) {
    IpPort ipPort = new IpPort("127.0.0.1", 8853);
    new Expectations() {
      {
        manager.getAvailableAddress(false);
        result = ipPort;
      }
    };

    new MockUp<CountDownLatch>() {
      @Mock
      public void await() throws InterruptedException {
      }
    };
    new MockUp<RestUtils>() {
      @Mock
      void httpDo(RequestContext requestContext, Handler<RestResponse> responseHandler) {
      }
    };

    new MockUp<WebsocketUtils>() {
      @Mock
      void open(IpPort ipPort, String url, Handler<Void> onOpen, Handler<Void> onClose,
          Handler<Buffer> onMessage, Handler<Throwable> onException,
          Handler<Throwable> onConnectFailed) {
      }
    };

    MicroserviceFactory microserviceFactory = new MicroserviceFactory();
    Microservice microservice = microserviceFactory.create("app", "ms");

    ServiceRegistryClientImpl oClient = new ServiceRegistryClientImpl(manager);
    oClient.init();
    oClient.registerMicroservice(microservice);
    oClient.registerMicroserviceInstance(microservice.getIntance());
    Assert.assertEquals(null, oClient.getMicroservice(microservice.getServiceId()));
    Assert.assertEquals(null, oClient.getMicroserviceInstance("testConsumerID", "testproviderID"));
    Assert.assertEquals(null,
        oClient.findServiceInstance(microservice.getServiceId(),
            microservice.getAppId(),
            microservice.getServiceName(),
            microservice.getVersion()));
    Assert.assertEquals(null,
        oClient.getMicroserviceId(microservice.getAppId(),
            microservice.getServiceName(),
            microservice.getVersion()));
    Assert.assertEquals(null,
        oClient.heartbeat(microservice.getServiceId(),
            microservice.getIntance().getInstanceId()));
    oClient.watch("",
        Mockito.mock(AsyncResultCallback.class));
    Assert.assertEquals(false,
        oClient.unregisterMicroserviceInstance(microservice.getServiceId(),
            microservice.getIntance().getInstanceId()));
  }

  @Test
  public void testRequestContext() {
    RequestContext oContext = new RequestContext();
    oContext.setUri("//test");
    oContext.setMethod(io.vertx.core.http.HttpMethod.POST);
    oContext.setIpPort(new IpPort("145.0.0.1", 8080));
    oContext.setParams(null);

    Assert.assertEquals("//test", oContext.getUri());
    Assert.assertEquals(io.vertx.core.http.HttpMethod.POST, oContext.getMethod());
    Assert.assertEquals(8080, oContext.getIpPort().getPort());
    Assert.assertEquals(null, oContext.getParams());

    RestResponse oResponse = new RestResponse(null, null);
    oResponse.setRequestContext(oContext);
    Assert.assertEquals(oContext, oResponse.getRequestContext());
    Assert.assertEquals(null, oResponse.getResponse());
  }

  @Test
  public void testRequestParam() {
    RequestParam oParam = new RequestParam();
    oParam.setCookies(null);
    oParam.setBody(null);
    oParam.setHeaders(null);
    oParam.addHeader("testKey", "testValue");
    oParam.addQueryParam("testParam", "ValueParam");
    oParam.addQueryParam("testParam1", "ValueParam");
    Assert.assertEquals(null, oParam.getCookies());
    Assert.assertEquals(null, oParam.getBody());
    Assert.assertNotEquals(null, oParam.getHeaders());
    Assert.assertNotEquals(null, oParam.getQueryParams());
    oParam.setQueryParams(null);
    Assert.assertEquals("", oParam.getQueryParams());
    oParam.setFormFields(null);
    Assert.assertEquals(null, oParam.getFormFields());
    Endpoints oEndpoints = new Endpoints();
    oEndpoints.setInstances(null);
    oEndpoints.setVersion("1.0");
    Assert.assertEquals(null, oEndpoints.getInstances());
    Assert.assertEquals("1.0", oEndpoints.getVersion());
  }

  @Test
  public void testIpPortManager(@Mocked InstanceCacheManager instanceCacheManager) throws Exception {
    IpPortManager oManager = new IpPortManager(ServiceRegistryConfig.INSTANCE, instanceCacheManager);
    IpPort oIPPort = oManager.getAvailableAddress(true);
    Assert.assertEquals(oIPPort.getHostOrIp(), oManager.getAvailableAddress(false).getHostOrIp());
  }
}
