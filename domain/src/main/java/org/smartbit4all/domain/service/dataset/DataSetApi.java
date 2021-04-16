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
package org.smartbit4all.domain.service.dataset;

import java.util.Set;
import org.smartbit4all.domain.meta.Property;

/**
 * This API is responsible for storing, accessing and using set of data. We can add a new set of
 * data by identifying it with an URI. This URI can be used to identify the data set later on. The
 * URI can be used to contribute a set. We can union, intersect, minus etc. to sets identified by an
 * URI and the result will be an updated set even if it's a new one or an existing.
 * 
 * @author Peter Boros
 */
public interface DataSetApi {

  /**
   * Activates a data set with the given values.
   *
   * @param property The property that the set of value is saved for.
   * @param values The values itself.
   * @return The entry for the identification of the entry that contains the URI as an identifier
   *         for the given set.
   */
  DataSetEntry activate(Property<?> property, Set<?> values);

  int getLimit();

}
