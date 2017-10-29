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

package io.servicecomb.bizkeeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

import io.servicecomb.core.Invocation;
import io.servicecomb.core.definition.OperationMeta;

public class TestConfiguration {
  @Test
  public void testConfiguration() {

    assertNotNull(Configuration.INSTANCE);
    assertEquals("returnnull", Configuration.FALLBACKPOLICY_POLICY_RETURN);
    assertEquals("throwexception", Configuration.FALLBACKPOLICY_POLICY_THROW);

    Configuration c = Configuration.INSTANCE;
    Invocation invocation = Mockito.mock(Invocation.class);
    String test2 = invocation.getMicroserviceName();

    Mockito.when(invocation.getOperationMeta()).thenReturn(Mockito.mock(OperationMeta.class));
    Mockito.when(invocation.getOperationMeta().getMicroserviceName()).thenReturn("testqualify");

    int res = c.getIsolationTimeoutInMilliseconds("groupname", test2, "testqualify");
    assertEquals(30000, res);
    boolean b1 = c.getIsolationTimeoutEnabled("groupname", test2, "testqualify");
    assertFalse(b1);
    int res1 = c.getIsolationMaxConcurrentRequests("groupname", test2, "testqualify");
    assertEquals(10, res1);
    boolean b2 = c.isCircuitBreakerEnabled("groupname", test2, "testqualify");
    assertTrue(b2);
    String str = c.getFallbackPolicyPolicy("groupname", test2, "testqualify");
    // no need to give default value now
    assertEquals(null, str);
  }
}
