package org.smartbit4all.domain.data.storage;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectSerializer;

public abstract class ObjectStreamWriter implements Closeable {

  private ObjectStream objectStream;

  public final long write(BinaryData bd) throws IOException {
    long newHeadPosition = writeImpl(bd);
    objectStream.setHeadPosition(newHeadPosition);
    return newHeadPosition;
  }

  public abstract long writeImpl(BinaryData bd) throws IOException;

  protected void onClose() {
    objectStream.clearCurrentWriter();
  }

  public long writeObject(Object o, ObjectDefinition<?> definition) throws IOException {
    return write(definition.serialize(o));
  }

  public long writeMap(Map<String, Object> map, ObjectSerializer serializer) throws IOException {
    return write(serializer.serialize(map, Map.class));
  }

  public ObjectStream getObjectStream() {
    return objectStream;
  }

  void setObjectStream(ObjectStream objectStream) {
    this.objectStream = objectStream;
  }

}
