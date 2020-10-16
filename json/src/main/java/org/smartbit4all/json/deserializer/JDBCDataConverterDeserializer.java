package org.smartbit4all.json.deserializer;

import java.io.IOException;
import org.smartbit4all.domain.meta.JDBCDataConverter;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class JDBCDataConverterDeserializer extends JsonDeserializer<JDBCDataConverter<?, ?>> {

  public static final String JDBCDATACONVERTER_SOURCECLASS = "sourceClass";
  
  @Autowired
  private JDBCDataConverterHelper helper;
  
  @Override
  public JDBCDataConverter<?, ?> deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    ObjectCodec oc = p.getCodec();
    JsonNode node = oc.readTree(p);
    
    String sourceClassName = node.get(JDBCDATACONVERTER_SOURCECLASS).asText();
    try {
      Class<?> sourceClass = Class.forName(sourceClassName);
      JDBCDataConverter<?, ?> converter = helper.from(sourceClass);
      return converter;
    } catch (BeansException | ClassNotFoundException e) {
      // TODO throw exception?
      e.printStackTrace();
    }
    return null;
  }

}
