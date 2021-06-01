package org.smartbit4all.storage.fs;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.types.binarydata.BinaryData;
import org.smartbit4all.types.binarydata.BinaryDataObjectSerializer;

public class StorageFS<T> implements ObjectStorage<T> {

  private Class<T> clazz;
  
  private File rootFolder;

  private Function<T, URI> uriProvider;
  
  private BinaryDataObjectSerializer serializer;
  
  public StorageFS(
      File rootFolder,
      Function<T, URI> uriProvider,
      BinaryDataObjectSerializer serializer,
      Class<T> clazz) {
    
    this.rootFolder = rootFolder;
    this.uriProvider = uriProvider;
    this.clazz = clazz;
    this.serializer = serializer;
  }
  
  @Override
  public void save(T object) throws Exception {
    save(object, uriProvider.apply(object));
  }

  @Override
  public void save(T object, URI uri) throws Exception {
    BinaryData data = serializer.toJsonBinaryData(object, clazz);
    FileIO.write(rootFolder, uri, data);
  }

  @Override
  public Optional<T> load(URI uri) throws Exception {
    BinaryData binaryData = FileIO.read(rootFolder, uri);

    if (binaryData == null) {
      return Optional.empty();
    }

    return serializer.fromJsonBinaryData(binaryData, clazz);
  }

  @Override
  public List<T> load(List<URI> uris) throws Exception {
    List<T> result = new ArrayList<>();

    for (URI uri : uris) {
      Optional<T> loaded = load(uri);
      if (loaded.isPresent()) {
        result.add(loaded.get());
      }
    }

    return result;
  }

  @Override
  public boolean delete(URI uri) throws Exception {
    return FileIO.delete(rootFolder, uri);
  }
  
}
