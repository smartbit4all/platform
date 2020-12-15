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
package org.smartbit4all.json.deserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class TableDataDeserializer extends JsonDeserializer<TableData<?>> {

  public static final String TABLEDATA_ENTITYDEF = "entityDef";

  public static final String TABLEDATA_COLUMNS = "columns";

  public static final String TABLEDATA_ROWS = "rows";

  public static final String TABLEDATA_ROW_VALUES = "values";

  public static final String TABLEDATA_ROW_VALUE = "value";

  public static final String TABLEDATA_ROW_VALUE_TYPE = "type";

  @Override
  @SuppressWarnings("unchecked")
  public TableData<?> deserialize(JsonParser p, DeserializationContext context)
      throws IOException, JsonProcessingException {

    ObjectMapper objectMapper = new ObjectMapper().setConfig(context.getConfig());
    ObjectCodec codec = p.getCodec();
    JsonNode node = codec.readTree(p);

    // Entity definition
    EntityDefinition entityDef =
        objectMapper.convertValue(node.get(TABLEDATA_ENTITYDEF), EntityDefinition.class);
    // EntityDefinition entityDef = context.readValue(node.get("entityDef").traverse(codec),
    // EntityDefinition.class);

    // Creating TableData
    TableData<?> td = new TableData<>(entityDef);

    // Columns
    ArrayNode columnsNode = (ArrayNode) node.get(TABLEDATA_COLUMNS);
    List<DataColumn<?>> columns = new ArrayList<>();
    for (JsonNode columnNode : columnsNode) {
      Property<?> columnProperty = objectMapper.convertValue(columnNode, Property.class);
      DataColumn<?> column = td.addColumnOwn(columnProperty);
      columns.add(column);
    }

    // Rows + Values
    ArrayNode rowsNode = (ArrayNode) node.get(TABLEDATA_ROWS);
    for (JsonNode rowNode : rowsNode) {
      DataRow row = td.addRow();

      ArrayNode valuesNode = (ArrayNode) rowNode.get(TABLEDATA_ROW_VALUES);
      for (int i = 0; i < valuesNode.size(); ++i) {
        JsonNode valueNode = valuesNode.get(i);

        Object value = null;
        try {
          JsonNode type = valueNode.get(TABLEDATA_ROW_VALUE_TYPE);
          Class<?> valueType = Class.forName(type.asText());
          value = objectMapper.convertValue(valueNode.get(TABLEDATA_ROW_VALUE), valueType);
          // JsonParser traverse = valueNode.get("value").traverse(codec);
          // traverse.nextToken();
          // value = (T) context.readValue(traverse, valueType);
        } catch (ClassNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        td.setObject(columns.get(i), row, value);
      }
    }

    return td;
  }
}
