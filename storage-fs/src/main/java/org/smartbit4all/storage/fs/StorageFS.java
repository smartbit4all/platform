package org.smartbit4all.storage.fs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.storage.bean.StorageObjectData;
import org.smartbit4all.api.storage.bean.StorageObjectRelationData;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.ObjectModificationException;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageLoadOption;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObject.StorageObjectOperation;
import org.smartbit4all.domain.data.storage.StorageObjectHistoryEntry;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.smartbit4all.domain.data.storage.StorageObjectPhysicalLock;
import org.smartbit4all.domain.data.storage.StorageSaveEvent;
import org.smartbit4all.domain.data.storage.StorageUtil;

public class StorageFS extends ObjectStorageImpl {

  private static final Logger log = LoggerFactory.getLogger(StorageFS.class);

  private File rootFolder;

  /**
   * The file extension (*.extension) of the serialized object files. Important, if multiple file
   * extensions stored in the storage.
   * 
   * If set, the loadAll method only look for files with the given extension.
   */
  private static final String storedObjectFileExtension = ".o";

  /**
   * The relation contents are stored in a file with this extension.
   */
  private static final String storedObjectRelationFileExtension = ".r";

  /**
   * The lock file postfix.
   */
  private static final String storedObjectLockFileExtension = ".l";

  private ObjectDefinition<StorageObjectData> storageObjectDataDef;

  private ObjectDefinition<StorageObjectRelationData> storageObjectRelationDataDef;

  /**
   * @param rootFolder The root folder, in which the storage place the files.
   */
  public StorageFS(File rootFolder, ObjectApi objectApi) {
    super(objectApi);
    this.rootFolder = rootFolder;
    this.storageObjectDataDef = objectApi.definition(StorageObjectData.class);
    this.storageObjectRelationDataDef = objectApi.definition(StorageObjectRelationData.class);
  }

  private File getObjectDataFile(StorageObject<?> object) {
    return getDataFileByUri(object.getUri(), storedObjectFileExtension);
  }

  private File getObjectVersionBasePath(StorageObject<?> object) {
    return getDataFileByUri(object.getUri(), StringConstant.EMPTY);
  }

  private File getDataFileByUri(URI objectUri, String extension) {
    return new File(rootFolder, objectUri.getScheme() + StringConstant.SLASH + objectUri.getPath()
        + extension);
  }

  private File getObjectLockFile(StorageObject<?> object) {
    return getDataFileByUri(object.getUri(), storedObjectLockFileExtension);
  }

  /**
   * Constructs the File of the given object version.
   * 
   * @param objectHistoryBasePath
   * @param newVersion
   * @return
   */
  private File getObjectVersionFile(File objectHistoryBasePath, long serialNo) {
    return new File(objectHistoryBasePath.getPath()
        + FileIO.constructObjectPathByIndexWithHexaStructure(serialNo));
  }

  /**
   * Constructs the File of the given object version.
   * 
   * @param objectHistoryBasePath
   * @param version
   * @return
   */
  private File getObjectRelationVersionFile(File objectHistoryBasePath, long serialNo) {
    return new File(
        objectHistoryBasePath.getPath() +
            FileIO.constructObjectPathByIndexWithHexaStructure(serialNo)
            + storedObjectRelationFileExtension);
  }

  @Override
  protected Supplier<StorageObjectPhysicalLock> getAcquire() {
    // TODO Implement the locking of the files to have cluster safe physical lock fro the objects.
    // // Lock the original object file if exists.
    // File objectLockFile = getObjectLockFile(object);
    // if (objectLockFile.exists()) {
    // try (RandomAccessFile raf = new RandomAccessFile(objectLockFile, "rw");
    // FileLock lock = raf.getChannel().lock()) {
    // // write to the channel
    // } catch (Exception e) {
    // throw new IllegalStateException("The storage object " + object + " save failed.", e);
    // }
    // } else {
    // // The given object doesn't exist so we can write the temp file and move it at the end.
    // }
    return super.getAcquire();
  }

  @Override
  protected Consumer<StorageObjectPhysicalLock> getReleaser() {
    return super.getReleaser();
  }

