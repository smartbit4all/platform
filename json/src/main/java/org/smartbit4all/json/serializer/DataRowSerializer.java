package org.smartbit4all.json.serializer;

import java.io.IOException;
import java.lang.reflect.Field;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DataRowSerializer extends JsonSerializer<DataRow>{

  @Override
  public void serialize(DataRow row, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    try {
      Field tdField = row.getClass().getDeclaredField("tableData");
      tdField.setAccessible(true);
      TableData<?> originalTableData = (TableData<?>) tdField.get(row);
      
      TableData<?> singleRowTableData = createSingleRowCopy(originalTableData, row);
      
      TableDataSerializer tdSerializer = new TableDataSerializer();
      tdSerializer.serialize(singleRowTableData, gen, serializers);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
      throw new IOException(e);
    }
    
  }

  private TableData<?> createSingleRowCopy(TableData<?> originalTableData, DataRow row) {
    TableData<?> singleRowTableData = new TableData<>(originalTableData.entity());
    for(DataColumn<?> dataCol : originalTableData.columns()) {
      singleRowTableData.addColumn(dataCol.getProperty());
    }
    DataRow singleRow = singleRowTableData.addRow();
    for(DataColumn<?> dataCol : originalTableData.columns()) {
      Property<?> property = dataCol.getProperty();
      singleRow.setObject(property, row.get(property));
    }
    
    return singleRowTableData;
  }

}
