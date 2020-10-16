package org.smartbit4all.json.deserializer;

import java.io.IOException;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DataRowDeserializer extends JsonDeserializer<DataRow>{

  @Override
  public DataRow deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    TableDataDeserializer tableDataDeserializer = new TableDataDeserializer();
    TableData<?> tableData = tableDataDeserializer.deserialize(p, ctxt);
    return tableData.rows().get(0);
  }

}
