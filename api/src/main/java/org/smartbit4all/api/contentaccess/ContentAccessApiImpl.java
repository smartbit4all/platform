package org.smartbit4all.api.contentaccess;

import java.net.URI;
import java.util.UUID;

import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryContentApi;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.objectshare.ObjectShareApi;
import org.smartbit4all.domain.data.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;

public class ContentAccessApiImpl implements ContentAccessApi{
	
	@Autowired
	private ObjectShareApi objectShareApi;
	
	@Autowired
	private Storage<BinaryContent> storage;
	
	@Autowired
	private BinaryContentApi binaryContentApi; 
	
	@Override
	public UUID share() throws Exception {
		BinaryContent binaryContent = new BinaryContent();
		return share(binaryContent);
	}

	@Override
	public UUID share(BinaryContent binaryContent) throws Exception {
		URI savedBinaryContentUri = storage.save(binaryContent);
		return objectShareApi.registerUri(savedBinaryContentUri);
	}

	@Override
	public BinaryContent download(UUID uuid) throws Exception {
		URI contentUri = objectShareApi.resolveUUID(uuid);
		return storage.load(contentUri).get();
	}

	@Override
	public void upload(UUID uuid, BinaryData binaryData) throws Exception {
		URI contentUri = objectShareApi.resolveUUID(uuid);
		BinaryContent binaryContent = storage.load(contentUri).get();
		binaryContentApi.saveIntoContent(binaryContent, binaryData, null);
		storage.save(binaryContent, contentUri);
	}
}
