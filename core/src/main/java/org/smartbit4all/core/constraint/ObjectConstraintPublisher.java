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
package org.smartbit4all.core.constraint;

import org.smartbit4all.core.event.EventPublisher;

/**
 * This interface is the inner API interface for the stateful apis providing constraints. The
 * constraints are meta data related to the api object hierarchy. The constraints can be the
 * following: Enabled/disable, Mandatory/optional, Format, Focus Selection - collection
 * 
 * The given constraints have different characteristic. All of them are managed in a hierarchical
 * way. The path is defined by the Api object hierarchy.
 * <ul>
 * <li><b>Editable - Boolean</b> - Whether or not possible to modify the value of a given property,
 * reference or collection.</li>
 * <li><b>Mandatory - Boolean</b> - If it's true then the given value must be set by the end of the
 * editing session.</li>
 * <li><b>Format - InputFormat</b> - We can define a symbolic instruction for the subscriber how to
 * format the input field. If the field native value is String then it's a simple formatting option.
 * But in case of other types it can serve the purpose of conversions.</li>
 * </ul>
 * 
 * @author Peter Boros
 */
public interface ObjectConstraintPublisher extends EventPublisher {

  /**
   * The mandatory events.
   * 
   * @return
   */
  public ObjectConstraintChangeEvent<Boolean> mandatory();

  /**
   * The editable events.
   * 
   * @return
   */
  public ObjectConstraintChangeEvent<Boolean> editable();

  /**
   * The format events.
   * 
   * @return
   */
  public ObjectConstraintChangeEvent<String> format();

}
