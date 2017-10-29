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

package io.servicecomb.core.definition;

import org.junit.Assert;
import org.junit.Test;

import io.servicecomb.core.unittest.UnitTestMeta;
import io.servicecomb.swagger.extend.annotations.ResponseHeaders;
import io.servicecomb.swagger.invocation.response.ResponseMeta;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ResponseHeader;

public class TestOperationMeta {
  class Impl {
    @ApiResponse(
        code = 300,
        response = String.class,
        message = "",
        responseHeaders = {@ResponseHeader(name = "h3", response = int.class)})
    @ResponseHeaders({@ResponseHeader(name = "h1", response = int.class),
        @ResponseHeader(name = "h2", response = String.class, responseContainer = "List")})
    public int test(int x) {
      return 100;
    }
  }

  @Test
  public void testOperationMeta() {
    UnitTestMeta meta = new UnitTestMeta();
    SchemaMeta schemaMeta = meta.getOrCreateSchemaMeta(Impl.class);
    OperationMeta operationMeta = schemaMeta.findOperation("test");

    Assert.assertEquals("POST", operationMeta.getHttpMethod());
    Assert.assertEquals("/test", operationMeta.getOperationPath());
    Assert.assertEquals(schemaMeta, operationMeta.getSchemaMeta());
    Assert.assertEquals("io.servicecomb.core.definition.TestOperationMeta$Impl.test",
        operationMeta.getSchemaQualifiedName());
    Assert.assertEquals("app:test.io.servicecomb.core.definition.TestOperationMeta$Impl.test",
        operationMeta.getMicroserviceQualifiedName());
    Assert.assertEquals("app:test", operationMeta.getMicroserviceName());
    Assert.assertEquals("test", operationMeta.getOperationId());
    Assert.assertEquals("x", operationMeta.getParamName(0));
    Assert.assertEquals(true, operationMeta.isSync());

    operationMeta.putExtData("ext", 1);
    Assert.assertEquals(1, (int) operationMeta.getExtData("ext"));

    ResponseMeta responseMeta = operationMeta.findResponseMeta(200);
    Assert.assertEquals("Ljava/lang/Integer;", responseMeta.getJavaType().getGenericSignature());
    Assert.assertEquals("Ljava/lang/Integer;", responseMeta.getHeaders().get("h1").getGenericSignature());
    Assert.assertEquals("Ljava/util/List<Ljava/lang/String;>;",
        responseMeta.getHeaders().get("h2").getGenericSignature());
    Assert.assertEquals(null, responseMeta.getHeaders().get("h3"));

    responseMeta = operationMeta.findResponseMeta(300);
    Assert.assertEquals("Ljava/lang/String;", responseMeta.getJavaType().getGenericSignature());
    Assert.assertEquals("Ljava/lang/Integer;", responseMeta.getHeaders().get("h1").getGenericSignature());
    Assert.assertEquals("Ljava/util/List<Ljava/lang/String;>;",
        responseMeta.getHeaders().get("h2").getGenericSignature());
    Assert.assertEquals("Ljava/lang/Integer;", responseMeta.getHeaders().get("h3").getGenericSignature());
  }
}
