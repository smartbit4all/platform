package org.smartbit4all.core.object;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import org.smartbit4all.api.object.ApplyChangeApi;
import org.smartbit4all.api.object.RetrievalApi;
import org.smartbit4all.api.object.RetrievalRequest;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.RetrievalMode;
import org.smartbit4all.api.object.bean.SnapshotData;
import org.smartbit4all.domain.data.storage.StorageApi;

/**
 * Provides a generic entry point into object handling. Allows creating, loading, retrieving and
 * saving objects.
 *
 * Soon to be deprecated functionality: collects the object definitions for the API objects, this
 * has been moved to {@link ObjectDefinitionApi}, use that instead.
 *
 * @author Peter Boros
 */
public interface ObjectApi {

  /**
   * See {@link ObjectDefinitionApi#definition(Class)}
   */
  <T> ObjectDefinition<T> definition(Class<T> clazz);

  /**
   * See {@link ObjectDefinitionApi#definition(URI)}
   */
  ObjectDefinition<?> definition(URI objectUri);

  /**
   * See {@link ObjectDefinitionApi#definition(String)}
   *
   */
  ObjectDefinition<?> definition(String className);

  /**
   * See {@link ObjectDefinitionApi#getDefaultSerializer()}
   */
  ObjectSerializer getDefaultSerializer();

  /**
   * Converts value to T, using the following preference:
   * <ul>
   * <li>if value is T -> (T) value</li>
   * <li>if value ObjectNode -> {@link ObjectNode#getObject(Class)}</li>
   * <li>if value ObjectNodeReference -> {@link ObjectNodeReference#get()}, and then
   * {@link ObjectNode#getObject(Class)}</li>
   * <li>if T is UUID and value is String -> {@link UUID#fromString(String)}</li>
   * <li>if T is URI and value is String -> {@link URI#create(String)}</li>
   * <li>if value is Map -> {@link ObjectDefinition#fromMap(Map)}</li>
   * <ul>
   * <li>which in turn uses {@link ObjectSerializer#fromMap(Map, Class)}</li>
   * </ul>
   * <li>if value is String -> {@link ObjectSerializer#fromString(String, Class)}</li>
   * </ul>
   * Class may not be ObjectNode os ObjectNodeReference. If you need that, use
   * {@link ObjectNode#getValue(String...)}
   *
   * @param <T>
   * @param clazz
   * @param value
   * @return
   */
  <T> T asType(Class<T> clazz, Object value);

  /**
   * Converts List<?> of undefined and possibly heterogen objects to List<E>, using
   * {@link #asType(Class, Object)} conversion.
   *
   * @param <E>
   * @param clazz
   * @param value
   * @return
   */
  <E> List<E> asList(Class<E> clazz, List<?> value);

  /**
   * Converts Map<String, ?> of undefined and possibly heterogen objects to Map<String, E>, using
   * {@link #asType(Class, Object)} conversion.
   *
   *
   * @param <V>
   * @param clazz
   * @param value
   * @return
   */
  <V> Map<String, V> asMap(Class<V> clazz, Map<String, ?> value);

  /**
   * Creates a new {@link RetrievalRequest} based on the parameter clazz and with the specified
   * retrieval mode. This request can be further parameterized with various fluent API methods, and
   * in the end, {@link RetrievalRequest#load(URI)} can be used to load the specified ObjectNode
   * structure. This is the same as to call {@link ObjectApi#load(RetrievalRequest, URI)}
   *
   * @param <T>
   * @param clazz
   * @return
   */
  <T> RetrievalRequest request(Class<T> clazz, RetrievalMode retrievalMode);

  /**
   * Default retrieval mode for {@link #request(Class, RetrievalMode)}
   *
   * @param <T>
   * @param clazz
   * @return
   */
  default <T> RetrievalRequest request(Class<T> clazz) {
    return request(clazz, RetrievalMode.NORMAL);
  }


  /**
   * Similar to {@link #load(URI, URI)} but will retrieve the latest version of the object.
   *
   * @param objectUri
   * @param branchUri
   * @return
   */
  ObjectNode loadLatest(URI objectUri, URI branchUri);

  default ObjectNode loadLatest(URI objectUri) {
    return loadLatest(objectUri, null);
  }

  /**
   * Creates a request based on the objectUri and retrieves it as an ObjectNode.
   *
   * @param objectUri
   * @param branchUri
   * @return
   */
  ObjectNode load(URI objectUri, URI branchUri);

  default ObjectNode load(URI objectUri) {
    return load(objectUri, null);
  }

