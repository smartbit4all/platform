package org.smartbit4all.core.object.serialize;

import java.io.IOException;
import java.net.URI;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class UriDeserializer extends JsonDeserializer<URI> {

  public static final UriDeserializer INSTANCE =
      new UriDeserializer();

  private UriDeserializer() {}

  @Override
  public URI deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    String text = p.getValueAsString();
    if (text != null) {
      if (text.length() == 0 || (text = text.trim()).length() == 0) {
        // we don't want to treat "" as a valid URI
        return null;
      }
      return URI.create(text);
    }
    return null;
  }

}
