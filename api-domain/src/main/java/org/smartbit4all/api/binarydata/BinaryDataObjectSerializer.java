package org.smartbit4all.api.binarydata;

import java.util.Optional;

public interface BinaryDataObjectSerializer {

  /**
   * Serialize the object to BinaryData.
   */
  <T> BinaryData toJsonBinaryData(T object, Class<T> clazz);

  /**
   * Deserialize the object from BinaryData.
   */
  <T> Optional<T> fromJsonBinaryData(BinaryData binaryData, Class<T> clazz);

}
