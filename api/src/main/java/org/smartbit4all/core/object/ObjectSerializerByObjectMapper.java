package org.smartbit4all.core.object;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataOutputStream;
import org.smartbit4all.core.object.serialize.UriDeserializer;
import org.smartbit4all.core.object.serialize.ZonedLocalDateDeserializer;
import org.smartbit4all.core.object.serialize.ZonedLocalDateTimeDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * The default serialization for the domain objects.
 *
 * @author Peter Boros
 */
public class ObjectSerializerByObjectMapper implements ObjectSerializer {

  private static final Logger log = LoggerFactory.getLogger(ObjectSerializerByObjectMapper.class);

  /**
   * 8Kb should be enough forever... :)
   */
  private static int MEMORYLIMIT = 0x2000;

  /**
   * The {@link ObjectMapper} instance that contains the default configuration. The
   * {@link ZonedDateTime} and {@link OffsetDateTime} serialization is added from the
   * jackson-datatype-jsr310 module. The adjustment of the dates are also disabled to preserve the
   * original time.
   */
  private ObjectMapper objectMapper;

  public ObjectSerializerByObjectMapper() {
    super();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    SimpleModule module = new SimpleModule();
    module.addDeserializer(LocalDate.class, ZonedLocalDateDeserializer.INSTANCE);
    module.addDeserializer(LocalDateTime.class, ZonedLocalDateTimeDeserializer.INSTANCE);
    module.addDeserializer(URI.class, UriDeserializer.INSTANCE);
    objectMapper.registerModule(module);
    // Task 5641: Storage - OffsetDateTime formatting with default serializer
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    // We might use it to avoid writing null values.
    // objectMapper.setSerializationInclusion(Include.NON_NULL);
  }

  @Override
  public String getName() {
    return ObjectMapper.class.getName();
  }

  @Override
  public BinaryData serialize(Object obj, Class<?> clazz) {
    return serialize(obj, clazz, MEMORYLIMIT);
  }

  @Override
  public BinaryData serialize(Object obj, Class<?> clazz, int memorylimit) {
    if (obj == null) {
      return null;
    }

    OutputStreamWriter osw = null;
    BinaryDataOutputStream os = null;
    try {
      os = new BinaryDataOutputStream(memorylimit);
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
  public <T> Optional<T> deserialize(BinaryData data, Class<T> clazz) throws IOException {
    if (data == null || data.length() == 0) {
      return Optional.empty();
    }
    InputStreamReader isr = null;
    // We write and read in UTF-8 to enable international characters.
    isr = new InputStreamReader(data.inputStream(), StandardCharsets.UTF_8);
    try {
      return Optional.of(objectMapper.readValue(isr, clazz));
    } catch (IOException e) {
      throw e;
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

  @Override
  public <T> T fromString(String data, Class<T> clazz) throws IOException {
    if (data == null && clazz == null) {
      return null;
    }
    return objectMapper.readValue(data, clazz);
  }

  @Override
  public String writeValueAsString(Object object) throws JsonProcessingException {
    if (object == null) {
      return null;
    }
    return objectMapper.writeValueAsString(object);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> toMap(Object object) {
    return objectMapper.convertValue(object, Map.class);
  }

  @Override
  public <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
    return objectMapper.convertValue(map, clazz);
  }

}
