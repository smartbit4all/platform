package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.storage.bean.ObjectMap;
import org.smartbit4all.api.storage.bean.ObjectMapRequest;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.storage.bean.StorageObjectReference;
import org.smartbit4all.api.storage.bean.StorageSettings;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;

/**
 *
 * The Storage is a logical unit managing a scheme of data. Every Api can use one or more scheme as
 * object storage. The Storage logical name is the scheme that is used to access the Storage by the
 * {@link StorageApi}. In the API implementations we must use this name.
 *
 * The Storage is always rely on an ObjectStorage implementations responsible for the atomic
 * transaction about the {@link StorageObject}. The uri (Unified Resource Identifier) of the object
 * must be situated in the current physical storage attached to this Storage. This routing relies on
 * the registry of the {@link StorageApi} and based on the scheme of the URI. The physical
 * {@link ObjectStorage} is hidden behind the Storage.
 *
 * @author Zoltan Szegedi
 *
 */
public final class Storage {

  private static final String SETTINGS = "settings";

  /**
   * The name of the storage transaction manager.
   */
  public static final String STORAGETX = "storageTX";

  /**
   * This is the physical storage that is responsible for save and load of the objects.
   */
  private ObjectStorage objectStorage;

  /**
   * The scheme managed by the given logical storage. This is used as naming of the
   */
  private String scheme;

  /**
   * The object api is responsible for accessing the {@link ObjectDefinition}s of the current
   * application context.
   */
  private ObjectDefinitionApi objectDefinitionApi;

  /**
   * The "global" setting uri for the storage schema. Used to save global reference objects, lists
   * and maps.
   */
  private URI settingsuri;

  /**
   * These are the version policies that can be used in the storage object to instruct the object
   * storage about the new and new versions of an object. The default is the
   * {@link VersionPolicy#ALLVERSION} to ensure the maximal audit level.
   */
  private VersionPolicy versionPolicy = VersionPolicy.ALLVERSION;

  /**
   * The UUID part of the URI ends with this post fix if the given object is a single version
   * object. These objects have only one actual value and can have one value in case of
   * modification. These are typically technical records used for administrating the application
   * mechanisms.
   */
  public static final String SINGLE_VERSION_URI_POSTFIX = "-s";

  /**
   * Construct a new storage that is a logical schema for the storage system.
   *
   * @param scheme
   * @param objectDefinitionApi
   * @param objectStorage
   */
  public Storage(String scheme, ObjectDefinitionApi objectDefinitionApi,
      ObjectStorage objectStorage) {
    this(scheme, objectDefinitionApi, objectStorage, VersionPolicy.ALLVERSION);
  }

  /**
   * Construct a new storage that is a logical schema for the storage system.
   *
   * @param scheme
   * @param objectDefinitionApi
   * @param objectStorage
   * @param versionPolicy
   */
  public Storage(String scheme, ObjectDefinitionApi objectDefinitionApi,
      ObjectStorage objectStorage,
      VersionPolicy versionPolicy) {
    this.objectStorage = objectStorage;
    this.objectDefinitionApi = objectDefinitionApi;
    this.scheme = scheme;
    this.versionPolicy = versionPolicy;
  }

  /**
   * Constructs a new instance of the given {@link Class}.
   *
   * @param <T>
   * @param clazz The class that represents a domain object.
   * @return A new Instance of the {@link StorageObject} that already has an URI! If we save this
   *         without {@link StorageObject#setObject(Object)} then it will be an empty object but we
   *         can subscribe for it's events.
   */
  public <T> StorageObject<T> instanceOf(Class<T> clazz) {
    return instanceOf(clazz, null);
  }

  /**
   * Constructs a new instance of the given {@link Class}.
   *
   * @param <T> the Java type of the persisted object
   * @param clazz The class that represents a domain object.
   * @param setName String
   * @return A new Instance of the {@link StorageObject} that already has an URI! If we save this
   *         without {@link StorageObject#setObject(Object)} then it will be an empty object but we
   *         can subscribe for it's events.
   */
  public <T> StorageObject<T> instanceOf(Class<T> clazz, String setName) {
    ObjectDefinition<T> objectDefinition = objectDefinitionApi.definition(clazz);
    return fromDefinition(objectDefinition, setName);

  }

