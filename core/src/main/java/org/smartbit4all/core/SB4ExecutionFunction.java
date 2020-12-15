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
package org.smartbit4all.core;

/**
 * This node is responsible for executing the instance of the given executable. Inner class for the
 * {@link SB4ExecutionNode}.
 * 
 * @author Peter Boros
 */
final class SB4ExecutionFunction implements SB4Execution {

  /**
   * The piece of code to be executed in the given code. This is always a new instance for the given
   * execution plan so it can use the fields to store information during the execution.
   */
  SB4Function<?, ?> service;

  SB4ExecutionFunction(SB4Function<?, ?> executable) {
    super();
    this.service = executable;
  }

}
