/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.service.entity;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinitionBuilder;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class EntityManagerImpl implements EntityManager {

  private static final Logger log = LoggerFactory.getLogger(EntityManagerImpl.class);

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private ApplicationContext ctx;

  Map<String, Map<String, EntityDefinition>> entityDefsByNameByDomain = new HashMap<>();
  Map<URI, EntityDefinition> entityDefsByUri = new HashMap<>();

  public EntityManagerImpl(List<EntityDefinition> entityDefs) {
    entityDefs.forEach(this::registerEntityDef);
    entityDefsByNameByDomain.computeIfAbsent(CLASS_BASED_ENTITY, d -> new HashMap<>());
  }

  @Override
  public void registerEntityDef(EntityDefinition entityDef) {
    if (entityDef == null) {
      throw new IllegalArgumentException("EntityDefinition can not be null!");
    }
    String domain = entityDef.getDomain();
    String entityName = entityDef.entityDefName();
    Map<String, EntityDefinition> entityDefsByName =
        entityDefsByNameByDomain.computeIfAbsent(domain, d -> new HashMap<>());
    entityDefsByName.put(entityName, entityDef);
    entityDefsByUri.put(entityDef.getUri(), entityDef);
  }

  @Override
  public List<EntityDefinition> allDefinitions() {
    return new ArrayList<>(entityDefsByUri.values());
  }

  @Override
  public EntityDefinition definition(URI uri) {
    String entityPath = EntityUris.getEntityPath(uri);
    checkEntityPath(entityPath);
    if (entityPath.contains("/")) {
      throw new IllegalArgumentException(
          "To request an EntityDefinition the entityPath can only contain a single entity name.");
    }
    Map<String, EntityDefinition> entityDefsByName = getEntitiesByNameForDomain(uri);
    EntityDefinition result = entityDefsByName.get(entityPath);
    if (result == null && CLASS_BASED_ENTITY.equals(EntityUris.getDomain(uri))) {
      // try to recreate and register entityDefinition based on class
      String entityName = EntityUris.getEntityName(uri);
      String className = entityName.replace("_", ".");
      try {
        Class<?> clazz = Class.forName(className);
        result = createEntityDef(clazz);
        registerEntityDef(result);
      } catch (Exception e) {
        log.warn("Unable to create entityDefinition for class {}", className);
      }
    }
    return result;
  }

  @Override
  public Property<?> property(URI uri) {
    Map<String, EntityDefinition> entityDefsByName = getEntitiesByNameForDomain(uri);
    String entityPath = EntityUris.getEntityPath(uri);
    checkEntityPath(entityPath);
    String propertyPath = EntityUris.getProperty(uri);
    checkPropertyName(propertyPath);
    String functionName = EntityUris.getFunctionName(uri);

    Property<?> property = null;
    if (propertyPath.contains(".")) {
      String[] path = propertyPath.split("\\.");
      String propertyName = path[path.length - 1];
      path = Arrays.copyOfRange(path, 0, path.length - 1);
      EntityDefinition entityDef = entityDefsByName.get(entityPath);
      property = entityDef.findOrCreateReferredProperty(path, propertyName);
    } else {
      EntityDefinition entityDef = entityDefsByName.get(entityPath);
      if (entityDef == null) {
        throw new IllegalStateException("There is no entity registered for name: " + entityPath);
      }
      property = entityDef.getProperty(propertyPath);
    }
    if (functionName != null && !functionName.isEmpty()) {
      // TODO handle property uris with functions that has other parameters
      return property.function(functionName);
    }
    return property;
  }

  @Override
  public Reference<?, ?> reference(URI referenceUri) {
    String entityPath = EntityUris.getEntityPath(referenceUri);
    checkEntityPath(entityPath);

    int slashIdx = entityPath.indexOf("/");
    if (slashIdx == -1 || slashIdx != entityPath.lastIndexOf("/")) {
      throw new IllegalArgumentException(
          "To request a Reference the entityPath only can contain the entity and the single reference name");
    }
    String entityName = entityPath.substring(0, slashIdx);
    String referenceName = entityPath.substring(slashIdx + 1);
    EntityDefinition entityDef = getEntitiesByNameForDomain(referenceUri).get(entityName);
    if (entityDef == null) {
      throw new RuntimeException(
          "There is no EntityDefinition for the referenceUri" + referenceUri);
    }
    Reference<?, ?> reference = entityDef.allReferences()
        .stream()
        .filter(r -> referenceName.equals(r.getName()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException(
            "There is no EntityDefinition for the referenceUri" + referenceUri));
    return reference;
  }

  private Map<String, EntityDefinition> getEntitiesByNameForDomain(URI uri) {
    String domain = EntityUris.getDomain(uri);
    Map<String, EntityDefinition> entityDefsByName = entityDefsByNameByDomain.get(domain);
    if (entityDefsByName == null) {
      throw new IllegalArgumentException(
          "There is no domain with entiyDefinitions registered for uri:" + uri.toString());
    }
    return entityDefsByName;
  }

  private void checkEntityPath(String entityPath) {
    if (entityPath == null || entityPath.isEmpty()) {
      throw new IllegalArgumentException("The entityPath can not be null or empty.");
    }
  }

  private void checkPropertyName(String propertyName) {
    if (propertyName == null || propertyName.isEmpty()) {
      throw new IllegalArgumentException("The propertyName can not be null or empty.");
    }
  }

  @Override
  public EntityDefinition createEntityDef(Class<?> clazz) {
    ObjectDefinition<?> definition = objectApi.definition(clazz);
    ObjectDefinitionData definitionData = definition.getDefinitionData();

    EntityDefinitionBuilder entityBuilder = EntityDefinitionBuilder.of(ctx);
    String entityName = definitionData.getQualifiedName();
    entityName = entityName.replaceAll("\\.", "_");
    entityBuilder
        .domain(CLASS_BASED_ENTITY)
        .tableName(entityName)
        .name(entityName);

    for (PropertyDefinitionData prop : definitionData.getProperties()) {
      String typeName = prop.getTypeClass();
      Class<?> typeClass;
      try {
        typeClass = Class.forName(typeName);
      } catch (ClassNotFoundException e1) {
        typeClass = String.class;
      }
      entityBuilder.addOwnedProperty(prop.getName(), typeClass, -1);
    }
    return entityBuilder.build();
  }
}