  /**
   * Constructs a new {@link StorageObject} instance based on a given {@link ObjectDefinition}.
   * 
   * <p>
   * Some domain objects may not have a corresponding Java class, if their definition is dynamically
   * created/modified during runtime. Supplying an object definition directly ensures the storage
   * mechanism may succeed for these objects as well.
   * 
   * @param <T> the Java type of the persisted object, if any
   * @param objectDefinition the {@link ObjectDefinition} of the persisted domain object, not null
   * @return a new {@link StorageObject} instance with an {@code URI}, but without any internal
   *         object set
   */
  public <T> StorageObject<T> fromDefinition(ObjectDefinition<T> objectDefinition) {
    return fromDefinition(objectDefinition, null);
  }

  /**
   * Constructs a new {@link StorageObject} instance based on a given {@link ObjectDefinition}.
   * 
   * <p>
   * Some domain objects may not have a corresponding Java class, if their definition is dynamically
   * created/modified during runtime. Supplying an object definition directly ensures the storage
   * mechanism may succeed for these objects as well.
   * 
   * <p>
   * Persistence operations conducted through the returned instance operate on the set identified by
   * the provided {@code setName}.
   * 
   * @param <T> the Java type of the persisted object, if any
   * @param objectDefinition the {@link ObjectDefinition} of the persisted domain object, not null
   * @param setName String
   * @return a new {@link StorageObject} instance with an {@code URI}, but without any internal
   *         object set
   */
  public <T> StorageObject<T> fromDefinition(ObjectDefinition<T> objectDefinition, String setName) {
    if (!objectDefinition.hasUri()) {
      throw new IllegalArgumentException(
          "Unable to use the " + objectDefinition.getQualifiedName()
              + " as domain object because the lack of URI property!");
    }
    StorageObject<T> storageObject = new StorageObject<>(objectDefinition, this);
    // At this point we already know the unique URI that can be used to refer from other objects
    // also.
    UUID uuid = UUID.randomUUID();
    storageObject.setUuid(uuid);
    if (!objectDefinition.isExplicitUri()) {
      storageObject.setUri(constructUri(objectDefinition, uuid, setName));
    }
    return storageObject;
  }

  /**
   * Constructs a new instance of the given {@link Class}.
   *
   * @param className The class name that represents a domain object.
   * @return A new Instance of the {@link StorageObject} that already has an URI! If we save this
   *         without {@link StorageObject#setObject(Object)} then it will be an empty object but we
   *         can subscribe for it's events.
   */
  public StorageObject create(String className) {
    ObjectDefinition objectDefinition = objectDefinitionApi.definition(className);
    StorageObject storageObject = new StorageObject<>(objectDefinition, this);
    // At this point we already know the unique URI that can be used to refer from other objects
    // also.
    return storageObject;
  }

  /**
   * Save the given {@link StorageObject}.
   *
   * @param <T>
   * @param object The storage object that must not be null otherwise a {@link NullPointerException}
   *        runtime exception is going to be thrown..
   * @return The URI of the saved object.
   */
  public <T> URI save(StorageObject<T> object) {
    StorageObject<?> save = objectStorage.save(object);
    return save != null ? save.getUri() : null;
  }

  /**
   * Save the given {@link StorageObject}.
   *
   * @param <T>
   * @param object The storage object that must not be null otherwise a {@link NullPointerException}
   *        runtime exception is going to be thrown..
   * @return The versioned URI of the saved object.
   */
  public <T> URI saveVersion(StorageObject<T> object) {
    StorageObject<?> save = objectStorage.save(object);
    return save != null ? save.getVersionUri() : null;
  }

  /**
   * Save the given object as new into the storage. If we save a new instance then there is no need
   * to use the {@link StorageObject} because there will no concurrent issue or any other problem.
   *
   * @param <T>
   * @param object The object to save. The URI will be generated so there is no need and no
   *        influence of the previously set URI! Don't set any URI or be aware of skipping this.
   * @return The URI of the newly created object.
   */
  public <T> URI saveAsNew(T object) {
    if (object == null) {
      return null;
    }
    StorageObject<T> newObject = saveAsNewObject(object, null);
    return newObject != null ? newObject.getUri() : null;
  }

  /**
   * Save the given object as new into the storage. If we save a new instance then there is no need
   * to use the {@link StorageObject} because there will no concurrent issue or any other problem.
   *
   * @param <T>
   * @param object The object to save. The URI will be generated so there is no need and no
   *        influence of the previously set URI! Don't set any URI or be aware of skipping this.
   * @return The URI of the newly created object.
   */
  public <T> URI saveAsNew(T object, String setName) {
    if (object == null) {
      return null;
    }
    StorageObject<T> newObject = saveAsNewObject(object, setName);
    return newObject != null ? newObject.getUri() : null;
  }

