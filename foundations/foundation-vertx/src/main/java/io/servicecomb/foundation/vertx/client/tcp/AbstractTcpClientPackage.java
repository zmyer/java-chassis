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
package io.servicecomb.foundation.vertx.client.tcp;

import java.util.concurrent.atomic.AtomicLong;

import io.servicecomb.foundation.vertx.tcp.TcpOutputStream;

public abstract class AbstractTcpClientPackage {
  private static AtomicLong reqId = new AtomicLong();

  public static long getAndIncRequestId() {
    return reqId.getAndIncrement();
  }

  protected long msgId = getAndIncRequestId();

  public long getMsgId() {
    return msgId;
  }

  public abstract TcpOutputStream createStream();
}
