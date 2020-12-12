package org.smartbit4all.domain.service.entity;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.springframework.stereotype.Service;

@Service
public class EntityManagerImpl implements EntityManager {

  Map<String, Map<String,EntityDefinition>> entityDefsByNameByDomain = new HashMap<>();
  
  public EntityManagerImpl(List<EntityDefinition> entityDefs) {
    entityDefs.forEach(this::registerEntityDef);
  }
  
  public void registerEntityDef(EntityDefinition entityDef) {
    String domain = entityDef.getDomain();
    String entityName = entityDef.entityDefName();
    Map<String, EntityDefinition> entityDefsByName = entityDefsByNameByDomain.get(domain);
    if(entityDefsByName == null) {
      entityDefsByName = new HashMap<>();
      entityDefsByNameByDomain.put(domain, entityDefsByName);
    }
    entityDefsByName.put(entityName, entityDef);
  }
  
  @Override
  public EntityDefinition definition(URI uri) {
    Map<String, EntityDefinition> entityDefsByName = getEntitiesByNameForDomain(uri);
    String entityPath = EntityUris.getEntityPath(uri);
    checkEntityPath(entityPath);
    if(entityPath.contains("/")) {
      throw new IllegalArgumentException(
          "To request an EntityDefinition the entityPath can only contain a single entity name.");
    }
    return entityDefsByName.get(entityPath);
  }

  @Override
  public Property<?> property(URI uri) {
    Map<String, EntityDefinition> entityDefsByName = getEntitiesByNameForDomain(uri);
    String entityPath = EntityUris.getEntityPath(uri);
    checkEntityPath(entityPath);
    String propertyName = EntityUris.getProperty(uri);
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
  
  private Map<String, EntityDefinition> getEntitiesByNameForDomain(URI uri) {
    String domain = EntityUris.getDomain(uri);
    Map<String, EntityDefinition> entityDefsByName = entityDefsByNameByDomain.get(domain);
    if(entityDefsByName == null) {
      throw new IllegalArgumentException(
          "There is no domain with entiyDefinitions registered for uri:" + uri.toString());
    }
    return entityDefsByName;
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
