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
package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The instance change means a new or deleted instance. The modification is not necessary as an
 * individual event because it consists of several other change item. If we have a new instance then
 * we get all the property changes also. There is no need to figure out what to in case of new
 * instance. In case of deleted instance we won't get any other change just the delete itself. The
 * root object is also produce this kind of event and we can identify it by it's URI.
 * 
 * This change event contains the property and referential changes. The object change itself can be
 * a new instance a modification and a deletion.
 * 
 * @author Peter Boros
 */
public class ObjectChange {

  /**
   * The objects have global uri that identifies the api, responsible for the given object.
   */
  private final URI objectUri;

  private final UUID id;

  /**
   * The changes of the properties.
   */
  private final List<PropertyChange> properties = new ArrayList<>();

  /**
   * The changes of the references.
   */
  private final List<ReferenceChange> references = new ArrayList<>();

  /**
   * The changes of the collections.
   */
  private final List<CollectionChange> collections = new ArrayList<>();

  /**
   * @param objectUri
   */
  ObjectChange(URI objectUri, UUID id) {
    this.objectUri = objectUri;
    this.id = id;
  }

  public final URI getObjectUri() {
    return objectUri;
  }

  public final UUID getId() {
    return id;
  }

  public final List<PropertyChange> getProperties() {
    return properties;
  }

  public final List<ReferenceChange> getReferences() {
    return references;
  }

  public final List<CollectionChange> getCollections() {
    return collections;
  }



}
