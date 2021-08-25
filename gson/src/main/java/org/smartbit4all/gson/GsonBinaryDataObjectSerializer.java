package org.smartbit4all.gson;

import java.io.IOException;
import java.util.Optional;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObjectSerializer;
import org.smartbit4all.core.object.ApiObjectRef;

public class GsonBinaryDataObjectSerializer implements BinaryDataObjectSerializer {

  @Override
  public <T> BinaryData toJsonBinaryData(T object, Class<T> clazz) {
    try {
      object = ApiObjectRef.unwrapObject(object);
      return GsonBinaryData.toJsonBinaryData(object, clazz);
    } catch (Exception e) {
      throw new RuntimeException("Cannot serialize the object of class: " + clazz.getName(), e);
    }
  }

  @Override
  public <T> Optional<T> fromJsonBinaryData(BinaryData binaryData, Class<T> clazz) {
    try {
      return GsonBinaryData.fromJsonBinaryData(binaryData, clazz);
    } catch (IOException e) {
      throw new RuntimeException("Cannot deserialize the object of class: " + clazz.getName(), e);
    }
  }

}
