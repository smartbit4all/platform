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

import java.util.Objects;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.EntityDefinition;

public class QueryOutput {

  /**
   * The name identifier of the query output. This should match the corresponding input's name.
   */
  private String name;

  /**
   * The in-memory tableData result or in case of serialized data it is an empty tableData holding
   * the meta information
   */
  private TableData<?> tableData;

  /**
   * When the result is serialized, it is stored in this {@link BinaryData}. The BinaryData can only
   * be file based, so the data will not be kept in memory.
   */
  private BinaryData serializedTableData;

  private EntityDefinition entityDef;

  public QueryOutput(String name, EntityDefinition entityDef) {
    Objects.requireNonNull(name, "name can not be null!");
    Objects.requireNonNull(entityDef, "entityDef can not be null!");
    this.name = name;
    this.entityDef = entityDef;
  }

  public String getName() {
    return name;
  }

  /**
   * Returns the {@link TableData} of the result. If the corresponding
   * {@link QueryInput#isResultSerialized()} was true, the table data holds only the meta of the
   * result and the data is stored in the {@link #serializedTableData}. Else the {@link TableData}
   * stores the data too.
   */
  public TableData<?> getTableData() {
    return tableData;
  }

  public void setTableData(TableData<?> tableData) {
    this.tableData = tableData;
  }

  /**
   * Returns the query result serialized {@link TableData} as a {@link BinaryData}. It is null if
   * the {@link QueryInput#setResultSerialized(boolean)} has not been set to true.
   */
  public BinaryData getSerializedTableData() {
    return serializedTableData;
  }

  public void setSerializedTableData(BinaryData serializedTableData) {
    this.serializedTableData = serializedTableData;
  }

  /**
   * Copies the result of the given query output to this, overriding the existing result.
   */
  public void copyResult(QueryOutput queryOutput) {
    Objects.requireNonNull(queryOutput, "queryOutput can not be null!");
    if (!queryOutput.entityDef.getUri().equals(this.entityDef.getUri())) {
      throw new IllegalArgumentException(
          "Can not copy queryOutput results with different EntityDefinitions!");
    }

    if (this.tableData != null) {
      this.tableData.clearRows();
      TableDatas.append(this.tableData, queryOutput.tableData);
    } else {
      this.tableData = queryOutput.tableData;
    }
    this.setSerializedTableData(queryOutput.getSerializedTableData());
  }

  /**
   * Appends the result of the given query output to this, adding to the existing result.
   */
  public void appendResult(QueryOutput queryOutput) {
    Objects.requireNonNull(queryOutput, "queryOutput can not be null!");
    if (!queryOutput.entityDef.getUri().equals(this.entityDef.getUri())) {
      throw new IllegalArgumentException(
          "Can not copy queryOutput results with different EntityDefinitions!");
    }

    TableDatas.append(this.tableData, queryOutput.tableData);
    // TODO append serielized file content
  }

  /**
   * @return if the result of the output serialized.
   */
  public boolean isResultSerialized() {
    return getSerializedTableData() != null;
  }

  public boolean hasResult() {
    return this.tableData != null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getName()).append("\n");
    sb.append("\tname: ").append(name).append("\n");
    sb.append("\tisSerialized: ").append(isResultSerialized()).append("\n");
    sb.append("\tentityDefUri: ").append(entityDef.getUri()).append("\n");
    sb.append("\tdata:\n");
    if (this.tableData != null) {
      sb.append(this.tableData.toString()).append("\n");
    } else {
      sb.append("No data yet\n");
    }
    return sb.toString();
  }

}
