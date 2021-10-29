package org.smartbit4all.api.contentaccess;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {ContentAccessTestConfig.class})
public class ContentAccessTest {

  @Autowired
  ContentAccessApi contentAccessApi;

  @Autowired
  StorageApi storageApi;

  private static final String UPLOAD_FOLDER_NAME = "upload";

  private static final String UPLOAD_FOLDER_PATH =
      ContentAccessTestConfig.getBinaryDataApiRootFolder().getPath()
          + "/" + UPLOAD_FOLDER_NAME;

  @BeforeAll
  static void clearContentAccessRootFolder() {
    File root = new File(UPLOAD_FOLDER_PATH);
    if (root.exists()) {
      for (File innerFile : root.listFiles()) {
        innerFile.delete();
      }
    }
  }

  @Test
  void shareTest() throws Exception {
    UUID uuidWithNotAddedUri = contentAccessApi.share();
    assertNotNull(uuidWithNotAddedUri);

    BinaryContent content = new BinaryContent();
    content.setUri(new URI("asd"));
    UUID uuidWithAddedUri = contentAccessApi.share(content);
    assertNotNull(uuidWithAddedUri);
  }

  @Test
  void downloadTest() throws Exception {
    BinaryContent content = new BinaryContent();
    content.setDataUri(new URI(ContentAccessApi.SCHEME, null, "/lorem-ipsum.pdf", null));
    UUID uuid = contentAccessApi.share(content);
    BinaryData data = contentAccessApi.download(uuid);
    assertNotNull(data);
  }

  @Test
  void uploadTest() throws Exception {
    BinaryContent content = new BinaryContent();
    UUID uuid = contentAccessApi.share(content);

    BinaryData binaryData = BinaryData
        .of(new FileInputStream("./src/test/resources/contentAccessData/lorem-ipsum.pdf"));
    contentAccessApi.upload(uuid,
        binaryData);

    BinaryDataObject binaryDataObject =
        storageApi.get(ContentAccessApi.SCHEME).read(content.getDataUri(), BinaryDataObject.class);

    Assertions.assertEquals(binaryData.hash(), binaryDataObject.getBinaryData().hash());
    assertThrows(NoSuchElementException.class, () -> contentAccessApi.download(uuid));
  }

  // @Test
  // void downloadWithZeroContentFileTest() throws Exception {
  // BinaryContent content = new BinaryContent();
  // content.setDataUri(new URI(ContentAccessApi.SCHEME, null, "/zero", null));
  // UUID uuid = contentAccessApi.share(content);
  // BinaryData data = contentAccessApi.download(uuid);
  // assertNotNull(data);
  // }
  //
  // @Test
  // void uploadTestWithZeroContentFile() throws Exception {
  // BinaryContent content = new BinaryContent();
  // content.setDataUri(new URI(ContentAccessApi.SCHEME, null, "/" + UPLOAD_FOLDER_NAME + "/zero",
  // null));
  // UUID uuid = contentAccessApi.share(content);
  //
  // contentAccessApi.upload(uuid, BinaryData.of(new
  // FileInputStream("./src/test/resources/contentAccessData/zero")));
  // assertTrue(new File(UPLOAD_FOLDER_PATH + "/zero").exists());
  // assertThrows(NoSuchElementException.class, () -> contentAccessApi.download(uuid));
  // }
}
