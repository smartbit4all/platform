package org.smartbit4all.json.deserializer.collection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.json.serializer.collection.DataRowCollectionSerializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Set of DataRows serialized with {@link DataRowCollectionSerializer} can be
 * unmarshalled into {@link HashSet} implementations with this deserializer.
 * 
 *  @see DataRowCollectionSerializer
 */
public class DataRowSetDeserializer extends JsonDeserializer<Set<DataRow>>{

  @Override
  public Set<DataRow> deserialize(JsonParser p, DeserializationContext context)
      throws IOException, JsonProcessingException {
    
    ObjectMapper objectMapper = new ObjectMapper().setConfig(context.getConfig());
    ObjectCodec codec = p.getCodec();
    TreeNode tree = codec.readTree(p);
    ArrayNode tableNodes = (ArrayNode) tree.get(DataRowCollectionSerializer.DATAROW_COLLECTION_TABLES);
    List<TableData<?>> tables = new ArrayList<TableData<?>>();
    for (JsonNode tableNode : tableNodes) {
      TableData<?> table = objectMapper.convertValue(tableNode, new TypeReference<TableData<?>>() {});
      tables.add(table);
    }
    
    Set<DataRow> rows = new HashSet<>();
    
    for(TableData<?> table : tables) {
      rows.addAll(table.rows());
    }
    
    return rows;
  }

}
