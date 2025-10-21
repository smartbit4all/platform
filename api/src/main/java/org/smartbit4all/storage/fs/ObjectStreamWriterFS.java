package org.smartbit4all.storage.fs;

import java.io.File;
import java.io.IOException;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.io.utility.ObjectStreamStore;
import org.smartbit4all.domain.data.storage.ObjectStreamWriter;

public class ObjectStreamWriterFS extends ObjectStreamWriter {

  private final ObjectStreamStore store;

  public ObjectStreamWriterFS(File file, long headPosition) throws IOException {
    super();
    this.store = ObjectStreamStore.openOrCreate(file, headPosition);
  }

  @Override
  public void close() throws IOException {
    store.close();
    onClose();
  }

  @Override
  public long writeImpl(BinaryData bd) throws IOException {
    return store.write(bd);
  }

}
