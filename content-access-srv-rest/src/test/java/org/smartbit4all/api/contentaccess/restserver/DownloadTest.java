package org.smartbit4all.api.contentaccess.restserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.contentaccess.ContentAccessApi;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class DownloadTest extends ContentAccessSrvRestTest{
	@Test
	void downloadTest() throws Exception {
		BinaryContent content = new BinaryContent();
		content.setDataUri(new URI(ContentAccessApi.SCHEME, null, "/lorem-ipsum.pdf", null));
		UUID uuid = contentAccessApi.share(content);
		
    String path = basePath() + "download/" + uuid;
    ResponseEntity<Resource> response = restTemplate.exchange(path, HttpMethod.GET,  HttpEntity.EMPTY, Resource.class);
    
		assertTrue(response.getStatusCode().is2xxSuccessful());
    assertNotNull(response.getBody());
    assertThrows(NoSuchElementException.class, () -> contentAccessApi.download(uuid));
    
	}
	
	@Test
	void downloadWithZeroContentFileTest() throws Exception {
		BinaryContent content = new BinaryContent();
		content.setDataUri(new URI(ContentAccessApi.SCHEME, null, "/zero", null));
		UUID uuid = contentAccessApi.share(content);
		
    String path = basePath() + "download/" + uuid;
    ResponseEntity<Resource> response = restTemplate.exchange(path, HttpMethod.GET,  HttpEntity.EMPTY, Resource.class);
    
		assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(null, response.getBody());
    assertThrows(NoSuchElementException.class, () -> contentAccessApi.download(uuid));
    
	}
}
