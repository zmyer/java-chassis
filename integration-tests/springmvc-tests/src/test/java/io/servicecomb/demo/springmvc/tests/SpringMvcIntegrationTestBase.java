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

package io.servicecomb.demo.springmvc.tests;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import io.servicecomb.common.rest.codec.RestObjectMapper;
import io.servicecomb.demo.compute.Person;
import io.servicecomb.demo.server.User;

@Ignore
public class SpringMvcIntegrationTestBase {

  private final String baseUrl = "http://127.0.0.1:8080/";

  private final RestTemplate restTemplate = new RestTemplate();

  private final String codeFirstUrl = baseUrl + "codeFirstSpringmvc/";

  private final String controllerUrl = baseUrl + "controller/";

  @Test
  public void ableToQueryAtRootBasePath() {
    ResponseEntity<String> responseEntity = restTemplate
        .getForEntity(baseUrl + "sayHi?name=Mike", String.class);

    assertThat(responseEntity.getStatusCode(), is(OK));
    assertThat(responseEntity.getBody(), is("Hi Mike"));

    List<HttpMessageConverter<?>> convertersOld = restTemplate.getMessageConverters();
    List<HttpMessageConverter<?>> converters = new ArrayList<>();
    converters.add(new MappingJackson2HttpMessageConverter());
    restTemplate.setMessageConverters(converters);
    responseEntity = restTemplate
        .getForEntity(baseUrl + "sayHi?name={name}", String.class, "小 强");

    assertThat(responseEntity.getStatusCode(), is(OK));
    assertThat(responseEntity.getBody(), is("Hi 小 强"));

    restTemplate.setMessageConverters(convertersOld);
  }

  @Test
  public void ableToQueryAtRootPath() {
    ResponseEntity<String> responseEntity = restTemplate
        .getForEntity(baseUrl, String.class);

    assertThat(responseEntity.getStatusCode(), is(OK));
    assertThat(responseEntity.getBody(), is("Welcome home"));
  }

  @Test
  public void ableToQueryAtNonRootPath() {
    ResponseEntity<String> responseEntity = restTemplate
        .getForEntity(baseUrl + "french/bonjour?name=Mike", String.class);

    assertThat(responseEntity.getStatusCode(), is(OK));
    assertThat(responseEntity.getBody(), is("Bonjour Mike"));
  }

  @Test
  public void ableToPostMap() {
    Map<String, User> users = new HashMap<>();
    users.put("user1", userOfNames("name11", "name12"));
    users.put("user2", userOfNames("name21", "name22"));

    ParameterizedTypeReference<Map<String, User>> reference = new ParameterizedTypeReference<Map<String, User>>() {
    };
    ResponseEntity<Map<String, User>> responseEntity = restTemplate.exchange(codeFirstUrl + "testUserMap",
        POST,
        jsonRequest(users),
        reference);

    assertThat(responseEntity.getStatusCode(), is(OK));

    Map<String, User> body = responseEntity.getBody();
    assertArrayEquals(body.get("user1").getNames(), new String[] {"name11", "name12"});
    assertArrayEquals(body.get("user2").getNames(), new String[] {"name21", "name22"});
  }

  private User userOfNames(String... names) {
    User user1 = new User();
    user1.setNames(names);
    return user1;
  }

  @Test
  public void ableToConsumeTextPlain() {
    String body = "a=1";

    String result = restTemplate.postForObject(
        codeFirstUrl + "textPlain",
        body,
        String.class);

    assertThat(jsonOf(result, String.class), is(body));
  }

  @Test
  public void ableToPostBytes() throws IOException {
    byte[] body = new byte[] {0, 1, 2};

    byte[] result = restTemplate.postForObject(
        codeFirstUrl + "bytes",
        jsonRequest(RestObjectMapper.INSTANCE.writeValueAsBytes(body)),
        byte[].class);

    result = RestObjectMapper.INSTANCE.readValue(result, byte[].class);

    assertEquals(1, result[0]);
    assertEquals(1, result[1]);
    assertEquals(2, result[2]);
    assertEquals(3, result.length);
  }

  @Test
  public void ableToPostDate() throws Exception {
    ZonedDateTime date = ZonedDateTime.now().truncatedTo(SECONDS);
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("date", RestObjectMapper.INSTANCE.convertToString(Date.from(date.toInstant())));

    HttpHeaders headers = new HttpHeaders();
    headers.add(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE);

    int seconds = 1;
    Date result = restTemplate.postForObject(codeFirstUrl + "addDate?seconds={seconds}",
        new HttpEntity<>(body, headers),
        Date.class,
        seconds);

    assertThat(result, is(Date.from(date.plusSeconds(seconds).toInstant())));
  }

  @Test
  public void ableToDeleteWithQueryString() {
    ResponseEntity<String> responseEntity = restTemplate.exchange(codeFirstUrl + "addstring?s=a&s=b",
        HttpMethod.DELETE,
        null,
        String.class);

    assertThat(responseEntity.getBody(), is("ab"));
  }

  @Test
  public void ableToGetBoolean() {
    boolean result = restTemplate.getForObject(codeFirstUrl + "istrue", boolean.class);

    assertThat(result, is(true));
  }

  @Test
  public void putsEndWithPathParam() {
    ResponseEntity<String> responseEntity = restTemplate
        .exchange(codeFirstUrl + "sayhi/{name}", PUT, null, String.class, "world");

    assertThat(responseEntity.getStatusCode(), is(ACCEPTED));
    assertThat(jsonOf(responseEntity.getBody(), String.class), is("world sayhi"));
  }

