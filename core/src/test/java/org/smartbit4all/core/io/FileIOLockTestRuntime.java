package org.smartbit4all.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import org.apache.logging.log4j.util.Strings;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.io.utility.FileLockData;
import org.smartbit4all.core.io.utility.FileLocked;
import org.smartbit4all.core.utility.StringConstant;
import com.google.common.io.ByteStreams;

public class FileIOLockTestRuntime {

  private static final byte[] EMPTY_BYTES = new byte[0];
  public static final File fileo = new File("test.o");
  public static final File filet = new File("test.t");
  public static final File fileresult = new File("test.txt");
  public static final File file = new File("test.l");

  private static final String runtimeId = UUID.randomUUID().toString();

  private static final Random rnd = new Random();

  public static void main(String[] args)
      throws InterruptedException, FileLocked, IOException {
    long start = System.currentTimeMillis();
    long waitTime = 0;
    // Preparation to have the result file.
    if (!fileresult.exists()) {
      Files.createFile(fileresult.toPath());
    }
    byte[] bytes = null;
    boolean standalone = false;
    for (int i = 0; i < 1000; i++) {

      // Check if the object file contains something else.
      byte[] currentBytes = FileIO.readFileAtomic(fileo, is -> {
        try {
          return ByteStreams.toByteArray(is);
        } catch (IOException e) {
          return EMPTY_BYTES;
        }
      });
      if (!standalone) {
        int tries = 0;
        // Wait until another instance modify the
        while (bytes != null && Arrays.equals(bytes, currentBytes)) {
          int wait = rnd.nextInt(20);
          waitTime += wait;
          Thread.sleep(wait);
          currentBytes = FileIO.readFileAtomic(fileo, is -> {
            try {
              return ByteStreams.toByteArray(is);
            } catch (IOException e) {
              return EMPTY_BYTES;
            }
          });
          tries++;
          if (tries > 50) {
            // If we found that we are alone we don't wait any more. The last jvm detects that there
            // is no need to wait.
            standalone = true;
            break;
          }
        }
      }

      String transactionId = UUID.randomUUID().toString();
      FileLockData fld = new FileLockData(runtimeId, transactionId);
      FileIO.lockObjectFile(fld, file, -1, l -> {
        return l != null && !Strings.isEmpty(l.getTransactionId());
      });
      // Write the log file and the object
      bytes =
          (runtimeId + StringConstant.SEMICOLON + transactionId + StringConstant.SEMICOLON + i
              + StringConstant.NEW_LINE).getBytes();
      {
        FileOutputStream fos = new FileOutputStream(fileresult, true);
        fos.write(bytes);
        fos.close();
      }
      {
        // Write the transaction file
        FileOutputStream fos = new FileOutputStream(filet);
        fos.write(bytes);
        fos.close();
      }
      // Finalize the transaction file like the StorageFS do.
      FileIO.finalizeWrite(filet, fileo);
      FileIO.unlockObjectFile(file, -1);
    }
    long end = System.currentTimeMillis();
    System.out.println("Running time: " + (end - start - waitTime));
  }

}
