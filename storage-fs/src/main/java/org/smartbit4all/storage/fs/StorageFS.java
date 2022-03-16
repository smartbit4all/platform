package org.smartbit4all.storage.fs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
import org.smartbit4all.domain.data.storage.ObjectHistoryIterator;
import org.smartbit4all.domain.data.storage.ObjectModificationException;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageLoadOption;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObject.StorageObjectOperation;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;
import org.smartbit4all.domain.data.storage.StorageObjectHistoryEntry;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.smartbit4all.domain.data.storage.StorageObjectPhysicalLock;
import org.smartbit4all.domain.data.storage.StorageSaveEvent;
import org.smartbit4all.domain.data.storage.StorageUtil;
import org.smartbit4all.domain.data.storage.TransactionalStorage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The file system based implementation of the {@link ObjectStorage} interface. This is responsible
 * for storing and retrieving the objects using an expandable file system structure. The location of
 * the object file is determined by the URI of the object so we have a very fast pointer based
 * access to the data. The objects are saved in immediate commit when we save them. If we need an
 * atomic transaction then we can use the {@link StorageTransactionManagerFS} with
 * {@link TransactionalStorage} annotations. This will collects the {@link StorageObjectLock}s
 * generated during the transaction and finalize them at the end of the transaction. If the given
 * application instance misses to finalize then the rest of them is going to detect this situation
 * and finalize them.
 * 
 * @author Peter Boros
 */
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

  /**
   * The transaction file extension. The transaction file always contains the pending content of the
   * object if there is a writing transaction in progress.
   */
  private static final String transactionFileExtension = ".t";

  /**
   * The {@link ObjectDefinition} of the {@link StorageObjectData} that is basic api object of the
   * {@link StorageApi}.
   */
  private ObjectDefinition<StorageObjectData> storageObjectDataDef;

  /**
   * The {@link ObjectDefinition} of the {@link StorageObjectRelationData} that is basic api object
   * of the {@link StorageApi}.
   */
  private ObjectDefinition<StorageObjectRelationData> storageObjectRelationDataDef;

  /**
   * The transaction manager (there must be only one in one application) that is configured. Used to
   * identify if there is an active transaction initiated on the current {@link Thread}. The lock of
   * the objects are collected in the {@link #transactionManager} and at the end of the transaction
   * all of them are finalized. The finalize is nothing else but the atomic move of the temporary
   * files generated by the transaction.
   */
  @Autowired(required = false)
  private StorageTransactionManagerFS transactionManager;

  private static Random rnd = new Random();

  /**
   * @param rootFolder The root folder, in which the storage place the files.
   */
  public StorageFS(File rootFolder, ObjectApi objectApi) {
    super(objectApi);
    this.rootFolder = rootFolder;
    this.storageObjectDataDef = objectApi.definition(StorageObjectData.class);
    this.storageObjectRelationDataDef = objectApi.definition(StorageObjectRelationData.class);
  }

  /**
   * The key function that constructs the {@link File} related to an URI based on the
   * {@link #rootFolder} and the structure of the URI.
   * 
   * @param objectUri The object URI
   * @param extension The extension of the file
   * @return The {@link File}.
   */
  private final File getDataFileByUri(URI objectUri, String extension) {
    return new File(rootFolder, objectUri.getScheme() + StringConstant.SLASH + objectUri.getPath()
        + extension);
  }

  /**
   * Constructs the object file for the URI.
   * 
   * @param objectUri The object URI.
   * @return We get an object file with extension {@link #storedObjectFileExtension}.
   */
  private final File getObjectDataFile(URI objectUri) {
    return getDataFileByUri(objectUri, storedObjectFileExtension);
  }

  /**
   * Constructs the base path of the version folder.
   * 
   * @param objectUri The object uri.
   * @return The folder that is the at the path of the URI. The folder itself is the {@link UUID}
   *         that is the last segment of the path.
   */
  private final File getObjectVersionBasePath(URI objectUri) {
    return getDataFileByUri(objectUri, StringConstant.EMPTY);
  }

  /**
   * The transaction file for the object uri.
   * 
   * @param objectUri The object URI.
   * @return The transaction file that is the same like the object file itself but with
   *         {@link #transactionFileExtension}.
   */
  private File getObjectTransactionFile(URI objectUri) {
    return getDataFileByUri(objectUri, transactionFileExtension);
  }

  /**
   * The lock file for the object uri.
   * 
   * @param objectUri The object URI.
   * @return The lock file that is the same like the object file itself but with
   *         {@link #storedObjectLockFileExtension}.
   */
  private File getObjectLockFile(URI objectUri) {
    return getDataFileByUri(objectUri, storedObjectLockFileExtension);
  }

  /**
   * Constructs the File of the given object version.
   * 
   * @param objectHistoryBasePath
   * @param serialNo
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
   * @param serialNo
   * @return
   */
  private File getObjectRelationVersionFile(File objectHistoryBasePath, long serialNo) {
    return new File(
        objectHistoryBasePath.getPath() +
            FileIO.constructObjectPathByIndexWithHexaStructure(serialNo)
            + storedObjectRelationFileExtension);
  }

  @Override
  protected Supplier<StorageObjectPhysicalLock> physicalLockSupplier() {
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
    return super.physicalLockSupplier();
  }

  @Override
  protected Consumer<StorageObjectPhysicalLock> physicalLockReleaser() {
    return super.physicalLockReleaser();
  }

  @Override
  public URI save(StorageObject<?> object) {

    StorageObjectLock storageObjectLock = getLock(object.getUri());
    storageObjectLock.lock();
    try {

      if (object.getStorage().getVersionPolicy() == VersionPolicy.SINGLEVERSION) {
        saveSingleVersionObject(object);
      } else {
        saveVersionedObject(object);
      }

    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to finalize the transaction on " + object, e);
    } finally {
      storageObjectLock.unlockAndRelease();
    }
    return object.getUri();
  }

  /**
   * This save the object as a single object. It's is faster but we don't have the previous
   * versions. We save the descriptor, the serialized form of the {@link StorageObjectData}, the
   * object itself and the references in one file. In this way there is no need to read the
   * descriptor and the object data separately. This structure is useful for administration data
   * like clustering or invocation registry. The transaction management is the same, we use a temp
   * file as write buffer and do an atomic move at the end of the transaction.
   * 
   * @param object The object.
   * @throws IOException If Exception occurred then it will be thrown to be able to manage the
   *         locking in the {@link #save(StorageObject)}.
   */
  private final void saveSingleVersionObject(StorageObject<?> object) throws IOException {
    File objectDataFile = getObjectDataFile(object.getUri());
    StorageObjectData storageObjectData = new StorageObjectData().uri(object.getUri())
        .className(object.definition().getClazz().getName());
    saveObjectDataInline(object, objectDataFile, storageObjectData);
  }

  /**
   * This save the object to have every modification as version of the object.
   * 
   * @param object The object.
   * @throws IOException If Exception occurred then it will be thrown to be able to manage the
   *         locking in the {@link #save(StorageObject)}.
   */
  private final void saveVersionedObject(StorageObject<?> object) throws IOException {
    // Load the StorageObjectData that is the api object of the storage itself.
    File objectDataFile = getObjectDataFile(object.getUri());
    File objectVersionBasePath = getObjectVersionBasePath(object.getUri());
    // The temporary file of the StorageObjectData will be identified by the transaction id as
    // extension.
    StorageObjectData storageObjectData;
    ObjectVersion newVersion;
    ObjectVersion currentVersion = null;
    if (objectDataFile.exists()) {
      // This is an existing data file.
      storageObjectData = readObjectData(objectDataFile);
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
      storageObjectData = new StorageObjectData().uri(object.getUri())
          .className(object.definition().getClazz().getName());
    }

    // The version is updated with the information attached if it's not a modification without
    // object.
    // TODO Inject transaction!
    newVersion.transactionId(object.getTransactionId().toString())
        .createdAt(OffsetDateTime.now());
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
    invokeOnSucceedFunctions(object, oldVersion, oldVersionUri, newVersionUri,
        objectVersionBasePath);
  }

  /**
   * Invoke the on succeed functions depending on having a transaction or not. If we have an active
   * transaction then the functions is going to be called at the successful transaction end.
   * 
   * @param object
   * @param oldVersion
   * @param oldVersionUri
   * @param newVersionUri
   * @param objectVersionBasePath
   */
  void invokeOnSucceedFunctions(StorageObject<?> object, ObjectVersion oldVersion,
      URI oldVersionUri, URI newVersionUri, File objectVersionBasePath) {
    StorageSaveEvent event = new StorageSaveEvent(
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
        object.getObject(),
        object.definition().getClazz());
    if (transactionManager != null && transactionManager.isInTransaction()) {
      transactionManager.addOnSucceed(object, event);
    } else {
      invokeOnSucceedFunctions(object, event);
    }
  }

  @Override
  protected void invokeOnSucceedFunctions(StorageObject<?> object,
      StorageSaveEvent storageSaveEvent) {
    super.invokeOnSucceedFunctions(object, storageSaveEvent);
  }

  private final void saveObjectData(StorageObject<?> object, File objectDataFile,
      StorageObjectData storageObjectData) throws IOException {
    // Write the data temporary file
    File objectDataFileTemp = getObjectTransactionFile(object.getUri());
    FileIO.write(objectDataFileTemp,
        storageObjectDataDef.serialize(storageObjectData));
    // Atomic move of the temp file.
    // TODO The move must be executed by the transaction manager at the end of the transaction.
    try {
      FileIO.finalizeWrite(objectDataFileTemp, objectDataFile);
    } catch (InterruptedException e) {
      throw new IOException("Unable to finalize the " + object + " write.", e);
    }
  }

  private final void saveObjectDataInline(StorageObject<?> object, File objectDataFile,
      StorageObjectData storageObjectData)
      throws IOException {
    // Write the data temporary file
    File objectDataFileTemp = getObjectTransactionFile(object.getUri());
    FileIO.writeMultipart(objectDataFileTemp, storageObjectDataDef.serialize(storageObjectData),
        object.serialize());
    // Atomic move of the temp file.
    // TODO The move must be executed by the transaction manager at the end of the transaction.
    try {
      FileIO.finalizeWrite(objectDataFileTemp, objectDataFile);
    } catch (InterruptedException e) {
      throw new IOException("Unable to finalize the " + object + " write.", e);
    }
  }

  @Override
  public boolean exists(URI uri) {
    if (uri == null) {
      return false;
    }
    File storageObjectDataFile = getDataFileByUri(uri, storedObjectFileExtension);
    if (!storageObjectDataFile.exists()) {
      return false;
    }
    return true;
  }

  @Override
  public Long lastModified(URI uri) {
    if (uri == null) {
      return null;
    }
    File storageObjectDataFile = getDataFileByUri(uri, storedObjectFileExtension);
    if (!storageObjectDataFile.exists()) {
      return null;
    }
    return storageObjectDataFile.lastModified();
  }

  @Override
  public <T> StorageObject<T> load(Storage storage, URI uri, Class<T> clazz,
      StorageLoadOption... options) {
    URI uriWithoutVersion = getUriWithoutVersion(uri);
    File storageObjectDataFile = getObjectDataFile(uriWithoutVersion);
    if (!storageObjectDataFile.exists()) {
      throw new ObjectNotFoundException(uri, clazz, "Object data file not found.");
    }
    if (uriWithoutVersion.getPath().endsWith(Storage.singleVersionURIPostfix)
        && storage.getVersionPolicy() != VersionPolicy.SINGLEVERSION) {
      throw new IllegalArgumentException("Unable to load single version object with .");
    }

    if (storage.getVersionPolicy() == VersionPolicy.SINGLEVERSION) {
      // Load the single version from file.
      return readObjectSingleVersion(storage, uriWithoutVersion, clazz, storageObjectDataFile);
    }

    StorageObjectData storageObjectData = readObjectData(storageObjectDataFile);
    @SuppressWarnings("unchecked")
    ObjectDefinition<T> definition =
        (ObjectDefinition<T>) getObjectDefinition(uri, storageObjectData, clazz);
    StorageObject<T> storageObject;
    ObjectVersion objectVersion = storageObjectData.getCurrentVersion();
    Long versionDataSerialNo = getVersionByUri(uri, storageObjectData);
    boolean skipData = StorageLoadOption.checkSkipData(options);
    File storageObjectVersionBasePath = getObjectVersionBasePath(uriWithoutVersion);
    if (versionDataSerialNo != null && !skipData) {

      StorageObjectHistoryEntry<T> loadObjectVersion =
          loadObjectVersion(definition, storageObjectVersionBasePath,
              versionDataSerialNo);
      T object = loadObjectVersion.getObject();
      objectVersion = loadObjectVersion.getVersion();

      if (object != null) {
        setObjectUriVersionByOptions(uri, definition, object, versionDataSerialNo, options);
      }

      storageObject = instanceOf(storage, definition, object, storageObjectData);
    } else {
      storageObject = instanceOf(storage, definition, uriWithoutVersion, storageObjectData);
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

  private <T> StorageObject<T> readObjectSingleVersion(Storage storage, URI uri, Class<T> clazz,
      File storageObjectDataFile) {
    long waitTime = 10;
    while (true) {
      if (storageObjectDataFile == null || !storageObjectDataFile.exists()
          || !storageObjectDataFile.isFile()) {
        throw new ObjectNotFoundException(uri, clazz, "Unable to load object data file.");
      }
      try {
        List<BinaryData> dataParts = FileIO.readMultipart(storageObjectDataFile);
        Optional<StorageObjectData> optObject =
            storageObjectDataDef.deserialize(dataParts.get(0));
        if (!optObject.isPresent()) {
          throw new ObjectNotFoundException(uri, clazz, "Unable to load object data file.");
        }
        @SuppressWarnings("unchecked")
        ObjectDefinition<T> definition =
            (ObjectDefinition<T>) getObjectDefinition(uri, optObject.get(), clazz);
        T obj = definition.deserialize(dataParts.get(1)).orElse(null);
        return instanceOf(storage, definition, obj, optObject.get());
      } catch (IOException e) {
        // We must try again.
        log.debug("Unable to read " + storageObjectDataFile);
        waitTime = waitTime * rnd.nextInt(4);
      }
      try {
        Thread.sleep(waitTime);
      } catch (InterruptedException e) {
        throw new RuntimeException("The reading was interrupted.", e);
      }
    }
  }

  private final StorageObjectData readObjectData(File objectDataFile) {
    long waitTime = 10;
    while (true) {
      if (objectDataFile == null || !objectDataFile.exists() || !objectDataFile.isFile()) {
        return null;
      }
      try {
        StorageObjectData storageObjectData;
        BinaryData dataFile = new BinaryData(objectDataFile);
        Optional<StorageObjectData> optStorageObject = storageObjectDataDef.deserialize(dataFile);
        // Extract the current version and create the new one based on this.
        storageObjectData = optStorageObject.get();
        return storageObjectData;
      } catch (Exception e) {
        // We must try again.
        log.debug("Unable to read " + objectDataFile);
        waitTime = waitTime * rnd.nextInt(4);
      }
      try {
        Thread.sleep(waitTime);
      } catch (InterruptedException e) {
        throw new RuntimeException("The reading was interrupted.", e);
      }
    }
  }

  private <T> void setObjectUriVersionByOptions(URI uri, ObjectDefinition<T> definition, T object,
      Long versionDataSerialNo,
      StorageLoadOption[] options) {
    if (!StorageLoadOption.checkUriWithVersionOption(options)) {
      // If no options specified the default behavior is to return the with the requested uri
      // This can ensure that the uri will be the exact uri used for the load.
      definition.setUri(object, uri);
    } else {
      Long uriVersion = getUriVersion(uri);
      boolean uriNeedsVersion = StorageLoadOption.checkUriWithVersionValue(options);

      URI uriToSet = null;
      // TODO manage the path itself.
      if (uriVersion == null && uriNeedsVersion) {
        uriToSet = URI.create(uri.toString() + versionPostfix + versionDataSerialNo);
      } else if (uriVersion != null && !uriNeedsVersion) {
        String uriTxt = uri.toString();
        uriToSet = URI.create(uriTxt.substring(0, uriTxt.lastIndexOf(versionPostfix)));
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

    StorageObjectRelationData relationData;
    try {
      relationData = storageObjectRelationDataDef.deserialize(versionBinaryData).orElse(null);
    } catch (IOException e) {
      log.error("Unable to read relation data", e);
      relationData = null;
    }
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

    ObjectVersion objectVersion;
    T object;
    try {
      objectVersion = objectApi.getDefaultSerializer()
          .deserialize(versionObjectBinaryData, ObjectVersion.class).get();
      object = definition.deserialize(versionBinaryData).orElse(null);
    } catch (IOException e) {
      log.error("Unable to read version data", e);
      return null;
    }
    return new StorageObjectHistoryEntry<>(objectVersion, object);
  }

  @Override
  public ObjectHistoryIterator objectHistory(URI uri, ObjectDefinition<?> definition) {
    if (definition == null) {
      return null;
    }

    File storageObjectDataFile = getDataFileByUri(uri, storedObjectFileExtension);
    if (!storageObjectDataFile.exists()) {
      return null;
    }

    BinaryData storageObjectBinaryData = new BinaryData(storageObjectDataFile);
    Optional<StorageObjectData> optObject;
    try {
      optObject = storageObjectDataDef.deserialize(storageObjectBinaryData);
    } catch (IOException e) {
      throw new ObjectNotFoundException(uri, null, "Unable to load object data file.");
    }
    if (!optObject.isPresent()) {
      throw new ObjectNotFoundException(uri, null, "Unable to load object data file.");
    }

    ObjectVersion currentObjectVersion = optObject.get().getCurrentVersion();
    if (currentObjectVersion.getSerialNoData() == null) {
      return null;
    }

    long serialNoDataMax = currentObjectVersion.getSerialNoData();

    return new ObjectHistoryIterator() {

      @Override
      public Iterator<StorageObjectHistoryEntry<?>> iterator() {
        return new Iterator<StorageObjectHistoryEntry<?>>() {

          @Override
          public boolean hasNext() {
            return i < serialNoDataMax;
          }

          @Override
          public StorageObjectHistoryEntry<?> next() {
            return loadObjectVersion(definition, getObjectVersionBasePath(uri), ++i);
          }

        };
      }

    };
  }

  @Override
  public ObjectHistoryIterator objectHistoryReverse(URI uri, ObjectDefinition<?> definition) {
    if (definition == null) {
      return null;
    }

    File storageObjectDataFile = getDataFileByUri(uri, storedObjectFileExtension);
    if (!storageObjectDataFile.exists()) {
      return null;
    }

    BinaryData storageObjectBinaryData = new BinaryData(storageObjectDataFile);
    Optional<StorageObjectData> optObject;
    try {
      optObject = storageObjectDataDef.deserialize(storageObjectBinaryData);
    } catch (IOException e) {
      throw new ObjectNotFoundException(uri, null, "Unable to load object data file.");
    }
    if (!optObject.isPresent()) {
      throw new ObjectNotFoundException(uri, null, "Unable to load object data file.");
    }

    ObjectVersion currentObjectVersion = optObject.get().getCurrentVersion();
    if (currentObjectVersion.getSerialNoData() == null) {
      return null;
    }

    long serialNoDataMax = currentObjectVersion.getSerialNoData();

    return new ObjectHistoryIterator() {

      private long i = serialNoDataMax + 1;

      @Override
      public Iterator<StorageObjectHistoryEntry<?>> iterator() {
        return new Iterator<StorageObjectHistoryEntry<?>>() {

          @Override
          public boolean hasNext() {
            return i > 0;
          }

          @Override
          public StorageObjectHistoryEntry<?> next() {
            return loadObjectVersion(definition, getObjectVersionBasePath(uri), --i);
          }

        };
      }

    };
  }

  // /**
  // * Reads an object uri based given extension file if exists.
  // *
  // * @param rootFolder
  // * @param uri
  // * @param extension The specific extension as post fix for the file.
  // * @return
  // */
  // private File lockFile(File rootFolder, URI uri) {
  // File file = new File(rootFolder, uri.getPath() + storedObjectFileExtension);
  // return getFileBinaryData(file);
  // }

  public String getStoredObjectFileExtension() {
    return storedObjectFileExtension;
  }

  public final File getRootFolder() {
    return rootFolder;
  }

}
