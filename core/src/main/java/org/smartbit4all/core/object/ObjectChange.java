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
package org.smartbit4all.core.object;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.core.utility.StringConstant;

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

  private final String path;

  /**
   * Can be new or modified. The deletion is managed in the modification of the parent context.
   */
  private final ChangeState operation;

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
   * The changes of the collection objects.
   */
  private final List<CollectionObjectChange> collectionObjects = new ArrayList<>();

  /**
   * @param objectUri
   */
  ObjectChange(String path, ChangeState operation) {
    this.path = path;
    this.operation = operation;
  }

  public String getPath() {
    return path;
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

  public final List<CollectionObjectChange> getCollectionObjects() {
    return collectionObjects;
  }

  public final ChangeState getOperation() {
    return operation;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(operation);
    // The toString() of the property changes in alphabetic order.
    properties.stream().sorted((i1, i2) -> i1.getName().compareTo(i2.getName())).forEach(i -> {
      sb.append(StringConstant.NEW_LINE);
      sb.append(i.toString());
    });
    references.stream().sorted((i1, i2) -> i1.getName().compareTo(i2.getName())).forEach(i -> {
      sb.append(StringConstant.NEW_LINE);
      sb.append(i.toString());
    });
    collections.stream().sorted((i1, i2) -> i1.getName().compareTo(i2.getName())).forEach(i -> {
      sb.append(StringConstant.NEW_LINE);
      sb.append(i.toString());
    });
    return sb.toString();
  }

}
