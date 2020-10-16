package org.smartbit4all.json.serializer;

import java.io.IOException;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.json.deserializer.TableDataDeserializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom Serializer for the TableData.
 * 
 * @author Zolt�n Suller
 */
public class TableDataSerializer extends JsonSerializer<TableData<?>> {

  @Override
  public void serialize(TableData<?> tableData, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartObject();

    // Entity definition
    serializers.defaultSerializeField(TableDataDeserializer.TABLEDATA_ENTITYDEF, tableData.entity(),
        gen);

    // Columns
    gen.writeArrayFieldStart(TableDataDeserializer.TABLEDATA_COLUMNS);
    for (DataColumn<?> column : tableData.columns()) {
      gen.writeObject(column.getProperty());
    }
    gen.writeEndArray(); // columns

    // Rows
    gen.writeArrayFieldStart(TableDataDeserializer.TABLEDATA_ROWS);
    for (DataRow row : tableData.rows()) {
      gen.writeStartObject();

      // values
      gen.writeArrayFieldStart(TableDataDeserializer.TABLEDATA_ROW_VALUES);
      for (DataColumn<?> column : tableData.columns()) {
        gen.writeStartObject();
        Object value = tableData.get(column, row);
        gen.writeObjectField(TableDataDeserializer.TABLEDATA_ROW_VALUE, value);
        gen.writeStringField(TableDataDeserializer.TABLEDATA_ROW_VALUE_TYPE,
            value.getClass().getName());

        gen.writeEndObject();
      }
      gen.writeEndArray(); // row values
      gen.writeEndObject();
    }
    gen.writeEndArray(); // rows

    gen.writeEndObject();
  }
}
