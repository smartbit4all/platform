package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.Collection;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;

/**
 * The marker interface for the {@link StoredList}, {@link StoredMap} and {@link StoredReference}
 * containers of the {@link CollectionApi}.
 */
public interface StoredContainer {

  StoredCollectionDescriptor getDescriptor();

  boolean removeAll(Collection<URI> uris);

}
