package org.smartbit4all.domain.service.entity;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

public class SourceBasedEntityManager implements EntityManager {

  private Map<String, EntitySource> sourcesById = new HashMap<>();
  
  public SourceBasedEntityManager(List<EntitySource> entitySources) {
    entitySources.forEach(this::registerEntitySource);
  }
  
  public void registerEntitySource(EntitySource entitySource) {
    sourcesById.put(entitySource.getSourceId(), entitySource);
  }
  
  @Override
  public EntityDefinition definition(URI uri) {
    EntitySource source = getSource(uri);
    String entityPath = EntityUris.getEntityPath(uri);
    return source.getEntity(entityPath);
  }

  @Override
  public Property<?> property(URI uri) {
    EntitySource source = getSource(uri);
    String entityPath = EntityUris.getEntityPath(uri);
    String property = EntityUris.getProperty(uri);
    return source.getProperty(entityPath, property);
  }

  private EntitySource getSource(URI uri) {
    String sourceId = EntityUris.getDomain(uri);
    EntitySource source = sourcesById.get(sourceId);
    if(source == null) {
      throw new IllegalStateException("There is no entity source registered with id: " + sourceId);
    }
    return source;
  }

}
