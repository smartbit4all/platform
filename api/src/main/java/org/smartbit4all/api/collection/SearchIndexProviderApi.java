package org.smartbit4all.api.collection;

import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.query.QueryInput;

/**
 * The search index api can manage a set of object to ensure that an indexing structure is
 * constructed online, updated always synchronous or asynchronous way. The search index can have
 * multiple implementation and one search api can serve more SearchIndex.
 * 
 * @author Peter Boros
 */
public interface SearchIndexProviderApi {

  EntityDefinition getDefinition();

  TableData<?> executeSearch(QueryInput queryInput);

}
