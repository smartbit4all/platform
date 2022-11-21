package org.smartbit4all.core.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.core.utility.StringConstant;
import com.google.common.io.ByteStreams;

public class FileIOTest {

  @BeforeAll
  static void beforeAll() throws IOException {
    TestFileUtil.initTestDirectory();
  }

  @AfterAll
  static void afterAll() throws IOException {
    TestFileUtil.clearTestDirectory();
  }

  @Test
  void constructObjectPathByIndexWithHexaStructure() {
    assertEquals("/00", FileIO.constructObjectPathByIndexWithHexaStructure(0));
    assertEquals("/01", FileIO.constructObjectPathByIndexWithHexaStructure(1));
    assertEquals("/0A", FileIO.constructObjectPathByIndexWithHexaStructure(10));
    assertEquals("/FF", FileIO.constructObjectPathByIndexWithHexaStructure(255));
    assertEquals("/1/00", FileIO.constructObjectPathByIndexWithHexaStructure(256));
    assertEquals("/1/01", FileIO.constructObjectPathByIndexWithHexaStructure(257));
    assertEquals("/1/FE", FileIO.constructObjectPathByIndexWithHexaStructure(510));
    assertEquals("/1/FF", FileIO.constructObjectPathByIndexWithHexaStructure(511));
    assertEquals("/2/00", FileIO.constructObjectPathByIndexWithHexaStructure(512));
    {
      long index = 256 * 16 - 1;
      System.out.println(index);
      assertEquals("/F/FF", FileIO.constructObjectPathByIndexWithHexaStructure(index));
      System.out.println(++index);
      assertEquals("/1/0/00", FileIO.constructObjectPathByIndexWithHexaStructure(index));
    }
    {
      long index = 256 * 256 - 1;
      System.out.println(index);
      assertEquals("/F/F/FF", FileIO.constructObjectPathByIndexWithHexaStructure(index));
      System.out.println(++index);
      assertEquals("/1/0/0/00", FileIO.constructObjectPathByIndexWithHexaStructure(index));
    }
    {
      long index = 256 * 256 * 16 - 1;
      System.out.println(index);
      assertEquals("/F/F/F/FF", FileIO.constructObjectPathByIndexWithHexaStructure(index));
      System.out.println(++index);
      assertEquals("/1/0/0/0/00", FileIO.constructObjectPathByIndexWithHexaStructure(index));
    }
    {
      long index = 256 * 256 * 256 - 1;
      System.out.println(index);
      assertEquals("/F/F/F/F/FF", FileIO.constructObjectPathByIndexWithHexaStructure(index));
      System.out.println(++index);
      assertEquals("/1/0/0/0/0/00", FileIO.constructObjectPathByIndexWithHexaStructure(index));
    }
    {
      long index = 256 * 256 * 256 * 16 - 1;
      System.out.println(index);
      assertEquals("/F/F/F/F/F/FF", FileIO.constructObjectPathByIndexWithHexaStructure(index));
      System.out.println(++index);
      assertEquals("/1/0/0/0/0/0/00", FileIO.constructObjectPathByIndexWithHexaStructure(index));
    }
    {
      long index = Long.MAX_VALUE;
      System.out.println(index);
      assertEquals("/7/F/F/F/F/F/F/F/F/F/F/F/F/F/FF",
          FileIO.constructObjectPathByIndexWithHexaStructure(index));
    }
  }

  @Test
  void multipartFileTest() throws IOException {
    BinaryData data1 = new BinaryData("first".getBytes());
    BinaryData data2 = new BinaryData("second".getBytes());
    BinaryData data3 = new BinaryData("third".getBytes());
    File multipartFile = new File(TestFileUtil.testFsRootFolder(), "/multipart/multipart.o");
    FileIO.writeMultipart(multipartFile, data1, data2);

    FileIO.writeMultipart(multipartFile, data1, data2, data3);

    List<BinaryData> readMultipart = FileIO.readMultipart(multipartFile);
    assertEquals(3, readMultipart.size());
    assertEquals("first", new String(ByteStreams.toByteArray(readMultipart.get(0).inputStream())));
    assertEquals("second", new String(ByteStreams.toByteArray(readMultipart.get(1).inputStream())));
    assertEquals("third", new String(ByteStreams.toByteArray(readMultipart.get(2).inputStream())));

  }

