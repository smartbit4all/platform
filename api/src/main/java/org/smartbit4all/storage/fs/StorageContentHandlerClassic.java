package org.smartbit4all.storage.fs;

import java.io.File;
import java.util.List;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.io.utility.FileIO;

public class StorageContentHandlerClassic extends StorageContentHandler {

  final File versionFile;

  public StorageContentHandlerClassic(File versionFile) {
    super();
    this.versionFile = versionFile;
  }

  @Override
  public void writeMultipart(BinaryData[] contents) {
    FileIO.writeMultipart(versionFile, contents);
  }

  @Override
  public List<BinaryData> readMultipart() {
    return FileIO.readMultipart(versionFile);
  }

}
