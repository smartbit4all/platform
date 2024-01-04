package org.smartbit4all.api.collection;

import java.util.List;
import org.smartbit4all.api.collection.bean.VectorSearchResultItem;

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

  boolean deletObject(String id);

  List<VectorSearchResultItem> search(Object obj, int limit);

}
