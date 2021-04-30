/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.types.binarydata;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.smartbit4all.types.binarydata.BinaryData.AutoCloseInputStream;
import com.google.common.io.ByteStreams;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    InputStream inputStream = data.inputStream();
    byte[] byteArray = ByteStreams.toByteArray(inputStream);


    if (inputStream instanceof AutoCloseInputStream) {
      assertEquals(null, ((AutoCloseInputStream) inputStream).getInnerStream(),
          "The inner stream must be closed after reading the whole content.");
    }

    String actual = new String(byteArray);

    assertEquals(value1 + value2 + value3, actual);
  }

}
