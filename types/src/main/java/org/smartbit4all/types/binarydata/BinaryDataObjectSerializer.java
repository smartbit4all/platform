package org.smartbit4all.types.binarydata;

import java.util.Optional;

public interface BinaryDataObjectSerializer {
  
  /**
   * Serialize the object to BinaryData.
   */
  public <T> BinaryData toJsonBinaryData(T object, Class<T> clazz);

  /**
   * Deserialize the object from BinaryData.
   */
  public <T> Optional<T> fromJsonBinaryData(BinaryData binaryData, Class<T> clazz);
  
}
