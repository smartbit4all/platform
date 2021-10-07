package org.smartbit4all.core.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.io.utility.FileIO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileIOTest {

  Executor exec = Executors.newFixedThreadPool(10);

  @Test
  void fileLockTest() throws IOException {
    File folder = TestFileUtil.testFsRootFolder();
    // Files.move(null, null, StandardCopyOption.ATOMIC_MOVE);
    File lockFile = new File(folder, "test.lock");
    lockFile.createNewFile();
    StringBuilder checkpoints = new StringBuilder();
    try (RandomAccessFile raf = new RandomAccessFile(lockFile, "rw");
        FileLock lock = raf.getChannel().lock()) {

      exec.execute(() -> {
        try (RandomAccessFile rafInner = new RandomAccessFile(lockFile, "rw");
            FileLock lockInner = raf.getChannel().lock()) {

          checkpoints.append("/2");
        } catch (Exception e) {
          throw new IllegalStateException("Exception occured while locking", e);
        }
      });

      TimeUnit.SECONDS.sleep(2);
      checkpoints.append("/1");
    } catch (Exception e) {
      throw new IllegalStateException("Exception occured while locking", e);
    }
    assertEquals("/1/2", checkpoints);
  }

  @Test
  void fileWriteAndReadTest() throws IOException {
    TestFileUtil.initTestDirectory();

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
