package org.smartbit4all.domain.data.storage.history;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;

public interface ObjectHistoryApi {

  List<ObjectHistoryEntry> getObjectHistory(URI objectUri, String scheme);
}
