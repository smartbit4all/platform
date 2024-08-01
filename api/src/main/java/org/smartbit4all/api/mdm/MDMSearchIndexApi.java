package org.smartbit4all.api.mdm;

import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.core.object.ObjectNode;

public interface MDMSearchIndexApi {

  SearchIndexImpl<BranchedObjectEntry> createSearchIndexForEntryInstance(
      MDMEntryDescriptor entryDescriptor, String mdmDefName);

  ObjectNode getActualObjectNodeOfBranchedNode(ObjectNode branchedObjectEntryNode, String aspect);

  SearchIndexImpl<?> createSearchIndexForEntry(MDMEntryDescriptor entryDescriptor,
      String mdmDefName);

  default ObjectNode getActualObjectNodeOfBranchedNode(ObjectNode branchedObjectEntryNode) {
    return getActualObjectNodeOfBranchedNode(branchedObjectEntryNode, null);
  }

}
