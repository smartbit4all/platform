package org.smartbit4all.api.binarydata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.net.URI;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;

public class SerializeBinaryContentTest {

  @Test
  void saveLoadBinaryDataTest() {

    Gson gson = new Gson();
    BinaryContent originalContent = new BinaryContent();
    originalContent
        .fileName("testfile.txt")
        .mimeType("test_mimetype")
        .uri(URI.create("testcontent:/test1"))
        .dataUri(URI.create("testfs:/testfolder/testfile.txt"))
        .loaded(true)
        .saveData(true);

    // Serialize BinaryContent
    String json = gson.toJson(originalContent);

    // Deserialize BinaryContent
    BinaryContent deserializedContent = gson.fromJson(json, BinaryContent.class);

    assertEquals(originalContent.getFileName(), deserializedContent.getFileName());
    assertEquals(originalContent.getMimeType(), deserializedContent.getMimeType());
    assertEquals(originalContent.getUri(), deserializedContent.getUri());
    assertEquals(originalContent.getDataUri(), deserializedContent.getDataUri());
    assertFalse(deserializedContent.isLoaded());
    assertFalse(deserializedContent.isSaveData());
  }


}
