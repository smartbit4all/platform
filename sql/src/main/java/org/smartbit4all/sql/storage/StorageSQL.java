package org.smartbit4all.sql.storage;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
import org.smartbit4all.api.collection.StoredSequence;
import org.smartbit4all.api.storage.bean.ObjectAspect;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.storage.bean.StorageObjectData;
import org.smartbit4all.api.storage.bean.StorageObjectRelationData;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.TableDatas.BuilderWithFixProperties;
import org.smartbit4all.domain.data.storage.ObjectHistoryIterator;
import org.smartbit4all.domain.data.storage.ObjectModificationException;
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
import org.smartbit4all.domain.data.storage.StorageUtil;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.service.identifier.CurrentIdentifier;
import org.smartbit4all.domain.service.identifier.IdentifierService;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.domain.utility.crud.CrudRead;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
    return saveObject(object, null);
  }

  /**
   * In case of the database the save process is almost the same. We select the object record for
   * update or insert this
   * 
   * @param object
   * @param relationBinaryData
   * @return
   */
  private final Long saveObject(StorageObject<?> object, BinaryData relationBinaryData) {
    // Identify the object record. If it exists then lock it. If doesn't exist then we insert int
    // (it locks the record by the unique index)
    DataRow objectRow;
    try {
      String uriWithoutVersion = getUriString(getUriWithoutVersion(object.getUri()));
      objectRow = Crud.read(objectEntryDef)
          .select(objectEntryDef.allProperties())
          .where(objectEntryDef.uri().eq(uriWithoutVersion)).lock()
          .onlyOne()
          .orElse(null);
    } catch (Exception e) {
      objectRow = null;
    }

    BuilderWithFixProperties<ObjectVersionDef> builderVersion = TableDatas
        .builder(objectVersionDef, objectVersionDef.entryId(), objectVersionDef.version(),
            objectVersionDef.createdAt(), objectVersionDef.objectContent(),
            objectVersionDef.refContent(), objectVersionDef.aspectContent());
    if (objectRow != null) {
      // It is an already existing object so it is an update
      OffsetDateTime now = OffsetDateTime.now();
      if (object.isSingleVersion()) {
        // If it is a single version then we update the one and only one version of the object.
        Long newVersion = Long.valueOf(0);
        Crud.update(builderVersion
            .addRow()
            .set(objectVersionDef.entryId(), objectRow.get(objectEntryDef.id()))
            .set(objectVersionDef.version(), newVersion)
            .set(objectVersionDef.createdAt(), now)
            .set(objectVersionDef.objectContent(), object.serializeMapAware())
            .set(objectVersionDef.refContent(), relationBinaryData)
            .set(objectVersionDef.aspectContent(), object.serializeAspects())
            .build());
        objectRow.set(objectEntryDef.version(), newVersion);
        objectRow.set(objectEntryDef.modifiedAt(), now);
        Crud.update(objectRow.tableData());
        return newVersion;
      } else {
        // Update the entry with the new version and insert the new version.
        Long newVersion = objectRow.get(objectEntryDef.version()) + 1;
        objectRow.set(objectEntryDef.version(), newVersion);
        objectRow.set(objectEntryDef.modifiedAt(), now);
        Crud.update(objectRow.tableData());
        Crud.create(builderVersion
            .addRow()
            .set(objectVersionDef.entryId(), objectRow.get(objectEntryDef.id()))
            .set(objectVersionDef.version(), newVersion)
            .set(objectVersionDef.createdAt(), now)
            .set(objectVersionDef.objectContent(), object.serializeMapAware())
            .set(objectVersionDef.refContent(), relationBinaryData)
            .set(objectVersionDef.aspectContent(), object.serializeAspects())
            .build());
        return newVersion;
      }
    } else {
      // It is a brand new object insert simply.
      URI uri = object.getUri();
      Long nextId = getNextId();
      OffsetDateTime now = OffsetDateTime.now();
      Crud.create(TableDatas
          .builder(objectEntryDef, objectEntryDef.uri(), objectEntryDef.id(),
              objectEntryDef.scheme(), objectEntryDef.className(), objectEntryDef.createdAt(),
              objectEntryDef.modifiedAt(), objectEntryDef.uuid(),
              objectEntryDef.version(), objectEntryDef.singleVersion())
          .addRow()
          .set(objectEntryDef.uri(), getUriString(uri))
          .set(objectEntryDef.id(), nextId)
          .set(objectEntryDef.scheme(), uri.getScheme())
          .set(objectEntryDef.className(), object.definition().getAlias())
          .set(objectEntryDef.createdAt(), now)
          .set(objectEntryDef.modifiedAt(), now)
          .set(objectEntryDef.uuid(),
              object.getUuid() == null ? null : object.getUuid().toString())
          .set(objectEntryDef.version(), FIRST_VERSION)
          .set(objectEntryDef.singleVersion(), object.isSingleVersion()).build());
      Crud.create(builderVersion
          .addRow()
          .set(objectVersionDef.entryId(), nextId)
          .set(objectVersionDef.version(), FIRST_VERSION)
          .set(objectVersionDef.createdAt(), now)
          .set(objectVersionDef.objectContent(), object.serializeMapAware())
          .set(objectVersionDef.refContent(), relationBinaryData)
          .set(objectVersionDef.aspectContent(), object.serializeAspects())
          .build());
      return FIRST_VERSION;
    }
  }

  private final String getUriString(URI uri) {
    return uri == null ? null : uri.toString();
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
    // saveObject(object);
    // updateStorageObjectWithVersion(object, newVersion);
    // return object.getVersionUri();

    URI uriWithoutVersion = getUriWithoutVersion(object.getUri());

    Optional<DataRow> optObjectRow = queryObjectEntry(uriWithoutVersion, true);
    DataRow objectRow = null;
    StorageObjectData storageObjectData = null;
    if (optObjectRow.isPresent()) {
      objectRow = optObjectRow.get();
      storageObjectData = readObjectDataFromRow(uriWithoutVersion, objectRow)
          .currentVersion(readObjectVersion(objectRow.get(objectEntryDef.id()),
              objectRow.get(objectEntryDef.version())));
    }

    ObjectVersion newVersion;
    ObjectVersion currentVersion = null;
    if (storageObjectData != null) {
      // This is an existing object
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
            try {
              throw new ObjectModificationException(message);
            } catch (ObjectModificationException e) {
              log.warn(e.getMessage(), e);
            }
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
          .className(object.definition().getQualifiedName());
    }

    // The version is updated with the information attached if it's not a modification without
    // object.
    // TODO Inject transaction!
    newVersion.transactionId(object.getTransactionId().toString())
        .createdAt(OffsetDateTime.now());
    newVersion.setCreatedBy(versionCreatedBy.get());
    Map<String, ObjectAspect> aspects = object.getAspects();
    if (aspects != null) {
      newVersion.setAspects(aspects);
    }

    // Manage the references, load the current references
    Long objectRelationVersion = storageObjectData.getCurrentVersion() != null
        && storageObjectData.getCurrentVersion().getSerialNoRelation() != null
            ? storageObjectData.getCurrentVersion().getSerialNoRelation()
            : null;
    StorageObjectRelationData storageObjectReferences =
        saveStorageObjectReferences(object,
            loadRelationData(objectRow != null ? objectRow.get(objectEntryDef.id()) : null,
                objectRelationVersion));
    BinaryData relationBinaryData = null;
    if (storageObjectReferences != null) {
      // The data serial number will be the serial number of the version.
      newVersion.setSerialNoRelation(
          (currentVersion == null || currentVersion.getSerialNoRelation() == null) ? 0L
              : (currentVersion.getSerialNoRelation() + 1));
      relationBinaryData = storageObjectRelationDataDef.serialize(storageObjectReferences);
    }


    // Write the version files
    newVersion.setSerialNoData(saveObject(object, relationBinaryData));

    // Set the current version, change it at the last point to be able to use earlier.
    storageObjectData.currentVersion(newVersion);

    URI oldVersionUri = object.getVersionUri();
    ObjectVersion oldVersion = currentVersion;
    updateStorageObjectWithVersion(object, newVersion);
    URI newVersionUri = object.getVersionUri();
    // addInvokeOnSucceedFunctions(object, objectRow.get(objectEntryDef.id()), oldVersion,
    // oldVersionUri, newVersionUri);
    return newVersionUri;
  }

  /**
   * Invoke the on succeed functions depending on having a transaction or not. If we have an active
   * transaction then the functions is going to be called at the successful transaction end.
   *
   * @param object
   * @param oldVersion
   * @param oldVersionUri
   * @param newVersionUri
   */
  void addInvokeOnSucceedFunctions(StorageObject<?> object, Long entryId, ObjectVersion oldVersion,
      URI oldVersionUri, URI newVersionUri) {
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
                .fromMap(loadObjectVersion(object.definition(), entryId,
                    oldVersion.getSerialNoData(), oldVersionUri).getObjectAsMap());
          }
          return null;
        },
        newVersionUri,
        object.getObject(),
        object.definition().getClazz());
    // TODO Add transaction managed post commit!
    // if (transactionManager != null && transactionManager.isInTransaction()) {
    // transactionManager.addOnSucceed(object, event);
    // } else {
    invokeOnSucceedFunctions(object, event);
    // }
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
          .where(objectEntryDef.uri().eq(getUriString(getUriWithoutVersion(uri))))
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
          .where(objectEntryDef.uri().eq(getUriString(getUriWithoutVersion(uri))))
          .onlyOne()
          .orElse(null);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to read the object record.", e);
    }
    if (objectRow == null) {
      return null;
    }
    OffsetDateTime modifiedAt = objectRow.get(objectEntryDef.modifiedAt());
    return modifiedAt == null ? null : modifiedAt.toInstant().toEpochMilli();
  }

  @Override
  public <T> StorageObject<T> load(Storage storage, URI uri, Class<T> clazz,
      StorageLoadOption... options) {
    long startTime = System.currentTimeMillis();
    URI uriWithoutVersion = getUriWithoutVersion(uri);
    Long version = getUriVersion(uri);

    // First we read the object entry
    Optional<DataRow> optObjectEntryRow = queryObjectEntry(uriWithoutVersion, false);

    DataRow objectEntryRow = optObjectEntryRow
        .orElseThrow(() -> new ObjectNotFoundException(uri, clazz, "Object not found."));

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

    Optional<DataRow> optObjectVersionRow =
        queryObjectVersion(objectEntryRow.get(objectEntryDef.id()), version, true);
    DataRow objectVersionRow = optObjectVersionRow
        .orElseThrow(() -> new ObjectNotFoundException(uri, clazz, "Object version not found."));

    StorageObjectData storageObjectData = readObjectDataFromRow(uri, objectEntryRow)
        .currentVersion(readObjectVersionFromRow(version, objectVersionRow));

    @SuppressWarnings("unchecked")
    ObjectDefinition<T> definition =
        (ObjectDefinition<T>) getObjectDefinition(uri, storageObjectData, clazz);
    StorageObject<T> storageObject;
    ObjectVersion objectVersion = storageObjectData.getCurrentVersion();
    Long versionDataSerialNo = getVersionByUri(uri, storageObjectData);
    boolean skipData = StorageLoadOption.checkSkipData(options);
    if (versionDataSerialNo != null && !skipData) {

      StorageObjectHistoryEntry loadObjectVersion =
          loadObjectVersion(definition, objectEntryRow.get(objectEntryDef.id()),
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

    // Load the relation if exists in the actual version.
    BinaryData relationBinaryData = objectVersionRow.get(objectVersionDef.refContent());
    if (relationBinaryData != null) {
      loadStorageObjectReferences(storageObject,
          loadRelationData(relationBinaryData));
    }

    if (skipData) {
      setOperation(storageObject, StorageObjectOperation.MODIFY_WITHOUT_DATA);
    }

    long endTime = System.currentTimeMillis();
    addRead(endTime - startTime);

    return storageObject
        .lastModified(objectEntryRow.get(objectEntryDef.modifiedAt()).toEpochSecond());
  }

  @Override
  protected <O> List<O> readAll(Storage storage, String setName, Class<?> clazz,
      Function<URI, O> reader) {
    // Check if the given directory exists or not.
    ObjectDefinition<?> objectDefinition = objectDefinitionApi.definition(clazz);

    String storageScheme = getStorageScheme(storage);
    String setPath =
        storageScheme + StringConstant.COLON + StringConstant.SLASH + objectDefinition.getAlias()
            + (Strings.isBlank(setName) ? StringConstant.EMPTY
                : StringConstant.SLASH
                    + setName);

    TableData<ObjectEntryDef> objectList;
    try {
      objectList = Crud.read(objectEntryDef)
          .select(objectEntryDef.allProperties())
          .where(
              objectEntryDef.scheme().eq(storageScheme)
                  .AND(objectEntryDef.uri().like(setPath + StringConstant.PERCENT)))
          .listData();
      return objectList.rows().stream()
          .map(r -> reader.apply(UriUtils.asUri(r.get(objectEntryDef.uri()))))
          .collect(toList());
    } catch (Exception e) {
      log.debug("Unable to read all the objects from the set.", e);
      return Collections.emptyList();
    }
  }

  @Override
  public boolean move(URI uri, URI targetUri) {
    // It is a simple update...
    Optional<DataRow> optObjectEntryRow = queryObjectEntry(uri, true);
    if (optObjectEntryRow.isPresent()) {
      DataRow objectEntryRow = optObjectEntryRow.get();
      objectEntryRow.set(objectEntryDef.uri(), getUriString(targetUri));
      Crud.update(objectEntryRow.tableData());
      return true;
    }
    return false;
  }

  private StorageObjectData readObjectDataFromRow(URI objectUri, DataRow objectRow) {
    return new StorageObjectData().className(objectRow.get(objectEntryDef.className()))
        .uri(objectUri);
  }

  private final Optional<DataRow> queryObjectEntry(URI objectUri, boolean lock) {
    try {
      CrudRead<ObjectEntryDef> read = Crud.read(objectEntryDef)
          .select(objectEntryDef.allProperties())
          .where(objectEntryDef.uri().eq(getUriString(objectUri)));
      if (lock) {
        read.lock();
      }
      return read.onlyOne();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Return the object version object.
   * 
   * @param id The id of the object entry.
   * @param version The version of the object.
   * @return
   */
  private final ObjectVersion readObjectVersion(Long id, Long version) {
    Optional<DataRow> objectRow = queryObjectVersion(id, version, true);
    if (!objectRow.isPresent()) {
      return null;
    }
    // TODO extract the current and the pending version...
    return readObjectVersionFromRow(version, objectRow.get());
  }

  /**
   * Executes the query to retrieve the {@link DataRow} of the given object version.
   * 
   * @param id The objet entry id.
   * @param version The version.
   * @param skipContent Indicate to skip the content itself fro better performance.
   * @return The {@link DataRow}
   */
  private Optional<DataRow> queryObjectVersion(Long id, Long version, boolean skipContent) {
    if (id == null || version == null) {
      return Optional.empty();
    }
    try {
      PropertySet properties = objectVersionDef.allProperties();
      if (skipContent) {
        properties.remove(objectVersionDef.objectContent());
      }
      return Crud.read(objectVersionDef)
          .select(properties)
          .where(objectVersionDef.entryId().eq(id).AND(objectVersionDef.version().eq(version)))
          .onlyOne();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private final ObjectVersion readObjectVersionFromRow(Long version, DataRow objectRow) {
    return new ObjectVersion()
        .commonAncestorUri(objectRow.get(objectVersionDef.commonAncestorUri()))
        .createdAt(objectRow.get(objectVersionDef.createdAt()))
        .createdBy(objectRow.get(objectVersionDef.createdBy()))
        .createdByUri(objectRow.get(objectVersionDef.createdByUri()))
        .mergedWithUri(objectRow.get(objectVersionDef.mergedWithUri()))
        .operation(objectRow.get(objectVersionDef.operation()))
        .rebasedFromUri(objectRow.get(objectVersionDef.rebasedFromUri()))
        .serialNoData(version)
        .transactionId(objectRow.get(objectVersionDef.transactionId()));
  }

  private final BinaryData readObjectContentFromRow(DataRow objectRow) {
    return objectRow.get(objectVersionDef.objectContent());
  }

  private final BinaryData readAspectContentFromRow(DataRow objectRow) {
    return objectRow.get(objectVersionDef.aspectContent());
  }

  /**
   * Return the {@link StorageObjectHistoryEntry} that contains the loaded object as object and as
   * map and the {@link ObjectVersion} also.
   * 
   * @param <T> The type of the object.
   * @param definition The definition of the object.
   * @param id The id of the object entry.
   * @param version
   * @param versionUri
   * @return
   */
  private <T> StorageObjectHistoryEntry loadObjectVersion(ObjectDefinition<T> definition,
      Long id,
      Long version,
      URI versionUri) {

    Optional<DataRow> optObjectVersion = queryObjectVersion(id, version, false);
    if (!optObjectVersion.isPresent()) {
      return null;
    }

    DataRow objectRow = optObjectVersion.get();
    ObjectVersion objectVersion = readObjectVersionFromRow(version, objectRow);
    BinaryData versionBinaryData = readObjectContentFromRow(objectRow);

    Map<String, Object> objectAsMap;
    try {
      objectAsMap = definition.deserializeAsMap(versionBinaryData);
      if (BinaryDataObject.class.equals(definition.getClazz())) {
        objectAsMap.put("uri", versionUri);
      }
    } catch (IOException e) {
      log.error("Unable to read version data", e);
      return null;
    }

    BinaryData aspectBinaryData = readAspectContentFromRow(objectRow);
    if (aspectBinaryData != null) {
      try {
        Map<String, ObjectAspect> aspectAsMap = definition.deserializeAsMap(aspectBinaryData)
            .entrySet().stream().collect(toMap(e -> e.getKey(), e -> objectDefinitionApi
                .definition(ObjectAspect.class).fromMap((Map<String, Object>) e.getValue())));
        objectVersion.aspects(aspectAsMap);
      } catch (IOException e) {
        log.error("Unable to read version data", e);
        return null;
      }
    }
    return new StorageObjectHistoryEntry(objectVersion, objectAsMap);
  }

  private final StorageObjectRelationData loadRelationData(Long entryId, Long relationVersione) {
    Optional<DataRow> optObjectVersion = queryObjectVersion(entryId, relationVersione, false);
    if (optObjectVersion.isPresent()) {
      BinaryData binaryData = optObjectVersion.get().get(objectVersionDef.refContent());
      try {
        return storageObjectRelationDataDef.deserialize(binaryData).orElse(null);
      } catch (IOException e) {
        log.error("Unable to deserialize reference", e);
      }
    }
    return null;
  }

  private final StorageObjectRelationData loadRelationData(BinaryData relContent) {
    if (relContent == null) {
      return null;
    }
    try {
      return storageObjectRelationDataDef.deserialize(relContent).orElse(null);
    } catch (IOException e) {
      log.error("Unable to deserialize reference", e);
    }
    return null;
  }

  @Override
  public ObjectHistoryIterator objectHistory(URI uri, ObjectDefinition<?> definition) {
    if (definition == null) {
      return null;
    }

    Optional<DataRow> optObjectRow = queryObjectEntry(uri, false);
    if (!optObjectRow.isPresent()) {
      return null;
    }
    DataRow objectRow = optObjectRow.get();
    StorageObjectData objectData = readObjectDataFromRow(uri, objectRow)
        .currentVersion(readObjectVersion(objectRow.get(objectEntryDef.id()),
            objectRow.get(objectEntryDef.version())));
    ObjectVersion currentObjectVersion = objectData.getCurrentVersion();
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
            return loadObjectVersion(definition, objectRow.get(objectEntryDef.id()), i,
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

    Optional<DataRow> optObjectRow = queryObjectEntry(uri, false);
    if (!optObjectRow.isPresent()) {
      return null;
    }
    DataRow objectRow = optObjectRow.get();
    StorageObjectData objectData = readObjectDataFromRow(uri, objectRow)
        .currentVersion(readObjectVersion(objectRow.get(objectEntryDef.id()),
            objectRow.get(objectEntryDef.version())));
    ObjectVersion currentObjectVersion = objectData.getCurrentVersion();
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
            return loadObjectVersion(definition, objectRow.get(objectEntryDef.id()), i,
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

  @Override
  public StoredSequence getSequence(String schema, String name) {
    return new StoredSequence() {

      @Override
      public List<Long> next(int count) {
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
          NextIdentifier next = identifierService.next();
          next.setInput(name);
          try {
            next.execute();
          } catch (Exception e) {
            throw new IllegalStateException(
                "Unable to retreive new identifier from database " + name + " sequence",
                e);
          }
          result.add(next.output());
        }
        return result;
      }

      @Override
      public Long next() {
        return next(1).get(0);
      }

      @Override
      public Long current() {
        CurrentIdentifier current = identifierService.current();
        current.setInput(name);
        try {
          current.execute();
        } catch (Exception e) {
          throw new IllegalStateException(
              "Unable to retreive the current value from database " + name + " sequence",
              e);
        }
        return current.output();
      }
    };
  }

}
