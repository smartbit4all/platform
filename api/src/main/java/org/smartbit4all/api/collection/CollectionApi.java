package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.function.Supplier;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.core.object.ObjectNodeReference;

/**
 * The collection api is responsible for the gathering all the collection implementations currently
 * available. They can have many implementations but we will have default storage based
 * implementations.
 * 
 * @author Peter Boros
 */
public interface CollectionApi {

  /**
   * A common problem to store object references mapped by a string.
   * 
   * @param logicalSchema The logical schema of the map. Helps the implementation to have a good
   *        namespace. Be careful don't use the normal storage schema because the schema will be non
   *        versioned!
   * @param mapName The name of the map.
   * @return The StoredMap as a collection of {@link ObjectNodeReference}.
   */
  StoredMap map(String logicalSchema, String mapName);

  /**
   * A common problem to store object references mapped by a string.
   * 
   * @param scopeObjectUri The scope object defines the object the container belongs to.
   * @param logicalSchema The logical schema of the map. Helps the implementation to have a good
   *        namespace. Be careful don't use the normal storage schema because the schema will be non
   *        versioned!
   * @param mapName The name of the map.
   * @return The StoredMap as a collection of {@link ObjectNodeReference}.
   */
  StoredMap map(URI scopeObjectUri, String logicalSchema, String mapName);

  /**
   * A common problem to store object uri list.
   * 
   * @param logicalSchema The logical schema of the map. Helps the implementation to have a good
   *        namespace. Be careful don't use the normal storage schema because the schema will be non
   *        versioned!
   * @param name The name of the map.
   * @return The StoredList.
   */
  StoredList list(String logicalSchema, String name);

  /**
   * A common problem to store object uri list.
   * 
   * @param scopeObjectUri The scope object defines the object the container belongs to.
   * @param logicalSchema The logical schema of the map. Helps the implementation to have a good
   *        namespace. Be careful don't use the normal storage schema because the schema will be non
   *        versioned!
   * @param name The name of the map.
   * @return The StoredList.
   */
  StoredList list(URI scopeObjectUri, String logicalSchema, String name);

  /**
   * A common problem to store object uri list.
   * 
   * @param descriptor The parameters of the list.
   * @return The StoredList.
   */
  StoredList list(StoredCollectionDescriptor descriptor);

  /**
   * This function will return a {@link StoredReference} that can contains one object.
   * 
   * @param logicalSchema The logical schema of the map. Helps the implementation to have a good
   *        namespace. Be careful don't use the normal storage schema because the schema will be non
   *        versioned!
   * @param name The name of the map.
   * @param clazz The type of the object to be stored in the reference.
   * @return The {@link StoredReference}.
   */
  <T> StoredReference<T> reference(String logicalSchema, String name, Class<T> clazz);

  /**
   * This function will return a {@link StoredReference} that can contains one object.
   * 
   * @param scopeObjectUri The scope object defines the object the container belongs to.
   * @param logicalSchema The logical schema of the map. Helps the implementation to have a good
   *        namespace. Be careful don't use the normal storage schema because the schema will be non
   *        versioned!
   * @param name The name of the map.
   * @param clazz The type of the object to be stored in the reference.
   * @return The {@link StoredReference}.
   */
  <T> StoredReference<T> reference(URI scopeObjectUri, String logicalSchema, String name,
      Class<T> clazz);

  /**
   * This function will return a {@link StoredReference} that can contains one object.
   * 
   * @param refUri The uri of the reference if we have it!
   * @param clazz The type of the object to be stored in the reference.
   * @return The {@link StoredReference}.
   */
  <T> StoredReference<T> reference(URI refUri, Class<T> clazz);

  /**
   * This function will return a {@link SearchIndex}.
   * 
   * @param name The name of the search index.
   * @return The {@link SearchIndex}.
   */
  SearchIndex<?> searchIndex(String logicalSchema, String name);

  /**
   * This function will return a {@link SearchIndex} if it is parameterized.
   * 
   * @param name The name of the search index.
   * @return The {@link SearchIndex}.
   */
  <O> SearchIndex<O> searchIndex(String logicalSchema, String name, Class<O> indexedObject);

  /**
   * This function will return a {@link SearchIndex} if it is already parameterized but register it
   * 
   * @param logicalSchema The logical schema of the search index.
   * @param name The name of the search index.
   * @param searchIndexSupplier The supplier of the search index.
   * @return The {@link SearchIndex}.
   */
  <T> SearchIndex<T> searchIndexComputeIfAbsent(String logicalSchema, String name,
      Supplier<SearchIndex<T>> searchIndexSupplier, Class<T> clazz);

  /**
   * This function will return a {@link SearchIndex} if it is parameterized.
   * 
   * @param name The name of the search index.
   * @return The {@link SearchIndex}.
   */
  <O, F> SearchIndexWithFilterBean<O, F> searchIndex(String logicalSchema, String name,
      Class<O> indexedObject, Class<F> filterObject);

  /**
   * This object provides an atomic sequence that provides globally unique incrementing value. Can
   * be used as the classic database sequence.
   * 
   * @param logicalSchema
   * @param name The unique name of the sequence inside the logical schema.
   * @return
   */
  StoredSequence sequence(String logicalSchema, String name);

  /**
   * Provides a vector collection that refers to a {@link VectorDBContibutionApi} and an
   * {@link EmbeddingContributionApi} named by the two {@link ServiceConnection}.
   * 
   * @param name The name of the collection in the Vector DB.
   * @param vectorDBConnection
   * @param embeddingConnection
   * @return The {@link VectorCollection} ready to use.
   */
  VectorCollection vectorCollection(String name, ServiceConnection vectorDBConnection,
      ServiceConnection embeddingConnection);

}
