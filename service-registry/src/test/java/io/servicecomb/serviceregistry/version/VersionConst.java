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

package io.servicecomb.serviceregistry.version;

public interface VersionConst {
  Version v0 = new Version((short) 0, (short) 0, (short) 0);

  Version v1 = new Version("1");

  Version v1Max = new Version((short) 1, Short.MAX_VALUE, Short.MAX_VALUE);

  Version v2 = new Version("2");
}
