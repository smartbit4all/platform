/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.service.entity;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.domain.meta.EntityConfiguration;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

public class ConfigEntitySource implements EntitySource {

  private String sourceId;
  private ApplicationContext appCtx;
  
  private Map<String, EntityDefinition> entityDefsByName = new HashMap<>();
  
  public ConfigEntitySource(String sourceId, Class<? extends EntityConfiguration> entityConfig, ApplicationContext appCtx) {
    this.sourceId = sourceId;
    this.appCtx = appCtx;
    collectEntityDefs(entityConfig);
  }
  
  private void collectEntityDefs(Class<? extends EntityConfiguration> entityConfigClazz) {
    Method[] configMethods = entityConfigClazz.getMethods();
    
    List<String> registeredEntityDefNames = Arrays.stream(configMethods)
      .filter(m -> m.isAnnotationPresent(Bean.class) && 
                   EntityDefinition.class.isAssignableFrom(m.getReturnType()))
      .map(this::getEntityDefName) 
      .collect(Collectors.toList());
    
    registeredEntityDefNames.forEach(entityName -> {
      EntityDefinition entityDef = appCtx.getBean(entityName, EntityDefinition.class);
      entityDefsByName.put(entityName, entityDef);
    });
    
  }

  private String getEntityDefName(Method entityCreationMethod) {
    Bean beanAnnot = entityCreationMethod.getAnnotation(Bean.class);
    String[] entityName = beanAnnot.value();
    if(entityName.length != 0) {
      return entityName[0];
    }
    return entityCreationMethod.getReturnType().getSimpleName();
  }

  @Override
  public String getSourceId() {
    return sourceId;
  }

  @Override
  public EntityDefinition getEntity(String entityPath) {
    checkEntityPath(entityPath);
    if(entityPath.contains("/")) {
      throw new IllegalArgumentException(
          "To request an EntityDefinition the entityPath can only contain a single entity name.");
    }
    return entityDefsByName.get(entityPath);
  }

  @Override
  public Property<?> getProperty(String entityPath, String propertyName) {
    checkEntityPath(entityPath);
    checkPropertyName(propertyName);
    
    if(entityPath.contains("/")) {
      String[] path = entityPath.split("/");
      String entityName = path[0];
      path = Arrays.copyOfRange(path, 1, path.length);
      EntityDefinition entityDef = entityDefsByName.get(entityName);
      Property<?> refProperty = entityDef.findOrCreateReferredProperty(path, propertyName);
      return refProperty;
    } else {
      EntityDefinition entityDef = entityDefsByName.get(entityPath);
      return entityDef.getProperty(propertyName);
    }
  }

  private void checkEntityPath(String entityPath) {
    if(entityPath == null || entityPath.isEmpty()) {
      throw new IllegalArgumentException("The entityPath can not be null or empty.");
    }
  }
  
  private void checkPropertyName(String propertyName) {
    if(propertyName == null || propertyName.isEmpty()) {
      throw new IllegalArgumentException("The propertyName can not be null or empty.");
    }
  }

}
