package org.smartbit4all.core.object;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataOutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The default serialization for the domain objects.
 * 
 * @author Peter Boros
 */
public class ObjectSerializerByObjectMapper implements ObjectSerializer {

  private static final Logger log = LoggerFactory.getLogger(ObjectSerializerByObjectMapper.class);

  /**
   * 4Kb should be enough forever... :)
   */
  private static int MEMORYLIMIT = 0xFFF;

  /**
   * The {@link ObjectMapper} instance that contains the default configuration.
   */
  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String getName() {
    return ObjectMapper.class.getName();
  }

  @Override
  public BinaryData serialize(Object obj, Class<?> clazz) {
    if (obj == null) {
      return null;
    }

    OutputStreamWriter osw = null;
    BinaryDataOutputStream os = null;
    try {
      os = new BinaryDataOutputStream(MEMORYLIMIT);
      // We write and read in UTF-8 to enable international characters.
      osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
      objectMapper.writeValue(osw, obj);
    } catch (Exception e) {
      String msg = "Unable to serialize " + obj;
      log.error(msg, e);
      throw new IllegalArgumentException(msg, e);
    } finally {
      if (osw != null) {
        try {
          osw.close();
        } catch (IOException e) {
          String msg = "Unable to serialize " + obj;
          log.error(msg, e);
          throw new IllegalStateException(msg, e);
        }
      }
    }
    return os.data();
  }

  @Override
  public <T> Optional<T> deserialize(BinaryData data, Class<T> clazz) {
    if (data == null || data.length() == 0) {
      return null;
    }
    InputStreamReader isr = null;
    // We write and read in UTF-8 to enable international characters.
    isr = new InputStreamReader(data.inputStream(), StandardCharsets.UTF_8);
    try {
      return Optional.of(objectMapper.readValue(isr, clazz));
    } catch (Exception e) {
      throw new IllegalArgumentException("Unable to load object.", e);
    } finally {
      try {
        isr.close();
      } catch (IOException e) {
        // NOP - We've tried at least.
      }
    }
  }

}
