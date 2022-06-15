package org.smartbit4all.core.io;

import java.io.IOException;

public class TestFSCleaner {

  public TestFSCleaner() {
    super();
    try {
      TestFileUtil.clearTestDirectory();
    } catch (IOException e) {
      throw new RuntimeException("Error while clearing test folder!");
    }
  }


}
