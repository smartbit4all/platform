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
