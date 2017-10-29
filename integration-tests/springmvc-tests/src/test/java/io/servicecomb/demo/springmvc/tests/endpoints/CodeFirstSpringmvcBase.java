/*
 *  Copyright 2017 Huawei Technologies Co., Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.servicecomb.demo.springmvc.tests.endpoints;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.servicecomb.common.rest.codec.RestObjectMapper;
import io.servicecomb.demo.controller.Person;
import io.servicecomb.demo.server.User;
import io.servicecomb.swagger.invocation.Response;
import io.servicecomb.swagger.invocation.context.ContextUtils;
import io.servicecomb.swagger.invocation.context.InvocationContext;
import io.servicecomb.swagger.invocation.response.Headers;

public class CodeFirstSpringmvcBase {
  public ResponseEntity<Date> responseEntity(InvocationContext c1, Date date) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("h1", "h1v " + c1.getContext().toString());

    InvocationContext c2 = ContextUtils.getInvocationContext();
    headers.add("h2", "h2v " + c2.getContext().toString());

    return new ResponseEntity<Date>(date, headers, HttpStatus.ACCEPTED);
  }

  public Response cseResponse(InvocationContext c1) {
    Response response = Response.createSuccess(Status.ACCEPTED, new User());
    Headers headers = response.getHeaders();
    headers.addHeader("h1", "h1v " + c1.getContext().toString());

    InvocationContext c2 = ContextUtils.getInvocationContext();
    headers.addHeader("h2", "h2v " + c2.getContext().toString());

    return response;
  }

  public Map<String, User> testUserMap(Map<String, User> userMap) {
    return userMap;
  }

  public String textPlain(String body) {
    return body;
  }

  public byte[] bytes(byte[] input) {
    input[0] = (byte) (input[0] + 1);
    return input;
  }

  public Date addDate(Date date, long seconds) {
    return new Date(date.getTime() + seconds * 1000);
  }

  public int add(int a, int b) {
    return a + b;
  }

  public int reduce(HttpServletRequest request, int b) {
    int a = Integer.parseInt(request.getParameter("a"));
    return a - b;
  }

  public Person sayHello(Person user) {
    user.setName("hello " + user.getName());
    return user;
  }

  @SuppressWarnings("unchecked")
  public String testRawJsonString(String jsonInput) {
    Map<String, String> person;
    try {
      person = RestObjectMapper.INSTANCE.readValue(jsonInput.getBytes(), Map.class);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return "hello " + person.get("name");
  }

  public String saySomething(String prefix, Person user) {
    return prefix + " " + user.getName();
  }

  public String sayHi(String name) {
    ContextUtils.getInvocationContext().setStatus(202);
    return name + " sayhi";
  }

  public String sayHi2(String name) {
    return name + " sayhi 2";
  }

  public boolean isTrue() {
    return true;
  }

  public String addString(List<String> s) {
    String result = "";
    for (String x : s) {
      result += x;
    }
    return result;
  }
}
