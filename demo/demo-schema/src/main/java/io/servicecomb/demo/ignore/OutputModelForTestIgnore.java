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

package io.servicecomb.demo.ignore;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.vertx.core.json.JsonObject;

public class OutputModelForTestIgnore {
  @JsonIgnore
  private String outputId = null;
  private String inputId = null;
  private String content = null;

  @JsonIgnore
  private Object inputObject = null;
  @JsonIgnore
  private JsonObject inputJsonObject = null;
  @JsonIgnore
  private IgnoreInterface inputIgnoreInterface = null;

  @JsonIgnore
  private Object outputObject = null;
  @JsonIgnore
  private JsonObject outputJsonObject = null;
  @JsonIgnore
  private IgnoreInterface outputIgnoreInterface = null;

  public String getOutputId() {
    return this.outputId;
  }

  public void setOutputId(String outputId) {
    this.outputId = outputId;
  }

  public String getInputId() {
    return this.inputId;
  }

  public void setInputId(String inputId) {
    this.inputId = inputId;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Object getInputObject() {
    return inputObject;
  }

  public void setInputObject(Object inputObject) {
    this.inputObject = inputObject;
  }

  public JsonObject getInputJsonObject() {
    return inputJsonObject;
  }

  public void setInputJsonObject(JsonObject inputJsonObject) {
    this.inputJsonObject = inputJsonObject;
  }

  public IgnoreInterface getInputIgnoreInterface() {
    return inputIgnoreInterface;
  }

  public void setInputIgnoreInterface(IgnoreInterface inputIgnoreInterface) {
    this.inputIgnoreInterface = inputIgnoreInterface;
  }

  public Object getOutputObject() {
    return outputObject;
  }

  public void setOutputObject(Object outputObject) {
    this.outputObject = outputObject;
  }

  public JsonObject getOutputJsonObject() {
    return outputJsonObject;
  }

  public void setOutputJsonObject(JsonObject outputJsonObject) {
    this.outputJsonObject = outputJsonObject;
  }

  public IgnoreInterface getOutputIgnoreInterface() {
    return outputIgnoreInterface;
  }

  public void setOutputIgnoreInterface(IgnoreInterface outputIgnoreInterface) {
    this.outputIgnoreInterface = outputIgnoreInterface;
  }

  public OutputModelForTestIgnore() {
  }

  public OutputModelForTestIgnore(String outputId, String inputId, String content, Object inputObject,
      JsonObject inputJsonObject, IgnoreInterface inputIgnoreInterface, Object outputObject,
      JsonObject outputJsonObject, IgnoreInterface outputIgnoreInterface) {
    this.outputId = outputId;
    this.inputId = inputId;
    this.content = content;
    this.inputObject = inputObject;
    this.inputJsonObject = inputJsonObject;
    this.inputIgnoreInterface = inputIgnoreInterface;
    this.outputObject = outputObject;
    this.outputJsonObject = outputJsonObject;
    this.outputIgnoreInterface = outputIgnoreInterface;
  }
}
