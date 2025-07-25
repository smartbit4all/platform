package org.smartbit4all.sql.storage;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.smartbit4all.core.utility.StringConstant.HYPHEN;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataCompressionUtil;
import org.smartbit4all.api.binarydata.BinaryDataCompressionUtil.CompressionType;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.collection.bean.StoredListData;
import org.smartbit4all.api.collection.bean.StoredMapData;
import org.smartbit4all.api.collection.bean.StoredReferenceData;
import org.smartbit4all.api.collection.bean.StoredSequenceData;
import org.smartbit4all.api.object.DataSourceContextHolder;
import org.smartbit4all.api.object.DataSourceContextTemplate;
import org.smartbit4all.api.storage.bean.ObjectAspect;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.storage.bean.StorageObjectData;
import org.smartbit4all.api.storage.bean.StorageObjectRelationData;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import org.smartbit4all.domain.data.DataColumn;
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
import org.smartbit4all.domain.data.storage.StorageObjectHistoryEntry;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.smartbit4all.domain.data.storage.StorageObjectPhysicalLock;
import org.smartbit4all.domain.data.storage.StorageSaveEvent;
import org.smartbit4all.domain.data.storage.StorageUtil;
import org.smartbit4all.domain.data.storage.TransactionUtils;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.service.identifier.IdentifierService;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.smartbit4all.domain.service.modify.DeleteOutput;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.domain.utility.crud.CrudRead;
import org.smartbit4all.sql.storage.StorageSQLCacheConfig.CachePolicy;
import org.smartbit4all.sql.storage.StorageSQLCacheConfig.CacheSettings;
import org.smartbit4all.sql.storage.StorageSQLExtensionApi.ManagedObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ObjectUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;

public class StorageSQL extends ObjectStorageImpl implements InitializingBean {

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
  ObjectEntryLockDef objectEntryLockDef;

  @Autowired
  public IdentifierService identifierService;

  @Autowired
  protected DataSourceContextTemplate dataSourceContextTemplate;

  @Autowired(required = false)
  List<StorageSQLExtensionApi> extensions;

  private final Map<String, StorageSQLExtensionApi> extensionsCache = new HashMap<>();

  @Value("${storageSql.maximumCacheSize:81920}")
  private long maximumCacheSize;

  @Value("${storageSql.cacheConcurrencyLevel:10}")
  private int cacheConcurrencyLevel;

  /**
   * The default is one hour
   */
  @Value("${storageSql.expireAfterAccessInMillis:3600000}")
  private int expireAfterAccessInMillis;

  @Value("${storageSql.useCache:true}")
  private boolean useCache;

  @Value("${storageSql.useTransactionCache:true}")
  private boolean useTransactionCache;

  // default cache and className based caches
  private Cache<String, DataRow> defaultCache = null;
  private Map<String, Cache<String, DataRow>> classCaches = new HashMap<>();

  @Autowired(required = false)
  private StorageSQLCacheConfig cacheConfig;

  @Value("${storageSql.enableCompression:false}")
  private boolean enableCompression;

  @Value("${storageSql.compressionType:gzip}")
  private String compressionTypeString;

  private CompressionType compressionType;


  public StorageSQL(ObjectDefinitionApi objectDefinitionApi) {
    super(objectDefinitionApi);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (cacheConfig == null) {
      cacheConfig = new StorageSQLCacheConfig();
      cacheConfig.setEnabled(useCache);
    }
    if (cacheConfig.isEnabled()) {
      defaultCache = CacheBuilder.newBuilder()
          .maximumSize(cacheConfig.getDefaultSettings().getMaxSize())
          .concurrencyLevel(cacheConcurrencyLevel)
          .expireAfterAccess(Duration.ofMillis(expireAfterAccessInMillis))
          .removalListener((RemovalNotification<String, DataRow> notif) -> {
            if (log.isTraceEnabled()) {
              log.trace("Cache - evict from default cache, {}", notif.getKey());
            }
          })
          // TODO removalListener
          .build();
    }

    if (extensions != null) {
      for (StorageSQLExtensionApi e : extensions) {
        if (e.getManagedObjects() != null) {
          for (ManagedObject mo : e.getManagedObjects()) {
            extensionsCache.put(extensionId(mo.schema, mo.qualifiedName), e);
          }
        }
      }
    }
  }

  private static final String extensionId(String schema, String qualifiedName) {
    return schema + StringConstant.COLON + qualifiedName;
  }

  private boolean isUriInInsertCache(URI objectUri) {
    StorageCacheTransactionHandler trHandler = getStorageTransactionHandlerIfExists();
    if (trHandler != null) {
      String uriWithoutVersion = getUriString(getUriWithoutVersion(objectUri));
      // this uri insertion is deferred to the end of this transaction, we can skip physical lock
      // and pretend it succeeded
      return trHandler.objectEntriesToInsert.containsKey(uriWithoutVersion);
    }
    return false;
  }

  @Override
  public StorageObjectPhysicalLock lockPhysicalObject(URI objectUri, long waitUntil) {
    if (isUriInInsertCache(objectUri)) {
      return new StorageObjectPhysicalLock(objectUri, true);
    }

    return dataSourceContextTemplate.executeWith(DataSourceContextHolder.DATASOURCE_LOCK, () -> {
      if (transactionManager == null) {
        // if no transactionManager just do it (won't happen, sql always have trManager)
        return lockPhysicalObjectInNewTransaction(objectUri, waitUntil);
      }
      // if there's a trManager, physical lock should be acquired in a separate transaction
      TransactionTemplate transaction = new TransactionTemplate(transactionManager);
      transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
      return transaction
          .execute(
              status -> lockPhysicalObjectInNewTransaction(objectUri, waitUntil));
    });
  }

  private StorageObjectPhysicalLock lockPhysicalObjectInNewTransaction(URI objectUri,
      long waitUntil) {
    String objectUriString = objectUri.toString();
    UUID currentRuntime = runtimeApi().self().getUuid();
    DataRow objectLockRow;
    try {
      objectLockRow = Crud.read(objectEntryLockDef)
          .select(objectEntryLockDef.allProperties())
          .where(objectEntryLockDef.objectUri().eq(objectUriString)).lock()
          .onlyOne()
          .orElse(null);
    } catch (Exception e) {
      objectLockRow = null;
    }
    try {
      if (objectLockRow != null) {
        UUID runtimeUUID =
            UUID.fromString(objectLockRow.get(objectEntryLockDef.applicationRuntime()));
        if (Objects.equals(runtimeUUID, currentRuntime)) {
          // it's us, return lock
          return new StorageObjectPhysicalLock(objectUri);
        }
        if (runtimeApi().getActiveRuntimes().stream()
            .anyMatch(r -> Objects.equals(runtimeUUID, r.getUuid()))) {
          // it's someone active, no success this time
          return null;
        }
        // inactive runtime detected, update runtime (claim to me)
        Crud.update(createLockRecord(objectUriString, currentRuntime));
        return new StorageObjectPhysicalLock(objectUri);
      }
      // lock didn't exist -> create new one
      try {
        Crud.create(createLockRecord(objectUriString, currentRuntime));
      } catch (DataAccessException e) {
        // other node already created the lock
        log.debug("other node already created the lock");
        return null;
      }
      return new StorageObjectPhysicalLock(objectUri);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to lock " + objectUri, e);
    }
  }

  @Override
  protected boolean ownsRequiredLocks(List<StorageObjectLock> locksToUnlock) {
    UUID currentRuntime = runtimeApi().self().getUuid();

    List<String> lockUris = locksToUnlock.stream()
        .map(lock -> getUriString(getUriWithoutVersion(lock.getObjectURI())))
        .collect(toList());
    TableData<ObjectEntryLockDef> locksNotOwned = Crud.read(objectEntryLockDef)
        .select(objectEntryLockDef.allProperties())
        .where(objectEntryLockDef.objectUri().in(lockUris)
            .AND(objectEntryLockDef.applicationRuntime().noteq(currentRuntime.toString())))
        .listData();
    return locksNotOwned.isEmpty();
  }

