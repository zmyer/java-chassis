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

package io.servicecomb.provider.springmvc.reference;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.ws.rs.core.HttpHeaders;

import io.servicecomb.common.rest.RestConst;
import io.servicecomb.foundation.vertx.http.AbstractHttpServletRequest;

// restTemplate convert parameters to invocation args.
public class CommonToHttpServletRequest extends AbstractHttpServletRequest {
  private Map<String, List<String>> queryParams;

  private Map<String, List<String>> httpHeaders;

  // gen by httpHeaders
  private Cookie[] cookies;

  @SuppressWarnings("unchecked")
  public CommonToHttpServletRequest(Map<String, String> pathParams, Map<String, List<String>> queryParams,
      Map<String, List<String>> httpHeaders, Object bodyObject, boolean isFormData) {
    setAttribute(RestConst.PATH_PARAMETERS, pathParams);

    if (isFormData) {
      setAttribute(RestConst.FORM_PARAMETERS, (Map<String, Object>) bodyObject);
    } else {
      setAttribute(RestConst.BODY_PARAMETER, bodyObject);
    }

    this.queryParams = queryParams;
    this.httpHeaders = httpHeaders;
  }

  @Override
  public String getContentType() {
    return getHeader(HttpHeaders.CONTENT_TYPE);
  }

  // not include form data, only for query
  @Override
  public String getParameter(String name) {
    List<String> queryValues = queryParams.get(name);
    if (queryValues == null || queryValues.isEmpty()) {
      return null;
    }

    return queryValues.get(0);
  }

  // not include form data, only for query
  @Override
  public String[] getParameterValues(String name) {
    List<String> queryValues = queryParams.get(name);
    if (queryValues == null || queryValues.isEmpty()) {
      return null;
    }

    return queryValues.toArray(new String[queryValues.size()]);
  }

  @Override
  public String getHeader(String name) {
    List<String> headerValues = httpHeaders.get(name);
    if (headerValues == null || headerValues.isEmpty()) {
      return null;
    }

    return headerValues.get(0);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    List<String> headerValues = httpHeaders.get(name);
    if (headerValues == null || headerValues.isEmpty()) {
      return Collections.emptyEnumeration();
    }

    return Collections.enumeration(headerValues);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    return Collections.enumeration(httpHeaders.keySet());
  }

  @Override
  public Cookie[] getCookies() {
    if (cookies == null) {
      cookies = createCookies();
    }

    return cookies;
  }

  private Cookie[] createCookies() {
    List<String> strCookies = httpHeaders.get(HttpHeaders.COOKIE);
    if (strCookies == null) {
      return new Cookie[] {};
    }

    List<Cookie> result = new ArrayList<>();
    for (String strCookie : strCookies) {
      List<HttpCookie> httpCookies = HttpCookie.parse(strCookie);
      for (HttpCookie httpCookie : httpCookies) {
        Cookie cookie = new Cookie(httpCookie.getName(), httpCookie.getValue());
        result.add(cookie);
      }
    }

    return result.toArray(new Cookie[result.size()]);
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return null;
  }

  @Override
  public void setHeader(String name, String value) {
    httpHeaders.put(name, Arrays.asList(value));
  }

  @Override
  public void addHeader(String name, String value) {
    List<String> list = httpHeaders.computeIfAbsent(name, key -> {
      return new ArrayList<>();
    });
    list.add(value);
  }
}
