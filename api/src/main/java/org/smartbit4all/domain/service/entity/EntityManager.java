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
   * Where: scheme is the 'entity' key identifying the uri target host is the name of the source
   * defining the entity. The path is the name of the entity itself.
   * 
   * @param uri
   * @return
   */
  EntityDefinition definition(URI uri);

  Property<?> property(URI uri);

  List<EntityDefinition> allDefinitions();

  Reference<?, ?> reference(URI referenceUri);

  void registerEntityDef(EntityDefinition entityDef);

}