  /**
   * Save the given object as new into the storage. If we save a new instance then there is no need
   * to use the {@link StorageObject} because there will no concurrent issue or any other problem.
   *
   * @param <T>
   * @param object The object to save. The URI will be generated so there is no need and no
   *        influence of the previously set URI! Don't set any URI or be aware of skipping this.
   * @return The newly created object.
   */
  @SuppressWarnings("unchecked")
  public <T> StorageObject<T> saveAsNewObject(T object) {
    return saveAsNewObject(object, null);
  }

  /**
   * Save the given object as new into the storage. If we save a new instance then there is no need
   * to use the {@link StorageObject} because there will no concurrent issue or any other problem.
   *
   * @param <T>
   * @param object The object to save. The URI will be generated so there is no need and no
   *        influence of the previously set URI! Don't set any URI or be aware of skipping this.
   * @return The URI of the newly created object.
   */
  @SuppressWarnings("unchecked")
  public <T> StorageObject<T> saveAsNewObject(T object, String setName) {
    if (object == null) {
      return null;
    }
    StorageObject<T> storageObject = (StorageObject<T>) instanceOf(object.getClass(), setName);
    storageObject.setObject(object);
    storageObject.setSkipLock(true);
    return (StorageObject<T>) objectStorage.save(storageObject);
  }

  /**
   * The add to set constructs a new {@link StorageObjectReference} and set its objectUri to the
   * given object URI. This new object is saved into the set we give.
   *
   * @param setName The name of the set.
   * @param objectUri The object URI to save as reference.
   * @return The URI of the newly created reference.
   */
  public URI addToSet(String setName, URI objectUri) {
    return saveAsNew(new StorageObjectReference().objectUri(objectUri), setName);
  }

  public URI moveToSet(URI uri, String setName) {
    if (uri != null && exists(uri)) {
      URI movedUri = constructUriForSet(uri, setName);
      return objectStorage.move(uri, movedUri) ? movedUri
          : null;
    }
    return null;
  }

  /**
   * This function is locking and loading the object identified by the object uri. If the update
   * function produce null as the result of the {@link Function#apply(Object)} function then it will
   * skip the update.
   *
   * @param <T>
   * @param objectUri
   * @param clazz
   * @param update
   * @return The URI of the new version.
   */
  public <T> URI update(URI objectUri, Class<T> clazz, UnaryOperator<T> update) {
    return update(objectUri, clazz, null, update);
  }

  public <T> URI update(URI objectUri, Class<T> clazz, ObjectVersion version,
      UnaryOperator<T> update) {
    StorageObjectLock lock = getLock(objectUri);
    lock.lock();
    try {
      StorageObject<T> so =
          load(objectUri, clazz).asMap();
      if (version != null &&
          !version.getSerialNoData().equals(so.getVersion().getSerialNoData())) {
        // TODO check whole version, but createdAt is buggy
        throw new IllegalStateException("Object version mismatch, unable to save!");
      }
      T object = update.apply(so.getObject());
      if (object != null) {
        so.setObject(object);
        return saveVersion(so);
      }
    } finally {
      lock.unlock();
    }
    return null;
  }

  /**
   * Load all the object identified by the uris from the uri list.
   *
   * @param <T> The class is a typed parameter.
   * @param uris
   * @param clazz The class to define the required object.
   * @return The list of the {@link StorageObject}s found.
   * @throws ObjectNotFoundException if any of the uris is not found on the storage.
   */
  public <T> List<StorageObject<T>> load(List<URI> uris, Class<T> clazz) {
    if (uris == null || uris.isEmpty()) {
      return Collections.emptyList();
    }

    return objectStorage.load(this, uris, clazz);
  }

  /**
   * Load the object with the given URI.
   *
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   * @param options When loading we can instruct the loading to retrieve the necessary data only.
   *
   */
  public <T> StorageObject<T> load(URI uri, Class<T> clazz,
      StorageLoadOption... options) {
    return objectStorage.load(this, uri, clazz, options);
  }

  /**
   * Load the object with the given URI.
   *
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param options When loading we can instruct the loading to retrieve the necessary data only.
   * @return We try to identify the class from the URI itself.
   */
  public StorageObject<?> load(URI uri, StorageLoadOption... options) {
    return objectStorage.load(this, uri, options);
  }

