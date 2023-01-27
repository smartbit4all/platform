package org.smartbit4all.api.collection;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.domain.data.TableData;

/**
 * This object contains all the {@link SearchEntityDefinition} {@link TableData} results that are
 * relevant in a given {@link SearchIndex}. This object is recursive because it contains result of
 * the details also.
 * 
 * @author Peter Boros
 */
class SearchEntityTableDataResult {

  SearchEntityDefinition searchEntityDefinition;

  TableData<?> result;

  Map<String, SearchEntityTableDataResult> detailResults = new HashMap<>();

  final SearchEntityTableDataResult searchEntityDefinition(
      SearchEntityDefinition searchEntityDefinition) {
    this.searchEntityDefinition = searchEntityDefinition;
    return this;
  }

  final SearchEntityTableDataResult result(
      TableData<?> result) {
    this.result = result;
    return this;
  }

}
