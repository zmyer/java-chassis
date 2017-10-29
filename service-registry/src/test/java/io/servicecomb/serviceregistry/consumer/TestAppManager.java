/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.serviceregistry.consumer;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.eventbus.EventBus;

import io.servicecomb.serviceregistry.RegistryUtils;
import io.servicecomb.serviceregistry.definition.DefinitionConst;
import mockit.Expectations;
import mockit.Mocked;

public class TestAppManager {
  EventBus eventBus = new EventBus();

  AppManager appManager = new AppManager(eventBus);

  String appId = "appId";

  String serviceName = "msName";

  String versionRule = "0+";

  @Test
  public void getOrCreateMicroserviceVersionRule() {
    new Expectations(RegistryUtils.class) {
      {
        RegistryUtils.findServiceInstance(appId, serviceName, DefinitionConst.VERSION_RULE_ALL);
        result = Collections.emptyList();
      }
    };

    MicroserviceVersionRule microserviceVersionRule =
        appManager.getOrCreateMicroserviceVersionRule(appId, serviceName, versionRule);
    Assert.assertEquals("0.0.0+", microserviceVersionRule.getVersionRule().getVersionRule());
    Assert.assertNull(microserviceVersionRule.getLatestMicroserviceVersion());
  }

  @Test
  public void setMicroserviceVersionFactory(@Mocked MicroserviceVersionFactory microserviceVersionFactory) {
    appManager.setMicroserviceVersionFactory(microserviceVersionFactory);

    Assert.assertSame(microserviceVersionFactory, appManager.getMicroserviceVersionFactory());
  }
}