  /**
   * Load the objects with the given URI.
   *
   * @param uris The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   * @param options When loading we can instruct the loading to retrieve the necessary data only.
   */
  public <T> List<StorageObject<T>> load(List<URI> uris, Class<T> clazz,
      StorageLoadOption... options) {
    return objectStorage.load(this, uris, clazz, options);
  }

  /**
   * Read the given object identified by the URI. We can not initiate a transaction with the result
   * of the read! Use the load instead.
   *
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   */
  public <T> T read(URI uri, Class<T> clazz) {
    return objectStorage.read(this, uri, clazz);
  }

  public <T> List<T> readAll(String setName, Class<T> clazz) {
    return objectStorage.readAll(this, setName, clazz);
  }

  /**
   * Use it carefully it will read all the objects with the given class.
   *
   * @param <T>
   * @param clazz The data bean we are looking for.
   * @return All the objects from the storage.
   */
  public <T> List<T> readAll(Class<T> clazz) {
    return objectStorage.readAll(this, null, clazz);
  }

  /**
   * Use it carefully it will read all the objects with the given class.
   *
   * @param <T>
   * @param clazz The data bean we are looking for.
   * @return All the objects from the storage.
   */
  public <T> List<URI> readAllUris(Class<T> clazz) {
    return objectStorage.readAllUris(this, null, clazz);
  }

  /**
   * This will read all the references from the storage set.
   *
   * @param <T>
   * @param setName
   * @param clazz
   * @return
   */
  public <T> List<T> readAllReferenceFromSet(String setName, Class<T> clazz) {
    List<StorageObjectReference> allReferences =
        objectStorage.readAll(this, setName, StorageObjectReference.class);
    List<T> result = new ArrayList<>();
    for (StorageObjectReference storageObjectReference : allReferences) {
      result.add(read(storageObjectReference.getObjectUri(), clazz));
    }
    return result;
  }

  public boolean exists(URI uri) {
    return objectStorage.exists(uri);
  }

  public boolean existsAll(List<URI> uris) {
    return !(uris.stream().anyMatch(uri -> !objectStorage.exists(uri)));
  }

  public List<URI> findExistings(List<URI> uris) {
    return Collections.emptyList();
  }

  /**
   * Read the given object identified by the URI. We can not initiate a transaction with the result
   * of the read! Use the load instead.
   *
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @return We try to identify the class from the URI itself.
   */
  public Object read(URI uri) {
    return objectStorage.read(this, uri);
  }

  /**
   * Read the given objects identified by the URI. We can not initiate a transaction with the result
   * of the read! Use the load instead.
   *
   * @param uris The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   */
  public <T> List<T> read(List<URI> uris, Class<T> clazz) {
    return objectStorage.read(this, uris, clazz);
  }

  /**
   * The archive is an atomic function that moves the given object all together into the default
   * archive set of this storage. Normally it is the storagescheme:/archive... URI. With this
   * operation the object won't exist any more so we have to save the archived URI if it is
   * necessary.
   *
   * @param uri
   * @return The URI of the archived object. If it's null then the move was not successful.
   */
  public URI archive(URI uri) {
    if (uri != null && exists(uri)) {
      URI constructArchiveUri = constructUriForSet(uri, "archive");
      return objectStorage.move(uri, constructArchiveUri) ? constructArchiveUri
          : null;
    }
    return null;
  }

  /**
   * Creates an {@link ObjectHistoryIterator} that can iterate through the
   * {@link StorageObjectHistoryEntry}s of the object found with the given uri, making available to
   * investigate the full history of that object. In order from the oldest to the most recent.
   *
   * @param uri
   * @return
   */
  public ObjectHistoryIterator objectHistory(URI uri) {
    ObjectDefinition<?> definition = objectDefinitionApi.definition(uri);
    return objectStorage.objectHistory(uri, definition);
  }

  /**
   * Creates an {@link ObjectHistoryIterator} that can iterate through the
   * {@link StorageObjectHistoryEntry}s of the object found with the given uri, making available to
   * investigate the full history of that object. In order from the most recent to the oldest.
   *
   * @param uri
   * @return
   */
  public ObjectHistoryIterator objectHistoryReverse(URI uri) {
    ObjectDefinition<?> definition = objectDefinitionApi.definition(uri);
    return objectStorage.objectHistoryReverse(uri, definition);
  }

  public final String getScheme() {
    return scheme;
  }

