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

package io.servicecomb.provider.pojo.instance;

import org.junit.Assert;
import org.junit.Test;

import io.servicecomb.provider.common.MockUtil;
import io.servicecomb.provider.pojo.PojoConst;

public class TestSpringInstanceFactory {

  @Test
  public void testInitException()
      throws Exception {

    SpringInstanceFactory lSpringInstanceFactory = new SpringInstanceFactory();
    MockUtil.getInstance().mockBeanUtils();
    try {
      lSpringInstanceFactory.create("TestSpringInstanceFactory");
    } catch (Error e) {
      Assert.assertEquals("Fail to find bean:TestSpringInstanceFactory", e.getMessage());
    }
  }

  @Test
  public void testInit()
      throws Exception {

    SpringInstanceFactory lSpringInstanceFactory = new SpringInstanceFactory();
    MockUtil.getInstance().mockBeanUtils();
    MockUtil.getInstance().mockBeanUtilsObject();
    lSpringInstanceFactory.create("io.servicecomb.provider.pojo.instance.TestPojoInstanceFactory");
    Assert.assertEquals(PojoConst.SPRING, lSpringInstanceFactory.getImplName());
  }
}