  private TableData<ObjectEntryLockDef> createLockRecord(String objectUriString,
      UUID currentRuntime) {
    return TableDatas
        .builder(objectEntryLockDef, objectEntryLockDef.objectUri(),
            objectEntryLockDef.applicationRuntime(), objectEntryLockDef.createdAt())
        .addRow()
        .set(objectEntryLockDef.objectUri(),
            objectUriString)
        .set(objectEntryLockDef.applicationRuntime(), currentRuntime.toString())
        .set(objectEntryLockDef.createdAt(), OffsetDateTime.now())
        .build();
  }

  @Override
  public void unlockPhysicalObject(StorageObjectPhysicalLock lock) {
    if (lock != null && !lock.isInMemory()) {
      dataSourceContextTemplate.executeWith(DataSourceContextHolder.DATASOURCE_LOCK, () -> {
        if (transactionManager == null) {
          // no transactionManager or already in transaction
          unlockPhysicalObjectInTransaction(lock);
          return;
        }

        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transaction
            .executeWithoutResult(
                status -> unlockPhysicalObjectInTransaction(lock));

      });
    }
  }

  private void unlockPhysicalObjectInTransaction(StorageObjectPhysicalLock lock) {
    UUID uuid = runtimeApi().self().getUuid();
    try {
      DeleteOutput delete = Crud.delete(objectEntryLockDef,
          objectEntryLockDef.objectUri().eq(getUriString(getUriWithoutVersion(lock.getObjectUri())))
              .AND(objectEntryLockDef.applicationRuntime().eq(uuid.toString())));
      if (delete.getUpdateCount() == 0) {
        log.warn("Couldn't find the {} lock to delete for {} runtime", lock.getObjectUri(),
            uuid.toString());
      }
    } catch (Exception e) {
      throw new IllegalStateException("Unable to unlock object " + lock.getObjectUri(), e);
    }
  }

  @Override
  protected boolean lockOnSave() {
    return false;
  }

  @Override
  protected Function<Boolean, StorageObjectPhysicalLock> physicalLockSupplier(URI objectUri) {
    Function<Boolean, StorageObjectPhysicalLock> result = super.physicalLockSupplier(objectUri);
    if (result == null) {
      // TODO move to super, if runtime if available
      throw new IllegalStateException("No physicalLockSupplier for object " + objectUri);
    }
    return result;
  }

  @Override
  protected Consumer<StorageObjectPhysicalLock> physicalLockReleaser() {
    Consumer<StorageObjectPhysicalLock> result = super.physicalLockReleaser();
    if (result == null) {
      throw new IllegalStateException("No physicalLockReleaser");
    }
    return result;
  }

  @Override
  public StorageObject<?> save(StorageObject<?> object) {
    // in StorageSQL save should be done in transaction
    if (transactionManager == null || TransactionSynchronizationManager.isSynchronizationActive()) {
      // no transactionManager or already in transaction
      return saveInTransaction(null, object);
    }
    TransactionTemplate transaction = new TransactionTemplate(transactionManager);
    return transaction
        .execute(status -> saveInTransaction(status, object));
  }

  private StorageObject<?> saveInTransaction(TransactionStatus status, StorageObject<?> object) {
    return super.save(object);
  }

  /**
   * In case of the database the save process is almost the same. We select the object record for
   * update or insert this
   *
   * @param object
   * @return
   */
  private final Long saveObject(StorageObject<?> object, TableData<ObjectEntryDef> objectEntry) {
    return saveObject(object, null, objectEntry);
  }

  /**
   * In case of the database the save process is almost the same. We select the object record for
   * update or insert this
   *
   * @param object
   * @param relationBinaryData
   * @return The version number of the newly saved object.
   */
  private final Long saveObject(StorageObject<?> object, BinaryData relationBinaryData,
      TableData<ObjectEntryDef> objectEntry) {
    // Identify the object record. If it exists then lock it. If doesn't exist then we insert int
    // (it locks the record by the unique index)
    if (transactionManager == null || TransactionSynchronizationManager.isSynchronizationActive()) {
      // no transactionManager or already in transaction
      return saveObjectInTransaction(null, object, relationBinaryData, objectEntry);
    }
    TransactionTemplate transaction = new TransactionTemplate(transactionManager);
    return transaction
        .execute(
            status -> saveObjectInTransaction(status, object, relationBinaryData, objectEntry));
  }

