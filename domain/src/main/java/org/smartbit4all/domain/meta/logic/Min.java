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
package org.smartbit4all.domain.meta.logic;

import org.smartbit4all.domain.meta.ComputationLogic;
import org.smartbit4all.domain.meta.PropertyComputed;

/**
 * The classical min operation that works at SQL level. The {@link PropertyComputed} property with
 * this logic will contain the smallest value. This logic is aware of the group by, if we have group
 * by properties then it will be calculated for the groups formed by the similar values of these
 * properties.
 * 
 * @author Peter Boros
 */
public interface Min extends ComputationLogic {

  /**
   * The constant for the naming.
   */
  public static String PROPERTTYNAME = "min";

}
