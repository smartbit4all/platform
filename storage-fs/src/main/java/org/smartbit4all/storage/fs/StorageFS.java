package org.smartbit4all.storage.fs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObjectSerializer;
import org.smartbit4all.api.storage.bean.ObjectReferenceList;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.ObjectReferenceRequest;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;

public class StorageFS<T> extends ObjectStorageImpl<T> {

  private static final Logger log = LoggerFactory.getLogger(StorageFS.class);

  private Class<T> clazz;

  private File rootFolder;

  private BinaryDataObjectSerializer serializer;

  /**
   * The file extension (*.extension) of the serialized object files. Important, if multiple file
   * extensions stored in the storage.
   * 
   * If set, the loadAll method only look for files with the given extension.
   */
  private String storedObjectFileExtension;

  private String referencesExtension = ".references";

  /**
   * @param rootFolder The root folder, in which the storage place the files.
   * @param uriAccessor With this function, the storage get the URI from the domain object.
   * @param serializer Serializer implementation used for serializing and deserializing the stored
   *        domain objects.
   * @param clazz The stored domain object class.
   */
  public StorageFS(
      File rootFolder,
      Function<T, URI> uriAccessor,
      BinaryDataObjectSerializer serializer,
      Class<T> clazz) {

    super(uriAccessor);

    this.rootFolder = rootFolder;
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
  public URI save(T object) {
    return save(object, uriAccessor.apply(object));
  }

  @Override
  public URI save(T object, URI uri) {
    URI result = constructUri(object, uri);
    if (result != null) {
      BinaryData data = serializer.toJsonBinaryData(object, clazz);
      FileIO.write(rootFolder, result, data);
    }
    return result;
  }

  @Override
  public Optional<T> load(URI uri) {
    BinaryData binaryData = FileIO.read(rootFolder, uri);

    if (binaryData == null) {
      return Optional.empty();
    }

    return fromJsonBinaryData(binaryData);
  }

  @Override
  public List<T> load(List<URI> uris) {
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
  public List<T> loadAll() {
    List<BinaryData> datas;
    try {
      datas = FileIO.readAllFiles(rootFolder, storedObjectFileExtension);
    } catch (IOException e) {
      String msg = "Unable to load all object from " + clazz + " storage (" + rootFolder + ")";
      log.error(msg, e);
      throw new IllegalStateException(msg, e);
    }

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
  public boolean delete(URI uri) {
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

  @Override
  public void saveReferences(ObjectReferenceRequest referenceRequest) {
    if (referenceRequest == null) {
      return;
    }
    Optional<ObjectReferenceList> currentReeferences =
        loadReferences(referenceRequest.getObjectUri(), referenceRequest.getTypeClassName());
    ObjectReferenceList references =
        currentReeferences.orElse(new ObjectReferenceList().uri(referenceRequest.getObjectUri())
            .referenceTypeClass(referenceRequest.getTypeClassName()));

    String extension =
        StringConstant.DOT + referenceRequest.getTypeClassName() + referencesExtension;
    if (!referenceRequest.updateReferences(references)) {
      // Delete the reference file
      FileIO.delete(rootFolder, referenceRequest.getObjectUri(),
          extension);
      return;
    }

    FileIO.write(rootFolder, referenceRequest.getObjectUri(),
        extension,
        serializer.toJsonBinaryData(references, ObjectReferenceList.class));
  }

  @Override
  public Optional<ObjectReferenceList> loadReferences(URI uri, String typeClassName) {
    String extension = StringConstant.DOT + typeClassName + referencesExtension;
    BinaryData binaryData = FileIO.read(rootFolder, uri, extension);

    if (binaryData == null) {
      return Optional.empty();
    }

    Optional<ObjectReferenceList> optional =
        serializer.fromJsonBinaryData(binaryData, ObjectReferenceList.class);
    return optional.isPresent() ? Optional.of(optional.get()) : Optional.empty();
  }

}