  private Long saveObjectInTransaction(TransactionStatus status, StorageObject<?> object,
      BinaryData relationBinaryData, TableData<ObjectEntryDef> objectEntry) {
    StorageSQLExtensionApi extensionApi =
        getExtensionApi(object.getStorage().getScheme(), object.definition().getQualifiedName());
    if (extensionApi != null) {
      return extensionApi.saveObject(object, relationBinaryData);
    }

    DataRow objectRow = null;
    StorageCacheTransactionHandler trHandler = getStorageTransactionHandlerIfExists();
    String uriWithoutVersion = getUriString(getUriWithoutVersion(object.getUri()));
    try {
      if (log.isTraceEnabled()) {
        log.trace("saveObject read: uriWithoutVersion={}", uriWithoutVersion);
      }
      if (objectEntry == null) {
        objectEntry = getOrQueryObjectEntry(uriWithoutVersion, true, null);
      }
      if (objectEntry.size() == 1) {
        objectRow = objectEntry.rows().get(0);
      }
    } catch (Exception e) {
      objectRow = null;
    }
    BuilderWithFixProperties<ObjectVersionDef> builderVersion = TableDatas
        .builder(objectVersionDef, objectVersionDef.versionId(),
            objectVersionDef.entryId(), objectVersionDef.version(),
            objectVersionDef.createdAt(), objectVersionDef.objectContent(),
            objectVersionDef.refContent(), objectVersionDef.aspectContent(),
            // new attributes, TODO where are they used / set?
            objectVersionDef.commonAncestorUri(),
            objectVersionDef.createdBy(),
            objectVersionDef.createdByUri(),
            objectVersionDef.mergedWithUri(),
            objectVersionDef.operation(),
            objectVersionDef.rebasedFromUri(),
            objectVersionDef.transactionId(),
            objectVersionDef.objectContentCompressionType());
    BinaryData objectContent = compressContent(object, object.serializeMapAware());
    boolean doCompress = objectContent != null && Boolean.TRUE.equals(objectContent.isCompressed());
    String objectContentCompressionType = doCompress ? compressionTypeString : null;
    BinaryData aspectContent = object.serializeAspects();
    if (objectRow != null) {
      // It is an already existing object so it is an update
      OffsetDateTime now = OffsetDateTime.now();
      if (object.isSingleVersion()) {
        // If it is a single version then we update the one and only one version of the object.
        Long newVersion = FIRST_VERSION;
        String versionId = createVersionId(objectRow.get(objectEntryDef.id()), newVersion);
        Long newRefVersion = relationBinaryData != null ? FIRST_VERSION : null;

        TableData<ObjectVersionDef> objectVersion = builderVersion
            .addRow()
            .set(objectVersionDef.versionId(), versionId)
            .set(objectVersionDef.entryId(), objectRow.get(objectEntryDef.id()))
            .set(objectVersionDef.version(), newVersion)
            .set(objectVersionDef.createdAt(), objectRow.get(objectVersionDef.createdAt()))
            .set(objectVersionDef.objectContent(), objectContent)
            .set(objectVersionDef.refContent(), relationBinaryData)
            .set(objectVersionDef.aspectContent(), aspectContent)
            .set(objectVersionDef.commonAncestorUri(), null)
            .set(objectVersionDef.createdBy(), null)
            .set(objectVersionDef.createdByUri(), null)
            .set(objectVersionDef.mergedWithUri(), null)
            .set(objectVersionDef.operation(), null)
            .set(objectVersionDef.rebasedFromUri(), null)
            .set(objectVersionDef.transactionId(), null)
            .set(objectVersionDef.objectContentCompressionType(), objectContentCompressionType)
            .build();
        objectRow.set(objectEntryDef.version(), newVersion);
        objectRow.set(objectEntryDef.refVersion(), newRefVersion);
        objectRow.set(objectEntryDef.modifiedAt(), now);
        if (useTransactionCache && trHandler != null) {
          trHandler.addObjectEntryToUpdate(uriWithoutVersion, objectEntry);
          trHandler.addObjectVersionToUpdate(versionId, objectVersion);
        } else {
          Crud.update(objectVersion);
          Crud.update(objectRow.tableData());
        }
        return newVersion;
      } else {
        // Update the entry with the new version and insert the new version.
        Long newVersion = objectRow.get(objectEntryDef.version()) + 1;
        Long currentRefVersion = objectRow.get(objectEntryDef.refVersion());
        Long newRefVersion = null;
        if (relationBinaryData != null) {
          newRefVersion = newVersion;
        } else {
          newRefVersion = currentRefVersion;
        }
        String versionId = createVersionId(objectRow.get(objectEntryDef.id()), newVersion);
        TableData<ObjectVersionDef> objectVersion = builderVersion
            .addRow()
            .set(objectVersionDef.versionId(), versionId)
            .set(objectVersionDef.entryId(), objectRow.get(objectEntryDef.id()))
            .set(objectVersionDef.version(), newVersion)
            .set(objectVersionDef.createdAt(), now)
            .set(objectVersionDef.objectContent(), objectContent)
            .set(objectVersionDef.refContent(), relationBinaryData)
            .set(objectVersionDef.aspectContent(), aspectContent)
            .set(objectVersionDef.commonAncestorUri(), null)
            .set(objectVersionDef.createdBy(), null)
            .set(objectVersionDef.createdByUri(), null)
            .set(objectVersionDef.mergedWithUri(), null)
            .set(objectVersionDef.operation(), null)
            .set(objectVersionDef.rebasedFromUri(), null)
            .set(objectVersionDef.transactionId(), null)
            .set(objectVersionDef.objectContentCompressionType(), objectContentCompressionType)
            .build();
        objectRow.set(objectEntryDef.version(), newVersion);
        objectRow.set(objectEntryDef.refVersion(), newRefVersion);
        objectRow.set(objectEntryDef.modifiedAt(), now);
        if (useTransactionCache && trHandler != null) {
          trHandler.addObjectEntryToUpdate(uriWithoutVersion, objectEntry);
          trHandler.addObjectVersionToInsert(versionId, objectVersion);
        } else {
          Crud.create(objectVersion);
          Crud.update(objectRow.tableData());
        }
        return newVersion;
      }
    } else {
      // It is a brand new object insert simply.
      URI uri = object.getUri();
      Long nextId = getNextId();
      OffsetDateTime now = OffsetDateTime.now();
      Long newRefVersion = relationBinaryData != null ? FIRST_VERSION : null;
      Long newVersion = FIRST_VERSION;
      objectEntry = TableDatas
          .builder(objectEntryDef, objectEntryDef.allProperties())
          // .builder(objectEntryDef, objectEntryDef.uri(), objectEntryDef.id(),
          // objectEntryDef.scheme(), objectEntryDef.className(), objectEntryDef.createdAt(),
          // objectEntryDef.modifiedAt(), objectEntryDef.uuid(),
          // objectEntryDef.version(), objectEntryDef.refVersion(),
          // objectEntryDef.singleVersion())
          .addRow()
          .set(objectEntryDef.uri(), getUriString(uri))
          .set(objectEntryDef.id(), nextId)
          .set(objectEntryDef.scheme(), uri.getScheme())
          .set(objectEntryDef.className(), object.definition().getAlias())
          .set(objectEntryDef.createdAt(),
              object.getCreatedAt() != null ? object.getCreatedAt() : now)
          .set(objectEntryDef.modifiedAt(), now)
          .set(objectEntryDef.uuid(),
              object.getUuid() == null ? null : object.getUuid().toString())
          .set(objectEntryDef.version(), newVersion)
          .set(objectEntryDef.refVersion(), newRefVersion)
          .set(objectEntryDef.singleVersion(), object.isSingleVersion()).build();
      String versionId = createVersionId(nextId, newVersion);
      TableData<ObjectVersionDef> objectVersion = builderVersion
          .addRow()
          .set(objectVersionDef.versionId(), versionId)
          .set(objectVersionDef.entryId(), nextId)
          .set(objectVersionDef.version(), newVersion)
          .set(objectVersionDef.createdAt(),
              object.getCreatedAt() != null ? object.getCreatedAt() : now)
          .set(objectVersionDef.objectContent(), objectContent)
          .set(objectVersionDef.refContent(), relationBinaryData)
          .set(objectVersionDef.aspectContent(), aspectContent)
          .set(objectVersionDef.commonAncestorUri(), null)
          .set(objectVersionDef.createdBy(), null)
          .set(objectVersionDef.createdByUri(), null)
          .set(objectVersionDef.mergedWithUri(), null)
          .set(objectVersionDef.operation(), null)
          .set(objectVersionDef.rebasedFromUri(), null)
          .set(objectVersionDef.transactionId(), null)
          .set(objectVersionDef.objectContentCompressionType(), objectContentCompressionType)
          .build();
      if (useTransactionCache && trHandler != null
          && !classesToSkipInsertCache.contains(object.definition().getQualifiedName())) {
        trHandler.addObjectEntryToInsert(uriWithoutVersion, objectEntry);
        trHandler.addObjectVersionToInsert(versionId, objectVersion);
      } else {
        Crud.create(objectEntry);
        Crud.create(objectVersion);
      }
      return newVersion;
    }
  }

  private StorageCacheTransactionHandler getStorageTransactionHandlerIfExists() {
    return TransactionSynchronizationManager.isSynchronizationActive()
        ? getOrRegisterStorageCacheTransactionHandler()
        : null;
  }

  private final List<String> classesToSkipInsertCache = Arrays.asList(
      StoredListData.class.getName(),
      StoredMapData.class.getName(),
      StoredReferenceData.class.getName(),
      StoredSequenceData.class.getName());

  private TableData<ObjectEntryDef> getOrQueryObjectEntry(
      String uriWithoutVersion, boolean lock, PropertySet properties) {
    StorageCacheTransactionHandler trHandler = getStorageTransactionHandlerIfExists();
    TableData<ObjectEntryDef> objectEntry = null;
    if (useTransactionCache && trHandler != null) {
      objectEntry = trHandler.getObjectEntry(uriWithoutVersion);
    }
    if (objectEntry == null) {
      CrudRead<ObjectEntryDef> read = Crud.read(objectEntryDef)
          .select(ObjectUtils.isEmpty(properties) ? objectEntryDef.allProperties() : properties)
          .where(objectEntryDef.uri().eq(uriWithoutVersion));

      if (lock) {
        read.lock();
      }
      objectEntry = read.listData();
      if (objectEntry == null || objectEntry.size() > 1) {
        throw new IllegalArgumentException("Null or more than one objectEntry by uri");
      }
    }
    return objectEntry;
  }

  public static final String getUriString(URI uri) {
    return uri == null ? null : uri.toString();
  }