  /**
   * Constructs the {@link StorageSettings} object if it doesn't exist.
   *
   * @return Return the URI of the existing {@link StorageSettings} object URI.
   */
  public final URI settingsUri() {
    URI uri = getSettingsUri();
    if (!objectStorage.exists(uri)) {
      constructSettingsObject(uri);
    }
    return uri;
  }

  public final StorageObject<StorageSettings> settings() {
    URI uri = getSettingsUri();
    // TODO Lock the settings!!!! Optimistic lock will be enough.
    StorageObject<StorageSettings> storageObject;
    try {
      storageObject =
          objectStorage.load(this, uri, StorageSettings.class);
    } catch (ObjectNotFoundException e) {
      storageObject = constructSettingsObject(uri);
    }
    return storageObject;
  }

  private final StorageObject<StorageSettings> constructSettingsObject(URI uri) {
    StorageObject<StorageSettings> storageObject;
    // It's missing now so we have to create is.
    storageObject = instanceOf(StorageSettings.class);
    // At this point we already know the unique URI that can be used to refer from other objects
    // also.
    storageObject.setUri(uri);
    storageObject.setObject(new StorageSettings().schemeName(scheme));
    save(storageObject);
    return storageObject;
  }

  /**
   * The basic implementation of the URI creation. It's rather a logical URI that is bound to the
   * physical location with a special mapping. The URI looks like the following:
   * scheme:/object_class/creation_time/UUID This can be managed by any {@link ObjectStorage}
   * implementation the scheme can separate the different logical units, the first item in the path
   * identifies the object type (by the class name) and the rest of the path is the creation time in
   * year/month/day/hour/min format. The final item is a UUID that should be unique individually
   * also. In a running application this URI always identifies a given object.
   */
  private final URI constructUri(ObjectDefinition<?> objectDefinition, UUID uuid, String setName) {
    LocalDateTime now = LocalDateTime.now();
    return URI.create(scheme + StringConstant.COLON + StringConstant.SLASH
        + objectDefinition.getAlias() + StringConstant.SLASH
        + (setName == null ? StringConstant.EMPTY : setName + StringConstant.SLASH)
        + now.getYear() + StringConstant.SLASH + now.getMonthValue() + StringConstant.SLASH
        + now.getDayOfMonth() + StringConstant.SLASH + now.getHour() + StringConstant.SLASH
        + now.getMinute() + StringConstant.SLASH
        + uuid + (versionPolicy == VersionPolicy.SINGLEVERSION ? SINGLE_VERSION_URI_POSTFIX
            : StringConstant.EMPTY));
  }

  private final URI constructUriForSet(URI uri, String setName) {
    return UriUtils.createUri(uri.getScheme(), null, StringConstant.SLASH + setName + uri.getPath(),
        null);
  }

  /**
   * Retrieve and generate if missing the setting object uri.
   *
   * @return Teh settings object uri of the storage.
   */
  private final URI getSettingsUri() {
    if (settingsuri == null) {
      ObjectDefinition<StorageSettings> objectDefinition =
          objectDefinitionApi.definition(StorageSettings.class);
      settingsuri = URI.create(scheme + StringConstant.COLON + StringConstant.SLASH
          + objectDefinition.getAlias() + StringConstant.SLASH
          + SETTINGS);
    }
    return settingsuri;
  }

  /**
   * Get or create the attached map with the given name.
   *
   * @param objectUri The object to attach.
   * @param mapName The name of the map. An application level content that is well-known by the
   *        parties.
   * @return The current version of the object map.
   *
   *         TODO Later on add some subscription.
   */
  public ObjectMap getAttachedMap(URI objectUri, String mapName) {

    return getOrCreateReferenceObject(objectUri, om -> {
      om.setName(mapName);
    }, ObjectMap.class, mapName).getObject();

  }

  /**
   * Reads the valid objects from the attached map.
   *
   * @param <T> The type of the objects in the map.
   * @param objectUri The uri of the object the amp is attached to.
   * @param mapName The name of the map.
   * @return The list of ther valid objects referred by the map.
   */
  public <T> List<T> readAttachedMap(URI objectUri, String mapName, Class<T> clazz) {
    ObjectMap attachedMap = getAttachedMap(objectUri, mapName);
    Map<String, URI> toRemove = null;
    List<T> result = null;
    for (Entry<String, URI> entry : attachedMap.getUris().entrySet()) {
      try {
        T read = read(entry.getValue(), clazz);
        if (result == null) {
          result = new ArrayList<>();
        }
        result.add(read);
      } catch (ObjectNotFoundException e) {
        // If the object is not exists then remove it from the map.
        if (toRemove == null) {
          toRemove = new HashMap<>();
        }
        toRemove.put(entry.getKey(), entry.getValue());
      }
    }
    if (toRemove != null) {
      updateAttachedMap(objectUri, new ObjectMapRequest().urisToRemove(toRemove));
    }
    return result == null ? Collections.emptyList() : result;
  }

