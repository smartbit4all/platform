package org.smartbit4all.api.objectshare;

import java.net.URI;
import java.util.UUID;

public interface ObjectShareApi {
	UUID registerUri(URI uri);
	
	URI resolveUUID(UUID uuid);
}
