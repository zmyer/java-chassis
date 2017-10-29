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

package io.servicecomb.serviceregistry.api.request;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.servicecomb.serviceregistry.api.registry.Microservice;

public class TestCreateServiceRequest {

  CreateServiceRequest oCreateServiceRequest = null;

  Microservice oMockMicroservice = null;

  @Before
  public void setUp() throws Exception {
    oCreateServiceRequest = new CreateServiceRequest();
    oMockMicroservice = Mockito.mock(Microservice.class);
  }

  @After
  public void tearDown() throws Exception {
    oCreateServiceRequest = null;
    oMockMicroservice = null;
  }

  @Test
  public void testDefaultValues() {
    Assert.assertNull(oCreateServiceRequest.getService());
  }

  @Test
  public void testIntializedValues() {
    initCreateServiceRequest(); //Initialize the Object
    Assert.assertEquals(oMockMicroservice, oCreateServiceRequest.getService());
  }

  private void initCreateServiceRequest() {
    oCreateServiceRequest.setService(oMockMicroservice);
  }
}
