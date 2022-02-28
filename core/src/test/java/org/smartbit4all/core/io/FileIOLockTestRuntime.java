package org.smartbit4all.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;
import java.util.UUID;
import org.apache.logging.log4j.util.Strings;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.io.utility.FileLockData;
import org.smartbit4all.core.io.utility.FileLocked;
import org.smartbit4all.core.utility.StringConstant;

public class FileIOLockTestRuntime {

  public static final File fileresult = new File("test.txt");
  public static final File file = new File("test.l");

  private static final String runtimeId = UUID.randomUUID().toString();

  private static final Random rnd = new Random();

  public static void main(String[] args)
      throws InterruptedException, FileLocked, IOException {
    long start = System.currentTimeMillis();
    long waitTime = 0;
    if (!fileresult.exists()) {
      Files.createFile(fileresult.toPath());
    }
    for (int i = 0; i < 1000; i++) {
      int wait = rnd.nextInt(20);
      waitTime += wait;
      Thread.sleep(wait);
      String transactionId = UUID.randomUUID().toString();
      FileLockData fld = new FileLockData(runtimeId, transactionId);
      FileIO.lockObjectFile(fld, file, -1, l -> {
        return l != null && !Strings.isEmpty(l.getTransactionId());
      });
      // TODO Write the log file...
      FileOutputStream fos = new FileOutputStream(fileresult, true);
      fos.write(
          (runtimeId + StringConstant.SEMICOLON + transactionId + StringConstant.SEMICOLON + i
              + StringConstant.NEW_LINE).getBytes());
      fos.close();
      FileIO.unlockObjectFile(fld, file, -1);
    }
    long end = System.currentTimeMillis();
    System.out.println("Running time: " + (end - start - waitTime));
  }

}
