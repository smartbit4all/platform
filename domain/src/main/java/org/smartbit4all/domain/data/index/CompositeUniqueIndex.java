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
package org.smartbit4all.domain.data.index;

import java.util.Map;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.utility.CompositeValue;

/**
 * The abstract superclass of the composite indices.
 * 
 * @author Peter Boros
 */
public abstract class CompositeUniqueIndex extends TableDataIndex {

  /**
   * If the given column requires a fast access to the row and the column value is unique then this
   * map will be maintained to ensure. If the given column is indexed in this way then we can use
   * this index for filtering and accessing the relevant rows by the value of this column.
   */
  protected Map<CompositeValue, DataRow> index = null;

  CompositeUniqueIndex(TableData<?> tableData) {
    super(tableData, IndexType.UNIQUE, true);
  }

}
