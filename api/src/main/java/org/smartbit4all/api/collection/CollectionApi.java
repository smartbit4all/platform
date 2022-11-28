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

}
