package org.smartbit4all.api.collection;

import java.util.List;
import org.smartbit4all.api.collection.bean.VectorSearchResultItem;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertySet;

/**
 * This collection is a vector db collection that provides an api based on the
 * {@link VectorDBContibutionApi} implementations. It requires also the {@link EmbeddingApi} to
 * create vector from the object that are stored in.
 * 
 * @author Peter Boros
 */
public interface VectorCollection {

  void ensureExist();

  void addObject(String id, Object obj);

  boolean deleteObject(String id);

  List<VectorSearchResultItem> search(Object obj, int limit);

  /**
   * Constructs a lookup based on the collection. It is searching with the
   * {@link #search(Object, int)} similarity search by passing a constructing simple string from the
   * property values from the input object with the following format:
   * <p>
   * Property name1: property value, Property name2: property value.
   * </p>
   * 
   * @param searchProperties The properties to use for the lookup as search parameters.
   * @param copyBackMapping The mapping for copying back the properties of the most relevant result.
   * @return
   */
  ObjectLookup lookup(ObjectPropertySet searchProperties, ObjectMappingDefinition copyBackMapping);

}
