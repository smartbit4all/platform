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
package org.smartbit4all.domain.meta;

import org.smartbit4all.core.SB4Function;

/**
 * This logic is implements and responsible for a computation in a given situation. It has necessary
 * properties to include and has some specific trigger for the given event. The logic can implement
 * the following computations:
 * <ul>
 * <li>Value change: The value of a property has been changed.</li>
 * <li>Record change: At the end of the record change the whole record is being processed.</li>
 * <li>Transaction finished: At the end of the data modification transaction. These are global
 * logics like aggregate values or so.</li>
 * <li>...</li>
 * </ul>
 * All the logics can depend on properties that are necessary for them. If a logic is installed into
 * a scenario then all the dependent properties must be included at the same time. --> The
 * computation framework must be fail safe to avoid contact checking boilerplate at the beginning of
 * the computation.
 * 
 * @author Peter Boros
 *
 */
public interface EventHandler extends SB4Function<EventParameter, EventParameter> {

  /**
   * The input/output parameters of the logic.
   * 
   * @return
   */
  EventParameter parameter();

  /**
   * The meta level parameters
   * 
   * @return
   */
  EventParameterMeta meta();

  /**
   * The entity that is the root of this computation.
   * 
   * @return
   */
  EntityDefinition entity();

}
