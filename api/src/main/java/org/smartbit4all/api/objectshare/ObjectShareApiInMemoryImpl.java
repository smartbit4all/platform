package org.smartbit4all.api.objectshare;

import java.net.URI;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class ObjectShareApiInMemoryImpl implements ObjectShareApi{
	
	private Map<UUID, URI> storage= new HashMap<>();
	
	@Override
	public UUID registerUri(URI uri) {
		UUID uuid = UUID.randomUUID();
		storage.put(uuid, uri);
		return uuid;
	}
	
	@Override
	public URI resolveUUID(UUID uuid) {
		URI uri = storage.get(uuid);
		storage.remove(uuid);
		return uri;
	}
}
