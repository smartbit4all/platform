package org.smartbit4all.api.binarydata.fs;

import java.io.File;
import java.net.URI;
import java.util.Optional;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataApiImpl;
import org.smartbit4all.core.io.utility.FileIO;

public class BinaryDataApiFS extends BinaryDataApiImpl {

  private File rootFolder;

  public BinaryDataApiFS(String name, File rootFolder) {
    super(name);
    this.rootFolder = rootFolder;
  }

  @Override
  public void save(BinaryData data, URI dataUri) {
    FileIO.write(rootFolder, dataUri, data);
  }

  @Override
  public Optional<BinaryData> load(URI dataUri) {
    BinaryData binaryData = FileIO.read(rootFolder, dataUri);

    if (binaryData == null) {
      return Optional.empty();
    }
    return Optional.of(binaryData);
  }

  @Override
  public void remove(URI dataUri) {
    FileIO.delete(rootFolder, dataUri);
  }

}
