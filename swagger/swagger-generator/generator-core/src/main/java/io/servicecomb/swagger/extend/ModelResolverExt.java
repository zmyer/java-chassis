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

package io.servicecomb.swagger.extend;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JavaType;

import io.servicecomb.foundation.common.exceptions.ServiceCombException;
import io.servicecomb.swagger.converter.property.StringPropertyConverter;
import io.servicecomb.swagger.extend.property.ByteProperty;
import io.servicecomb.swagger.extend.property.ShortProperty;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.jackson.ModelResolver;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.ByteArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import io.swagger.util.Json;

public class ModelResolverExt extends ModelResolver {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModelResolverExt.class);
  
  private interface PropertyCreator {
    Property createProperty();
  }

  private Map<Class<?>, PropertyCreator> creatorMap = new HashMap<>();

  public ModelResolverExt() {
    super(Json.mapper());

    addCreator(() -> {
      return new ByteProperty();
    }, Byte.class, byte.class);

    addCreator(() -> {
      return new ShortProperty();
    }, Short.class, short.class);

    addCreator(() -> {
      return new ByteArrayProperty();
    }, Byte[].class, byte[].class);
  }

  private void addCreator(PropertyCreator creator, Class<?>... clsArr) {
    for (Class<?> cls : clsArr) {
      creatorMap.put(cls, creator);
    }
  }

  private void setType(JavaType type, Map<String, Object> vendorExtensions) {
    vendorExtensions.put(ExtendConst.EXT_JAVA_CLASS, type.getRawClass().getName());
  }

  private void checkType(JavaType type) {
    // 原子类型/string在java中是abstract的
    if (type.getRawClass().isPrimitive()
        || byte[].class.equals(type.getRawClass())
        || Byte[].class.equals(type.getRawClass())
        || String.class.equals(type.getRawClass())) {
      return;
    }

    String msg = "Must be a concrete type.";
    if (type.getRawClass().equals(Object.class)) {
      LOGGER.warn("***********************");
      LOGGER.warn(type.getRawClass().getName() + " have some potential problems when working with "
          + "different platforms and transports. It's recommented to change your service defintion. "
          + "This feature will be removed without notice in the future.");
      LOGGER.warn("***********************");
    }

    if (type.isMapLikeType()) {
      if (!String.class.equals(type.getKeyType().getRawClass())) {
        // swagger中map的key只允许为string
        throw new Error("Key of map must be string.");
      }
    }

    if (type.isContainerType()) {
      checkType(type.getContentType());
      return;
    }

    if (type.getRawClass().isInterface()) {
      throw new ServiceCombException(type.getTypeName() + " is interface. " + msg);
    }

    if (Modifier.isAbstract(type.getRawClass().getModifiers())) {
      throw new ServiceCombException(type.getTypeName() + " is abstract class. " + msg);
    }
  }

  @Override
  public Model resolve(JavaType type, ModelConverterContext context, Iterator<ModelConverter> next) {
    Model model = super.resolve(type, context, next);
    if (model == null) {
      return null;
    }

    checkType(type);

    // 只有声明model的地方才需要标注类型
    if (ModelImpl.class.isInstance(model) && !StringUtils.isEmpty(((ModelImpl) model).getName())) {
      setType(type, model.getVendorExtensions());
    }
    return model;
  }

  @Override
  public Property resolveProperty(JavaType propType, ModelConverterContext context, Annotation[] annotations,
      Iterator<ModelConverter> next) {
    checkType(propType);

    PropertyCreator creator = creatorMap.get(propType.getRawClass());
    if (creator != null) {
      return creator.createProperty();
    }

    Property property = super.resolveProperty(propType, context, annotations, next);
    if (StringProperty.class.isInstance(property)) {
      if (StringPropertyConverter.isEnum((StringProperty) property)) {
        setType(propType, property.getVendorExtensions());
      }
    }
    return property;
  }
}
