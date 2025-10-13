package org.smartbit4all.core.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.io.utility.IndexedMultipartStore;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.core.utility.StringConstant;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  private Random rnd = new Random();

  @Test
  @Disabled
  void randomAcccessTest() throws IOException {
    String versionFileName = "testio/version00";
    File versionFile = new File(versionFileName);
    int indexSize = 12;
    {
      versionFile.createNewFile();
      RandomAccessFile raf = new RandomAccessFile(versionFile, "rws");
      for (long i = 0; i < 1024; i++) {
        org.assertj.core.api.Assertions.assertThat(raf.getFilePointer()).isEqualTo(i * indexSize);
        raf.writeLong(-1);
        raf.writeInt(-1);
      }
      raf.getFD().sync();
      raf.close();

    }
    List<byte[]> contents = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      byte[] b = new byte[rnd.nextInt(100)];
      rnd.nextBytes(b);
      contents.add(b);
    }

    int i = 0;
    for (byte[] bs : contents) {
      RandomAccessFile raf = new RandomAccessFile(versionFile, "rws");
      long dataPosition;
      if (i == 0) {
        dataPosition = 1024;
      } else {
        // Seak to the prevoius entry and read the position of the previous one and add the length.
        raf.seek((i - 1) * indexSize);
        dataPosition = raf.readLong() + raf.readInt();
      }
      // Write the next entry data position and length to the index table.
      raf.writeLong(dataPosition);
      raf.writeInt(bs.length);
      raf.seek(dataPosition);
      raf.write(bs);
      raf.getFD().sync();
      raf.close();
      i++;
    }

    // Read all the data
    List<byte[]> contentsRead = new ArrayList<>();
    for (long j = 0; j < 6; j++) {
      RandomAccessFile raf = new RandomAccessFile(versionFile, "rws");
      raf.seek(j * indexSize);
      long dataPosition = raf.readLong();
      int length = raf.readInt();
      byte[] bytes = new byte[length];
      raf.read(bytes);
      contentsRead.add(bytes);
    }

    org.assertj.core.api.Assertions.assertThat(contentsRead).containsExactlyElementsOf(contents);

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


  // ---------- helpers ----------

  private static BinaryData bd(byte[] bytes) {
    return new BinaryData(bytes);
  }

  private static byte[] bytes(String s) {
    return s.getBytes(StandardCharsets.UTF_8);
  }

  private static byte[] readAll(BinaryData bd) throws Exception {
    try (InputStream in = bd.inputStream2()) {
      return in.readAllBytes(); // Java 9+
    }
  }

  // ---------- tests ----------

  @Test
  void createInitializesHeaderAndAppendOffset() throws Exception {
    File f = TestFileUtil.testFsRootFolder().toPath()
        .resolve("createInitializesHeaderAndAppendOffset.bin").toFile();
    int slots = 8;

    try (IndexedMultipartStore store = IndexedMultipartStore.create(f, slots)) {
      assertEquals(slots, store.slots());
      long header = slots * 16L;
      assertEquals(header, store.indexRegionSize());
      assertEquals(header, store.nextAppendOffset());
    }

    assertTrue(f.exists());
    assertTrue(f.length() >= slots * 16L);
  }

  @Test
  void writeAndReadSingleSegment() throws Exception {
    File f =
        TestFileUtil.testFsRootFolder().toPath().resolve("writeAndReadSingleSegment.bin").toFile();

    try (IndexedMultipartStore store = IndexedMultipartStore.create(f, 4)) {
      byte[] payload = bytes("hello");
      long start = store.writeAt(1, bd(payload));
      assertTrue(start >= store.indexRegionSize());

      BinaryData got = store.readAt(1);
      assertNotNull(got);
      assertArrayEquals(payload, readAll(got));
    }
  }

  @Test
  void unsetEntryReturnsNull() throws Exception {
    File f = TestFileUtil.testFsRootFolder().toPath().resolve("unsetEntryReturnsNull.bin").toFile();
    try (IndexedMultipartStore store = IndexedMultipartStore.create(f, 3)) {
      assertNull(store.readAt(0));
      assertNull(store.readAt(2));
    }
  }

  @Test
  void persistenceAcrossReopen() throws Exception {
    File f =
        TestFileUtil.testFsRootFolder().toPath().resolve("persistenceAcrossReopen.bin").toFile();
    byte[] a = bytes("A");
    byte[] b = bytes("BEE");

    try (IndexedMultipartStore store = IndexedMultipartStore.create(f, 5)) {
      store.writeAt(0, bd(a));
      store.writeAt(4, bd(b));
    }

    try (IndexedMultipartStore store = IndexedMultipartStore.open(f, 5)) {
      BinaryData r0 = store.readAt(0);
      BinaryData r4 = store.readAt(4);

      assertNotNull(r0);
      assertNotNull(r4);
      assertNull(store.readAt(1));

      assertArrayEquals(a, readAll(r0));
      assertArrayEquals(b, readAll(r4));
      assertEquals(5 * 16L, store.indexRegionSize());
    }
  }

  @Test
  void presenceMapReflectsWrittenEntries() throws Exception {
    File f = TestFileUtil.testFsRootFolder().toPath()
        .resolve("presenceMapReflectsWrittenEntries.bin").toFile();
    try (IndexedMultipartStore store = IndexedMultipartStore.create(f, 6)) {
      store.writeAt(1, bd(new byte[] {1}));
      store.writeAt(3, bd(new byte[] {3, 3, 3}));
      store.writeAt(5, bd(new byte[] {5}));

      BitSet bs = store.presenceMap();
      assertFalse(bs.get(0));
      assertTrue(bs.get(1));
      assertFalse(bs.get(2));
      assertTrue(bs.get(3));
      assertFalse(bs.get(4));
      assertTrue(bs.get(5));
    }
  }

  @Test
  void overwriteAppendsAndUpdatesIndex() throws Exception {
    File f = TestFileUtil.testFsRootFolder().toPath().resolve("overwriteAppendsAndUpdatesIndex.bin")
        .toFile();
    try (IndexedMultipartStore store = IndexedMultipartStore.create(f, 2)) {
      long afterHeader = store.nextAppendOffset();
      long firstStart = store.writeAt(0, bd(new byte[] {1, 2, 3}));
      long afterFirst = store.nextAppendOffset();

      long secondStart = store.writeAt(0, bd(new byte[] {9, 9})); // overwrite same index -> append
      long afterSecond = store.nextAppendOffset();

      assertEquals(afterHeader, firstStart);
      assertTrue(afterFirst > firstStart);
      assertEquals(afterFirst, secondStart);

      // Index should now point to the latest (2-byte) payload
      BinaryData now = store.readAt(0);
      assertArrayEquals(new byte[] {9, 9}, readAll(now));

      // File contains both payloads back-to-back (header excluded)
      byte[] raw = Files.asByteSource(f).read();
      int header = (int) store.indexRegionSize();
      byte[] concat = Arrays.copyOfRange(raw, header, raw.length);
      assertArrayEquals(new byte[] {1, 2, 3, 9, 9}, concat);
      assertTrue(afterSecond > afterFirst);
    }
  }

  @Test
  void writeMultipartAndReadMultipartSequentially() throws Exception {
    File f = TestFileUtil.testFsRootFolder().toPath()
        .resolve("writeMultipartAndReadMultipartSequentially.bin").toFile();
    try (IndexedMultipartStore store = IndexedMultipartStore.create(f, 10)) {
      int startIdx = 4;
      byte[] d1 = bytes("foo");
      byte[] d2 = bytes("barbaz");
      byte[] d3 = bytes("Q");

      store.writeMultipart(startIdx, bd(d1), bd(d2), bd(d3));

      // Direct reads
      assertArrayEquals(d1, readAll(store.readAt(4)));
      assertArrayEquals(d2, readAll(store.readAt(5)));
      assertArrayEquals(d3, readAll(store.readAt(6)));

      // Batch read
      List<BinaryData> parts = store.readBatch(startIdx, 3);
      assertEquals(3, parts.size());
      assertArrayEquals(d1, readAll(parts.get(0)));
      assertArrayEquals(d2, readAll(parts.get(1)));
      assertArrayEquals(d3, readAll(parts.get(2)));
    }
  }

  @Test
  void readMultipartIncludesNullsForUnsetSlots() throws Exception {
    File f = TestFileUtil.testFsRootFolder().toPath()
        .resolve("readMultipartIncludesNullsForUnsetSlots.bin").toFile();
    try (IndexedMultipartStore store = IndexedMultipartStore.create(f, 5)) {
      store.writeAt(1, bd(new byte[] {1}));
      store.writeAt(3, bd(new byte[] {3}));

      List<BinaryData> parts = store.readBatch(0, 5);
      assertEquals(5, parts.size());
      assertNull(parts.get(0));
      assertNotNull(parts.get(1));
      assertNull(parts.get(2));
      assertNotNull(parts.get(3));
      assertNull(parts.get(4));
    }
  }

  @Test
  void boundsChecks() throws Exception {
    File f = TestFileUtil.testFsRootFolder().toPath().resolve("store.bin").toFile();
    try (IndexedMultipartStore store = IndexedMultipartStore.create(f, 2)) {
      assertThrows(IndexOutOfBoundsException.class, () -> store.writeAt(-1, bd(new byte[1])));
      assertThrows(IndexOutOfBoundsException.class, () -> store.writeAt(2, bd(new byte[1])));
      assertThrows(IndexOutOfBoundsException.class, () -> store.readAt(2));
    }
  }

  @Nested
  class LargerPayloads {
    @Test
    void writeAndReadLargerPayload() throws Exception {
      File f = TestFileUtil.testFsRootFolder().toPath().resolve("writeAndReadLargerPayload.bin")
          .toFile();
      byte[] big = new byte[128 * 1024]; // 128 KiB
      for (int i = 0; i < big.length; i++)
        big[i] = (byte) (i & 0xFF);

      try (IndexedMultipartStore store = IndexedMultipartStore.create(f, 3)) {
        store.writeAt(2, bd(big));
        BinaryData got = store.readAt(2);
        assertNotNull(got);
        assertArrayEquals(big, readAll(got));
      }
    }
  }
}
