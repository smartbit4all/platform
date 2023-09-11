package org.smartbit4all.api.contentaccess.restserver;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.contentaccess.ContentAccessApi;
import org.smartbit4all.api.contentaccess.restserver.config.ContentAccessSrvRestTestConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;

class UploadTest extends ContentAccessSrvRestTest {

  private static final String UPLOAD_FOLDER_NAME = "upload";

  private static final String UPLOAD_FOLDER_PATH =
      ContentAccessSrvRestTestConfig.getBinaryDataApiRootFolder().getPath()
          + "/" + UPLOAD_FOLDER_NAME;

  @Test
  void uploadTest() throws Exception {
    BinaryContent content = new BinaryContent();
    content.setDataUri(
        new URI(ContentAccessApi.SCHEME, null, "/" + UPLOAD_FOLDER_NAME + "/asd.pdf", null));
    UUID uuid = contentAccessApi.share(content);

    String url = basePath() + "upload";
    final String filepath = "./src/test/resources/contentAccessData/lorem-ipsum.pdf";

    RequestEntity<MultiValueMap<String, HttpEntity<?>>> req = fileUploadRequest(
        uuid,
        url,
        filepath);
    ResponseEntity<Void> resp = restTemplate.exchange(req, Void.class);

    assertTrue(resp.getStatusCode().is2xxSuccessful());
    assertThrows(NoSuchElementException.class, () -> contentAccessApi.download(uuid));

  }

  private RequestEntity<MultiValueMap<String, HttpEntity<?>>> fileUploadRequest(UUID uuid,
      String url, String filepath) {
    MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
    multipartBodyBuilder.part("uuid", uuid); // DO NOT CALL toString()!!!
    multipartBodyBuilder.part("file",
        new FileSystemResource(filepath));

    RequestEntity<MultiValueMap<String, HttpEntity<?>>> req = RequestEntity
        .post(URI.create(url))
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .accept(MediaType.MULTIPART_FORM_DATA)
        .body(multipartBodyBuilder.build());
    return req;
  }

  @Test
  void uploadWithZeroContentFileTest() throws Exception {
    BinaryContent content = new BinaryContent();
    content.setDataUri(
        new URI(ContentAccessApi.SCHEME, null, "/" + UPLOAD_FOLDER_NAME + "/zero", null));
    UUID uuid = contentAccessApi.share(content);
    final String filepath = "./src/test/resources/contentAccessData/zero";
    String url = basePath() + "upload";
    RequestEntity<MultiValueMap<String, HttpEntity<?>>> fileUploadRequest = fileUploadRequest(
        uuid,
        url,
        filepath);

    ResponseEntity<Void> resp = restTemplate.exchange(fileUploadRequest, Void.class);

    assertTrue(resp.getStatusCode().is2xxSuccessful());
    assertThrows(NoSuchElementException.class, () -> contentAccessApi.download(uuid));

  }
}
