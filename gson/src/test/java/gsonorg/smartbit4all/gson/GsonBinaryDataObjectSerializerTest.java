package gsonorg.smartbit4all.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.smartbit4all.gson.GsonBinaryDataObjectSerializer;
import org.smartbit4all.types.binarydata.BinaryData;
import org.smartbit4all.types.binarydata.BinaryDataObjectSerializer;

class GsonBinaryDataObjectSerializerTest {

  public static class TestData {

    private URI uri;

    private String data;

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

  }

  @Test
  void simpleSerializationAndDeserializationTest() {
    BinaryDataObjectSerializer serializer = new GsonBinaryDataObjectSerializer();

    String testUri = "testuri:/test.uri";
    String testText = "testData";

    TestData testData = new TestData();
    testData.setUri(URI.create(testUri));
    testData.setData(testText);

    BinaryData jsonBinaryData = serializer.toJsonBinaryData(testData, TestData.class);
    TestData dataAfterConversion = serializer.fromJsonBinaryData(
        jsonBinaryData,
        TestData.class)
        .get();
    
    assertEquals(testUri, dataAfterConversion.getUri().toString());
    assertEquals(testText, dataAfterConversion.getData());
  }

}
