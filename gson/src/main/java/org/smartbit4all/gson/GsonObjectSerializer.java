package org.smartbit4all.gson;

import java.io.IOException;
import java.util.Optional;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectSerializer;

public class GsonObjectSerializer implements ObjectSerializer {

  public static final String GSON = "GSON";

  private final byte[] EMPTY = new byte[0];

  @Override
  public String getName() {
    return GSON;
  }

  @Override
  public BinaryData serialize(Object obj, Class<?> clazz) {
    if (obj == null) {
      return new BinaryData(EMPTY);
    }
    try {
      Object object = ApiObjectRef.unwrapObject(obj);
      return GsonBinaryData.toJsonBinaryData(object, clazz);
    } catch (Exception e) {
      throw new RuntimeException("Cannot serialize the object of class: " + obj, e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> deserialize(BinaryData data, Class<T> clazz) {
    try {
      return Optional.of((T) GsonBinaryData.fromJsonBinaryData(data, clazz));
    } catch (IOException e) {
      throw new RuntimeException("Cannot deserialize the object of class: " + clazz.getName(), e);
    }
  }

}
