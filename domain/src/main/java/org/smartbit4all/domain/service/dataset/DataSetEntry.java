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
package org.smartbit4all.domain.service.dataset;

import java.net.URI;

/**
 * This API object is a descriptor for the data set. This is used by the {@link DataSetApi}.
 * 
 * @author Peter Boros
 */
public class DataSetEntry {

  /**
   * The identification of the data set. Consists of the provider as scheme. The provider is a kind
   * of storage for the data set. The path refers the storage inside the provider. Something like
   * numbers or strings. Usually it is the table name for example in case of RDBMS storage. The
   * fragment will identify the data set as a name inside the given provider.
   */
  private URI uri;

  /**
   * The URI of the property that is stored in the data set. It's important to see what is the
   * content. It can help to identify how to apply this set in a filtering situation. The URI looks
   * like this: provider:/entity#property
   */
  private URI propertyUri;

}
