package org.smartbit4all.types.binarydata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.BufferedInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.smartbit4all.types.binarydata.BinaryData;
import org.smartbit4all.types.binarydata.BinaryDataOutputStream;

class BinaryDataTest {

  @Test
  void testInMemory() throws Exception {
    BinaryDataOutputStream os = new BinaryDataOutputStream(-1, null);

    testWrite(os);

  }

  @Test
  void testFileOnly() throws Exception {
    BinaryDataOutputStream os = new BinaryDataOutputStream(0, null);

    testWrite(os);

  }

  @Test
  void testChangeWhileWriting() throws Exception {
    BinaryDataOutputStream os = new BinaryDataOutputStream(5, null);

    testWrite(os);

  }

  private void testWrite(BinaryDataOutputStream os) throws IOException {
    String value1 = "123";
    String value2 = "456";
    String value3 = "789";

    os.write(value1.getBytes());

    {
      byte[] bytes = value2.getBytes();
      os.write(bytes, 0, bytes.length);
    }

    {
      byte[] bytes = value3.getBytes();
      for (int i = 0; i < bytes.length; i++) {
        os.write(bytes[i]);
      }
    }

    os.close();

    int length = value1.length() + value2.length() + value3.length();
    assertEquals(length, os.counter());

    os.close();

    BinaryData data = os.data();

    assertEquals(length, data.length());

    BufferedInputStream oSBuf = new BufferedInputStream(data.inputStream());
//
//    String actual = new String(oSBuf.readAllBytes());
//    oSBuf.close();
//
//    assertEquals(value1 + value2 + value3, actual);
  }

}
