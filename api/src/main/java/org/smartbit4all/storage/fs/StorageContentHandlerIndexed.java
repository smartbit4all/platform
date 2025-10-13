package org.smartbit4all.storage.fs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.io.utility.IndexedMultipartStore;

public class StorageContentHandlerIndexed extends StorageContentHandler {

  private final IndexedMultipartStore store;

  int indexNr;

  public StorageContentHandlerIndexed(File versionFile, int indexNr, int slots) {
    super();
    try {
      store = IndexedMultipartStore.openOrCreate(versionFile, slots);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to access the version file (" + versionFile + ")",
          e);
    }
    this.indexNr = indexNr;
  }

  @Override
  public void writeMultipart(BinaryData[] contents) {
    try {
      store.writeAtMultipart(indexNr, contents);
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Unable to access the version file (" + store.getFile().getAbsolutePath() + ")",
          e);
    }
  }

  @Override
  public List<BinaryData> readMultipart() {
    try {
      return store.readMultipart(indexNr);
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Unable to access the version file (" + store.getFile().getAbsolutePath() + ")",
          e);
    }
  }

}
