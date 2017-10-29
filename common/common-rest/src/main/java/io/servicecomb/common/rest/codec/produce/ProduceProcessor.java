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

package io.servicecomb.common.rest.codec.produce;

import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.JavaType;

import io.servicecomb.foundation.vertx.stream.BufferInputStream;
import io.servicecomb.foundation.vertx.stream.BufferOutputStream;
import io.vertx.core.buffer.Buffer;

public interface ProduceProcessor {
  String getName();

  default void encodeResponse(OutputStream output, Object result) throws Exception {
    if (result == null) {
      return;
    }

    doEncodeResponse(output, result);
  }

  void doEncodeResponse(OutputStream output, Object result) throws Exception;

  default Buffer encodeResponse(Object result) throws Exception {
    if (null == result) {
      return null;
    }

    try (BufferOutputStream output = new BufferOutputStream()) {
      doEncodeResponse(output, result);
      return output.getBuffer();
    }
  }

  default Object decodeResponse(InputStream input, JavaType type) throws Exception {
    if (input.available() == 0) {
      return null;
    }

    return doDecodeResponse(input, type);
  }

  Object doDecodeResponse(InputStream input, JavaType type) throws Exception;

  default Object decodeResponse(Buffer buffer, JavaType type) throws Exception {
    if (buffer.length() == 0) {
      return null;
    }

    try (BufferInputStream input = new BufferInputStream(buffer.getByteBuf())) {
      return doDecodeResponse(input, type);
    }
  }
}
