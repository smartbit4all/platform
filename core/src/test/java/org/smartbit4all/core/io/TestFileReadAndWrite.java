package org.smartbit4all.core.io;

import java.io.FileInputStream;
import java.io.IOException;

public class TestFileReadAndWrite {

  public static void main(String[] args) throws IOException {
    FileInputStream fis = new FileInputStream(TestFileCreateAndDelete.fileo);
    String result;

    try {
      byte[] bytes = new byte["apple".length()];
      {
        for (int i = 0; i < bytes.length; i++) {
          bytes[i] = (byte) fis.read();
        }
        result = new String(bytes);
      }
    } finally {
      fis.close();
    }

    System.out.println(result);

    if (TestFileCreateAndDelete.fileo.exists()) {
      System.out.println(TestFileCreateAndDelete.fileo.getPath());
    }
  }

}
