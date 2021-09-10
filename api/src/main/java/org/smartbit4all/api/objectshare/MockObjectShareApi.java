package org.smartbit4all.api.objectshare;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.UUID;

public class MockObjectShareApi implements ObjectShareApi{

	private Map<UUID, URI> uuidUriPairs = new HashMap<>();
	private Queue<UUID> uuids = new LinkedList<>();
	
	@Override
	public UUID registerUri(URI uri) {
		try {
			UUID uuid = uuids.poll();
			uuidUriPairs.put(uuid, uri);
			return uuid;
		}
		catch (Exception ex) {
			throw new NoSuchElementException("No more registered uuid");
		}
	}

	@Override
	public URI resolveUUID(UUID uuid) {
		return uuidUriPairs.get(uuid);
	}
	
	public void registerUUIDForTest(UUID uuid) {
		uuids.add(uuid);
	}
	
}
