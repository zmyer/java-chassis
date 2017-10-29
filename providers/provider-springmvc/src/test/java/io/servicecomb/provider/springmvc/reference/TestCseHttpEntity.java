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
package io.servicecomb.provider.springmvc.reference;

import org.junit.Assert;
import org.junit.Test;

import io.servicecomb.swagger.invocation.context.InvocationContext;

public class TestCseHttpEntity {
  @Test
  public void test() {
    CseHttpEntity<String> entity = new CseHttpEntity<>(null, null);
    entity.addContext("c1", "c1v");

    Assert.assertEquals(1, entity.getContext().getContext().size());
    Assert.assertEquals("c1v", entity.getContext().getContext("c1"));

    InvocationContext context = new InvocationContext();
    context.addContext("c2", "c2v");
    entity.setContext(context);

    Assert.assertEquals(1, entity.getContext().getContext().size());
    Assert.assertEquals("c2v", entity.getContext().getContext("c2"));
  }
}
