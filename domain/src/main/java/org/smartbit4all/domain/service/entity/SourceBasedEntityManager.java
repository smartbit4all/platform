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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.Reference;

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

  @Override
  public List<EntityDefinition> allDefinitions() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Reference<?, ?> reference(URI referenceUri) {
    // TODO Auto-generated method stub
    return null;
  }

}
