package org.smartbit4all.storage.fs;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObjectSerializer;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.domain.data.storage.ObjectStorage;

public class StorageFS<T> implements ObjectStorage<T> {

  private Class<T> clazz;

  private File rootFolder;

  private Function<T, URI> uriProvider;

  private BinaryDataObjectSerializer serializer;

  /**
   * The file extension (*.extension) of the serialized object files. Important, if multiple file
   * extensions stored in the storage.
   * 
   * If set, the loadAll method only look for files with the given extension.
   */
  private String storedObjectFileExtension;

  /**
   * @param rootFolder The root folder, in which the storage place the files.
   * @param uriProvider With this function, the storage get the URI from the domain object.
   * @param serializer Serializer implementation used for serializing and deserializing the stored
   *        domain objects.
   * @param clazz The stored domain object class.
   */
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

  /**
   * @param storedObjectFileExtension The file extension (*.extension) of the serialized object
   *        files.
   */
  public StorageFS(
      File rootFolder,
      Function<T, URI> uriProvider,
      BinaryDataObjectSerializer serializer,
      Class<T> clazz,
      String storedObjectFileExtension) {

    this(rootFolder, uriProvider, serializer, clazz);
    this.storedObjectFileExtension = storedObjectFileExtension;
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

    return fromJsonBinaryData(binaryData);
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
  public List<T> loadAll() throws Exception {
    List<BinaryData> datas = FileIO.readAllFiles(rootFolder, storedObjectFileExtension);

    if (datas.isEmpty()) {
      return Collections.emptyList();
    }

    List<T> objects = new ArrayList<>();
    for (BinaryData data : datas) {
      Optional<T> object = fromJsonBinaryData(data);
      if (object.isPresent()) {
        objects.add(object.get());
      }
    }

    return objects;
  }

  @Override
  public boolean delete(URI uri) throws Exception {
    return FileIO.delete(rootFolder, uri);
  }

  private Optional<T> fromJsonBinaryData(BinaryData binaryData) {
    return serializer.fromJsonBinaryData(binaryData, clazz);
  }

  public String getStoredObjectFileExtension() {
    return storedObjectFileExtension;
  }

  public void setStoredObjectFileExtension(String storedObjectFileExtension) {
    this.storedObjectFileExtension = storedObjectFileExtension;
  }

}
