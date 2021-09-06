package org.smartbit4all.api.contentaccess;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryContentApi;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.objectshare.ObjectShareApi;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.springframework.beans.factory.annotation.Autowired;

public class ContentAccessApiImpl implements ContentAccessApi{
	
	@Autowired
	private ObjectShareApi objectShareApi;
	
	@Autowired
	private ObjectStorage<BinaryContent> storage;
	
	@Autowired
	private BinaryContentApi binaryContentApi; 
	
	@Override
	public UUID share() throws Exception {
		BinaryContent binaryContent = new BinaryContent();
		binaryContent.setDataUri(new URI(SCHEME, null, "/" + UUID.randomUUID(), null));
		return share(binaryContent);
	}

	@Override
	public UUID share(BinaryContent binaryContent) throws Exception {
		URI savedBinaryContentUri = storage.save(binaryContent);
		return objectShareApi.registerUri(savedBinaryContentUri);
	}

	@Override
	public BinaryData download(UUID uuid) throws Exception {
		URI contentUri = objectShareApi.resolveUUID(uuid);
		
		if (contentUri != null) {
			Optional<BinaryContent> content = storage.load(contentUri);
			return binaryContentApi.getBinaryData(content.get());
		}
		else {
			throw new NoSuchElementException("The content was not found with the given uuid");
		}
	}

	@Override
	public void upload(UUID uuid, BinaryData binaryData) throws Exception {
		URI contentUri = objectShareApi.resolveUUID(uuid);
		
		if (contentUri != null) {
			Optional<BinaryContent> content = storage.load(contentUri);
			BinaryContent binaryContent = content.get();
			binaryContentApi.uploadContent(binaryContent, binaryData, binaryContent.getDataUri());
			storage.save(binaryContent, contentUri);
		}
		else {
			throw new NoSuchElementException("The content was not found with the given uuid");
		}
	}
}
