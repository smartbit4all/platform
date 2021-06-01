package org.smartbit4all.storage.fs;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.smartbit4all.types.binarydata.BinaryData;

public class FileIOTest {

  @Test
  void fileWriteAndReadTest() {
    BinaryData data = new BinaryData("test text".getBytes());
    URI uri = URI.create("teststoragefs:/testfolder/testfile.fs");
    
    FileIO.write(TestFileUtil.testFsRootFolder(), uri, data);
    assertNotNull(FileIO.read(TestFileUtil.testFsRootFolder(), uri));
    
    FileIO.write(TestFileUtil.testFsRootFolder(), uri, data);
    assertNotNull(FileIO.read(TestFileUtil.testFsRootFolder(), uri));
  }
  
}
