package org.smartbit4all.sql.storage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.storage.bean.StorageObjectData;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.TableDatas.BuilderWithFixProperties;
import org.smartbit4all.domain.data.storage.BlobObjectStorageAccessApi;
import org.smartbit4all.domain.data.storage.ObjectHistoryIterator;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageLoadOption;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObject.StorageObjectOperation;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;
import org.smartbit4all.domain.data.storage.StorageObjectHistoryEntry;
import org.smartbit4all.domain.data.storage.StorageObjectPhysicalLock;
import org.smartbit4all.domain.data.storage.StorageSaveEvent;
import org.smartbit4all.domain.service.identifier.IdentifierService;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.storage.fs.StorageSetFSVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonParseException;

public class StorageSQL extends ObjectStorageImpl {

  private static final Logger log = LoggerFactory.getLogger(StorageSQL.class);

  /**
   * The sequence name in the database name.
   */
  public static final String SEQUENCE_NAME = "SEQ_OBJECT";

  /**
   * The first version number.
   */
  public static final Long FIRST_VERSION = Long.valueOf(0);

  @Autowired
  ObjectEntryDef objectEntryDef;

  @Autowired
  ObjectVersionDef objectVersionDef;

  @Autowired
  private BlobObjectStorageAccessApi storageAccessApi;

  @Autowired
  public IdentifierService identifierService;

  public StorageSQL(ObjectDefinitionApi objectDefinitionApi) {
    super(objectDefinitionApi);
  }

  @Override
  protected Supplier<StorageObjectPhysicalLock> physicalLockSupplier(URI objectUri) {
    if (runtimeApi() == null || runtimeApi().self() == null) {
      return super.physicalLockSupplier(objectUri);
    }

    // return () -> {
    // StorageTransaction transaction =
    // transactionManager != null ? transactionManager.getCurrentTransaction() : null;
    // FileLockData fld = new FileLockData(runtimeApi().self().getUuid().toString(),
    // transaction != null ? transaction.getData().getUri().toString() : null);
    // try {
    // FileIO.lockObjectFile(fld, getObjectLockFile(objectUri), -1, this::isValidLock);
    // } catch (Exception e) {
    // throw new IllegalStateException("Unable to lock object " + objectUri, e);
    // }
    // return new StorageObjectPhysicalLock(objectUri);
    // };
    return null;
  }

  @Override
  protected Consumer<StorageObjectPhysicalLock> physicalLockReleaser() {
    // if (runtimeApi() == null || runtimeApi().self() == null) {
    // return super.physicalLockReleaser();
    // }
    // return l -> {
    // if (l != null) {
    // try {
    // FileIO.unlockObjectFile(getObjectLockFile(l.getObjectUri()), -1);
    // } catch (Exception e) {
    // throw new IllegalStateException("Unable to lock object " + l.getObjectUri(), e);
    // }
    // }
    // };
    return null;
  }

