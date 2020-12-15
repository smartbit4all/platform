package org.smartbit4all.domain.service.entity;

import java.net.URI;
import java.util.List;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.service.dataset.DataSetEntry;

/**
 * The entities in a system can come from several domains. All the application logics can use these
 * {@link EntityDefinition}s simply. But in the background we need to know what is the location of
 * the given entity. This is the provider that offers some services as an API.
 * 
 * It manages the API for the {@link DataSetEntry}s.
 * 
 */
public interface EntityManager {

  /**
   * Retrieves an {@link EntityDefinition} identified by it's URI. The URI looks like:
   * 
   * scheme:/host/path
   * 
   * Where:
   * scheme is the 'entity' key identifying the uri target
   * host is the name of the source defining the entity. The path is the name of the entity
   * itself.
   * 
   * @param uri
   * @return
   */
  EntityDefinition definition(URI uri);

  Property<?> property(URI uri);

  List<EntityDefinition> allDefinitions();

  Reference<?, ?> reference(URI referenceUri);
  

}
