package org.smartbit4all.domain.service.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.binarydata.BinaryDataOutputStream;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.utility.serialize.TableDataSerializer;

public class QueryOutputResultAssemblers {

  /**
   * Based on the {@link QueryInput} a {@link QueryOutputResultAssembler} will be created.
   */
  public static QueryOutputResultAssembler create(QueryInput input, QueryOutput output) {
    Objects.requireNonNull(input, "input can not be null!");
    Objects.requireNonNull(output, "output can not be null!");

    if (input.isResultSerialized()) {
      return new QueryOutputResultAssemblerFile(input, output);
    } else {
      return new QueryOutputResultAssemblerInMem(input, output);
    }
  }

  private static class QueryOutputResultAssemblerInMem implements QueryOutputResultAssembler {

    private QueryOutput queryOutput;

    /**
     * The existing or the newly created {@link TableData}.
     */
    private TableData<?> tableData;


    /**
     * The current row used for setting the values.
     */
    DataRow row;

    /**
     * The {@link EntityDefinition} the query is based on.
     */
    EntityDefinition entityDef;

    /**
     * The index based access to the {@link DataColumn}.
     */
    List<DataColumn<?>> columnsByIndex = new ArrayList<>();

    private QueryOutputResultAssemblerInMem(QueryInput queryInput, QueryOutput queryOutput) {
      Objects.requireNonNull(queryOutput, "queryOutput can not be null!");
      this.queryOutput = queryOutput;
      this.entityDef = queryInput.entityDef();
    }

    public void start() {
      if (tableData == null) {
        tableData = new TableData<>(entityDef);
      }
      tableData.clearRows();
    }


    public int accept(Property<?> property) {
      columnsByIndex.add(tableData.addColumnOwn(property));
      return columnsByIndex.size() - 1;
    }

    @Override
    public void finishColumns() {
      // NOPE
    }

    public void setValue(int index, Object value) {
      tableData.setObject(columnsByIndex.get(index), row, value);
    }

    public boolean startRow() {
      row = tableData.addRow();
      return true;
    }

    public void finishRow() {
      row = null;
    }

    public void finish() {
      queryOutput.setTableData(tableData);
    }

  }

  private static class QueryOutputResultAssemblerFile implements QueryOutputResultAssembler {

    private QueryOutput queryOutput;

    private TableData<?> tableData;

    DataRow row;

    EntityDefinition entityDef;

    List<DataColumn<?>> columnsByIndex = new ArrayList<>();

    private TableDataSerializer serializer;

    private BinaryDataOutputStream os;

    private QueryOutputResultAssemblerFile(QueryInput queryInput, QueryOutput queryOutput) {
      if (!queryInput.isResultSerialized()) {
        throw new IllegalStateException(
            "Can not assemble output result serialized when query is not set to do so.");
      }
      this.queryOutput = queryOutput;
      this.entityDef = queryInput.entityDef();
      // use parameter 0 here to serialize into a temp file
      os = new BinaryDataOutputStream(0);
      serializer = TableDataSerializer.to(os);
    }

    @Override
    public void start() {
      if (tableData == null) {
        tableData = new TableData<>(entityDef);
      }
      tableData.clearRows();
    }

    @Override
    public int accept(Property<?> property) {
      columnsByIndex.add(tableData.addColumnOwn(property));
      return columnsByIndex.size() - 1;
    }

    @Override
    public void finishColumns() {
      queryOutput.setTableData(tableData);
      try {
        serializer.tableData(tableData);
      } catch (IOException e) {
        throw new IllegalStateException("Unable to finalize table data meta  for serialization", e);
      }
    }

    @Override
    public boolean startRow() {
      try {
        row = serializer.addRow();
      } catch (IOException e) {
        throw new IllegalStateException("Unable to add row to table data during serialization", e);
      }
      return true;
    }

    @Override
    public void setValue(int index, Object value) {
      row.setObject(columnsByIndex.get(index), value);
    }

    @Override
    public void finishRow() {
      // release the row instance
      row = null;
    }

    @Override
    public void finish() {
      try {
        serializer.finish();
        queryOutput.setSerializedTableData(os.data());
      } catch (IOException e) {
        throw new IllegalStateException("Unable to finalize table data serialization", e);
      }
    }

  }

}
