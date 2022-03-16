package org.smartbit4all.core.object;

import java.io.IOException;
import java.util.Optional;
import org.smartbit4all.api.binarydata.BinaryData;

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

}
