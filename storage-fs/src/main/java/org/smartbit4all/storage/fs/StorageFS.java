package org.smartbit4all.storage.fs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.storage.bean.StorageObjectData;
import org.smartbit4all.api.storage.bean.StorageObjectRelationData;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageLoadOption;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObject.StorageObjectOperation;
import org.smartbit4all.domain.data.storage.StorageObjectLock;

public class StorageFS extends ObjectStorageImpl {

  private static final Logger log = LoggerFactory.getLogger(StorageFS.class);

  private File rootFolder;

  /**
   * The file extension (*.extension) of the serialized object files. Important, if multiple file
   * extensions stored in the storage.
   * 
   * If set, the loadAll method only look for files with the given extension.
   */
  private static final String storedObjectFileExtension = ".object";

  private static final String storedObjectRelationFileExtension = ".relations";

  private static final String storedObjectLockFileExtension = ".lock";

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

  private File getDataFileByUri(URI objectUri, String extension) {
    return new File(rootFolder, objectUri.getScheme() + StringConstant.SLASH + objectUri.getPath()
        + extension);
  }

  private File getObjectLockFile(StorageObject<?> object) {
    return getDataFileByUri(object.getUri(), storedObjectLockFileExtension);
  }

  @Override
  public URI save(StorageObject<?> object) {
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

    StorageObjectLock storageObjectLock = acquire(object.getUri());
    try {
      // Load the StorageObjectData that is the api object of the storage itself.
      File objectDataFile = getObjectDataFile(object);
      // The temporary file of the StorageObjectData will be identified by the transaction id as
      // extension.
      StorageObjectData storageObjectData;
      ObjectVersion newVersion;
      if (objectDataFile.exists()) {
        // This is an existing data file.
        BinaryData dataFile = new BinaryData(objectDataFile, false);
        Optional<StorageObjectData> optStorageObject = storageObjectDataDef.deserialize(dataFile);
        storageObjectData = optStorageObject.get();
        // Extract the current version and create the new one based on this.
        ObjectVersion currentVersion = storageObjectData.getCurrentVersion();
        // Increment the serial number. The given object is locked in the meantime so there is no
        // need to worry about the parallel modification.
        newVersion = new ObjectVersion().serialNo(currentVersion.getSerialNo() + 1);
      } else {
        // The first version in the new object.
        newVersion = new ObjectVersion();
        // This will be a new data file, fists we create the StorageObjectData save it into a new
        // data file.
        storageObjectData = new StorageObjectData().uri(object.getUri());
      }

      // The version is updated with the information attached if it's not a modification without
      // object.
      newVersion.transactionId(object.getTransactionId()).createdAt(ZonedDateTime.now());
      storageObjectData.addVersionsItem(newVersion);

      // TODO Add dependency to UserSession!!
      if (object.getObject() != null && object.getOperation() != StorageObjectOperation.DELETE) {
        File objectVersionFile = getObjectVersionFile(objectDataFile, newVersion);
        // Write the version file first
        FileIO.write(objectVersionFile,
            object.definition().serialize(object.getObject()));
        // The data serial number will be the serial number of the version.
        newVersion.setSerialNoData(newVersion.getSerialNo());
      }

      // Manage the references, load the current references
      File objectRelationFile = storageObjectData.getCurrentVersion() != null
          && storageObjectData.getCurrentVersion().getSerialNoRelation() != null
              ? getObjectRelationVersionFile(objectDataFile,
                  storageObjectData.getCurrentVersion().getSerialNoRelation())
              : null;
      StorageObjectRelationData relationData = null;
      if (objectRelationFile != null && objectRelationFile.exists()) {
        BinaryData dataFile = new BinaryData(objectRelationFile, false);
        Optional<StorageObjectRelationData> relationDataOpt =
            storageObjectRelationDataDef.deserialize(dataFile);
        relationData = relationDataOpt.orElse(null);
      }
      StorageObjectRelationData storageObjectReferences =
          saveStorageObjectReferences(object, relationData);
      if (storageObjectReferences != null) {
        File objectRelationVersionFile =
            getObjectRelationVersionFile(objectDataFile, newVersion.getSerialNo());
        // Write the version file first
        FileIO.write(objectRelationVersionFile,
            storageObjectRelationDataDef.serialize(storageObjectReferences));
        // The data serial number will be the serial number of the version.
        newVersion.setSerialNoRelation(newVersion.getSerialNo());
      }

      // Set the current version, change it at the last point to be able to use earlier.
      storageObjectData.currentVersion(newVersion);

      // Write the data temporary file
      File objectDataFileTemp =
          new File(objectDataFile.getPath() + StringConstant.DOT + object.getTransactionId());
      FileIO.write(objectDataFileTemp,
          storageObjectDataDef.serialize(storageObjectData));
      // Atomic move of the temp file.
      Files.move(objectDataFileTemp.toPath(), objectDataFile.toPath(),
          StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to finalize the transaction on " + object, e);
    } finally {
      storageObjectLock.leave();
    }
    return object.getUri();
  }

  /**
   * Constructs the File of the given object version.
   * 
   * @param objectDataFile
   * @param newVersion
   * @return
   */
  private File getObjectVersionFile(File objectDataFile, ObjectVersion newVersion) {
    File objectVersionFile =
        new File(objectDataFile.getPath() + StringConstant.DOT + newVersion.getSerialNo());
    return objectVersionFile;
  }

  /**
   * Constructs the File of the given object version.
   * 
   * @param objectDataFile
   * @param version
   * @return
   */
  private File getObjectRelationVersionFile(File objectDataFile, int serialNo) {
    File objectVersionFile =
        new File(objectDataFile.getPath() + storedObjectRelationFileExtension + StringConstant.DOT
            + serialNo);
    return objectVersionFile;
  }

  @Override
  public <T> Optional<StorageObject<T>> load(Storage storage, URI uri, Class<T> clazz,
      StorageLoadOption... options) {
    // The normal load is not locking anything. There is an optimistic lock implemented by default.
    // Identify the class from the URI. The first part of the path in the URI is standing for the
    // object type (the class).
    ObjectDefinition<T> definition = objectApi.definition(clazz);
    if (definition == null) {
      return Optional.empty();
    }
    File storageObjectDataFile = getDataFileByUri(uri, storedObjectFileExtension);
    if (!storageObjectDataFile.exists()) {
      return Optional.empty();
    }
    BinaryData storageObjectBinaryData = new BinaryData(storageObjectDataFile, false);
    Optional<StorageObjectData> optObject =
        storageObjectDataDef.deserialize(storageObjectBinaryData);
    if (!optObject.isPresent()) {
      return Optional.empty();
    }
    StorageObjectData storageObjectData = optObject.get();
    StorageObject<T> storageObject;
    boolean skipData = StorageLoadOption.checkSkipData(options);
    if (storageObjectData.getCurrentVersion().getSerialNoData() != null
        && !skipData) {
      File objectVersionFile =
          getObjectVersionFile(storageObjectDataFile, storageObjectData.getCurrentVersion());
      BinaryData versionBinaryData = new BinaryData(objectVersionFile, false);

      T object = definition.deserialize(versionBinaryData).orElse(null);
      storageObject = instanceOf(storage, definition, object);
    } else {
      storageObject = instanceOf(storage, definition, uri);
    }

    if (storageObjectData.getCurrentVersion().getSerialNoRelation() != null) {
      File relationVersionFile =
          getObjectRelationVersionFile(storageObjectDataFile,
              storageObjectData.getCurrentVersion().getSerialNoRelation());
      if (relationVersionFile.exists()) {
        BinaryData versionBinaryData = new BinaryData(relationVersionFile, false);

        StorageObjectRelationData relationData =
            storageObjectRelationDataDef.deserialize(versionBinaryData).orElse(null);
        loadStorageObjectReferences(storageObject, relationData);
      }
    }


    if (skipData) {
      setOperation(storageObject, StorageObjectOperation.MODIFY_WITHOUT_DATA);
    }

    return Optional.of(storageObject);
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