  @Test
  public void putsContainingPathParam() {
    ResponseEntity<String> responseEntity = restTemplate
        .exchange(codeFirstUrl + "sayhi/{name}/v2", PUT, null, String.class, "world");

    assertThat(jsonOf(responseEntity.getBody(), String.class), is("world sayhi 2"));
  }

  @Test
  public void ableToPostWithHeader() {
    Person person = new Person();
    person.setName("person name");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);
    headers.add("prefix", "prefix  prefix");

    HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);
    ResponseEntity<String> responseEntity = restTemplate
        .postForEntity(codeFirstUrl + "saysomething", requestEntity, String.class);

    assertThat(jsonOf(responseEntity.getBody(), String.class), is("prefix  prefix person name"));
  }

  @Test
  public void ableToPostObjectAsJson() {
    Map<String, String> personFieldMap = new HashMap<>();
    personFieldMap.put("name", "person name from map");

    Person person = restTemplate
        .postForObject(codeFirstUrl + "sayhello", jsonRequest(personFieldMap), Person.class);
    assertThat(person.toString(), is("hello person name from map"));

    Person input = new Person();
    input.setName("person name from Object");
    person = restTemplate.postForObject(codeFirstUrl + "sayhello", jsonRequest(input), Person.class);

    assertThat(person.toString(), is("hello person name from Object"));
  }

  @Test
  public void ableToPostForm() {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("a", "5");
    params.add("b", "3");

    HttpHeaders headers = new HttpHeaders();
    headers.add(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE);
    int result = restTemplate
        .postForObject(codeFirstUrl + "add", new HttpEntity<>(params, headers), Integer.class);

    assertThat(result, is(8));
  }

  @Test
  public void ableToExchangeCookie() {
    Map<String, String> params = new HashMap<>();
    params.put("a", "5");

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.COOKIE, "b=3");

    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<Integer> result = restTemplate.exchange(
        codeFirstUrl + "reduce?a={a}",
        GET,
        requestEntity,
        Integer.class,
        params);

    assertThat(result.getBody(), is(2));
  }

  @Test
  public void getsEndWithRequestVariables() {
    int result = restTemplate.getForObject(
        controllerUrl + "add?a={a}&b={b}",
        Integer.class,
        3,
        4);

    assertThat(result, is(7));
  }

  @Test
  public void postsEndWithPathParam() {
    String result = restTemplate.postForObject(
        controllerUrl + "sayhello/{name}",
        null,
        String.class,
        "world");

    assertThat(jsonOf(result, String.class), is("hello world"));

    List<HttpMessageConverter<?>> convertersOld = restTemplate.getMessageConverters();
    List<HttpMessageConverter<?>> converters = new ArrayList<>();
    converters.add(new MappingJackson2HttpMessageConverter());
    restTemplate.setMessageConverters(converters);
    result = restTemplate.postForObject(
        controllerUrl + "sayhello/{name}",
        null,
        String.class,
        "中 国");

    assertThat(result, is("hello 中 国"));
    restTemplate.setMessageConverters(convertersOld);
  }

  @Test
  public void ableToPostObjectAsJsonWithRequestVariable() {
    Person input = new Person();
    input.setName("world");

    String result = restTemplate.postForObject(
        controllerUrl + "saysomething?prefix={prefix}",
        jsonRequest(input),
        String.class,
        "hello");

    assertThat(jsonOf(result, String.class), is("hello world"));

    List<HttpMessageConverter<?>> convertersOld = restTemplate.getMessageConverters();
    List<HttpMessageConverter<?>> converters = new ArrayList<>();
    converters.add(new MappingJackson2HttpMessageConverter());
    restTemplate.setMessageConverters(converters);
    input = new Person();
    input.setName("中国");

    result = restTemplate.postForObject(
        controllerUrl + "saysomething?prefix={prefix}",
        jsonRequest(input),
        String.class,
        "hello");

    assertThat(result, is("hello 中国"));
    restTemplate.setMessageConverters(convertersOld);
  }

  @Test
  public void ensureServerWorksFine() {
    String result = restTemplate.getForObject(
        controllerUrl + "sayhi?name=world",
        String.class);

    assertThat(jsonOf(result, String.class), is("hi world [world]"));
  }

  @Test
  public void ensureServerBlowsUp() {
    try {
      restTemplate.getForObject(
          controllerUrl + "sayhi?name=throwexception",
          String.class);
      fail("Exception expected, but threw nothing");
    } catch (UnknownHttpStatusCodeException e) {
      assertThat(e.getRawStatusCode(), is(590));
    }
  }

  @Test
  public void ableToSetCustomHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("name", "world");

    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<String> result = restTemplate.exchange(
        controllerUrl + "sayhei",
        GET,
        requestEntity,
        String.class);

    assertThat(jsonOf(result.getBody(), String.class), is("hei world"));
  }

  private <T> HttpEntity<T> jsonRequest(T body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);
    return new HttpEntity<>(body, headers);
  }

  private <T> T jsonOf(String json, Class<T> aClass) {
    try {
      return RestObjectMapper.INSTANCE.readValue(json, aClass);
    } catch (IOException e) {
      throw new IllegalStateException(
          "Failed to read JSON from " + json + ", Exception is: " + e);
    }
  }
}
