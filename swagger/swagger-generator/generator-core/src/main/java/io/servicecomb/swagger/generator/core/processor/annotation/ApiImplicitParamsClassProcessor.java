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

package io.servicecomb.swagger.generator.core.processor.annotation;

import io.servicecomb.swagger.generator.core.ClassAnnotationProcessor;
import io.servicecomb.swagger.generator.core.SwaggerGenerator;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

public class ApiImplicitParamsClassProcessor implements ClassAnnotationProcessor {
  @Override
  public void process(Object annotation, SwaggerGenerator swaggerGenerator) {
    ApiImplicitParams apiImplicitParamsAnnotation = (ApiImplicitParams) annotation;

    ClassAnnotationProcessor processor =
        swaggerGenerator.getContext().findClassAnnotationProcessor(ApiImplicitParam.class);
    for (ApiImplicitParam paramAnnotation : apiImplicitParamsAnnotation.value()) {
      processor.process(paramAnnotation, swaggerGenerator);
    }
  }
}
