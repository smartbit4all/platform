package org.smartbit4all.domain.data.storage.history;

import java.net.URI;
import org.smartbit4all.api.storage.bean.ObjectHistory;

public interface ObjectHistoryApi {

  ObjectHistory getObjectHistory(URI objectUri, String scheme);
}
