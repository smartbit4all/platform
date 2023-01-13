package org.smartbit4all.api.collection;

import org.smartbit4all.domain.data.TableData;

public interface SearchIndexWithFilterBean<O, F> extends SearchIndex<O> {

  TableData<?> executeSearch(F filterObject);

}
