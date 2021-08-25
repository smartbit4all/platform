package org.smartbit4all.api.binarydata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.fs.BinaryDataApiFS;
import org.smartbit4all.core.io.TestFileUtil;

public class BinaryContentTest {

  public static final String BINARYDATA_SCHEMA = "testFS";

  final static BinaryData testFile =
      new BinaryData(new File("src/test/resources/lorem-ipsum.pdf"), false);

  private static BinaryDataApiFS binaryDataApi;

  private static BinaryContentApiImpl binaryContentApi;

  private static URI testFileDataURI;

  @BeforeEach
  void initEach() throws IOException {
    TestFileUtil.initTestDirectory();
  }

  @BeforeAll
  static void init() {
    binaryDataApi =
        new BinaryDataApiFS(BINARYDATA_SCHEMA, TestFileUtil.testFsRootFolder());
    binaryContentApi = new BinaryContentApiImpl(binaryDataApi);
    testFileDataURI = URI.create(BINARYDATA_SCHEMA + ":/testfolder/testfile.bd");
  }

  @Test
  void saveLoadBinaryDataTest() {

    // Try to load not existing BinaryData
    Optional<BinaryData> binaryData = binaryDataApi.load(testFileDataURI);

    assertFalse(binaryData.isPresent());

    // Save and Load BinaryData
    binaryDataApi.save(testFile, testFileDataURI);

    binaryData = binaryDataApi.load(testFileDataURI);

    assertTrue(binaryData.isPresent());
  }

  @Test
  void getBinaryContentInputStreamTest() throws Exception {
    BinaryContent binaryContent = new BinaryContent().dataUri(testFileDataURI);

    // Try to load not existing BinaryData
    assertFalse(binaryContent.isLoaded());
    assertNull(binaryContent.getData());
    assertThrows(IllegalStateException.class, () -> binaryContentApi.getInputStream(binaryContent));

    // Save BinaryData and try to get InputStream
    binaryDataApi.save(testFile, binaryContent.getDataUri());

    try (InputStream inputStream = binaryContentApi.getInputStream(binaryContent)) {
      assertNotNull(inputStream);
    }
    assertTrue(binaryContent.isLoaded());
    assertFalse(binaryContent.isSaveData());
    assertNotNull(binaryContent.getData());
  }

  @Test
  void loadBinaryContentTest() throws Exception {
    BinaryContent binaryContent = new BinaryContent().dataUri(testFileDataURI);

    // Try to load not existing BinaryData
    assertFalse(binaryContent.isLoaded());
    assertNull(binaryContent.getData());
    assertThrows(IllegalStateException.class, () -> binaryContentApi.getInputStream(binaryContent));

    // Save and Load BinaryData
    binaryDataApi.save(testFile, binaryContent.getDataUri());
    binaryContentApi.load(binaryContent);

    assertTrue(binaryContent.isLoaded());
    assertFalse(binaryContent.isSaveData());
    BinaryData data = binaryContent.getData();
    assertNotNull(data);

    // Get BinaryDataInputStream
    try (InputStream inputStream = binaryContentApi.getInputStream(binaryContent)) {
      assertNotNull(inputStream);
    }
    assertTrue(binaryContent.isLoaded());
    assertFalse(binaryContent.isSaveData());
    assertNotNull(binaryContent.getData());
    assertEquals(data, binaryContent.getData());
  }

  @Test
  void contentOfTest() {
    BinaryContent binaryContent = new BinaryContent().dataUri(testFileDataURI);
    binaryContent = binaryContentApi.contentOf(binaryContent, testFile.inputStream());

    // Try to load not existing BinaryData
    Optional<BinaryData> binaryData = binaryDataApi.load(binaryContent.getDataUri());

    assertFalse(binaryData.isPresent());

    // The BinaryData was set to the BinaryConent
    assertTrue(binaryContent.isSaveData());
    assertTrue(binaryContent.isLoaded());
    assertNotNull(binaryContent.getData());
  }
}
