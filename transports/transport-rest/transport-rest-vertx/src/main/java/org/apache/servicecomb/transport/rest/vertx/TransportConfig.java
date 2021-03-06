/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.transport.rest.vertx;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

import io.vertx.core.http.HttpServerOptions;

public final class TransportConfig {
  private TransportConfig() {
  }

  public static String getAddress() {
    DynamicStringProperty address =
        DynamicPropertyFactory.getInstance().getStringProperty("cse.rest.address", null);
    return address.get();
  }

  public static int getThreadCount() {
    DynamicIntProperty address =
        DynamicPropertyFactory.getInstance().getIntProperty("cse.rest.server.thread-count", 1);
    return address.get();
  }

  public static int getConnectionIdleTimeoutInSeconds() {
    return DynamicPropertyFactory.getInstance()
        .getIntProperty("cse.rest.server.connection.idleTimeoutInSeconds", 60)
        .get();
  }

  public static boolean getCompressed() {
    return DynamicPropertyFactory.getInstance()
        .getBooleanProperty("cse.rest.server.compression", false)
        .get();
  }

  public static int getMaxHeaderSize() {
    return DynamicPropertyFactory.getInstance()
        .getIntProperty("cse.rest.server.maxHeaderSize", HttpServerOptions.DEFAULT_MAX_HEADER_SIZE)
        .get();
  }
}
