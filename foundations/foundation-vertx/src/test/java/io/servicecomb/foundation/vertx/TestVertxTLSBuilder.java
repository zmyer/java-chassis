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

package io.servicecomb.foundation.vertx;

import org.junit.Assert;
import org.junit.Test;

import io.servicecomb.foundation.ssl.SSLCustom;
import io.servicecomb.foundation.ssl.SSLOption;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServerOptions;
import mockit.Mock;
import mockit.MockUp;

public class TestVertxTLSBuilder {

  @Test
  public void testbuildHttpServerOptions() {
    SSLOption option = SSLOption.buildFromYaml("rest.provider");
    SSLCustom custom = SSLCustom.createSSLCustom(option.getSslCustomClass());
    HttpServerOptions serverOptions = new HttpServerOptions();
    VertxTLSBuilder.buildNetServerOptions(option, custom, serverOptions);
    Assert.assertEquals(serverOptions.getEnabledSecureTransportProtocols().toArray().length, 1);
    Assert.assertEquals(serverOptions.getClientAuth(), ClientAuth.REQUEST);
  }

  @Test
  public void testbuildHttpClientOptions() {
    SSLOption option = SSLOption.buildFromYaml("rest.consumer");
    SSLCustom custom = SSLCustom.createSSLCustom(option.getSslCustomClass());
    HttpClientOptions serverOptions = new HttpClientOptions();
    VertxTLSBuilder.buildHttpClientOptions(option, custom, serverOptions);
    Assert.assertEquals(serverOptions.getEnabledSecureTransportProtocols().toArray().length, 1);
    Assert.assertEquals(serverOptions.isTrustAll(), true);
  }

  @Test
  public void testbuildClientOptionsBase() {
    SSLOption option = SSLOption.buildFromYaml("rest.consumer");
    SSLCustom custom = SSLCustom.createSSLCustom(option.getSslCustomClass());
    HttpClientOptions serverOptions = new HttpClientOptions();
    VertxTLSBuilder.buildClientOptionsBase(option, custom, serverOptions);
    Assert.assertEquals(serverOptions.getEnabledSecureTransportProtocols().toArray().length, 1);
    Assert.assertEquals(serverOptions.isTrustAll(), true);
  }

  @Test
  public void testbuildClientOptionsBaseFileNull() {
    SSLOption option = SSLOption.buildFromYaml("rest.consumer");
    option.setKeyStore(null);
    option.setTrustStore(null);
    option.setCrl(null);
    SSLCustom custom = SSLCustom.createSSLCustom(option.getSslCustomClass());
    HttpClientOptions serverOptions = new HttpClientOptions();
    VertxTLSBuilder.buildClientOptionsBase(option, custom, serverOptions);
    Assert.assertEquals(serverOptions.getEnabledSecureTransportProtocols().toArray().length, 1);
    Assert.assertEquals(serverOptions.isTrustAll(), true);
  }

  @Test
  public void testbuildClientOptionsBaseAuthPeerFalse() {
    SSLOption option = SSLOption.buildFromYaml("rest.consumer");
    SSLCustom custom = SSLCustom.createSSLCustom(option.getSslCustomClass());
    HttpClientOptions serverOptions = new HttpClientOptions();
    new MockUp<SSLOption>() {

      @Mock
      public boolean isAuthPeer() {
        return false;
      }
    };
    VertxTLSBuilder.buildClientOptionsBase(option, custom, serverOptions);
    Assert.assertEquals(serverOptions.getEnabledSecureTransportProtocols().toArray().length, 1);
    Assert.assertEquals(serverOptions.isTrustAll(), true);
  }

  @Test
  public void testbuildClientOptionsBaseSTORE_JKS() {
    SSLOption option = SSLOption.buildFromYaml("rest.consumer");
    SSLCustom custom = SSLCustom.createSSLCustom(option.getSslCustomClass());
    HttpClientOptions serverOptions = new HttpClientOptions();
    new MockUp<SSLOption>() {

      @Mock
      public String getKeyStoreType() {
        return "JKS";
      }
    };
    VertxTLSBuilder.buildClientOptionsBase(option, custom, serverOptions);
    Assert.assertEquals(serverOptions.getEnabledSecureTransportProtocols().toArray().length, 1);
    Assert.assertEquals(serverOptions.isTrustAll(), true);
  }

  @Test
  public void testbuildClientOptionsBaseSTORE_PKCS12() {
    SSLOption option = SSLOption.buildFromYaml("rest.consumer");
    SSLCustom custom = SSLCustom.createSSLCustom(option.getSslCustomClass());
    HttpClientOptions serverOptions = new HttpClientOptions();
    new MockUp<SSLOption>() {

      @Mock
      public String getTrustStoreType() {
        return "PKCS12";
      }
    };
    VertxTLSBuilder.buildClientOptionsBase(option, custom, serverOptions);
    Assert.assertEquals(serverOptions.getEnabledSecureTransportProtocols().toArray().length, 1);
    Assert.assertEquals(serverOptions.isTrustAll(), true);
  }

  @Test
  public void testbuildHttpServerOptionsRequest() {
    SSLOption option = SSLOption.buildFromYaml("rest.provider");
    SSLCustom custom = SSLCustom.createSSLCustom(option.getSslCustomClass());
    HttpServerOptions serverOptions = new HttpServerOptions();

    new MockUp<SSLOption>() {

      @Mock
      public boolean isAuthPeer() {
        return false;
      }
    };
    VertxTLSBuilder.buildNetServerOptions(option, custom, serverOptions);
    Assert.assertEquals(serverOptions.getEnabledSecureTransportProtocols().toArray().length, 1);
    Assert.assertEquals(serverOptions.getClientAuth(), ClientAuth.REQUEST);
  }
}
