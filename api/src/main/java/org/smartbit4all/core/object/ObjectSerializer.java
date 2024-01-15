package org.smartbit4all.core.object;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.smartbit4all.api.binarydata.BinaryData;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * The domain objects must be serialized by definition. The serialization method could be different
 * for object definitions and storages. This interface contains the two method for the
 * serialization. It can be configured for every {@link ObjectDefinition} but the {@link ObjectApi}
 * will define a default one also.
 *
 * @author Peter Boros
 */
public interface ObjectSerializer {

  String getName();

  /**
   * Serialize a domain object and produce a {@link BinaryData} with the binary content.
   *
   * @param obj The object.
   * @param clazz The class of the object.
   * @return The result {@link BinaryData}.
   */
  BinaryData serialize(Object obj, Class<?> clazz);

  BinaryData serialize(Object obj, Class<?> clazz, int memorylimit);

  /**
   * Read a domain object from a {@link BinaryData} into an object.
   *
   * @param <T> The class type template.
   * @param data The binary format of the object.
   * @param clazz The expected class of the object.
   * @return The result object.
   * @throws IOException The {@link IOException} is different as any other exception. This exception
   *         can occur temporarily so it might be useful to separate from other exceptions. When we
   *         have {@link IOException} then we can retry the serialization.
   */
  <T> Optional<T> deserialize(BinaryData data, Class<T> clazz) throws IOException;

  /**
   * Read a domain object from a String into an object.
   *
   * @param <T> The class type template.
   * @param data The string format of the object.
   * @param clazz The expected class of the object.
   * @return The result object.
   * @throws IOException The {@link IOException} is different as any other exception. This exception
   *         can occur temporarily so it might be useful to separate from other exceptions. When we
   *         have {@link IOException} then we can retry the serialization.
   */
  <T> T fromString(String data, Class<T> clazz) throws IOException;

  /**
   * Transform the object to a Map.
   *
   * @param object The object to transform.
   * @return The map as the result of the object values. (Typical JSON like mapping) Returns empty
   *         map if the object is null.
   */
  Map<String, Object> toMap(Object object);

  /**
   * Constructs the object from the values of map.
   *
   * @param <T>
   * @param map The map with the values
   * @return The object as a result. Returns null if the map is null.
   */
  <T> T fromMap(Map<String, Object> map, Class<T> clazz);

  /**
   * Can be used to serialize any Java object as a String.
   *
   * @param object The object to serialize
   * @return The result string. Typical JSON like mapping. Returns null if the object is null.
   * @throws JsonProcessingException
   */
  String writeValueAsString(Object object) throws JsonProcessingException;

}
