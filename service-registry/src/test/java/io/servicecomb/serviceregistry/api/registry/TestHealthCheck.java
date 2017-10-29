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

package io.servicecomb.serviceregistry.api.registry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestHealthCheck {

  HealthCheck oHealthCheck = null;

  @Before
  public void setUp() throws Exception {
    oHealthCheck = new HealthCheck();
  }

  @After
  public void tearDown() throws Exception {
    oHealthCheck = null;
  }

  @Test
  public void testDefaultValues() {
    Assert.assertEquals(0, oHealthCheck.getInterval());
    Assert.assertEquals(0, oHealthCheck.getPort());
    Assert.assertEquals(0, oHealthCheck.getTimes());
    Assert.assertNull(oHealthCheck.getMode());
    Assert.assertEquals(0, oHealthCheck.getTTL());
  }

  @Test
  public void testIntializedValues() {
    initHealthCheck(); //Initialize the Values
    Assert.assertEquals(10, oHealthCheck.getInterval());
    Assert.assertEquals(8080, oHealthCheck.getPort());
    Assert.assertEquals(2, oHealthCheck.getTimes());
    Assert.assertEquals(HealthCheckMode.PLATFORM, oHealthCheck.getMode());
    Assert.assertEquals(0, oHealthCheck.getTTL()); //TTL Values will changes based on the Mode
    Assert.assertEquals("pull", oHealthCheck.getMode().getName());

    //Testing Different modes of the HealthCheckMode
    oHealthCheck.setMode(HealthCheckMode.HEARTBEAT);
    Assert.assertEquals(HealthCheckMode.HEARTBEAT, oHealthCheck.getMode());
    Assert.assertEquals(30, oHealthCheck.getTTL()); //TTL Values will changes based on the Mode
    Assert.assertEquals("push", oHealthCheck.getMode().getName());

    oHealthCheck.setMode(HealthCheckMode.UNKNOWN);
    Assert.assertEquals(HealthCheckMode.UNKNOWN, oHealthCheck.getMode());
    Assert.assertEquals(0, oHealthCheck.getTTL()); //TTL Values will changes based on the Mode
    Assert.assertEquals("unknow", oHealthCheck.getMode().getName());
  }

  private void initHealthCheck() {
    oHealthCheck.setInterval(10);
    oHealthCheck.setMode(HealthCheckMode.PLATFORM);
    oHealthCheck.setPort(8080);
    oHealthCheck.setTimes(2);
  }
}