  /**
   * Loads ObjectNode with structure specified in request, starting from objectUri. This method uses
   * {@link RetrievalApi} for retrieving {@link ObjectNodeData} structure, and then converts them to
   * ObjectNode.
   *
   * @param request
   * @param objectUri
   * @param branchUri
   * @return
   */
  ObjectNode load(RetrievalRequest request, URI objectUri, URI branchUri);

  default ObjectNode load(RetrievalRequest request, URI objectUri) {
    return load(request, objectUri, null);
  }

  /**
   * Similar to {@link ObjectApi#load(RetrievalRequest, URI)}, but starts from multiple objectUris
   * and returns list of ObjectNodes.
   *
   * @param request
   * @param objectUris
   * @param branchUri
   * @return
   */
  List<ObjectNode> load(RetrievalRequest request, List<URI> objectUris, URI branchUri);

  default List<ObjectNode> load(RetrievalRequest request, List<URI> objectUris) {
    return load(request, objectUris, null);
  }

  /**
   * Creates an ObjectNode from snapshot data.
   *
   * @param data
   * @return
   */
  ObjectNode loadSnapshot(SnapshotData data);

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
  <T> T read(URI uri, Class<T> clazz);

  /**
   * Create a new ObjectNode based on a new ObjectNodeData based on the Object parameter. This node
   * will be created as a new object in storage when saved.
   *
   * @param storageScheme logical scheme in which to create this new object
   * @param object the object data
   * @return
   */
  ObjectNode create(String storageScheme, Object object);

  /**
   * Create a new ObjectNode based on a new ObjectNodeData based on the Object parameter. This node
   * will be created as a new object in storage when saved.
   *
   * @param storageScheme logical scheme in which to create this new object
   * @param definition The {@link ObjectDefinition} of the given object node.
   * @param objectMap The data of the node as map.
   * @return
   */
  ObjectNode create(String storageScheme, ObjectDefinition<?> definition,
      Map<String, Object> objectMap);

  /**
   * Save the ObjectNode structure to the specified branch. If brancUri is null, it will be saved on
   * main branch. Saves only changes specified in {@link ObjectNode#getState()}. Uses
   * {@link ApplyChangeApi#applyChanges(ObjectNode, BranchEntry)} under the hood.
   *
   */
  URI save(ObjectNode node, URI branchUri);

  /**
   * See {@link ApplyChangeApi#applyChanges(ObjectNode)}
   *
   */
  default URI save(ObjectNode node) {
    return save(node, null);
  }

  URI getLatestUri(URI uri);

  default URI saveAsNew(String storageScheme, Object object, URI branchUri) {
    ObjectNode node = create(storageScheme, object);
    return save(node, branchUri);
  }

  default URI saveAsNew(String storageScheme, Object object) {
    return saveAsNew(storageScheme, object, null);
  }

  <T> T getValueFromObject(Class<T> clazz, Object object, String... paths);

  <E> List<E> getListFromObject(Class<E> clazz, Object object, String... paths);

  <V> Map<String, V> getMapFromObject(Class<V> clazz, Object object, String... paths);

  Object getValueFromObjectMap(Map<String, Object> map, String... paths);

  boolean equalsIgnoreVersion(URI a, URI b);

  /**
   * Constructs an object property resolver instance. The object property resolver is the central
   * logic that can help to access the values in an application logic. To perform resolution we have
   * to define the context that is a bunch of named object uri that can be referred during the
   * resolution. The great advantage of this approach that we can use a standard URI format to point
   * to every property. This object is like a session is a stateful object that remembers the
   * already loaded part of the object graph to avoid repetitive reload of the objects for every
   * resolve call. But be careful it cannot be refreshed so if we make changes on the original
   * object then this will resolve the previous values until we create a new resolver via the
   * {@link ObjectApi#resolver()}.
   * 
   * @return
   */
  ObjectPropertyResolver resolver();

  /**
   * Get a lock object for the given URI. The URI is not necessarily exists at the moment of the
   * lock creation. We can use this lock one time to place a lock and remove it at the end.
   * 
   * @param uri The URI of the object. It doesn't matter if it is latest or not the lock will be
   *        applied on the object not on the version of the object.
   * @return A {@link Lock} object that can be used like a normal Java Lock.
   */
  Lock getLock(URI uri);

  /**
   * Retrieves the last modification of the given object identified by the URI.
   * 
   * @param uri The uri of the object
   * @return return the last modification time epoch or null if the object doesn't exist.
   */
  Long getLastModified(URI uri);

  /**
   * Get a cache entry for the given object type. If the entry doesn't exist then it will create a
   * new one.
   * 
   * @param clazz The class to manage by the cache.
   * @return The cache entry.
   */
  <T> ObjectCacheEntry<T> getCacheEntry(Class<T> clazz);

}
