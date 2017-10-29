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

package io.servicecomb.codec.protobuf.utils.schema;

import io.protostuff.Schema;
import io.servicecomb.codec.protobuf.utils.WrapSchema;
import io.servicecomb.codec.protobuf.utils.WrapType;

public final class WrapSchemaFactory {
  public static WrapSchema createSchema(Schema<?> schema, WrapType type) {
    switch (type) {
      case NOT_WRAP:
        return new NotWrapSchema(schema);
      case NORMAL_WRAP:
        return new NormalWrapSchema(schema);
      case ARGS_NOT_WRAP:
        return new ArgsNotWrapSchema(schema);
      case ARGS_WRAP:
        return new ArgsWrapSchema(schema);
      default:
        throw new Error("impossible");
    }
  }

  private WrapSchemaFactory() {
  }
}
