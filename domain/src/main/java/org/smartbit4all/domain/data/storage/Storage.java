package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.api.storage.bean.ObjectMap;
import org.smartbit4all.api.storage.bean.ObjectMapRequest;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.StorageSettings;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.index.ExpressionEntityDefinitionExtractor;
import org.smartbit4all.domain.data.storage.index.StorageIndex;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.springframework.util.Assert;

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
public class Storage {

  private static final String SETTINGS = "settings";

  private static final Logger log = LoggerFactory.getLogger(Storage.class);

  /**
   * This is the physical storage that is responsible for save and load of the objects.
   */
  private ObjectStorage objectStorage;

  /**
   * The index definitions of the storage
   */
  private Map<Class<?>, StorageObjectIndices<?>> indexes = new HashMap<>();

  private boolean indexInitiated = false;

  /**
   * The scheme managed by the given logical storage. This is used as naming of the
   */
  private String scheme;

  /**
   * The object api is responsible for accessing the {@link ObjectDefinition}s of the current
   * application context.
   */
  private ObjectApi objectApi;

  private URI settingsuri;

  /**
   * @param clazz The class that is managed by the storage instance.
   * @param objectStorage Stores the serialized objects
   * @param indexes Indexes for the given type
   */
  public Storage(String scheme, ObjectApi objectApi, ObjectStorage objectStorage) {
    this.objectStorage = objectStorage;
    this.objectApi = objectApi;
    this.scheme = scheme;
  }

  /**
   * Constructs a new instance of the given {@link Class}.
   * 
   * @param <T>
   * @param clazz The class that represents a domain object.
   * @return A new Instance of the {@link StorageObject} that already has an URI! If we save this
   *         without {@link #getObjectUri(T)} then it will be an empty object but we can subscribe
   *         for it's events.
   */
  public <T> StorageObject<T> instanceOf(Class<T> clazz) {
    ObjectDefinition<T> objectDefinition = objectApi.definition(clazz);
    if (!objectDefinition.hasUri()) {
      throw new IllegalArgumentException(
          "Unable to use the " + clazz
              + " as domain object because the lack of URI property!");
    }
    StorageObject<T> storageObject = new StorageObject<>(objectDefinition, this);
    // At this point we already know the unique URI that can be used to refer from other objects
    // also.
    UUID uuid = UUID.randomUUID();
    storageObject.setUri(constructUri(objectDefinition, uuid));
    storageObject.setUuid(uuid);
    return storageObject;
  }

