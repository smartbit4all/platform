package org.smartbit4all.api.collection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.collection.bean.VectorSearchResultItem;

/**
 * This collection is a vector db collection that provides an api based on the
 * {@link VectorDBContibutionApi} implementations. It requires also the {@link EmbeddingApi} to
 * create vector from the object that are stored in.
 * 
 * @author Peter Boros
 */
public interface VectorCollection {

  static final String VECTOR_AUDIT_LOG = "vector.audit";

  Boolean exists();

  void ensureExist();

  default String addObject(Object obj) {
    return addObject(obj, null);
  }

  String addObject(Object obj, List<String> restictedColumns);

  /**
   * Add a given value to the vector index and append some additional data. The value itself is not
   * included in the index entry.
   * 
   * @param value
   * @param additionalData
   * @return
   */
  String add(Map<String, Object> value, Map<String, Object> additionalData);

  /**
   * Add a given value to the vector index and append some additional data. The value itself is not
   * included in the index entry.
   * 
   * @param value
   * @param additionalData
   * @return
   */
  String add(String value, Map<String, Object> additionalData);

  void delete(String id);

  void delete(Collection<String> ids);

  void clear();

  List<VectorSearchResultItem> search(Object obj, int limit, Map<String, Object> parameters);

  /**
   * Constructs a lookup based on the collection. It is searching with the
   * {@link #search(Object, int, Map)} similarity search by passing a constructing simple string
   * from the property values from the input object with the following format:
   * <p>
   * Property name1: property value, Property name2: property value.
   * </p>
   * 
   * @return
   */
  ObjectLookup lookup();

}
