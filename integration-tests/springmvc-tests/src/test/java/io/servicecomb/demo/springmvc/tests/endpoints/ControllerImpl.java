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

package io.servicecomb.demo.springmvc.tests.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.servicecomb.demo.controller.Person;
import io.servicecomb.provider.rest.common.RestSchema;

@Profile("!SimplifiedMapping")
@RestSchema(schemaId = "controller")
@RequestMapping(path = "/controller", produces = MediaType.APPLICATION_JSON)
public class ControllerImpl extends ControllerBase {
  @RequestMapping(path = "/add", method = RequestMethod.GET)
  @Override
  public int add(@RequestParam("a") int a, @RequestParam("b") int b) {
    return super.add(a, b);
  }

  @RequestMapping(path = "/sayhello/{name}", method = RequestMethod.POST)
  @Override
  public String sayHello(@PathVariable("name") String name) {
    return super.sayHello(name);
  }

  @RequestMapping(path = "/saysomething", method = RequestMethod.POST)
  @Override
  public String saySomething(String prefix, @RequestBody Person user) {
    return super.saySomething(prefix, user);
  }

  @RequestMapping(path = "/sayhi", method = RequestMethod.GET)
  @Override
  public String sayHi(HttpServletRequest request) {
    return super.sayHi(request);
  }

  @RequestMapping(path = "/sayhei", method = RequestMethod.GET)
  @Override
  public String sayHei(@RequestHeader("name") String name) {
    return super.sayHei(name);
  }
}