  public <T> URI save(StorageObject<T> object) {
    URI result = objectStorage.save(object);

    // TODO HACK. Deleted case should be handled, if the indexing is not dead idea
    if (object.getObject() != null) {
      updateIndexes(object);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  private final <T> List<StorageIndex<T>> getIndices(ObjectDefinition<T> objectDefinition) {
    StorageObjectIndices<T> storageObjectIndices =
        (StorageObjectIndices<T>) indexes.get(objectDefinition.getClazz());
    return storageObjectIndices != null ? storageObjectIndices.get() : Collections.emptyList();
  }

  private <T> void updateIndexes(StorageObject<T> object) {
    for (StorageIndex<T> index : getIndices(object.definition())) {
      try {
        index.updateIndex(object.getObject());
      } catch (Exception e) {
        log.error("Unable to update storage index.", e);
        throw new IllegalStateException(
            "Failed to update storage index for " + object, e);
      }
    }
  }

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
   * Loads the full history of the given object.
   * 
   * @param uri The uri of the object.
   * @return
   */
  public List<ObjectHistoryEntry> loadHistory(URI uri) {
    ObjectDefinition<?> definition = objectApi.definition(uri);
    return objectStorage.loadHistory(this, uri, definition);
  }


  /**
   * List the datas which fulfill the criteria of the given expression. The expression must only
   * contains properties of the given entity definition!
   */
  public <T> List<StorageObject<T>> listDatas(ObjectDefinition<T> objectDefinition,
      Expression expression) throws Exception {
    List<URI> uris = listUris(objectDefinition, expression);
    return load(uris, objectDefinition.getClazz());
  }

  /**
   * TODO This evaluation should be handled by Query API (eg. exists), so use this judiciously, only
   * if other solutions are not fit for the task.
   * 
   * List the datas by the intersection of the given expression results The result list contains the
   * values which fulfill all expression criterion. The expression must only contains properties of
   * the given entity definition!
   */
  public <T> List<StorageObject<T>> listDatas(ObjectDefinition<T> objectDefinition,
      List<Expression> expressions)
      throws Exception {

    Assert.notEmpty(expressions, "No expression given in listData call");

    List<URI> resultUris = new ArrayList<>();

    Iterator<Expression> iterExpression = expressions.iterator();

    Expression firstExpression = iterExpression.next();
    resultUris.addAll(listUris(objectDefinition, firstExpression));

    while (iterExpression.hasNext()) {
      Expression expression = iterExpression.next();
      List<URI> uris = listUris(objectDefinition, expression);
      resultUris = intersectUriLists(resultUris, uris);
    }

    return load(resultUris, objectDefinition.getClazz());
  }

  public <T> List<URI> listUris(ObjectDefinition<T> objectDefinition, Expression expression)
      throws Exception {
    List<URI> uris = new ArrayList<>();

    for (StorageIndex<T> index : getIndices(objectDefinition)) {
      Optional<EntityDefinition> expressionEntityDef =
          ExpressionEntityDefinitionExtractor.getOnlyEntityDefinition(expression);

      if (expressionEntityDef.isPresent() &&
          index.getEntityDef().equals(expressionEntityDef.get())) {

        uris.addAll(index.listUris(expression));
      }
    }

    return uris;
  }

  public static List<URI> intersectUriLists(List<URI> resultUris, List<URI> uris) {
    return uris.stream()
        .distinct()
        .filter(resultUris::contains)
        .collect(Collectors.toList());
  }

  protected final String getScheme() {
    return scheme;
  }

  public final StorageObject<StorageSettings> settings() {
    URI uri = getSettingsUri();
    // TODO Lock the settings!!!! Optimistic lock will be enough.
    StorageObject<StorageSettings> storageObject;
    try {
      storageObject =
          objectStorage.load(this, uri, StorageSettings.class);
    } catch (ObjectNotFoundException e) {
      // It's missing now so we have to create is.
      storageObject = instanceOf(StorageSettings.class);
      // At this point we already know the unique URI that can be used to refer from other objects
      // also.
      storageObject.setUri(uri);
      storageObject.setObject(new StorageSettings().schemeName(scheme));
      save(storageObject);
    }
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
  private final URI constructUri(ObjectDefinition<?> objectDefinition, UUID uuid) {
    LocalDateTime now = LocalDateTime.now();
    URI uri = URI.create(scheme + StringConstant.COLON + StringConstant.SLASH
        + objectDefinition.getAlias() + StringConstant.SLASH
        + now.getYear() + StringConstant.SLASH + now.getMonthValue() + StringConstant.SLASH
        + now.getDayOfMonth() + StringConstant.SLASH + now.getHour() + StringConstant.SLASH
        + uuid);
    return uri;
  }

  private final URI getSettingsUri() {
    if (settingsuri == null) {
      ObjectDefinition<StorageSettings> objectDefinition =
          objectApi.definition(StorageSettings.class);
      settingsuri = URI.create(scheme + StringConstant.COLON + StringConstant.SLASH
          + objectDefinition.getAlias() + StringConstant.SLASH
          + SETTINGS);
    }
    return settingsuri;
  }

  public void addIndex(StorageObjectIndices<?> index, Class<?> clazz) {
    if (indexes == null) {
      indexes = new HashMap<>();
    }
    indexes.put(clazz, index);
  }

  boolean isIndexInitiated() {
    return indexInitiated;
  }

  void setIndexInitiated(boolean indexInitiated) {
    this.indexInitiated = indexInitiated;
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
    StorageObject<?> storageObject =
        load(objectUri, StorageLoadOption.skipData(), StorageLoadOption.lock());
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
      parameterSetters.accept(newObject);
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
      return load(referenceData.getUri(), clazz, StorageLoadOption.lock());
    }
  }


}
