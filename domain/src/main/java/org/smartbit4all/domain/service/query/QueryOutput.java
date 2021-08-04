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
package org.smartbit4all.domain.service.query;

import org.smartbit4all.domain.data.TableData;

public class QueryOutput {

  /**
   * The name identifier of the query output. This should match the corresponding input's name.
   */
  private String name;

  /**
   * The existing or the newly created {@link TableData}.
   */
  private TableData<?> tableData;
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TableData<?> getTableData() {
    return tableData;
  }

  public void setTableData(TableData<?> tableData) {
    this.tableData = tableData;
  }

  @Override
  public String toString() {
    return getTableData().toString();
  }

}