  /**
   * In case of the database the save process is almost the same. We select the object record for
   * update or insert this
   * 
   * @param object
   * @return
   */
  private final Long saveObject(StorageObject<?> object) {
    // Identify the object record. If it exists then lock it. If doesn't exist then we insert int
    // (it locks the record by the unique index)
    DataRow objectRow;
    try {
      objectRow = Crud.read(objectEntryDef)
          .select(objectEntryDef.allProperties())
          .where(objectEntryDef.uri().eq(getUriWithoutVersion(object.getUri()))).lock()
          .onlyOne()
          .orElse(null);
    } catch (Exception e) {
      objectRow = null;
    }

    // TODO manage the object version from the version table.
    ObjectVersion result = new ObjectVersion();

    BuilderWithFixProperties<ObjectVersionDef> builderVersion = TableDatas
        .builder(objectVersionDef, objectVersionDef.entryId(), objectVersionDef.version(),
            objectVersionDef.createdAt(), objectVersionDef.objectContent());
    if (objectRow != null) {
      // It is an already existing object so it is an update
      if (object.isSingleVersion()) {
        // If it is a single version then we update the one and only one version of the object.
        Crud.update(builderVersion
            .addRow()
            .set(objectVersionDef.entryId(), objectRow.get(objectEntryDef.id()))
            .set(objectVersionDef.version(), Long.valueOf(0))
            .set(objectVersionDef.createdAt(), LocalDateTime.now())
            .set(objectVersionDef.objectContent(), object.serializeMapAware())
            .build());
        return Long.valueOf(0);
      } else {
        // Update the entry with the new version and insert the new version.
        Long newVersion = objectRow.get(objectEntryDef.version()) + 1;
        objectRow.set(objectEntryDef.version(), newVersion);
        Crud.update(objectRow.tableData());
        Crud.create(builderVersion
            .addRow()
            .set(objectVersionDef.entryId(), objectRow.get(objectEntryDef.id()))
            .set(objectVersionDef.version(), newVersion)
            .set(objectVersionDef.createdAt(), LocalDateTime.now())
            .set(objectVersionDef.objectContent(), object.serializeMapAware())
            .build());
        return newVersion;
      }
    } else {
      // It is a brand new object insert simply.
      URI uri = object.getUri();
      LocalDateTime now = LocalDateTime.now();
      Long nextId = getNextId();
      Crud.create(TableDatas
          .builder(objectEntryDef, objectEntryDef.uri(), objectEntryDef.id(),
              objectEntryDef.scheme(), objectEntryDef.className(), objectEntryDef.createdAt(),
              objectEntryDef.modifiedAt(), objectEntryDef.uuid(),
              objectEntryDef.version(), objectEntryDef.singleVersion())
          .addRow()
          .set(objectEntryDef.uri(), uri)
          .set(objectEntryDef.id(), nextId)
          .set(objectEntryDef.scheme(), uri.getScheme())
          .set(objectEntryDef.className(), object.definition().getAlias())
          .set(objectEntryDef.createdAt(), now)
          .set(objectEntryDef.modifiedAt(), now)
          .set(objectEntryDef.uuid(),
              object.getUuid() == null ? null : object.getUuid().toString())
          .set(objectEntryDef.version(), FIRST_VERSION)
          .set(objectEntryDef.singleVersion(), object.isSingleVersion()).build());
      Crud.update(builderVersion
          .addRow()
          .set(objectVersionDef.entryId(), nextId)
          .set(objectVersionDef.version(), FIRST_VERSION)
          .set(objectVersionDef.createdAt(), LocalDateTime.now())
          .set(objectVersionDef.objectContent(), object.serializeMapAware())
          .build());
      return FIRST_VERSION;
    }
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
  @Override
  protected final void saveSingleVersionObject(StorageObject<?> object) throws IOException {
    saveObject(object);
    // File objectDataFile = getObjectDataFile(object.getUri());
    // StorageObjectData storageObjectData = new StorageObjectData().uri(object.getUri())
    // .className(object.definition().getClazz().getName());
    // saveObjectDataInline(object, objectDataFile, storageObjectData);
  }

  /**
   * This save the object to have every modification as version of the object.
   *
   * @param object The object.
   * @return The URI of the saved version.
   * @throws IOException If Exception occurred then it will be thrown to be able to manage the
   *         locking in the {@link #save(StorageObject)}.
   */
  @Override
  protected final URI saveVersionedObject(StorageObject<?> object) throws IOException {
    saveObject(object);
    updateStorageObjectWithVersion(object, newVersion);
    return object.getVersionUri();

    // Load the StorageObjectData that is the api object of the storage itself.
    // File objectDataFile = getObjectDataFile(object.getUri());
    // File objectVersionBasePath = getObjectVersionBasePath(object.getUri());
    // // The temporary file of the StorageObjectData will be identified by the transaction id as
    // // extension.
    // StorageObjectData storageObjectData;
    // ObjectVersion newVersion;
    // ObjectVersion currentVersion = null;
    // if (storageAccessApi.exists(objectDataFile, getUriWithoutVersion(object.getUri()))) {
    // // This is an existing data file.
    // storageObjectData = readObjectData(objectDataFile);
    // currentVersion = storageObjectData.getCurrentVersion();
    // // We should check if the current version is the same.
    // if (object.getVersion() != null
    // && !StorageUtil.equalsVersion(object.getVersion(), currentVersion)) {
    // if (object.isStrictVersionCheck()) {
    // throw new ObjectModificationException("Unable to save " + object.getUri()
    // + " object because it has been modified in the meantime from " + object.getVersion()
    // + " --> " + currentVersion + " version");
    // } else {
    // if (log.isWarnEnabled()) {
    // String message = String.format(
    // "The save of the %s object is overwriting the %s version with the modification of %s earlier
    // version. It could lead loss of modification data!",
    // object.getUri(), currentVersion, object.getVersion());
    // try {
    // throw new ObjectModificationException(message);
    // } catch (ObjectModificationException e) {
    // log.warn(e.getMessage(), e);
    // }
    // }
    // }
    // }
    // // Increment the serial number. The given object is locked in the meantime so there is no
    // // need to worry about the parallel modification.
    // newVersion = new ObjectVersion();
    // } else {
    // // The first version in the new object. The version starts from 0. The object data and the
    // // object relation is also null. There is no version.
    // newVersion = new ObjectVersion();
    // // This will be a new data file, first we create the StorageObjectData save it into a new
    // // data file.
    // storageObjectData = new StorageObjectData().uri(object.getUri())
    // .className(object.definition().getQualifiedName());
    // }
    //
    // // The version is updated with the information attached if it's not a modification without
    // // object.
    // // TODO Inject transaction!
    // newVersion.transactionId(object.getTransactionId().toString())
    // .createdAt(OffsetDateTime.now());
    // newVersion.setCreatedBy(versionCreatedBy.get());
    // Map<String, ObjectAspect> aspects = object.getAspects();
    // if (aspects != null) {
    // newVersion.setAspects(aspects);
    // }
    // File objectVersionFile = null;
    // if (object.getOperation() == StorageObjectOperation.MODIFY_WITHOUT_DATA) {
    // // Set the new version data to the current version data there will be no new data version.
    // if (currentVersion != null) {
    // newVersion.setSerialNoData(currentVersion.getSerialNoData());
    // }
    // } else if (object.getObject() != null
    // && object.getOperation() != StorageObjectOperation.DELETE) {
    // newVersion
    // .setSerialNoData(
    // (currentVersion == null || currentVersion.getSerialNoData() == null) ? 0
    // : (currentVersion.getSerialNoData() + 1));
    // objectVersionFile =
    // getObjectVersionFile(objectVersionBasePath, newVersion.getSerialNoData());
    // }
    //
    // // Manage the references, load the current references
    // File objectRelationFile = storageObjectData.getCurrentVersion() != null
    // && storageObjectData.getCurrentVersion().getSerialNoRelation() != null
    // ? getObjectRelationVersionFile(objectVersionBasePath,
    // storageObjectData.getCurrentVersion().getSerialNoRelation())
    // : null;
    // StorageObjectRelationData storageObjectReferences =
    // saveStorageObjectReferences(object, loadRelationData(objectRelationFile));
    // File objectRelationVersionFile = null;
    // if (storageObjectReferences != null) {
    // // The data serial number will be the serial number of the version.
    // newVersion.setSerialNoRelation(
    // (currentVersion == null || currentVersion.getSerialNoRelation() == null) ? 0L
    // : (currentVersion.getSerialNoRelation() + 1));
    // objectRelationVersionFile =
    // getObjectRelationVersionFile(objectVersionBasePath, newVersion.getSerialNoRelation());
    // }
    //
    // // Write the version files
    // if (objectVersionFile != null || objectRelationVersionFile != null) {
    // BinaryData binaryDataVersion =
    // objectDefinitionApi.getDefaultSerializer().serialize(newVersion, ObjectVersion.class);
    // if (objectVersionFile != null) {
    // // Write the data version file
    // storageAccessApi.writeVersion(objectVersionFile, object.getUri(),
    // binaryDataVersion,
    // object.definition()
    // .serialize(object.getMode() == OperationMode.AS_MAP ? object.getObjectAsMap()
    // : object.getObject()));
    // // FileIO.writeMultipart(objectVersionFile,
    // // binaryDataVersion,
    // // object.definition()
    // // .serialize(object.getMode() == OperationMode.AS_MAP ? object.getObjectAsMap()
    // // : object.getObject()));
    // }
    // if (objectRelationVersionFile != null) {
    // // Write the version file first
    // FileIO.writeMultipart(objectRelationVersionFile, binaryDataVersion,
    // storageObjectRelationDataDef.serialize(storageObjectReferences));
    // }
    // }
    //
    // // Set the current version, change it at the last point to be able to use earlier.
    // storageObjectData.currentVersion(newVersion);
    //
    // saveObjectData(object, objectDataFile, storageObjectData);
    //
    // URI oldVersionUri = object.getVersionUri();
    // ObjectVersion oldVersion = currentVersion;
    // updateStorageObjectWithVersion(object, newVersion);
    // URI newVersionUri = object.getVersionUri();
    // addInvokeOnSucceedFunctions(object, oldVersion, oldVersionUri, newVersionUri,
    // objectVersionBasePath);
    // return newVersionUri;
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
  void addInvokeOnSucceedFunctions(StorageObject<?> object, ObjectVersion oldVersion,
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
            return object.definition()
                .fromMap(loadObjectVersion(object.definition(), objectVersionBasePath,
                    oldVersion.getSerialNoData(), oldVersionUri).getObjectAsMap());
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

  void invokeOnSucceedFunctionsFS(StorageObject<?> object,
      StorageSaveEvent storageSaveEvent) {
    invokeOnSucceedFunctions(object, storageSaveEvent);
  }

  @Override
  public boolean exists(URI uri) {
    DataRow objectRow;
    try {
      objectRow = Crud.read(objectEntryDef)
          .select(objectEntryDef.modifiedAt())
          .where(objectEntryDef.uri().eq(getUriWithoutVersion(uri)))
          .onlyOne()
          .orElse(null);
    } catch (Exception e) {
      objectRow = null;
    }
    if (objectRow == null) {
      return false;
    }
    return true;
  }

  @Override
  public Long lastModified(URI uri) {
    DataRow objectRow;
    try {
      objectRow = Crud.read(objectEntryDef)
          .select(objectEntryDef.modifiedAt())
          .where(objectEntryDef.uri().eq(getUriWithoutVersion(uri)))
          .onlyOne()
          .orElse(null);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to read the object record.", e);
    }
    if (objectRow == null) {
      return null;
    }
    LocalDateTime modifiedAt = objectRow.get(objectEntryDef.modifiedAt());
    return modifiedAt == null ? null : modifiedAt.toEpochSecond(ZoneOffset.UTC);
  }

  @Override
  public <T> StorageObject<T> load(Storage storage, URI uri, Class<T> clazz,
      StorageLoadOption... options) {
    long startTime = System.currentTimeMillis();
    URI uriWithoutVersion = getUriWithoutVersion(uri);
    Long version = getUriVersion(uri);

    // First we read the object entry
    DataRow objectEntryRow = Crud.read(objectEntryDef).select(objectEntryDef.allProperties())
        .where(objectEntryDef.uri().eq(uriWithoutVersion)).onlyOne().orElse(null);

    if (objectEntryRow == null) {
      throw new ObjectNotFoundException(uri, clazz, "Object not found.");
    }
    if (uriWithoutVersion.getPath().endsWith(Storage.SINGLE_VERSION_URI_POSTFIX)
        && storage.getVersionPolicy() != VersionPolicy.SINGLEVERSION) {
      throw new IllegalArgumentException("Unable to load single version object with .");
    }

    if (uriWithoutVersion.getPath().endsWith(Storage.SINGLE_VERSION_URI_POSTFIX)) {
      // Load the single version from file.
      version = FIRST_VERSION;
    }

    if (version == null) {
      // Use the latest version.
      version = objectEntryRow.get(objectEntryDef.version());
    }

    DataRow objectVersionRow = Crud.read(objectVersionDef).select(objectVersionDef.allProperties())
        .where(objectVersionDef.entryId().eq(objectEntryRow.get(objectEntryDef.id()))
            .AND(objectVersionDef.version().eq(version)))
        .onlyOne().orElse(null);

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

      StorageObjectHistoryEntry loadObjectVersion =
          loadObjectVersion(definition, storageObjectVersionBasePath,
              versionDataSerialNo, getUriWithVersion(uriWithoutVersion, versionDataSerialNo));

      // if (loadObjectVersion != null) {
      objectVersion = loadObjectVersion.getVersion();
      setObjectUriVersionByOptions(uri, definition, loadObjectVersion.getObjectAsMap(),
          versionDataSerialNo, options);
      storageObject =
          instanceOf(storage, definition, loadObjectVersion.getObjectAsMap(),
              objectVersion);
      storageObject.setAspects(objectVersion.getAspects());
      // }

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

    long endTime = System.currentTimeMillis();
    addRead(endTime - startTime);

    return storageObject.lastModified(lastModified);
  }

  @Override
  protected <O> List<O> readAll(Storage storage, String setName, Class<?> clazz,
      Function<URI, O> reader) {
    // Check if the given directory exists or not.
    ObjectDefinition<?> objectDefinition = objectDefinitionApi.definition(clazz);

    String storageScheme = getStorageScheme(storage);
    String setPath = StringConstant.SLASH + objectDefinition.getAlias()
        + (Strings.isBlank(setName) ? StringConstant.EMPTY
            : StringConstant.SLASH
                + setName);
    File setFolder =
        new File(rootFolder,
            storageScheme + setPath);
    Path setFolderPath = setFolder.toPath();

    if (setFolder.exists()) {
      // The depth of the walk is defined by the depth of the uri path. It's about 6-7 so we use 8
      // as maximum depth.
      List<O> objects = new ArrayList<>();
      // TODO Cleanup the empty directories.
      Deque<DirFileCounter> stack = new ArrayDeque<>();
      List<Path> emptyDirOrderedList = new ArrayList<>();
      try {
        Files.walkFileTree(setFolderPath, Collections.emptySet(), 8,
            new StorageSetFSVisitor() {

              @SuppressWarnings("unchecked")
              @Override
              public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                  throws IOException {
                String fileName = file.getFileName().toString();
                if (fileName.endsWith(SO_FILEEXTENSION)) {
                  // We read the object with the standard operation by creating a valid URI from the
                  // path.
                  String path = setPath + StringConstant.SLASH
                      + setFolderPath.relativize(file.getParent()).toString().replace('\\', '/')
                      + StringConstant.SLASH
                      + fileName.substring(0,
                          fileName.length() - SO_FILEEXTENSION.length());
                  URI uri = UriUtils.createUri(storageScheme, null,
                      path,
                      null);
                  objects.add(reader.apply(uri));
                  stack.peek().fileCount++;
                }
                return FileVisitResult.CONTINUE;
              }

              @Override
              public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                  throws IOException {
                // Add a new node to the stack before we enter the directory.
                stack.push(new DirFileCounter(dir));
                return super.preVisitDirectory(dir, attrs);
              }

              @Override
              public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                  throws IOException {
                DirFileCounter dirFileCounter = stack.pop();
                if (dirFileCounter.fileCount == 0) {
                  // It was empty let's add this to the empty list
                  emptyDirOrderedList.add(dir);
                } else {
                  // Increase the number of the file count in the parent.
                  if (stack.peek() != null) {
                    stack.peek().fileCount += dirFileCounter.fileCount;
                  }
                }
                return super.postVisitDirectory(dir, exc);
              }

            });
      } catch (IOException e) {
        log.debug("Unable to read all the objects from the set.", e);
      }
      // cleanupEmptyDirs(emptyDirOrderedList);
      return objects;
    }

    return Collections.emptyList();
  }

  @Override
  public boolean move(URI uri, URI targetUri) {
    // TODO For the first time we implement only the single version.
    File sourceObjectFile = getObjectDataFile(uri);
    File targetObjectFile = getObjectDataFile(targetUri);
    try {
      FileIO.move(sourceObjectFile, targetObjectFile);
      return true;
    } catch (InterruptedException e) {
      log.warn("Unable to move {} --> {}", sourceObjectFile, targetObjectFile);
    }
    return false;
  }

  private <T> StorageObject<T> readObjectSingleVersion(Storage storage, URI uri, Class<T> clazz,
      File storageObjectDataFile) {
    if (log.isTraceEnabled()) {
      log.trace("Reading single version: {}", storageObjectDataFile.getAbsolutePath());
    }
    long waitTime = 10;
    while (true) {
      if (storageObjectDataFile == null || !storageObjectDataFile.exists()
          || !storageObjectDataFile.isFile()) {
        throw new ObjectNotFoundException(uri, clazz, "Unable to load object data file.");
      }
      try {
        List<BinaryData> dataParts = FileIO.readMultipart(storageObjectDataFile);
        StorageObjectData dataObject;
        if (dataParts.get(0).length() != 0) {
          Optional<StorageObjectData> optObject =
              storageObjectDataDef.deserialize(dataParts.get(0));
          if (!optObject.isPresent()) {
            throw new ObjectNotFoundException(uri, clazz, "Unable to load object data file.");
          }
          dataObject = optObject.get();
        } else {
          dataObject = null;
        }
        @SuppressWarnings("unchecked")
        ObjectDefinition<T> definition =
            (ObjectDefinition<T>) getObjectDefinition(uri, dataObject, clazz);

        Map<String, Object> obj = null;
        if (dataParts.get(1).length() != 0) {
          try {
            obj = definition.deserializeAsMap(dataParts.get(1));
          } catch (JsonParseException e) {
            log.error("Unable to deserialize " + storageObjectDataFile.toString(), e);
            obj = new HashMap<>();
            obj.put("uri", uri);
          }
          if (obj != null && BinaryDataObject.class.equals(definition.getClazz())) {
            obj.put("uri", uri);
          }
        }
        return instanceOf(storage, definition, obj,
            dataObject == null ? null : dataObject.getCurrentVersion());
      } catch (IOException e) {
        // We must try again.
        log.debug("Unable to read {}", storageObjectDataFile);
        log.debug("Read error, waiting " + waitTime, e);
        waitTime = FileIO.getNextRandomWaitTime(waitTime);
      } catch (IllegalStateException e) {
        if (e.getCause() instanceof IOException) {
          log.debug("Unable to read {}", storageObjectDataFile);
          log.debug("Embedded read error, waiting " + waitTime, e);
          waitTime = FileIO.getNextRandomWaitTime(waitTime);
        } else {
          log.debug("Embedded read error, throwing", e);
          throw e;
        }
      }
      try {
        Thread.sleep(waitTime);
      } catch (InterruptedException e) {
        throw new RuntimeException("The reading was interrupted.", e);
      }
    }
  }

  private final StorageObjectData readObjectData(File objectDataFile) {
    if (log.isTraceEnabled()) {
      log.trace("Reading version: {}", objectDataFile.getAbsolutePath());
    }
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
      } catch (IOException e) {
        // We must try again.
        log.debug("Unable to read {}", objectDataFile);
        waitTime = FileIO.getNextRandomWaitTime(waitTime);
        log.debug("Read error, waiting " + waitTime, e);
      }
      try {
        Thread.sleep(waitTime);
      } catch (InterruptedException e) {
        throw new RuntimeException("The reading was interrupted.", e);
      }
    }
  }

  private <T> void setObjectUriVersionByOptions(URI uri, ObjectDefinition<T> definition,
      Map<String, Object> object,
      Long versionDataSerialNo,
      StorageLoadOption[] options) {
    if (!StorageLoadOption.checkUriWithVersionOption(options)) {
      // If no options specified the default behavior is to return the with the requested uri
      // This can ensure that the uri will be the exact uri used for the load.
      object.put("uri", uri);
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
      object.put("uri", uriToSet);
    }
  }

  private <T> StorageObjectHistoryEntry loadObjectVersion(ObjectDefinition<T> definition,
      File historyBasePath,
      long version,
      URI versionUri) {
    File objectVersionFile = getObjectVersionFile(
        historyBasePath,
        version);

    List<BinaryData> multipart = storageAccessApi.readVersion(objectVersionFile, versionUri);

    BinaryData versionObjectBinaryData = multipart.get(0);
    BinaryData versionBinaryData = multipart.get(1);

    ObjectVersion objectVersion;
    T object;
    Map<String, Object> objectAsMap;
    try {
      objectVersion = objectDefinitionApi.getDefaultSerializer()
          .deserialize(versionObjectBinaryData, ObjectVersion.class).get();
      objectAsMap = definition.deserializeAsMap(versionBinaryData);
      if (BinaryDataObject.class.equals(definition.getClazz())) {
        objectAsMap.put("uri", versionUri);
      }
    } catch (IOException e) {
      log.error("Unable to read version data", e);
      return null;
    }
    return new StorageObjectHistoryEntry(objectVersion, objectAsMap);
  }

  @Override
  public ObjectHistoryIterator objectHistory(URI uri, ObjectDefinition<?> definition) {
    if (definition == null) {
      return null;
    }

    File storageObjectDataFile =
        getDataFileByUri(getUriWithoutVersion(uri), SO_FILEEXTENSION);
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
      public Iterator<StorageObjectHistoryEntry> iterator() {
        return new Iterator<StorageObjectHistoryEntry>() {

          @Override
          public boolean hasNext() {
            return i < serialNoDataMax;
          }

          @Override
          public StorageObjectHistoryEntry next() {
            i++;
            return loadObjectVersion(definition, getObjectVersionBasePath(uri), i,
                getUriWithVersion(uri, i));
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

    File storageObjectDataFile =
        getDataFileByUri(getUriWithoutVersion(uri), SO_FILEEXTENSION);
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
      public Iterator<StorageObjectHistoryEntry> iterator() {
        return new Iterator<StorageObjectHistoryEntry>() {

          @Override
          public boolean hasNext() {
            return i > 0;
          }

          @Override
          public StorageObjectHistoryEntry next() {
            i--;
            return loadObjectVersion(definition, getObjectVersionBasePath(uri), i,
                getUriWithVersion(uri, i));
          }

        };
      }

    };
  }

  private final Long getNextId() {
    NextIdentifier next = identifierService.next();
    next.setInput(SEQUENCE_NAME);
    try {
      next.execute();
    } catch (Exception e) {
      throw new IllegalStateException(
          "Unable to retreive new identifier from database " + SEQUENCE_NAME + " sequence", e);
    }
    return next.output();
  }

}
