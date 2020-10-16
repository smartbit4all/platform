package org.smartbit4all.json.serializer;

import java.io.IOException;
import org.smartbit4all.domain.meta.JDBCDataConverter;
import org.smartbit4all.json.deserializer.JDBCDataConverterDeserializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JDBCDataConverterSerializer extends JsonSerializer<JDBCDataConverter<?, ?>> {

  @Override
  public void serialize(JDBCDataConverter<?, ?> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartObject();
    gen.writeStringField(JDBCDataConverterDeserializer.JDBCDATACONVERTER_SOURCECLASS, value.appType().getName());
    gen.writeEndObject();    
  }
}