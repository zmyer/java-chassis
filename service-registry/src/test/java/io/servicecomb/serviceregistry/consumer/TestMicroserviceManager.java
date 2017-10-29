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
import java.util.Map;

import javax.xml.ws.Holder;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.eventbus.EventBus;

import io.servicecomb.serviceregistry.RegistryUtils;
import io.servicecomb.serviceregistry.definition.DefinitionConst;
import io.servicecomb.serviceregistry.task.event.PeriodicPullEvent;
import io.servicecomb.serviceregistry.task.event.RecoveryEvent;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;

public class TestMicroserviceManager {
  String appId = "appId";

  String serviceName = "msName";

  String versionRule = "0+";

  EventBus eventBus = new EventBus();

  AppManager appManager = new AppManager(eventBus);

  MicroserviceManager microserviceManager = new MicroserviceManager(appManager, appId);

  @Test
  public void getOrCreateMicroserviceVersionRule() {
    new Expectations(RegistryUtils.class) {
      {
        RegistryUtils.findServiceInstance(appId, serviceName, DefinitionConst.VERSION_RULE_ALL);
        result = Collections.emptyList();
      }
    };

    MicroserviceVersionRule microserviceVersionRule =
        microserviceManager.getOrCreateMicroserviceVersionRule(serviceName, versionRule);
    Assert.assertEquals("0.0.0+", microserviceVersionRule.getVersionRule().getVersionRule());
    Assert.assertNull(microserviceVersionRule.getLatestMicroserviceVersion());
  }

  @Test
  public void periodicPull() {
    testPullEvent(new PeriodicPullEvent());
  }

  @Test
  public void serviceRegistryRecovery() {
    testPullEvent(new RecoveryEvent());
  }

  private void testPullEvent(Object event) {
    Map<String, MicroserviceVersions> versionsByName = Deencapsulation.getField(microserviceManager, "versionsByName");

    Holder<Integer> count = new Holder<>();
    count.value = 0;
    MicroserviceVersions versions = new MockUp<MicroserviceVersions>() {
      @Mock
      void submitPull() {
        count.value++;
      }
    }.getMockInstance();
    versionsByName.put("ms", versions);

    eventBus.post(event);
    Assert.assertEquals(1, (int) count.value);
  }
}
