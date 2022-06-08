package org.smartbit4all.core.utility;

import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryData;

class ImageUtilsTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void testResize() throws IOException {
    BinaryData resizeImage = ImageUtils.resizeImage(
        BinaryData.of(
            new FileInputStream("src/test/resources/utility/original.png")),
        64, 64);
    BinaryData expected =
        BinaryData.of(new FileInputStream("src/test/resources/utility/resize.png"));

    Assertions.assertEquals(expected.length(), resizeImage.length());

  }

}
