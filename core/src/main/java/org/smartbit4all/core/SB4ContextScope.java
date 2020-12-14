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
 * If the {@link SB4Service} implements this then it has its own context. So invoking an operation
 * in this service means that we change the active context in the invocation handler. So it will be
 * the active context until this operation will end.
 * 
 * @author Peter Boros
 *
 */
public interface SB4ContextScope {

  /**
   * The accessor method for the context owned by the scope.
   * 
   * @return
   */
  SB4Context context();

}
