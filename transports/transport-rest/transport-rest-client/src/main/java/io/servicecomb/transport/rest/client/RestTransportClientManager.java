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

package io.servicecomb.transport.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.servicecomb.foundation.vertx.VertxUtils;
import io.vertx.core.Vertx;

/**
 * REST客户端。只需要两个实例， 一个ssl，一个非ssl.
 */
public final class RestTransportClientManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(RestTransportClientManager.class);

  public static final RestTransportClientManager INSTANCE = new RestTransportClientManager();

  // same instance in AbstractTranport. need refactor in future.
  private final Vertx transportVertx = VertxUtils.getOrCreateVertxByName("transport", null);

  private static final Object LOCK = new Object();

  private volatile RestTransportClient sslClient = null;

  private volatile RestTransportClient nonSslCient = null;

  private RestTransportClientManager() {
  }

  public RestTransportClient getRestTransportClient(boolean sslEnabled) {
    try {
      if (sslEnabled) {
        if (sslClient == null) {
          synchronized (LOCK) {
            if (sslClient == null) {
              RestTransportClient client = new RestTransportClient(true);
              client.init(transportVertx);
              sslClient = client;
            }
          }
        }
        return sslClient;
      } else {
        if (nonSslCient == null) {
          synchronized (LOCK) {
            if (nonSslCient == null) {
              RestTransportClient client = new RestTransportClient(false);
              client.init(transportVertx);
              nonSslCient = client;
            }
          }
        }
        return nonSslCient;
      }
    } catch (Exception e) {
      LOGGER.error("");
      throw new IllegalStateException("init rest client transport failed.");
    }
  }
}
