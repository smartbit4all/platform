package org.smartbit4all.api.collection;

import java.net.URI;
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
   * This function will return a {@link SearchIndex} if it is parameterized.
   * 
   * @param name The name of the search index.
   * @return The {@link SearchIndex}.
   */
  SearchIndex searchIndex(String logicalSchema, String name);

  /**
   * This object provides an atomic sequence that provides globally unique incrementing value. Can
   * be used as the classic database sequence.
   * 
   * @param logicalSchema
   * @param name The unique name of the sequence inside the logical schema.
   * @return
   */
  StoredSequence sequence(String logicalSchema, String name);

}
