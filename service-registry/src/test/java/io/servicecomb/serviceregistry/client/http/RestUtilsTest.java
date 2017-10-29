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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;

import org.junit.Test;

import io.servicecomb.foundation.common.net.IpPort;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;

public class RestUtilsTest {
  @Test
  public void defaultHeadersContainServiceRegistryAndAuthentication() throws Exception {
    
    MultiMap headers = RestUtils.getDefaultHeaders();
    headers.addAll(RestUtils.getSignAuthHeaders(RestUtils.createSignRequest(HttpMethod.GET.toString(),
        new IpPort("127.0.0.1", 443),
        new RequestParam().addQueryParam("testParam", "test"),
        "test",
        new HashMap<String, String>())));
    assertThat(headers.get("Content-Type"), is("application/json"));
    assertThat(headers.get("User-Agent"), is("cse-serviceregistry-client/1.0.0"));
    assertThat(headers.get("x-domain-name"), is("default"));
    assertThat(headers.get("X-Service-AK"), is("blah..."));
  }
}
