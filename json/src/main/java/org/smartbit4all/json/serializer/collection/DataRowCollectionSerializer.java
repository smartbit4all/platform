package org.smartbit4all.json.serializer.collection;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.json.serializer.CollectionTypeSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Returns a basic serialization of any collection of {@link DataRow}. The result json
 * string is basically a {@link TableData} json array, where each element holds the
 * records of the same tableData.
 * <p>
 * This way, for example if the collection that is the subject of serialization only
 * contains rows of one tabledata, the json array will have exactly one element.
 * 
 *  @see CollectionTypeSerializer
 */
public class DataRowCollectionSerializer extends JsonSerializer<Collection<DataRow>> {

  public static final String DATAROW_COLLECTION_TABLES = "tables";

  @Override
  public void serialize(Collection<DataRow> rows, JsonGenerator gen,
      SerializerProvider provider) throws IOException {
    try {
      Map<TableData<?>, Collection<DataRow>> rowsByTableData = new HashMap<>();
      
      for(DataRow row : rows) {
        Field tdField = row.getClass().getDeclaredField("tableData");
        tdField.setAccessible(true);
        TableData<?> tableData = (TableData<?>) tdField.get(row);
        Collection<DataRow> collection = rowsByTableData.get(tableData);
        if(collection == null) {
          collection = new ArrayList<>();
          rowsByTableData.put(tableData, collection);
        }
        collection.add(row);
      }
      
      List<TableData<?>> tdsToSerialize = new ArrayList<>();
      for(Entry<TableData<?>, Collection<DataRow>> entry : rowsByTableData.entrySet()) {
        TableData<?> copyTableData = createEmptyTableDataCopy(entry.getKey());
        for(DataRow row : entry.getValue()) {
          addRow(copyTableData, entry.getKey(), row);
        }
        tdsToSerialize.add(copyTableData);
      }
      
      gen.writeStartObject();
      provider.defaultSerializeField(DATAROW_COLLECTION_TABLES, tdsToSerialize, gen);
      gen.writeEndObject();
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
      throw new IOException(e);
    }
    
  }
  
  private TableData<?> createEmptyTableDataCopy(TableData<?> originalTableData) {
    TableData<?> copyTableData = new TableData<>(originalTableData.entity());
    for(DataColumn<?> dataCol : originalTableData.columns()) {
      copyTableData.addColumn(dataCol.getProperty());
    }
    return copyTableData;
  }
  
  private void addRow(TableData<?> copyTableData, TableData<?> originalTableData, DataRow row) {
    DataRow singleRow = copyTableData.addRow();
    for(DataColumn<?> dataCol : originalTableData.columns()) {
      Property<?> property = dataCol.getProperty();
      singleRow.setObject(property, row.get(property));
    }
  }

}