  /**
   * We can add and remove items to the named object map. If the map doesn't exist then it will
   * create it first.
   *
   * @param objectUri The object to attach.
   * @param request The request that contains the name or the uri.
   */
  public void updateAttachedMap(URI objectUri, ObjectMapRequest request) {
    if (request == null || ((request.getUrisToAdd() == null || request.getUrisToAdd().isEmpty())
        && (request.getUrisToRemove() == null || request.getUrisToRemove().isEmpty()))) {
      return;
    }

    StorageObjectLock lock = getLock(objectUri);
    lock.lock();
    try {
      StorageObject<ObjectMap> mapObject = getOrCreateReferenceObject(objectUri, om -> {
        om.setName(request.getMapName());
      }, ObjectMap.class, request.getMapName());

      if (request.getUrisToAdd() != null) {
        mapObject.getObject().getUris().putAll(request.getUrisToAdd());
      }
      if (request.getUrisToRemove() != null) {
        for (Entry<String, URI> entry : request.getUrisToRemove().entrySet()) {
          mapObject.getObject().getUris().remove(entry.getKey());
        }
      }

      save(mapObject);
    } finally {
      lock.unlock();
    }

  }

  /**
   * This function can find or create the attached reference object of an object identified by the
   * uri.
   *
   * @param <T>
   * @param objectUri The master object uri
   * @param parameterSetters The initialization of the newly created referred object.
   * @param clazz The class of the referred object.
   * @param referenceName The unique name of the reference inside the master object.
   * @return
   */
  public final <T> StorageObject<T> getOrCreateReferenceObject(URI objectUri,
      Consumer<T> parameterSetters, Class<T> clazz, String referenceName) {
    StorageObjectLock objectLock = getLock(objectUri);
    objectLock.lock();
    try {
      StorageObject<?> storageObject =
          load(objectUri, StorageLoadOption.skipData());
      StorageObjectReferenceEntry referenceEntry = storageObject.getReference(referenceName);
      StorageObject<T> newObjectSo;
      if (referenceEntry == null) {
        // Construct the ObjectMap.
        newObjectSo = instanceOf(clazz);
        T newObject;
        try {
          newObject = clazz.getConstructor().newInstance();
        } catch (Exception e) {
          throw new IllegalArgumentException("Unable to instanciate the " + clazz + " bean.", e);
        }
        if (parameterSetters != null) {
          parameterSetters.accept(newObject);
        }
        newObjectSo.setObject(newObject);
        URI uri = save(newObjectSo);
        storageObject.setReference(referenceName,
            new ObjectReference().uri(uri).referenceId(referenceName));
        storageObject.setStrictVersionCheck(true);
        try {
          save(storageObject);
        } catch (ObjectModificationException e) {
          // If the given object is modified in the mean time then we must retry the whole function.
          return getOrCreateReferenceObject(objectUri, parameterSetters, clazz, referenceName);
        }
        return newObjectSo;
      } else {
        ObjectReference referenceData = referenceEntry.getReferenceData();
        return load(referenceData.getUri(), clazz);
      }
    } finally {
      objectLock.unlock();
    }
  }

  /**
   * Retrieve the {@link StorageObjectLock} attached to the given object URI. This is just the Lock
   * object that should be used as a normal {@link Lock} implementation.
   *
   * @param objectUri The object URI the lock is attached to.
   * @return The {@link StorageObjectLock}
   */
  public StorageObjectLock getLock(URI objectUri) {
    return objectStorage.getLock(objectUri);
  }

  /**
   * These are the version policies that can be used in the storage object to instruct the object
   * storage about the new and new versions of an object.
   *
   * @return The {@link #versionPolicy}
   */
  public final VersionPolicy getVersionPolicy() {
    return versionPolicy;
  }

  /**
   * These are the version policies that can be used in the storage object to instruct the object
   * storage about the new and new versions of an object.
   *
   * @param versionPolicy The {@link #versionPolicy}
   */
  public final Storage setVersionPolicy(VersionPolicy versionPolicy) {
    this.versionPolicy = versionPolicy;
    return this;
  }

}
