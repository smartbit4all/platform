package org.smartbit4all.api.contentaccess.restserver.impl;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.contentaccess.ContentAccessApi;
import org.smartbit4all.api.contentaccess.restserver.ContentAccessApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public class ContentAccessApiDelegateImpl implements ContentAccessApiDelegate {

	@Autowired
	ContentAccessApi contentAccessApi;
	
	@Override
	public ResponseEntity<Resource> download(UUID uuid) throws Exception {
		try {
			BinaryData content = contentAccessApi.download(uuid);
			Resource res = new InputStreamResource(content.inputStream());
			
			return ResponseEntity.ok()
					.contentLength(res.contentLength())
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(res);
		}
		catch (NoSuchElementException ex) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@Override
	public ResponseEntity<Void> upload(UUID uuid, MultipartFile file) throws Exception {
		try {
			BinaryData binaryData = BinaryData.of(file.getInputStream());
			contentAccessApi.upload(uuid, binaryData);
			
			return ResponseEntity.ok().build();
		}
		catch (NoSuchElementException ex) {
			return ResponseEntity.notFound().build();
		}
	}
	
}
