package org.smartbit4all.api.contentaccess.restserver;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.contentaccess.ContentAccessApi;
import org.smartbit4all.api.contentaccess.restserver.config.ContentAccessSrvRestTestConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class UploadTest extends ContentAccessSrvRestTest {

	private static final String UPLOAD_FOLDER_NAME = "upload";

	private static final String UPLOAD_FOLDER_PATH = ContentAccessSrvRestTestConfig.getBinaryDataApiRootFolder().getPath()
			+ "/" + UPLOAD_FOLDER_NAME;

	@BeforeAll
	static void clearContentAccessRootFolder() {
		File root = new File(UPLOAD_FOLDER_PATH);
		for (File innerFile : root.listFiles()) {
			innerFile.delete();
		}
	}

	@Test
	void uploadTest() throws Exception {
		BinaryContent content = new BinaryContent();
		content.setDataUri(new URI(ContentAccessApi.SCHEME, null, "/" + UPLOAD_FOLDER_NAME + "/asd.pdf", null));
		UUID uuid = contentAccessApi.share(content);

		String url = basePath() + "upload";
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource("./src/test/resources/contentAccessData/lorem-ipsum.pdf"));
		body.add("uuid", uuid.toString());
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);

		ResponseEntity<Void> resp = restTemplate.postForEntity(url, requestEntity, Void.class);

		assertTrue(resp.getStatusCode().is2xxSuccessful());
		assertTrue(new File(UPLOAD_FOLDER_PATH + "/asd.pdf").exists());
		assertThrows(NoSuchElementException.class, () -> contentAccessApi.download(uuid));

	}

	@Test
	void uploadWithZeroContentFileTest() throws Exception {
		BinaryContent content = new BinaryContent();
		content.setDataUri(new URI(ContentAccessApi.SCHEME, null, "/" + UPLOAD_FOLDER_NAME + "/zero", null));
		UUID uuid = contentAccessApi.share(content);

		String url = basePath() + "upload";
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource("./src/test/resources/contentAccessData/zero"));
		body.add("uuid", uuid.toString());
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);

		ResponseEntity<Void> resp = restTemplate.postForEntity(url, requestEntity, Void.class);

		assertTrue(resp.getStatusCode().is2xxSuccessful());
		assertTrue(new File(UPLOAD_FOLDER_PATH + "/zero").exists());
		assertThrows(NoSuchElementException.class, () -> contentAccessApi.download(uuid));

	}
}
