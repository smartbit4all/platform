package org.smartbit4all.domain.data.storage.index;

import java.net.URI;
import java.util.List;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;

/**
 * StorageIndexer can update the index values, and list URIs based on the stored values and an
 * expression.
 * 
 * @author Zoltan Szegedi
 */
public interface StorageIndexer {

  public List<URI> listUris(StorageIndex<?> index, Expression expression) throws Exception;

  public <V> List<URI> listUris(Property<URI> key, Property<V> indexField, V value)
      throws Exception;

  public <T> void updateIndex(T object, StorageIndex<T> index) throws Exception;

  public <F> boolean canUseFor(Property<F> valueField, Expression expression);

}
