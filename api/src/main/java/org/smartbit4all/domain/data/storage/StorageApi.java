package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredSequence;

/**
 * The storage api is the access for the {@link Storage} instances defined in the configurations of
 * the system. The Storage are the logical unit of object storage all of them are identified by
 * their scheme. One logical Storage always have a physical {@link ObjectStorage} implementation in
 * the background to ensure atomic transaction on the save of the {@link StorageObject}.
 *
 * @author Peter Boros
 */
public interface StorageApi {

  /**
   * Retrieves the {@link Storage} instance responsible for persisting in the given scheme.
   * Typically every module has a scheme used by the apis in the module.
   *
   * @param scheme The scheme name.
   * @return The storage if it exists.
   */
  Storage get(String scheme);

  /**
   * Retrieves the effective alias for the specified <b>scheme</b>.
   * 
   * <p>
   * If the application does not alias the given scheme, this method returns the input itself.
   * 
   * <p>
   * <em>When constructing custom storage entries, such as - but not limited to -
   * {@link CollectionApi} managed entities, client's should <b>always</b> call this method to
   * acquire the effective scheme to use in their application.</em>
   * 
   * @param scheme a {@code String} scheme to check, not null
   * @return the actual {@code String} scheme to use instead of the input in the application, never
   *         null
   */
  String getSchemeAlias(final String scheme);

  /**
   * Retrieves the {@link Storage} instance responsible for persisting in the given scheme.
   * Typically every module has a scheme used by the apis in the module.
   *
   * @param uri The uri that defines the scheme to find the proper {@link Storage}.
   * @return The storage if it exists or else null.
   */
  Storage getStorage(URI uri);

  /**
   * This function can be used to load any object managed by the StorageApi. It identifies the given
   * {@link Storage} by the clazz and the URI. The uri can define the exact physical location of the
   * object and can define the {@link Storage} responsible for.
   *
   * @param <T> The type
   * @param uri The uri of the object.
   * @param clazz The class of the object we need.
   * @return The object.
   */
  <T> StorageObject<T> load(URI uri, Class<T> clazz);

  /**
   * The StorageObject is loaded by the URI itself. In this case we are not sure about the type of
   * the object. It will be discovered by the persisted information.
   *
   * @param uri The uri of the object to load.
   * @return
   */
  StorageObject<?> load(URI uri);

  List<StorageObject<?>> loadBatch(List<URI> uris);

  /**
   * Creates an {@link ObjectHistoryIterator} that can iterate through the
   * {@link StorageObjectHistoryEntry}s of the object found with the given uri, making available to
   * investigate the full history of that object. In order from the most oldest to the most recent.
   *
   * @param uri
   * @return
   */
  ObjectHistoryIterator objectHistory(URI uri);

  /**
   * Creates an {@link ObjectHistoryIterator} that can iterate through the
   * {@link StorageObjectHistoryEntry}s of the object found with the given uri, making available to
   * investigate the full history of that object. In order from the most recent to the oldest.
   *
   * @param uri
   * @return
   */
  ObjectHistoryIterator objectHistoryReverse(URI uri);

  ObjectStorage getDefaultObjectStorage();

  /**
   * Get a {@link StoredSequence} adequate for the given storage.
   *
   * @param schema The schema for the sequence
   * @param name The name of the sequence
   * @return The {@link StoredSequence} instance.
   */
  StoredSequence getSequence(String schema, String name);

  /**
   * Get a {@link StoredSequence} adequate for the given storage.
   *
   * @param scopeObjectUri The scope object defines the object the container belongs to.
   * @param schema The schema for the sequence
   * @param name The name of the sequence
   * @return The {@link StoredSequence} instance.
   */
  StoredSequence getSequence(URI scopeObjectUri, String schema, String name);

  ObjectStream getObjectStream(URI scopeObjectUri, String schema, String name,
      long headPosition);

}
