package org.smartbit4all.core.io;

import java.io.IOException;
import org.springframework.beans.factory.InitializingBean;

/**
 * This way the {@link TestFileUtil#clearTestDirectory()} will run when spring beans are
 * initialized, so it won't clear the files that are created at
 * {@link InitializingBean#afterPropertiesSet()}. If the {@link TestFileUtil#clearTestDirectory()}
 * code is called in our test, it will clear all files created by our beans.
 * 
 * @author Zoltan Suller
 */
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
