package org.smartbit4all.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObjectSerializer;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;

class GsonBinaryDataObjectSerializerTest {

  public static class TestData {

    private URI uri;

    private String data;

    private IncludedTestData includedTestData;

    public URI getUri() {
      return uri;
    }

    public void setUri(URI uri) {
      this.uri = uri;
    }

    public String getData() {
      return data;
    }

    public void setData(String data) {
      this.data = data;
    }

    public IncludedTestData getIncludedTestData() {
      return includedTestData;
    }

    public void setIncludedTestData(IncludedTestData includedTestData) {
      this.includedTestData = includedTestData;
    }

  }

  public static class IncludedTestData {

    private String includedData1;

    public String getIncludedData1() {
      return includedData1;
    }

    public void setIncludedData1(String includedData1) {
      this.includedData1 = includedData1;
    }

    public String getIncludedData2() {
      return includedData2;
    }

    public void setIncludedData2(String includedData2) {
      this.includedData2 = includedData2;
    }

    private String includedData2;

  }

  private String testUri = "testuri:/test.uri";
  private String testText = "testData";
  private String testIncludedText1 = "includedTestData1";
  private String testIncludedText2 = "includedTestData2";

  private TestData createTestData() {
    TestData testData = new TestData();
    testData.setUri(URI.create(testUri));
    testData.setData(testText);
    IncludedTestData includedTestData = new IncludedTestData();
    includedTestData.setIncludedData1(testIncludedText1);
    includedTestData.setIncludedData2(testIncludedText2);
    testData.setIncludedTestData(includedTestData);
    return testData;
  }

  private ApiObjectRef createApiObjectRef(Object testData) {
    Set<Class<?>> testBeans = new HashSet<>();
    testBeans.add(TestData.class);
    testBeans.add(IncludedTestData.class);
    Map<Class<?>, ApiBeanDescriptor> descriptors = ApiBeanDescriptor.of(testBeans);

    ApiObjectRef ref = new ApiObjectRef(null, testData, descriptors);
    return ref;
  }

  private void asserts(TestData dataAfterConversion) {
    assertEquals(testUri, dataAfterConversion.getUri().toString());
    assertEquals(testText, dataAfterConversion.getData());
    assertEquals(testIncludedText1, dataAfterConversion.getIncludedTestData().getIncludedData1());
    assertEquals(testIncludedText2, dataAfterConversion.getIncludedTestData().getIncludedData2());
  }

  @Test
  void simpleSerializationAndDeserializationTest() {
    BinaryDataObjectSerializer serializer = new GsonBinaryDataObjectSerializer();

    TestData testData = createTestData();

    BinaryData jsonBinaryData = serializer.toJsonBinaryData(testData, TestData.class);
    TestData dataAfterConversion = serializer.fromJsonBinaryData(
        jsonBinaryData,
        TestData.class)
        .get();

    asserts(dataAfterConversion);
  }

  @Test
  void wrappedSerializationAndDeserializationTest() {
    BinaryDataObjectSerializer serializer = new GsonBinaryDataObjectSerializer();

    TestData testData = createTestData();

    ApiObjectRef refTestData = createApiObjectRef(testData);

    BinaryData jsonBinaryData =
        serializer.toJsonBinaryData(refTestData.getWrapper(TestData.class), TestData.class);
    TestData dataAfterConversion = serializer.fromJsonBinaryData(
        jsonBinaryData,
        TestData.class)
        .get();

    asserts(dataAfterConversion);
  }

  @Test
  @Disabled
  void partiallyWrappedSerializationAndDeserializationTest() {
    BinaryDataObjectSerializer serializer = new GsonBinaryDataObjectSerializer();

    TestData testData = createTestData();

    ApiObjectRef refIncludedTestData = createApiObjectRef(testData.getIncludedTestData());
    IncludedTestData wrappedIncludedTestData =
        refIncludedTestData.getWrapper(IncludedTestData.class);
    testData.setIncludedTestData(wrappedIncludedTestData);

    BinaryData jsonBinaryData = serializer.toJsonBinaryData(testData, TestData.class);
    TestData dataAfterConversion = serializer.fromJsonBinaryData(
        jsonBinaryData,
        TestData.class)
        .get();

    asserts(dataAfterConversion);
  }

}
