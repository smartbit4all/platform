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
package org.smartbit4all.api;

import java.util.Map;

/**
 * To show the modification status of the api object we can use this folder object. The subclasses
 * will show new / changed / deleted status. The change
 * 
 * @author Peter Boros
 * @param <T>
 */
public class ApiItemChangeEvent<T> {

  /**
   * The operation of the api item.
   */
  private final ApiItemOperation operation;

  /**
   * The object itself.
   */
  private final T item;

  /**
   * The changes is an optional thing that can be provided by the api to help the fine grained
   * management of the api objects.
   */
  Map<String, ApiItemChange> changes = null;

  /**
   * Constructs an api item with the given operation.
   * 
   * @param operation
   * @param item
   */
  public ApiItemChangeEvent(ApiItemOperation operation, T item) {
    super();
    this.operation = operation;
    this.item = item;
  }

  /**
   * Constructs an api item with {@link ApiItemOperation#CHANGED} operation.
   * 
   * @param item
   * @param changes
   */
  public ApiItemChangeEvent(T item, Map<String, ApiItemChange> changes) {
    super();
    this.operation = ApiItemOperation.CHANGED;
    this.item = item;
    this.changes = changes;
  }

  /**
   * @return The api object itself.
   */
  public T item() {
    return item;
  }

  public ApiItemOperation operation() {
    return operation;
  }

}