  @Override
  public URI save(StorageObject<?> object) {

    StorageObjectLock storageObjectLock = acquire(object.getUri());
    try {
      // Load the StorageObjectData that is the api object of the storage itself.
      File objectDataFile = getObjectDataFile(object);
      File objectVersionBasePath = getObjectVersionBasePath(object);
      // The temporary file of the StorageObjectData will be identified by the transaction id as
      // extension.
      StorageObjectData storageObjectData;
      ObjectVersion newVersion;
      ObjectVersion currentVersion = null;
      if (objectDataFile.exists()) {
        // This is an existing data file.
        BinaryData dataFile = new BinaryData(objectDataFile);
        Optional<StorageObjectData> optStorageObject = storageObjectDataDef.deserialize(dataFile);
        // Extract the current version and create the new one based on this.
        storageObjectData = optStorageObject.get();
        currentVersion = storageObjectData.getCurrentVersion();
        // We should check if the current version is the same.
        if (object.getVersion() != null
            && !StorageUtil.equalsVersion(object.getVersion(), currentVersion)) {
          if (object.isStrictVersionCheck()) {
            throw new ObjectModificationException("Unable to save " + object.getUri()
                + " object because it has been modified in the meantime from " + object.getVersion()
                + " --> " + currentVersion + " version");
          } else {
            if (log.isWarnEnabled()) {
              String message = String.format(
                  "The save of the %s object is overwriting the %s version with the modification of %s earlier version. It could lead loss of modification data!",
                  object.getUri(), currentVersion, object.getVersion());
              log.warn(message, new Exception());
            }
          }
        }
        // Increment the serial number. The given object is locked in the meantime so there is no
        // need to worry about the parallel modification.
        newVersion = new ObjectVersion();
      } else {
        // The first version in the new object. The version starts from 0. The object data and the
        // object relation is also null. There is no version.
        newVersion = new ObjectVersion();
        // This will be a new data file, first we create the StorageObjectData save it into a new
        // data file.
        storageObjectData = new StorageObjectData().uri(object.getUri());
      }

      // The version is updated with the information attached if it's not a modification without
      // object.
      newVersion.transactionId(object.getTransactionId()).createdAt(OffsetDateTime.now());
      newVersion.setCreatedBy(versionCreatedBy.get());

      File objectVersionFile = null;
      if (object.getOperation() == StorageObjectOperation.MODIFY_WITHOUT_DATA) {
        // Set the new version data to the current version data there will be no new data version.
        if (currentVersion != null) {
          newVersion.setSerialNoData(currentVersion.getSerialNoData());
        }
      } else if (object.getObject() != null
          && object.getOperation() != StorageObjectOperation.DELETE) {
        newVersion
            .setSerialNoData(
                (currentVersion == null || currentVersion.getSerialNoData() == null) ? 0
                    : (currentVersion.getSerialNoData() + 1));
        objectVersionFile =
            getObjectVersionFile(objectVersionBasePath, newVersion.getSerialNoData());
      }

      // Manage the references, load the current references
      File objectRelationFile = storageObjectData.getCurrentVersion() != null
          && storageObjectData.getCurrentVersion().getSerialNoRelation() != null
              ? getObjectRelationVersionFile(objectVersionBasePath,
                  storageObjectData.getCurrentVersion().getSerialNoRelation())
              : null;
      StorageObjectRelationData storageObjectReferences =
          saveStorageObjectReferences(object, loadRelationData(objectRelationFile));
      File objectRelationVersionFile = null;
      if (storageObjectReferences != null) {
        // The data serial number will be the serial number of the version.
        newVersion.setSerialNoRelation(
            (currentVersion == null || currentVersion.getSerialNoRelation() == null) ? 0L
                : (currentVersion.getSerialNoRelation() + 1));
        objectRelationVersionFile =
            getObjectRelationVersionFile(objectVersionBasePath, newVersion.getSerialNoRelation());
      }

      // Write the version files
      if (objectVersionFile != null || objectRelationVersionFile != null) {
        BinaryData binaryDataVersion =
            objectApi.getDefaultSerializer().serialize(newVersion, ObjectVersion.class);
        if (objectVersionFile != null) {
          // Write the data version file
          FileIO.writeMultipart(objectVersionFile,
              binaryDataVersion,
              object.definition().serialize(object.getObject()));
        }
        if (objectRelationVersionFile != null) {
          // Write the version file first
          FileIO.writeMultipart(objectRelationVersionFile, binaryDataVersion,
              storageObjectRelationDataDef.serialize(storageObjectReferences));
        }
      }

      // Set the current version, change it at the last point to be able to use earlier.
      storageObjectData.currentVersion(newVersion);

      saveObjectData(object, objectDataFile, storageObjectData);

      URI oldVersionUri = object.getVersionUri();
      ObjectVersion oldVersion = currentVersion;
      updateStorageObjectWithVersion(object, newVersion);
      URI newVersionUri = object.getVersionUri();
      invokeOnSucceedFunctions(object, new StorageSaveEvent<>(
          () -> {
            if (oldVersion != null) {
              return oldVersionUri;
            }
            return null;
          },
          () -> {
            if (oldVersion != null) {
              Object o = loadObjectVersion(object.definition(), objectVersionBasePath,
                  oldVersion.getSerialNoData()).getObject();

              return o;
            }
            return null;
          },
          newVersionUri,
          object.getObject()));

    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to finalize the transaction on " + object, e);
    } finally {
      storageObjectLock.leave();
    }
    return object.getUri();
  }

