package org.smartbit4all.core.object;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.object.ApplyChangeApi;
import org.smartbit4all.api.object.RetrievalApi;
import org.smartbit4all.api.object.RetrievalRequest;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.RetrievalMode;
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
   * Creates a request based on the objectUri and retrieves it as an ObjectNode with the specified
   * retrieval mode.
   * 
   * @param objectUri
   * @param retrievalMode
   * @return
   */
  ObjectNode load(URI objectUri, RetrievalMode retrievalMode);


  /**
   * Similar to {@link #load(URI, RetrievalMode)}, but initial request's loadLates will be set to
   * true, so this method will load the latest version of the object.
   * 
   * @param objectUri
   * @return
   */
  ObjectNode loadLatest(URI objectUri, RetrievalMode retrievalMode);

  /**
   * Default retrieval mode for {@link #load(URI, RetrievalMode)}
   * 
   * @param objectUri
   * @return
   */
  default ObjectNode loadLatest(URI objectUri) {
    return loadLatest(objectUri, RetrievalMode.NORMAL);
  }

  /**
   * Default retrieval mode for {@link #load(URI, RetrievalMode)}
   * 
   * @param objectUri
   * @return
   */
  default ObjectNode load(URI objectUri) {
    return load(objectUri, RetrievalMode.NORMAL);
  }

  /**
   * Loads ObjectNode with structure specified in request, starting from objectUri. This method uses
   * {@link RetrievalApi} for retrieving {@link ObjectNodeData} structure, and then converts them to
   * ObjectNode.
   * 
   * @param request
   * @param objectUri
   * @return
   */
  ObjectNode load(RetrievalRequest request, URI objectUri);

  /**
   * Similar to {@link ObjectApi#load(RetrievalRequest, URI)}, but starts from multiple objectUris
   * and returns list of ObjectNodes.
   * 
   * @param request
   * @param objectUris
   * @return
   */
  List<ObjectNode> load(RetrievalRequest request, List<URI> objectUris);

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
   * Save the ObjectNode structure to the specified branch. If brancUri is null, it will be saved on
   * main branch. Saves only changes specified in {@link ObjectNode#getState()}. Uses
   * {@link ApplyChangeApi#applyChanges(ObjectNode, URI)} under the hood.
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

  default URI saveAsNew(String storageScheme, Object object) {
    ObjectNode node = create(storageScheme, object);
    return save(node);
  }
}
