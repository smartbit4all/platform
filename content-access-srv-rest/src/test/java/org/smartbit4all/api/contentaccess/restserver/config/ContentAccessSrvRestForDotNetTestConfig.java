package org.smartbit4all.api.contentaccess.restserver.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryContentApi;
import org.smartbit4all.api.contentaccess.ContentAccessApi;
import org.smartbit4all.api.contentaccess.ContentAccessApiImpl;
import org.smartbit4all.api.objectshare.MockObjectShareApi;
import org.smartbit4all.api.objectshare.ObjectShareApi;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ContentAccessSrvRestConfig.class)
@EnableAutoConfiguration
public class ContentAccessSrvRestForDotNetTestConfig extends ContentAccessSrvRestTestConfig {

  private final URI loremIpsum;
  private final URI copyOfLoremIpsum;
  private final URI asd;
  private final URI asdDoc;

  public ContentAccessSrvRestForDotNetTestConfig() throws URISyntaxException {
    loremIpsum = new URI(ContentAccessApi.SCHEME, null, "/lorem-ipsum.pdf", null);
    copyOfLoremIpsum =
        new URI(ContentAccessApi.SCHEME, null, "/upload/copy_of_lorem-ipsum.pdf", null);
    asd = new URI(ContentAccessApi.SCHEME, null, "/asd.txt", null);
    asdDoc = new URI(ContentAccessApi.SCHEME, null, "/asd.docx", null);
  }

  @Override
  ContentAccessApi contentAccessApi(ObjectShareApi objectShareApi,
      ObjectStorage<BinaryContent> objectStorage,
      BinaryContentApi binaryContentApi) throws Exception {
    ContentAccessApi contentAccessApi =
        new ContentAccessApiImpl(objectShareApi, objectStorage, binaryContentApi);

    BinaryContent downloadPdfContent = new BinaryContent();
    downloadPdfContent.setDataUri(loremIpsum);
    contentAccessApi.share(downloadPdfContent);

    BinaryContent uploadPdfContent = new BinaryContent();
    uploadPdfContent.setDataUri(copyOfLoremIpsum);
    contentAccessApi.share(uploadPdfContent);

    BinaryContent asdContent = new BinaryContent();
    asdContent.setDataUri(asd);
    contentAccessApi.share(asdContent);

    BinaryContent asdDocContent = new BinaryContent();
    asdDocContent.setDataUri(asdDoc);
    contentAccessApi.share(asdDocContent);

    return contentAccessApi;
  }

  @Override
  ObjectShareApi objectShareApi() {
    MockObjectShareApi mockShareApi = new MockObjectShareApi();
    mockShareApi.registerUUIDForTest(UUID.fromString("ce952d1a-10a6-11ec-82a8-0242ac130003"));
    mockShareApi.registerUUIDForTest(UUID.fromString("ce952f5e-10a6-11ec-82a8-0242ac130003"));
    mockShareApi.registerUUIDForTest(UUID.fromString("ce95304e-10a6-11ec-82a8-0242ac130003"));
    mockShareApi.registerUUIDForTest(UUID.fromString("ce95321a-10a6-11ec-82a8-0242ac130003"));

    return mockShareApi;
  }

}
