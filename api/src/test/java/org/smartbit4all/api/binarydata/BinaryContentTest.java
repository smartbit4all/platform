package org.smartbit4all.api.binarydata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.fs.BinaryDataApiFS;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BinaryContentTest {

  public static final String BINARYDATA_SCHEMA = "testFS";

  final static BinaryData testFile =
      new BinaryData(new File("src/test/resources/lorem-ipsum.pdf"));

  private static BinaryDataApiFS binaryDataApi;

  private static BinaryContentApiImpl binaryContentApi;

  private static URI testFileDataURI;

  public static final Map<Class<?>, ApiBeanDescriptor> TEST_BINARY_CONTENT_DESCRIPTOR =
      initTestBinaryContentDescriptor();

  private static Map<Class<?>, ApiBeanDescriptor> initTestBinaryContentDescriptor() {
    Set<Class<?>> domainBeans = new HashSet<>();

    domainBeans.add(TestBinaryContentObject.class);
    domainBeans.add(BinaryContent.class);

    return ApiBeanDescriptor.of(domainBeans);
  }


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
    assertNotNull(binaryContent.getSize());
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
    assertNotNull(binaryContent.getSize());
    assertEquals(data, binaryContent.getData());
  }

  @Test
  void saveIntoBinaryContentTest() {
    BinaryContent binaryContent = new BinaryContent().dataUri(testFileDataURI);
    binaryContentApi.saveIntoContent(binaryContent, testFile.inputStream(),
        binaryContent.getDataUri());

    // Try to load not existing BinaryData
    Optional<BinaryData> binaryData = binaryDataApi.load(binaryContent.getDataUri());

    assertFalse(binaryData.isPresent());

    // The BinaryData was set to the BinaryConent
    assertTrue(binaryContent.isSaveData());
    assertTrue(binaryContent.isLoaded());
    assertNotNull(binaryContent.getData());
    assertNotNull(binaryContent.getSize());
  }

  @Test
  void uploadBinaryContentTest() {
    BinaryContent binaryContent = new BinaryContent().dataUri(testFileDataURI);
    binaryContentApi.uploadContent(binaryContent, testFile, testFileDataURI);

    // Get the BinaryData
    BinaryData binaryData = binaryContentApi.getBinaryData(binaryContent);

    assertNotNull(binaryData);

    // The BinaryData was set to the BinaryConent
    assertFalse(binaryContent.isSaveData());
    assertTrue(binaryContent.isLoaded());
    assertNotNull(binaryContent.getData());
    assertNotNull(binaryContent.getSize());
    assertEquals(binaryData, binaryContent.getData());
  }

  @Test
  void apiObjectRefSaveTest() {
    BinaryContent binaryContent = new BinaryContent();
    TestBinaryContentObject testBinaryContentObject =
        new TestBinaryContentObject().content(binaryContent);
    ApiObjectRef ref =
        new ApiObjectRef(null, testBinaryContentObject, TEST_BINARY_CONTENT_DESCRIPTOR);
    TestBinaryContentObject testBinaryContentObjectWrapper =
        ref.getWrapper(TestBinaryContentObject.class);

    BinaryContent contentWrapper = testBinaryContentObjectWrapper.getContent();
    binaryContentApi.saveIntoContent(contentWrapper, testFile,
        testFileDataURI);

    assertTrue(binaryContent.isSaveData());
    assertTrue(binaryContent.isLoaded());
    assertNotNull(binaryContent.getData());
    assertNotNull(binaryContent.getSize());

    assertTrue(contentWrapper.isLoaded());
    assertNotNull(contentWrapper.getSize());
  }

  @Test
  void apiObjectRefUploadTest() {
    BinaryContent binaryContent = new BinaryContent();
    TestBinaryContentObject testBinaryContentObject =
        new TestBinaryContentObject().content(binaryContent);
    ApiObjectRef ref =
        new ApiObjectRef(null, testBinaryContentObject, TEST_BINARY_CONTENT_DESCRIPTOR);
    TestBinaryContentObject testBinaryContentObjectWrapper =
        ref.getWrapper(TestBinaryContentObject.class);

    BinaryContent contentWrapper = testBinaryContentObjectWrapper.getContent();
    binaryContentApi.uploadContent(contentWrapper, testFile, testFileDataURI);
    BinaryData binaryData = binaryContentApi.getBinaryData(contentWrapper);

    assertNotNull(binaryData);
    assertFalse(binaryContent.isSaveData());
    assertTrue(binaryContent.isLoaded());
    assertNotNull(binaryContent.getData());
    assertNotNull(binaryContent.getSize());

    assertTrue(contentWrapper.isLoaded());
    assertNotNull(contentWrapper.getSize());
  }

  @Test
  void removeDataFromBinaryContentTest() {
    BinaryContent binaryContent = new BinaryContent().dataUri(testFileDataURI);
    binaryContentApi.uploadContent(binaryContent, testFile, testFileDataURI);

    binaryContentApi.removeContent(binaryContent);

    Optional<BinaryData> binaryData = binaryDataApi.load(binaryContent.getDataUri());
    assertFalse(binaryData.isPresent());

    assertThrows(IllegalStateException.class, () -> binaryContentApi.load(binaryContent));
    assertFalse(binaryContent.isSaveData());
    assertFalse(binaryContent.isLoaded());
    assertNull(binaryContent.getDataUri());
    assertNull(binaryContent.getData());
    assertNull(binaryContent.getSize());
  }
}