  private String createVersionId(Long entryId, Long version) {
    return entryId + HYPHEN + version;
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
    saveObject(object, null);
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

    TableData<ObjectEntryDef> objectEntry =
        getOrQueryObjectEntry(getUriString(uriWithoutVersion), true, null);
    DataRow objectRow;
    if (objectEntry.size() == 1) {
      objectRow = objectEntry.rows().get(0);
    } else {
      objectRow = null;
    }
    StorageObjectData storageObjectData = null;
    if (objectRow != null) {
      storageObjectData = readObjectDataFromRow(uriWithoutVersion, objectRow)
          .currentVersion(readObjectVersion(
              objectRow.get(objectEntryDef.id()),
              objectRow.get(objectEntryDef.version()),
              uriWithoutVersion));
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
        .createdAt(object.getCreatedAt() == null ? OffsetDateTime.now() : object.getCreatedAt());
    newVersion.setCreatedBy(versionCreatedBy.get());
    Map<String, ObjectAspect> aspects = object.getAspects();
    if (aspects != null) {
      newVersion.setAspects(aspects);
    }

    // Manage the references, load the current references
    Long objectRelationVersion =
        objectRow != null ? objectRow.get(objectEntryDef.refVersion()) : null;
    StorageObjectRelationData storageObjectReferences =
        saveStorageObjectReferences(object,
            loadRelationData(
                objectRow != null ? objectRow.get(objectEntryDef.id()) : null,
                objectRelationVersion,
                uriWithoutVersion));
    BinaryData relationBinaryData = null;
    if (storageObjectReferences != null) {
      // The data serial number will be the serial number of the version.
      newVersion.setSerialNoRelation(
          (currentVersion == null || currentVersion.getSerialNoRelation() == null) ? 0L
              : (currentVersion.getSerialNoRelation() + 1));
      relationBinaryData = storageObjectRelationDataDef.serialize(storageObjectReferences);
    }


    // Write the version files
    newVersion.setSerialNoData(saveObject(object, relationBinaryData, objectEntry));

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
    handleStorageSaveEvent(object, event);
  }

  @Override
  public boolean exists(Storage storage, URI uri) {
    DataRow objectRow;
    try {
      if (log.isTraceEnabled()) {
        log.trace("exists: uri={}", uri);
      }
      ObjectDefinition<?> definition = objectDefinitionApi.definition(uri);
      if (definition != null) {
        StorageSQLExtensionApi extensionApi =
            getExtensionApi(storage.getScheme(), definition.getQualifiedName());
        if (extensionApi != null) {
          return extensionApi.exists(uri);
        }
      }
      objectRow =
          queryObjectEntry(uri, false, new PropertySet(Arrays.asList(objectEntryDef.uri())));
    } catch (Exception e) {
      objectRow = null;
    }
    return objectRow != null;
  }

  @Override
  public Long lastModified(URI uri) {
    DataRow objectRow;
    try {
      if (log.isTraceEnabled()) {
        log.trace("lastModified: uri={}", uri);
      }
      objectRow = queryObjectEntry(uri, false,
          new PropertySet(Arrays.asList(objectEntryDef.uri(), objectEntryDef.modifiedAt())));
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
    return loadBatch(storage, Arrays.asList(uri), clazz, options).get(0);
  }

  @Override
  public List<StorageObject<?>> loadBatch(Storage storage, List<URI> uris,
      StorageLoadOption... options) {
    List<StorageObject<Object>> result = loadBatch(storage, uris, null, options);
    return new ArrayList<>(result);
  }

  private Cache<String, DataRow> getClassCache(String className) {
    if (cacheConfig == null) {
      return null;
    }
    CacheSettings classCacheSettings = cacheConfig.getSettingsForClass(className);
    if (classCacheSettings.getPolicy() == CachePolicy.IGNORE) {
      return null;
    }
    if (classCacheSettings == cacheConfig.getDefaultSettings()) {
      return defaultCache;
    }

    // cacheSettings exist specifically for this class, and not IGNORE
    return classCaches.computeIfAbsent(className, (key) -> {
      long maxSize = cacheConfig.getMaxSize(className);
      if (log.isDebugEnabled()) {
        log.debug("Creating cache for class {}, maxSize: {}", className, maxSize);
      }

      return CacheBuilder.newBuilder()
          .maximumSize(maxSize)
          .concurrencyLevel(cacheConcurrencyLevel)
          .expireAfterAccess(Duration.ofMillis(expireAfterAccessInMillis))
          .removalListener((RemovalNotification<String, DataRow> notif) -> {
            if (log.isTraceEnabled()) {
              log.trace("Cache - evict from {} cache, {}", className, notif.getKey());
            }
          })
          .build();
    });
  }

  public class UriInfo {
    final URI uri;
    final String baseUri;
    final Long originalVersion;
    final boolean isSingleVersion;
    final String className;

    Long entryId;
    Long calculatedVersion;
    String versionId;

    UriInfo(URI uri) {
      this.uri = uri;
      this.baseUri = getUriString(getUriWithoutVersion(uri));
      this.isSingleVersion = isSingleVersion(uri);
      if (this.isSingleVersion) {
        this.originalVersion = FIRST_VERSION;
      } else {
        this.originalVersion = getUriVersion(uri);
      }
      this.className = UriUtils.getClassName(uri);
    }

    UriInfo(URI uri, Long id, Long version) {
      this(uri);
      this.entryId = id;
      this.calculatedVersion = version;
      this.versionId = createVersionId(entryId, calculatedVersion);
    }
  }

  // @Transactional
  @Override
  public <T> List<StorageObject<T>> loadBatch(Storage storage, List<URI> uris, Class<T> clazz,
      StorageLoadOption... options) {
    if (uris == null || uris.isEmpty()) {
      return Collections.emptyList();
    }

    long startTime = System.currentTimeMillis();

    // create URI metadata list preserving order
    List<UriInfo> uriInfos = uris.stream()
        .map(UriInfo::new)
        .collect(Collectors.toList());

    if (clazz != null) {
      StorageSQLExtensionApi extensionApi = getExtensionApi(storage.getScheme(), clazz.getName());
      if (extensionApi != null) {
        return extensionApi.loadBatch(self, storage, uriInfos, clazz, options);
      }
    }

    // batch query object entries
    Map<String, DataRow> objectEntryRows = queryObjectEntries(uriInfos);

    // calculate uriInfo data
    for (UriInfo info : uriInfos) {
      DataRow entryRow = objectEntryRows.get(info.baseUri);
      if (entryRow != null) {
        // surely will exist, queryObjectEntries will throw an exception if not
        info.entryId = entryRow.get(objectEntryDef.id());
        Long entryVersion = entryRow.get(objectEntryDef.version());
        info.calculatedVersion =
            info.originalVersion != null ? info.originalVersion
                : entryVersion;
        if (info.originalVersion != null
            && entryVersion != null
            && entryVersion < info.originalVersion) {
          if (log.isErrorEnabled()) {
            log.error("Error when trying to query {}! latestVersion ({}) < requestedVersion ({})",
                info.uri, entryVersion, info.originalVersion);
            log.error("Using latest version instead!", new Exception());
          }
          info.calculatedVersion = entryVersion;
        }
        info.versionId = createVersionId(info.entryId, info.calculatedVersion);
      }
    }

    // batch query object versions
    Map<String, DataRow> versionRows = queryObjectVersions(uriInfos);

    // process results and create StorageObjects
    List<StorageObject<T>> result = new ArrayList<>();

    for (UriInfo info : uriInfos) {
      String uriString = info.baseUri;
      DataRow entryRow = objectEntryRows.get(uriString);

      if (entryRow == null) {
        continue;
      }

      URI uri = info.uri;
      Long entryId = info.entryId;
      Long version = info.calculatedVersion; // original version might be null
      String versionKey = info.versionId;
      DataRow versionRow = versionRows.get(versionKey);

      if (versionRow == null) {
        continue;
      }

      try {
        StorageObjectData storageObjectData = readObjectDataFromRow(uri, entryRow)
            .currentVersion(readObjectVersionFromRow(version, versionRow));

        ObjectDefinition<T> definition = getObjectDefinition(uri, storageObjectData, clazz);

        StorageObject<T> storageObject;
        ObjectVersion objectVersion = storageObjectData.getCurrentVersion();

        boolean skipData = StorageLoadOption.checkSkipData(options);
        if (!skipData) {
          Map<String, Object> objectMap = definition.deserializeAsMap(
              readObjectContentFromRow(versionRow));
          setObjectUriVersionByOptions(uri, definition, objectMap, version, options);
          storageObject =
              instanceOf(storage, definition, objectMap, objectVersion, entryId.toString());

          // Handle aspects
          BinaryData aspectBinaryData = readAspectContentFromRow(versionRow);
          if (aspectBinaryData != null) {
            try {
              Map<String, ObjectAspect> aspectAsMap = definition.deserializeAsMap(aspectBinaryData)
                  .entrySet().stream()
                  .collect(toMap(
                      Entry::getKey,
                      e -> objectDefinitionApi.definition(ObjectAspect.class)
                          .fromMap((Map<String, Object>) e.getValue())));
              objectVersion.aspects(aspectAsMap);
              storageObject.setAspects(aspectAsMap);
            } catch (IOException e) {
              log.error("Unable to read aspect data for uri: " + uri, e);
            }
          }
        } else {
          storageObject =
              instanceOf(storage, definition, uri, storageObjectData, entryId.toString());
          setOperation(storageObject, StorageObjectOperation.MODIFY_WITHOUT_DATA);
        }
        // handle relations
        Long refVersion = entryRow.get(objectEntryDef.refVersion());
        if (refVersion != null) {
          loadStorageObjectReferences(storageObject,
              loadRelationData(entryId, refVersion, uri));
        }

        result.add(storageObject
            .lastModified(entryRow.get(objectEntryDef.modifiedAt()).toEpochSecond()));

      } catch (Exception e) {
        log.error("Failed to load object: " + uri, e);
        // TODO throw exception or continue with next object?
      }
    }

    long endTime = System.currentTimeMillis();
    addRead(endTime - startTime);

    return result;
  }

  private Map<String, DataRow> queryObjectVersions(List<UriInfo> uriInfos) {
    if (uriInfos.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, DataRow> versionRows = new HashMap<>();

    Map<String, List<UriInfo>> urisByClass = uriInfos.stream()
        .filter(info -> info.versionId != null)
        .collect(Collectors.groupingBy(info -> info.className != null ? info.className : ""));

    // split entries into cacheable and non-cacheable groups
    Map<String, Set<String>> idsToQueryByClass = new HashMap<>();
    Set<String> nonCachedIds = new HashSet<>();

    for (Map.Entry<String, List<UriInfo>> entry : urisByClass.entrySet()) {
      String className = entry.getKey();
      List<UriInfo> classUris = entry.getValue();

      Cache<String, DataRow> cache = getClassCache(className);
      Set<String> idsToQuery = new HashSet<>();
      classUris.forEach((info) -> {
        String versionId = info.versionId;
        boolean isSingleVersion = info.isSingleVersion;

        // skip cache for single version objects
        if (!isSingleVersion && cache != null) {
          DataRow cachedRow = cache.getIfPresent(versionId);
          if (cachedRow != null) {
            if (log.isTraceEnabled()) {
              log.trace("Cache - hit, {}", versionId);
            }
            versionRows.put(versionId, cachedRow);
            return;
          }
        }
        // not found in cache or singleVersion -> we should query this versionId
        idsToQuery.add(versionId);
        if (!isSingleVersion) {
          nonCachedIds.add(versionId);
        }
      });

      if (!idsToQuery.isEmpty()) {
        idsToQueryByClass.put(className, idsToQuery);
      }
    }

    Set<String> allIdsToQuery = idsToQueryByClass.values().stream()
        .flatMap(Set::stream)
        .collect(Collectors.toSet());

    // query database only for idsToQuery
    if (!allIdsToQuery.isEmpty()) {
      // check transaction cache is present
      Map<String, DataRow> objectVersionsFromTransactionCache = new HashMap<>();
      Set<String> allIdsToQueryFromDB =
          fillObjectVersionsFromTransactionCache(allIdsToQuery, objectVersionsFromTransactionCache);
      // read remaining versions from db
      Map<String, DataRow> dbRows = Crud.read(objectVersionDef)
          .select(objectVersionDef.allProperties())
          .where(objectVersionDef.versionId().in(allIdsToQueryFromDB))
          .listData()
          .rows()
          .stream()
          .collect(Collectors.toMap(
              row -> row.get(objectVersionDef.versionId()),
              row -> row));
      if (!objectVersionsFromTransactionCache.isEmpty()) {
        dbRows.putAll(objectVersionsFromTransactionCache);
      }

      // add to cache by class
      for (Map.Entry<String, Set<String>> entry : idsToQueryByClass.entrySet()) {
        String className = entry.getKey();
        Cache<String, DataRow> cache = getClassCache(className);
        if (cache == null) {
          continue;
        }
        for (String id : entry.getValue()) {
          if (!nonCachedIds.contains(id)) {
            continue;
          }
          DataRow row = dbRows.get(id);
          if (row != null) {
            // check object size // TODO later
            // long maxObjectSize = cacheConfig.getMaxObjectSize(className);
            // BinaryData content = row.get(objectVersionDef.objectContent());
            // long objectSize = content != null ? getObjectSize(content) : 0;

            // if (objectSize <= maxObjectSize) {
            if (log.isTraceEnabled()) {
              log.trace("Cache - storing for class {}, id: {}",
                  className, id);
              // log.trace("Cache - storing for class {}, id: {}, size: {} bytes",
              // className, id, objectSize);
            }
            cache.put(id, row);
            addedToCache(id, className);
            // } else if (log.isTraceEnabled()) {
            // log.trace("Not caching {} for class {} - size {} exceeds limit {}",
            // id, className, objectSize, maxObjectSize);
            // }
          }
        }
      }

      versionRows.putAll(dbRows);
    }

    return versionRows;
  }

  private Set<String> fillObjectVersionsFromTransactionCache(Set<String> allIdsToQuery,
      Map<String, DataRow> objectVersionsFromTransactionCache) {
    if (!useTransactionCache) {
      return allIdsToQuery;
    }
    StorageCacheTransactionHandler trHandler = getStorageTransactionHandlerIfExists();
    Set<String> allIdsToQueryFromDB;
    if (trHandler != null) {
      allIdsToQueryFromDB = new HashSet<>();
      for (String versionId : allIdsToQuery) {
        TableData<ObjectVersionDef> objectVersion = trHandler.getObjectVersion(versionId);
        if (objectVersion != null && objectVersion.size() == 1) {
          objectVersionsFromTransactionCache.put(versionId, objectVersion.rows().get(0));
        } else {
          allIdsToQueryFromDB.add(versionId);
        }
      }
    } else {
      allIdsToQueryFromDB = allIdsToQuery;
    }
    return allIdsToQueryFromDB;
  }

  private Set<String> fillObjectEntriesFromTransactionCache(Set<String> allUrisToQuery,
      Map<String, DataRow> objectEntriesFromTransactionCache) {
    if (!useTransactionCache) {
      return allUrisToQuery;
    }
    StorageCacheTransactionHandler trHandler = getStorageTransactionHandlerIfExists();
    Set<String> allUrisToQueryFromDB;
    if (trHandler != null) {
      allUrisToQueryFromDB = new HashSet<>();
      for (String uri : allUrisToQuery) {
        TableData<ObjectEntryDef> objectEntry = trHandler.getObjectEntry(uri);
        if (objectEntry != null && objectEntry.size() == 1) {
          objectEntriesFromTransactionCache.put(uri, objectEntry.rows().get(0));
        } else {
          allUrisToQueryFromDB.add(uri);
        }
      }
    } else {
      allUrisToQueryFromDB = allUrisToQuery;
    }
    return allUrisToQueryFromDB;
  }

  private long getObjectSize(BinaryData content) {
    if (content == null) {
      return 0;
    }

    try {
      return content.length();
    } catch (Exception e) {
      log.warn("Failed to get object size", e);
      return 0;
    }
  }

  private void addedToCache(String versionId, String className) {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      getOrRegisterStorageCacheTransactionHandler().addVersionToCachedInTransaction(versionId,
          className);
    }
  }

  /**
   * resource identifier for StorageObjectLock unlock handler
   */
  private static final String STORAGE_CACHE_HANDLER = "STORAGE_CACHE_HANDLER";

  protected void registerStorageCacheTransactionHandler() {
    if (!TransactionSynchronizationManager.hasResource(STORAGE_CACHE_HANDLER)) {
      TransactionSynchronizationManager
          .registerSynchronization(new StorageCacheTransactionHandler());
      TransactionSynchronizationManager.bindResource(STORAGE_CACHE_HANDLER, true);
    }
  }

  private StorageCacheTransactionHandler getOrRegisterStorageCacheTransactionHandler() {
    return TransactionUtils.getOrRegisterTransactionHandler(
        STORAGE_CACHE_HANDLER,
        StorageCacheTransactionHandler.class,
        StorageCacheTransactionHandler::new);
  }

  public void ensureTransactionHandler() {
    getOrRegisterStorageCacheTransactionHandler();
  }

  protected final class StorageCacheTransactionHandler
      implements TransactionSynchronization {

    private Map<String, Set<String>> cachedKeysPerClass = new HashMap<>();

    private Map<String, TableData<ObjectEntryDef>> objectEntriesToInsert = new HashMap<>();
    private Map<String, TableData<ObjectEntryDef>> objectEntriesToUpdate = new HashMap<>();
    private Map<String, TableData<ObjectVersionDef>> objectVersionsToInsert = new HashMap<>();
    private Map<String, TableData<ObjectVersionDef>> objectVersionsToUpdate = new HashMap<>();

    boolean isCompleted = false;

    public void addVersionToCachedInTransaction(String versionId, String className) {
      cachedKeysPerClass
          .computeIfAbsent(className != null ? className : "", k -> new HashSet<>())
          .add(versionId);
    }

    public void addObjectEntryToInsert(String uri, TableData<ObjectEntryDef> objectEntry) {
      if (isCompleted || !useTransactionCache) {
        // after completion there won't be another completion, must execute now
        Crud.create(objectEntry);
      } else {
        objectEntriesToInsert.put(uri, objectEntry);
      }
    }

    public void addObjectEntryToUpdate(String uri, TableData<ObjectEntryDef> objectEntry) {
      if (isCompleted || !useTransactionCache) {
        // after completion there won't be another completion, must execute now
        Crud.update(objectEntry);
      } else {
        objectEntriesToUpdate.put(uri, objectEntry);
      }
    }

    public void addObjectVersionToInsert(String uri, TableData<ObjectVersionDef> objectVersion) {
      if (isCompleted || !useTransactionCache) {
        // after completion there won't be another completion, must execute now
        Crud.create(objectVersion);
      } else {
        objectVersionsToInsert.put(uri, objectVersion);
      }
    }

    public void addObjectVersionToUpdate(String uri, TableData<ObjectVersionDef> objectVersion) {
      if (isCompleted || !useTransactionCache) {
        // after completion there won't be another completion, must execute now
        Crud.update(objectVersion);
      } else {
        objectVersionsToUpdate.put(uri, objectVersion);
      }
    }

    public TableData<ObjectEntryDef> getObjectEntry(String uriWithourVersion) {
      if (objectEntriesToUpdate.containsKey(uriWithourVersion)) {
        return objectEntriesToUpdate.get(uriWithourVersion);
      }
      return objectEntriesToInsert.get(uriWithourVersion);
    }

    public TableData<ObjectVersionDef> getObjectVersion(String versionId) {
      if (objectVersionsToUpdate.containsKey(versionId)) {
        return objectVersionsToUpdate.get(versionId);
      }
      return objectVersionsToInsert.get(versionId);
    }

    @Override
    public int getOrder() {
      // this should run last..
      return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void suspend() {
      TransactionSynchronizationManager.unbindResource(STORAGE_CACHE_HANDLER);
      log.trace("StorageCache suspend");
    }

    @Override
    public void resume() {
      TransactionSynchronizationManager.bindResource(STORAGE_CACHE_HANDLER, true);
      log.trace("StorageCache resume");
    }

    @Override
    public void beforeCommit(boolean readOnly) {
      try {
        if (!objectEntriesToInsert.isEmpty()) {
          Crud.create(append(objectEntryDef, objectEntriesToInsert));
          objectEntriesToInsert.clear();
        }
        if (!objectEntriesToUpdate.isEmpty()) {
          Crud.update(append(objectEntryDef, objectEntriesToUpdate));
          objectEntriesToUpdate.clear();
        }
        if (!objectVersionsToInsert.isEmpty()) {
          Crud.create(append(objectVersionDef, objectVersionsToInsert));
          objectVersionsToInsert.clear();
        }
        if (!objectVersionsToUpdate.isEmpty()) {
          Crud.update(append(objectVersionDef, objectVersionsToUpdate));
          objectVersionsToUpdate.clear();
        }
      } finally {
        isCompleted = true;
        clearIfEmpty(objectEntriesToInsert, "objectEntriesToInsert");
        clearIfEmpty(objectEntriesToUpdate, "objectEntriesToUpdate");
        clearIfEmpty(objectVersionsToInsert, "objectVersionsToInsert");
        clearIfEmpty(objectVersionsToUpdate, "objectVersionsToUpdate");
      }
    }

    private <T extends EntityDefinition> TableData<T> append(
        T entityDef,
        Map<String, TableData<T>> tableDatas) {
      TableData<T> tabledata = TableDatas
          .builder(entityDef, entityDef.allProperties())
          .build();
      tableDatas.values().forEach(td -> TableDatas.append(tabledata, td));
      if (!useTransactionCache) {
        log.warn("StorageTransactionHandler in use but useTransactionCache = false");
      }
      log.trace("Appended {} rows ({})", tabledata.size(), entityDef.entityDefName());
      return tabledata;
    }

    private void clearIfEmpty(Map<?, ?> map, String mapName) {
      if (!map.isEmpty()) {
        if (!useTransactionCache) {
          log.warn(
              "StorageTransactionHandler in use but useTransactionCache = false! Clearing not saved {} rows from {} before completion",
              map.size(), mapName);
        } else {
          log.debug("Clearing not saved {} rows from {} before completion",
              map.size(), mapName);
        }
        map.clear();
      }
    }

    @Override
    public void afterCompletion(int status) {
      if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
        for (Map.Entry<String, Set<String>> entry : cachedKeysPerClass.entrySet()) {
          String className = entry.getKey();
          Set<String> keys = entry.getValue();
          if (!keys.isEmpty()) {
            Cache<String, DataRow> cache = getClassCache(className);
            if (cache != null) {
              cache.invalidateAll(keys);
            }
            keys.clear();
          }
        }
      }

      cachedKeysPerClass.clear();

      if (status == TransactionSynchronization.STATUS_UNKNOWN) {
        log.warn("Transaction state is STATUS_UNKNOWN!");
      }
      TransactionSynchronizationManager.unbindResource(STORAGE_CACHE_HANDLER);
    }
  }

  private Map<String, DataRow> queryObjectEntries(List<UriInfo> uriInfos) {
    Set<String> uniqueBaseUris = uriInfos.stream()
        .map(info -> info.baseUri)
        .collect(Collectors.toSet());
    Map<String, DataRow> objectEntriesFromTransactionCache = new HashMap<>();
    Set<String> urisToQueryFromDB =
        fillObjectEntriesFromTransactionCache(uniqueBaseUris, objectEntriesFromTransactionCache);
    Map<String, DataRow> objectEntryRows;
    if (!urisToQueryFromDB.isEmpty()) {
      objectEntryRows = Crud.read(objectEntryDef)
          .select(objectEntryDef.allProperties())
          .where(objectEntryDef.uri().in(urisToQueryFromDB))
          .listData()
          .rows()
          .stream()
          .collect(Collectors.toMap(
              row -> row.get(objectEntryDef.uri()),
              row -> row));
      if (!objectEntriesFromTransactionCache.isEmpty()) {
        objectEntryRows.putAll(objectEntriesFromTransactionCache);
      }
    } else {
      objectEntryRows = objectEntriesFromTransactionCache;
    }

    if (objectEntryRows.size() != uniqueBaseUris.size()) {
      throw new ObjectNotFoundException(uniqueBaseUris, null, "Object not found.");
    }
    return objectEntryRows;
  }

  @Override
  public <T> List<T> readAll(Storage storage, String setName, Class<T> clazz) {
    List<URI> uris = readAllUris(storage, setName, clazz);
    return loadBatch(storage, uris, clazz).stream()
        .map(StorageObject::getObject)
        .collect(toList());
  }

  @Override
  public List<URI> readAllUris(Storage storage, String setName, String clazzName) {
    // Check if the given directory exists or not.
    ObjectDefinition<?> objectDefinition = objectDefinitionApi.definition(clazzName);

    String storageScheme = storage.getScheme();
    if (clazzName != null) {
      StorageSQLExtensionApi extensionApi = getExtensionApi(storageScheme, clazzName);
      if (extensionApi != null) {
        return extensionApi.readAllUris(self, storage, setName, clazzName);
      }
    }

    String setPath =
        storageScheme + StringConstant.COLON + StringConstant.SLASH + objectDefinition.getAlias()
            + (Strings.isBlank(setName) ? StringConstant.EMPTY
                : StringConstant.SLASH
                    + setName);

    TableData<ObjectEntryDef> objectList;
    try {
      if (log.isTraceEnabled()) {
        log.trace("readAll: setName={}", setName);
      }
      objectList = Crud.read(objectEntryDef)
          .select(objectEntryDef.uri())
          .where(objectEntryDef.uri().like(setPath + StringConstant.PERCENT))
          .listData();
      // TODO check if transaction cache is available and uris present only there
      List<URI> result = objectList.rows().stream()
          .map(r -> UriUtils.asUri(r.get(objectEntryDef.uri())))
          .collect(toList());
      if (log.isTraceEnabled()) {
        log.trace("readAll: setName={} size{}", setName, result.size());
      }
      return result;
    } catch (Exception e) {
      log.debug("Unable to read all the objects from the set.", e);
      return Collections.emptyList();
    }
  }

  @Override
  public List<URI> readOldests(Storage storage, String setName, String clazzName) {
    // Check if the given directory exists or not.
    ObjectDefinition<?> objectDefinition = objectDefinitionApi.definition(clazzName);

    String storageScheme = storage.getScheme();
    if (clazzName != null) {
      StorageSQLExtensionApi extensionApi = getExtensionApi(storageScheme, clazzName);
      if (extensionApi != null) {
        throw new UnsupportedOperationException(
            "The readOldests is not implemented for " + extensionApi);
      }
    }

    String setPath =
        storageScheme + StringConstant.COLON + StringConstant.SLASH + objectDefinition.getAlias()
            + (Strings.isBlank(setName) ? StringConstant.EMPTY
                : StringConstant.SLASH
                    + setName);

    try {
      if (log.isTraceEnabled()) {
        log.trace("readAll: setName={}", setName);
      }
      TableData<ObjectEntryDef> objectList = Crud.read(objectEntryDef)
          .select(objectEntryDef.uri(), objectEntryDef.createdAt())
          .where(objectEntryDef.uri().like(setPath + StringConstant.PERCENT))
          .order(objectEntryDef.createdAt())
          .limit(500)
          .listData();
      // TODO check if transaction cache is available and uris present only there
      if (objectList.isEmpty()) {
        return Collections.emptyList();
      }
      OffsetDateTime firstCreatedAt = null;
      List<URI> result = new ArrayList<>();
      for (DataRow row : objectList.rows()) {
        if (firstCreatedAt == null) {
          firstCreatedAt = row.get(objectEntryDef.createdAt());
        }
        OffsetDateTime createdAt = row.get(objectEntryDef.createdAt());
        if (firstCreatedAt.getYear() == createdAt.getYear()
            && firstCreatedAt.getMonth() == createdAt.getMonth()
            && firstCreatedAt.getDayOfMonth() == createdAt.getDayOfMonth()
            && firstCreatedAt.getHour() == createdAt.getHour()
            && firstCreatedAt.getMinute() == createdAt.getMinute()
            && firstCreatedAt.getSecond() == createdAt.getSecond()) {
          result.add(URI.create(row.get(objectEntryDef.uri())));
        } else {
          break;
        }
      }
      return result;
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  @Override
  public boolean move(Storage storage, URI uri, URI targetUri) {
    ObjectDefinition<?> definition = objectDefinitionApi.definition(uri);
    if (definition != null) {
      StorageSQLExtensionApi extensionApi =
          getExtensionApi(storage.getScheme(), definition.getQualifiedName());
      if (extensionApi != null) {
        return extensionApi.move(uri, targetUri);
      }
    }

    // It is a simple update...
    DataRow objectEntryRow = queryObjectEntry(uri, true, null);
    if (objectEntryRow != null) {
      objectEntryRow.set(objectEntryDef.uri(), getUriString(targetUri));
      Crud.update(objectEntryRow.tableData());
      return true;
    }
    return false;
  }

  @Override
  public List<URI> remove(Collection<URI> urisToRemove) {
    // Deleting the still existing lock files, then the versions and at last the netry itself.
    // Select all the entries for update.
    if (urisToRemove == null) {
      return Collections.emptyList();
    }
    List<String> uris = urisToRemove.stream().filter(Objects::nonNull)
        .map(u -> getUriWithoutVersion(u).toString()).collect(toList());
    TableData<ObjectEntryDef> objectEntries =
        Crud.read(objectEntryDef).select(objectEntryDef.id(), objectEntryDef.uri())
            .where(objectEntryDef.uri().in(uris))
            .tryLock().listData();
    TableData<ObjectEntryLockDef> lockTableData =
        TableDatas.builder(objectEntryLockDef, objectEntryLockDef.objectUri()).build();
    DataColumn<String> uriCol = lockTableData.getColumn(objectEntryLockDef.objectUri());
    for (String uri : uris) {
      lockTableData.addRow().set(uriCol, uri);
    }
    Crud.delete(lockTableData);
    // Now delete the versions.
    Crud.delete(Crud.read(objectVersionDef).select(objectVersionDef.versionId())
        .where(objectVersionDef.entryId().in(objectEntries.values(objectEntryDef.id())))
        .listData());
    // And at last delete the object entries.
    Crud.delete(objectEntries);
    return objectEntries.values(objectEntryDef.uri()).stream().map(s -> URI.create(s))
        .collect(toList());
  }

  private StorageObjectData readObjectDataFromRow(URI objectUri, DataRow objectRow) {
    return new StorageObjectData().className(objectRow.get(objectEntryDef.className()))
        .uri(objectUri);
  }

  private final DataRow queryObjectEntry(URI objectUri, boolean lock, PropertySet properties) {
    DataRow objectRow;
    String uriWithoutVersion = getUriString(getUriWithoutVersion(objectUri));
    TableData<ObjectEntryDef> objectEntry =
        getOrQueryObjectEntry(uriWithoutVersion, lock, properties);
    if (objectEntry.size() == 1) {
      objectRow = objectEntry.rows().get(0);
    } else {
      objectRow = null;
    }
    return objectRow;
  }

  /**
   * Return the object version object.
   *
   * @param id The id of the object entry.
   * @param version The version of the object.
   * @return
   */
  private final ObjectVersion readObjectVersion(Long id, Long version, URI uri) {

    Optional<DataRow> objectRow = queryObjectVersion(id, version, true, uri);
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
  private Optional<DataRow> queryObjectVersion(Long id, Long version, boolean skipContent,
      URI uri) {
    UriInfo uriInfo = new UriInfo(uri, id, version);
    Map<String, DataRow> rows = queryObjectVersions(Arrays.asList(uriInfo));
    return Optional.of(rows.get(uriInfo.versionId));
    // boolean isSingleVersion = isSingleVersion(uri);
    // if (defaultCache == null) {
    // DataRow result = queryObjectVersionInner(id, version, skipContent);
    // return result == null ? Optional.empty() : Optional.of(result);
    // }
    //
    // if (id == null || version == null) {
    // return Optional.empty();
    // }
    // if (isSingleVersion) {
    // // skip cache
    // DataRow result = queryObjectVersionInner(id, version, skipContent);
    // return result == null ? Optional.empty() : Optional.of(result);
    // }
    // String cacheId = id + "-" + version;
    // DataRow cachedRow = versionContentCache.getIfPresent(cacheId);
    // if (cachedRow != null) {
    // if (log.isTraceEnabled()) {
    // log.trace("Cache - hit, {} ", cacheId);
    // }
    // return Optional.of(cachedRow);
    // }
    // DataRow result;
    // if (skipContent) {
    // // don't cache when skipContent
    // if (log.isTraceEnabled()) {
    // log.trace("Cache - wont cache, {}", cacheId);
    // }
    // result = queryObjectVersionInner(id, version, skipContent);
    // } else {
    // try {
    // result = versionContentCache.get(cacheId,
    // () -> {
    // if (log.isTraceEnabled()) {
    // log.trace("Cache - load, size:{} ", versionContentCache.size());
    // }
    // DataRow row = queryObjectVersionInner(id, version, skipContent);
    // addedToCache(cacheId);
    // return row;
    // });
    // } catch (ExecutionException e) {
    // log.warn("Unable to load to cache", e);
    // result = null;
    // }
    // }
    //
    // return result == null ? Optional.empty() : Optional.of(result);
  }

  // private DataRow queryObjectVersionInner(Long id, Long version, boolean skipContent) {
  // try {
  // PropertySet properties = objectVersionDef.allProperties();
  // if (skipContent) {
  // properties.remove(objectVersionDef.objectContent());
  // }
  // String versionId = createVersionId(id, version);
  //
  // if (log.isTraceEnabled()) {
  // log.trace("queryObjectVersion: versionId={}, version={}, skipContent={}", versionId);
  // }
  // return Crud.read(objectVersionDef)
  // .select(properties)
  // .where(objectVersionDef.versionId().eq(versionId))
  // .onlyOne().get();
  // } catch (Exception e) {
  // return null;
  // }
  // }

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
    BinaryData content = objectRow.get(objectVersionDef.objectContent());
    String rowCompressionType = objectRow.get(objectVersionDef.objectContentCompressionType());

    if (!ObjectUtils.isEmpty(rowCompressionType)) {
      // we don't check enableCompression, because if it is compressed, we must decompress is
      return decompressContent(content, getCompressionType(rowCompressionType));
    }
    return content;
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

    Optional<DataRow> optObjectVersion = queryObjectVersion(id, version, false, versionUri);
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

  private final StorageObjectRelationData loadRelationData(Long entryId, Long relationVersion,
      URI uri) {
    if (relationVersion == null) {
      return null;
    }
    Optional<DataRow> optObjectVersion = queryObjectVersion(entryId, relationVersion, false, uri);
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

  @Override
  public ObjectHistoryIterator objectHistory(URI uri, ObjectDefinition<?> definition) {
    if (definition == null) {
      return null;
    }

    DataRow objectRow = queryObjectEntry(uri, false, null);
    if (objectRow == null) {
      return null;
    }
    StorageObjectData objectData = readObjectDataFromRow(uri, objectRow)
        .currentVersion(readObjectVersion(
            objectRow.get(objectEntryDef.id()),
            objectRow.get(objectEntryDef.version()),
            uri));
    ObjectVersion currentObjectVersion = objectData.getCurrentVersion();
    if (currentObjectVersion.getSerialNoData() == null) {
      return null;
    }

    long serialNoDataMax = currentObjectVersion.getSerialNoData();

    return new ObjectHistoryIterator() {

      @Override
      public Iterator<StorageObjectHistoryEntry> iterator() {
        return new Iterator<>() {

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

    DataRow objectRow = queryObjectEntry(uri, false, null);
    if (objectRow == null) {
      return null;
    }
    StorageObjectData objectData = readObjectDataFromRow(uri, objectRow)
        .currentVersion(readObjectVersion(
            objectRow.get(objectEntryDef.id()),
            objectRow.get(objectEntryDef.version()),
            uri));
    ObjectVersion currentObjectVersion = objectData.getCurrentVersion();
    if (currentObjectVersion.getSerialNoData() == null) {
      return null;
    }

    long serialNoDataMax = currentObjectVersion.getSerialNoData();

    return new ObjectHistoryIterator() {

      private long i = serialNoDataMax + 1;

      @Override
      public Iterator<StorageObjectHistoryEntry> iterator() {
        return new Iterator<>() {

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

  private final StorageSQLExtensionApi getExtensionApi(String shema, String qualifiedName) {
    if (extensions != null) {
      return extensionsCache.get(extensionId(shema, qualifiedName));
    }
    return null;
  }

  private CompressionType getCompressionType() {
    if (compressionType == null) {
      compressionType = getCompressionType(compressionTypeString);
    }
    return compressionType;
  }

  private CompressionType getCompressionType(String compressionTypeString) {
    switch (compressionTypeString) {
      case "zlib":
        return CompressionType.ZLIB;
      case "gzip":
      default:
        return CompressionType.GZIP;
    }
  }

  private BinaryData compressContent(StorageObject<?> object, BinaryData objectContent) {
    if (enableCompression && objectContent != null && objectContent.isCompressOnSave()) {
      try {
        objectContent = BinaryDataCompressionUtil.compress(objectContent, getCompressionType());
      } catch (IOException e) {
        log.warn("Failed to compress object content for {}, saving uncompressed",
            object.getUri(), e);
      }
    }
    return objectContent;
  }

  private BinaryData decompressContent(BinaryData content, CompressionType compressionType) {
    if (content != null) {
      try {
        content = BinaryDataCompressionUtil.decompress(content, compressionType);
      } catch (IOException e) {
        log.error("Failed to decompress object content, this might indicate data corruption", e);
      }
    }
    return content;
  }


}