  @Test
  void uriFragment() throws IOException {
    URI uri = URI.create("scheme:/path1/path2#frag1/frag2");
    String[] fragPath = PathUtility.decomposePath(uri.getFragment());
    assertEquals(2, fragPath.length);
    assertEquals("frag1", fragPath[0]);
    assertEquals("frag2", fragPath[1]);
  }

  @Test
  void fileWriteAndReadTest() throws IOException {

    BinaryData data = new BinaryData("test text".getBytes());
    String testFolder = "testfolder";

    URI uri = URI.create("teststoragefs:/" + testFolder + "/testfile.fs1");

    FileIO.write(TestFileUtil.testFsRootFolder(), uri, data);
    assertNotNull(FileIO.read(TestFileUtil.testFsRootFolder(), uri));

    FileIO.write(TestFileUtil.testFsRootFolder(), uri, data);
    assertNotNull(FileIO.read(TestFileUtil.testFsRootFolder(), uri));
    assertEquals(1, readAllFiles(testFolder).size());

    URI uri2 = URI.create("teststoragefs:/" + testFolder + "/extrapath/testfile2.fs2");
    FileIO.write(TestFileUtil.testFsRootFolder(), uri2, data);
    assertEquals(2, readAllFiles(testFolder).size());
    assertEquals(1, readAllFiles(testFolder, "fs1").size());
    assertEquals(1, readAllFiles(testFolder, "fs2").size());
    assertEquals(0, readAllFiles(testFolder, "unknownextension").size());

    FileIO.delete(TestFileUtil.testFsRootFolder(), uri);
    assertEquals(1, readAllFiles(testFolder).size());

    FileIO.delete(TestFileUtil.testFsRootFolder(), uri);
    assertEquals(1, readAllFiles(testFolder).size());

    FileIO.delete(TestFileUtil.testFsRootFolder(), uri2);
    assertEquals(0, readAllFiles(testFolder).size());
  }

  /**
   * Special test case examining the result of the {@link FileIOLockTestRuntime}. It is the test.txt
   * that is analyzed. If the test.txt is missing then the test is succeeded to avoid unnecessary
   * pipeline fails.
   * 
   * @throws IOException
   */
  @Test
  void testLockTestRuntime() throws IOException {
    File file = new File("test.txt");
    if (!file.exists()) {
      return;
    }
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line = reader.readLine();
    Map<String, List<Integer>> numbersByInstances = new HashMap<>();
    while (line != null) {
      String[] split = line.split(StringConstant.SEMICOLON);
      if (split != null && split.length == 3) {
        List<Integer> list = numbersByInstances.computeIfAbsent(split[0], s -> new ArrayList<>());
        list.add(Integer.valueOf(split[2]));
      }
      line = reader.readLine();
    }
    reader.close();
    for (List<Integer> list : numbersByInstances.values()) {
      // If we have all the items.
      Assertions.assertEquals(1000, list.size());
      // The list must be monotone from 0 - 999
      for (int i = 0; i < 1000; i++) {
        Assertions.assertEquals(i, list.get(i));
      }
    }
  }

  @Test
  void fileNameValidationTest() {
    String validFilename = "validFile.txt";
    String invalidFilename1 = "valid/File.txt";
    String invalidFilename2 = new String(new byte[] {(byte) 0x7F}) + "valid/File.txt";

    assertEquals(true, FileIO.checkfileName(validFilename));
    assertEquals(false, FileIO.checkfileName(invalidFilename1));
    assertEquals(false, FileIO.checkfileName(invalidFilename2));


  }

  private List<BinaryData> readAllFiles(String testFolder) throws IOException {
    return readAllFiles(testFolder, null);
  }

  private List<BinaryData> readAllFiles(String testFolder, String fileExtension)
      throws IOException {
    List<BinaryData> allFiles =
        FileIO.readAllFiles(TestFileUtil.testFsRootFolder().toPath().resolve(testFolder).toFile(),
            fileExtension);

    return allFiles;
  }

}
