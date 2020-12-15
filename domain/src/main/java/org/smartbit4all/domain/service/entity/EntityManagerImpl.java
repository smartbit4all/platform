package org.smartbit4all.domain.service.entity;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.Reference;
import org.springframework.stereotype.Service;

@Service
public class EntityManagerImpl implements EntityManager {

  Map<String, Map<String,EntityDefinition>> entityDefsByNameByDomain = new HashMap<>();
  Map<URI, EntityDefinition> entityDefsByUri = new HashMap<>();
  
  public EntityManagerImpl(List<EntityDefinition> entityDefs) {
    entityDefs.forEach(this::registerEntityDef);
  }
  
  public void registerEntityDef(EntityDefinition entityDef) {
    if(entityDef == null ) {
      throw new IllegalArgumentException("EntityDefinition can not be null!");
    }
    String domain = entityDef.getDomain();
    String entityName = entityDef.entityDefName();
    Map<String, EntityDefinition> entityDefsByName = entityDefsByNameByDomain.get(domain);
    if(entityDefsByName == null) {
      entityDefsByName = new HashMap<>();
      entityDefsByNameByDomain.put(domain, entityDefsByName);
    }
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
    if(entityPath.contains("/")) {
      throw new IllegalArgumentException(
          "To request an EntityDefinition the entityPath can only contain a single entity name.");
    }
    Map<String, EntityDefinition> entityDefsByName = getEntitiesByNameForDomain(uri);
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
  
  @Override
  public Reference<?, ?> reference(URI referenceUri) {
    String entityPath = EntityUris.getEntityPath(referenceUri);
    checkEntityPath(entityPath);
    
    int slashIdx = entityPath.indexOf("/");
    if(slashIdx == -1 || slashIdx != entityPath.lastIndexOf("/")) {
      throw new IllegalArgumentException(
          "To request a Reference the entityPath only can contain the entity and the single reference name");
    }
    String entityName = entityPath.substring(0, slashIdx);
    String referenceName = entityPath.substring(slashIdx + 1);
    EntityDefinition entityDef = getEntitiesByNameForDomain(referenceUri).get(entityName);
    if(entityDef == null) {
      throw new RuntimeException("There is no EntityDefinition for the referenceUri" + referenceUri);
    }
    Reference<?, ?> reference = entityDef.allReferences()
        .stream()
        .filter(r -> referenceName.equals(r.getName()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("There is no EntityDefinition for the referenceUri" + referenceUri));
    return reference;
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