  private void saveObjectData(StorageObject<?> object, File objectDataFile,
      StorageObjectData storageObjectData) throws IOException {
    // Write the data temporary file
    File objectDataFileTemp =
        new File(objectDataFile.getPath() + StringConstant.DOT + object.getTransactionId());
    FileIO.write(objectDataFileTemp,
        storageObjectDataDef.serialize(storageObjectData));
    // Atomic move of the temp file.
    Files.move(objectDataFileTemp.toPath(), objectDataFile.toPath(),
        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
  }

  @Override
  public boolean exists(URI uri) {
    File storageObjectDataFile = getDataFileByUri(uri, storedObjectFileExtension);
    if (!storageObjectDataFile.exists()) {
      return false;
    }
    return true;
  }

  @Override
  public <T> StorageObject<T> load(Storage storage, URI uri, Class<T> clazz,
      StorageLoadOption... options) {
    // The normal load is not locking anything. There is an optimistic lock implemented by default.
    // Identify the class from the URI. The first part of the path in the URI is standing for the
    // object type (the class).
    ObjectDefinition<T> definition = objectApi.definition(clazz);
    if (definition == null) {
      throw new ObjectNotFoundException(uri, clazz, "Unable to retrieve object definition.");
    }
    File storageObjectDataFile = getDataFileByUri(uri, storedObjectFileExtension);
    File storageObjectVersionBasePath = getDataFileByUri(uri, StringConstant.EMPTY);
    if (!storageObjectDataFile.exists()) {
      throw new ObjectNotFoundException(uri, clazz, "Object data file not found.");
    }
    BinaryData storageObjectBinaryData = new BinaryData(storageObjectDataFile);
    Optional<StorageObjectData> optObject =
        storageObjectDataDef.deserialize(storageObjectBinaryData);
    if (!optObject.isPresent()) {
      throw new ObjectNotFoundException(uri, clazz, "Unable to load object data file.");
    }
    StorageObjectData storageObjectData = optObject.get();
    StorageObject<T> storageObject;
    ObjectVersion objectVersion = storageObjectData.getCurrentVersion();
    Long versionDataSerialNo = getVersionByUri(uri, storageObjectData);
    boolean skipData = StorageLoadOption.checkSkipData(options);
    if (versionDataSerialNo != null && !skipData) {

      StorageObjectHistoryEntry<T> loadObjectVersion =
          loadObjectVersion(definition, storageObjectVersionBasePath,
              versionDataSerialNo);
      T object = loadObjectVersion.getObject();
      objectVersion = loadObjectVersion.getVersion();

      if (object != null) {
        setObjectUriVersioByOptions(uri, definition, object, versionDataSerialNo, options);
      }

      storageObject = instanceOf(storage, definition, object, storageObjectData);
    } else {
      storageObject = instanceOf(storage, definition, uri, storageObjectData);
    }

    if (objectVersion != null && objectVersion.getSerialNoRelation() != null) {
      loadStorageObjectReferences(storageObject,
          loadRelationData(getObjectRelationVersionFile(storageObjectVersionBasePath,
              objectVersion.getSerialNoRelation())));
    }

    if (skipData) {
      setOperation(storageObject, StorageObjectOperation.MODIFY_WITHOUT_DATA);
    }

    return storageObject;
  }

  private <T> void setObjectUriVersioByOptions(URI uri, ObjectDefinition<T> definition, T object,
      Long versionDataSerialNo,
      StorageLoadOption[] options) {
    if (!StorageLoadOption.checkUriWithVersionOption(options)) {
      // If no options specified the default behavior is to return the with the requested uri
      // This can ensure that the uri will be the exact uri used for the load.
      definition.setUri(object, uri);
    } else {
      String uriTxt = uri.toString();
      boolean uriHasVersion = uriTxt.contains(StringConstant.HASH);
      boolean uriNeedsVersion = StorageLoadOption.checkUriWithVersionValue(options);

      URI uriToSet = null;
      if (!uriHasVersion && uriNeedsVersion) {
        uriToSet = URI.create(uriTxt + StringConstant.HASH + versionDataSerialNo);
      } else if (uriHasVersion && !uriNeedsVersion) {
        uriToSet = URI.create(uriTxt.substring(0, uriTxt.indexOf(StringConstant.HASH)));
      } else {
        // (has and need) OR (has not and dont need)
        uriToSet = uri;
      }
      definition.setUri(object, uriToSet);
    }
  }

  private final StorageObjectRelationData loadRelationData(File relationVersionFile) {
    if (relationVersionFile == null || !relationVersionFile.exists()) {
      return null;
    }
    List<BinaryData> multipart = FileIO.readMultipart(relationVersionFile);
    BinaryData versionBinaryData = multipart.get(1);

    StorageObjectRelationData relationData =
        storageObjectRelationDataDef.deserialize(versionBinaryData).orElse(null);
    return relationData;
  }

  private <T> StorageObjectHistoryEntry<T> loadObjectVersion(ObjectDefinition<T> definition,
      File historyBasePath,
      long version) {
    File objectVersionFile = getObjectVersionFile(
        historyBasePath,
        version);

    List<BinaryData> multipart = FileIO.readMultipart(objectVersionFile);

    BinaryData versionObjectBinaryData = multipart.get(0);
    BinaryData versionBinaryData = multipart.get(1);

    ObjectVersion objectVersion = objectApi.getDefaultSerializer()
        .deserialize(versionObjectBinaryData, ObjectVersion.class).get();
    T object = definition.deserialize(versionBinaryData).orElse(null);
    return new StorageObjectHistoryEntry<>(objectVersion, object);
  }

  @Override
  public List<ObjectHistoryEntry> loadHistory(Storage storage, URI uri,
      ObjectDefinition<?> defnition) {
    ObjectDefinition<?> definition = getDefinitionFromUri(uri);
    if (definition == null) {
      return Collections.emptyList();
    }
    File storageObjectDataFile = getDataFileByUri(uri, storedObjectFileExtension);
    if (!storageObjectDataFile.exists()) {
      return Collections.emptyList();
    }
    File storageObjectVersionBasePath = getDataFileByUri(uri, StringConstant.EMPTY);
    BinaryData storageObjectBinaryData = new BinaryData(storageObjectDataFile);
    Optional<StorageObjectData> optObject =
        storageObjectDataDef.deserialize(storageObjectBinaryData);
    if (!optObject.isPresent()) {
      throw new ObjectNotFoundException(uri, null, "Unable to load object data file.");
    }
    StorageObjectData storageObjectData = optObject.get();
    ObjectVersion currentObjectVersion = storageObjectData.getCurrentVersion();

    if (currentObjectVersion.getSerialNoData() == null) {
      return Collections.emptyList();
    }

    long serialNoDataMax = currentObjectVersion.getSerialNoData() + 1;
    List<ObjectHistoryEntry> result = new ArrayList<>();
    for (long i = 0; i < serialNoDataMax; i++) {
      getObjectVersionFile(storageObjectVersionBasePath, i);
      StorageObjectHistoryEntry<?> loadObjectVersion =
          loadObjectVersion(definition, storageObjectVersionBasePath, i);
      ObjectVersion objectVersion = loadObjectVersion.getVersion();
      result
          .add(new ObjectHistoryEntry().version(objectVersion)
              .summary(objectVersion.getSerialNoData() + StringConstant.COMMA_SPACE
                  + objectVersion.getCreatedAt() + StringConstant.COMMA_SPACE
                  + objectVersion.getCreatedBy())
              .objectType(definition.getAlias()).versionUri(URI.create(
                  uri.toString() + StringConstant.HASH + objectVersion.getSerialNoData())));
    }
    return result;
  }

  /**
   * Reads an object uri based given extension file if exists.
   * 
   * @param rootFolder
   * @param uri
   * @param extension The specific extension as post fix for the file.
   * @return
   */
  // private File lockFile(File rootFolder, URI uri) {
  // File file = new File(rootFolder, uri.getPath() + storedObjectFileExtension);
  // return getFileBinaryData(file);
  // }

  // @Override
  // public Optional<T> load(URI uri) {
  // BinaryData binaryData = FileIO.read(rootFolder, uri);
  //
  // if (binaryData == null) {
  // return Optional.empty();
  // }
  //
  // return fromJsonBinaryData(binaryData);
  // }
  //
  // @Override
  // public List<T> load(List<URI> uris) {
  // List<T> result = new ArrayList<>();
  //
  // for (URI uri : uris) {
  // Optional<T> loaded = load(uri);
  // if (loaded.isPresent()) {
  // result.add(loaded.get());
  // }
  // }
  //
  // return result;
  // }

  // @Override
  // public List<T> loadAll() {
  // List<BinaryData> datas;
  // try {
  // datas = FileIO.readAllFiles(rootFolder, storedObjectFileExtension);
  // } catch (IOException e) {
  // String msg = "Unable to load all object from " + clazz + " storage (" + rootFolder + ")";
  // log.error(msg, e);
  // throw new IllegalStateException(msg, e);
  // }
  //
  // if (datas.isEmpty()) {
  // return Collections.emptyList();
  // }
  //
  // List<T> objects = new ArrayList<>();
  // for (BinaryData data : datas) {
  // Optional<T> object = fromJsonBinaryData(data);
  // if (object.isPresent()) {
  // objects.add(object.get());
  // }
  // }
  //
  // return objects;
  // }

  // @Override
  // public boolean delete(URI uri) {
  // return FileIO.delete(rootFolder, uri);
  // }

  public String getStoredObjectFileExtension() {
    return storedObjectFileExtension;
  }

  public final File getRootFolder() {
    return rootFolder;
  }

  public final void setRootFolder(File rootFolder) {
    this.rootFolder = rootFolder;
  }

  // @Override
  // public void saveReferences(ObjectReferenceRequest referenceRequest) {
  // if (referenceRequest == null) {
  // return;
  // }
  // Optional<ObjectReferenceList> currentReeferences =
  // loadReferences(referenceRequest.getObjectUri(), referenceRequest.getTypeClassName());
  // ObjectReferenceList references =
  // currentReeferences.orElse(new ObjectReferenceList().uri(referenceRequest.getObjectUri())
  // .referenceTypeClass(referenceRequest.getTypeClassName()));
  //
  // String extension =
  // StringConstant.DOT + referenceRequest.getTypeClassName() + referencesExtension;
  // if (!referenceRequest.updateReferences(references)) {
  // // Delete the reference file
  // FileIO.delete(rootFolder, referenceRequest.getObjectUri(),
  // extension);
  // return;
  // }
  //
  // FileIO.write(rootFolder, referenceRequest.getObjectUri(),
  // extension,
  // serializer.toJsonBinaryData(references, ObjectReferenceList.class));
  // }
  //
  // @Override
  // public Optional<ObjectReferenceList> loadReferences(URI uri, String typeClassName) {
  // String extension = StringConstant.DOT + typeClassName + referencesExtension;
  // BinaryData binaryData = FileIO.read(rootFolder, uri, extension);
  //
  // if (binaryData == null) {
  // return Optional.empty();
  // }
  //
  // Optional<ObjectReferenceList> optional =
  // serializer.fromJsonBinaryData(binaryData, ObjectReferenceList.class);
  // return optional.isPresent() ? Optional.of(optional.get()) : Optional.empty();
  // }

}
