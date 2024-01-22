package org.smartbit4all.core.io.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.utility.StringConstant;
import com.google.common.base.Strings;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Longs;

/**
 * The basic file IO for the file system related operations based on objects.
 *
 * @author Peter Boros
 */
public class FileIO {

  private FileIO() {
    // static utility
  }

  /**
   * This crc checksum algorithm is used for validating the temporary files managed by the
   * application. It is necessary to avoid the corrupted BinaryData contents because these belongs
   * to the memory of the application.
   */
  public static final HashFunction crcChecksumFunction = Hashing.crc32();

  private static final Logger log = LoggerFactory.getLogger(FileIO.class);

  private static Random rnd = new Random();

  private static interface InputStreamSupplier {
    InputStream get() throws IOException;
  }

  public static BinaryData read(File rootFolder, URI uri) {
    if (uri == null || uri.getPath() == null) {
      return null;
    }
    File file = new File(rootFolder, uri.getPath());
    return getFileBinaryData(file);
  }

  private static BinaryData getFileBinaryData(File file) {
    if (file.exists() && file.isFile()) {
      return new BinaryData(file);
    }
    return null;
  }

  public static void write(File rootFolder, URI uri, BinaryData content) {
    File newFile = getFileByUri(rootFolder, uri);
    write(newFile, content);
  }

  public static void write(File newFile, BinaryData content) {
    writeFile(newFile, content::inputStream2);
  }

  private static void writeFile(File file, InputStreamSupplier inputStream) {
    long waitTime = 2;
    long fullWaitTime = 0;
    try {
      if (file.getParentFile() != null) {
        file.getParentFile().mkdirs();
      } else {
        file.mkdirs();
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Cannot create file's dir: " + file, e);
    }
    while (true) {
      try (InputStream in = inputStream.get();
          FileOutputStream fos = new FileOutputStream(file)) {
        if (in == null) {
          log.error("Unable to open InputStream for reading the content of {}", file);
        } else {
          ByteStreams.copy(in, fos);
          fos.flush();
          fos.getFD().sync();
          return;
        }
      } catch (FileNotFoundException e) {
        throw new IllegalStateException(
            "Cannot write file because some of the temporary files are missing: " + file, e);
      } catch (IOException e) {
        if (fullWaitTime > 1000) {
          // we waited enough, and still doesn't work, show last exception
          throw new IllegalArgumentException("Cannot write file after several trying: " + file, e);
        }
        // We must try again.
        log.debug("Unable to write " + file, e);
      } catch (Exception e) {
        // Not IOException - we should terminate, something other kind of bad happened
        throw new IllegalArgumentException("Cannot write file: " + file, e);
      }
      try {
        Thread.sleep(waitTime);
      } catch (InterruptedException e) {
        throw new IllegalStateException("Cannot write file: " + file, e);
      }
      fullWaitTime += waitTime;
      waitTime = getNextRandomWaitTime(waitTime);
    }
  }

  /**
   * Multiplies current waitTime by random 1..4
   *
   * @param waitTime
   * @return
   */
  public static long getNextRandomWaitTime(long waitTime) {
    return waitTime * (rnd.nextInt(3) + 1);
  }

  private static class BinaryDataCRCRecord {

    BinaryData binaryData;

    HashingInputStream hashingInputStream;

    public BinaryDataCRCRecord(BinaryData binaryData, HashingInputStream hashingInputStream) {
      super();
      this.binaryData = binaryData;
      this.hashingInputStream = hashingInputStream;
    }

    void check() {
      if (binaryData.getCrcCheckSum() != null && hashingInputStream != null) {
        if (hashingInputStream.hash().asInt() != binaryData.getCrcCheckSum()) {
          throw new IllegalStateException("CRC checksum error (" + binaryData.getCrcCheckSum()
              + " != " + hashingInputStream.hash().asInt() + ") in " + binaryData.toString());
        }
      }
    }

  }

  /**
   * Special write that concatenates multiple {@link BinaryData}s into one single file. To be able
   * to read the contents again the length appears in front of every content.
   *
   * @param newFile
   * @param contents
   */
  public static void writeMultipart(File newFile, BinaryData... contents) {
    if (contents == null || contents.length == 0) {
      return;
    }
    List<ByteSource> byteSources = new ArrayList<>(contents.length * 2);
    List<BinaryDataCRCRecord> crcRecords = new ArrayList<>(contents.length);
    for (BinaryData binaryData : contents) {
      // Write the length first and the content next.
      byteSources.add(ByteSource.wrap(Longs.toByteArray(binaryData.length())));
      byteSources.add(new ByteSource() {

        @Override
        public InputStream openStream() throws IOException {
          InputStream inputStream = binaryData.inputStream2();
          if (inputStream instanceof HashingInputStream) {
            crcRecords.add(new BinaryDataCRCRecord(binaryData, (HashingInputStream) inputStream));
          }
          return inputStream;
        }
      });
    }
    ByteSource byteSource = ByteSource.concat(byteSources);
    writeFile(newFile, byteSource::openStream);
    for (BinaryDataCRCRecord binaryDataCRCRecord : crcRecords) {
      binaryDataCRCRecord.check();
    }
  }

  /**
   * Read the content of the multipart file into {@link BinaryData} objects.
   *
   * @param file The multipart file.
   * @return All the {@link BinaryData} contains the {@link ByteSource} points to the given
   */
  public static List<BinaryData> readMultipart(File file) {
    if (file == null || !file.exists() || !file.isFile()) {
      return null;
    }
    ByteSource byteSource = com.google.common.io.Files.asByteSource(file);
    long offset = 0;
    long length = Long.BYTES;
    ByteSource sliceLen = byteSource.slice(offset, length);
    List<BinaryData> result = null;
    try {
      while (!sliceLen.isEmpty()) {
        offset += length;
        length = Longs.fromByteArray(sliceLen.read());
        ByteSource byteSourceSlice = byteSource.slice(offset, length);
        BinaryData binaryData = new BinaryData(byteSourceSlice);
        if (result == null) {
          result = new ArrayList<>();
        }
        result.add(binaryData);
        offset += length;
        length = Long.BYTES;
        sliceLen = byteSource.slice(offset, length);
      }
    } catch (Exception e) {
      throw new IllegalStateException("Unable to read multipart file " + file, e);
    }
    return result != null ? result : Collections.emptyList();
  }

  public static boolean delete(File rootFolder, URI uri) {
    File file = getFileByUri(rootFolder, uri);
    return file.delete();
  }

  public static boolean delete(File rootFolder, URI uri, String extension) {
    File file = getFileByUri(rootFolder, uri, extension);
    return file.delete();
  }

  public static File getFileByUri(File rootFolder, URI uri) {
    return new File(rootFolder, uri.getPath());
  }

  public static File getFileByUri(File rootFolder, URI uri, String extension) {
    return new File(rootFolder, uri.getPath() + extension);
  }

  public static List<BinaryData> readAllFiles(File rootFolder, String fileExtension)
      throws IOException {
    List<Path> allFilePaths = getAllFilePathsRecuresively(rootFolder, fileExtension);

    if (allFilePaths.isEmpty()) {
      return Collections.emptyList();
    }

    return getAllFileBinaryDatas(allFilePaths);
  }

  private static List<BinaryData> getAllFileBinaryDatas(List<Path> allFilePaths) {
    List<BinaryData> fileBinaryDatas = new ArrayList<>();
    for (Path filePath : allFilePaths) {
      File file = filePath.toFile();
      BinaryData fileBinaryData = getFileBinaryData(file);
      if (fileBinaryData != null) {
        fileBinaryDatas.add(fileBinaryData);
      }
    }

    return fileBinaryDatas;
  }

  private static List<Path> getAllFilePathsRecuresively(File rootFolder, String fileExtension)
      throws IOException {

    List<Path> allFilePaths = new ArrayList<>();

    Files.walkFileTree(rootFolder.toPath(), new SimpleFileVisitor<Path>() {

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile() && file.getFileName() != null) {

          String fileName = file.getFileName().toString();
          if (fileExtension != null) {
            String actualFileExtension =
                com.google.common.io.Files.getFileExtension(fileName);

            if (fileExtension.equals(actualFileExtension)) {
              allFilePaths.add(file);
            }
          } else {
            if (!fileName.equals(".gitignore")) {
              allFilePaths.add(file);
            }
          }
        }
        return FileVisitResult.CONTINUE;
      }

    });

    return allFilePaths;
  }

  private static final int BUCKET_INDEX_MASK = 0xFF;

  /**
   * Constructs the path of an index in a list. To identify the given index exactly we use an
   * algorithm where index is exactly identifies the version and the storage in a file system can be
   * unlimited.
   *
   * @param index
   * @return
   */
  public static final String constructObjectPathByIndexWithHexaStructure(long index) {
    int bucketIndex = (int) (index & BUCKET_INDEX_MASK);
    long bucket = index >> 8;
    StringBuilder sbBucket = new StringBuilder();
    if (bucket > 0) {
      // The path will be the hexadecimal format of the bucket digit by digit
      String hexString = Long.toHexString(bucket).toUpperCase();
      for (int i = 0; i < hexString.length(); i++) {
        sbBucket.append(StringConstant.SLASH);
        sbBucket.append(hexString.charAt(i));
      }
    }
    sbBucket.append(StringConstant.SLASH);
    String bucketIndexHex = Long.toHexString(bucketIndex).toUpperCase();
    if (bucketIndexHex.length() == 1) {
      sbBucket.append(StringConstant.ZERO);
    }
    sbBucket.append(bucketIndexHex);
    return sbBucket.toString();
  }

  /**
   * The lock file method is going to create the new object file as a lock file. The lock file is
   * the new version of the object file with the owner runtime instance and the transaction. To be
   * faster the lock file will be created while copying the content of the original file. The
   * runtime and the transaction will be appended to the last section.
   *
   * @param newLockData The lock data of the current lock session. Runtime and transaction.
   * @param lockFile The lock file to create.
   * @param waitUntil If the parameter is -1 then the operation will try to lock the file wait again
   *        and again. If it's 0 then after the first try it fails and if we set a given time it
   *        will try to get the lock until this wait time.
   * @return The lock file itself with a content that refers to this runtime at least.
   */
  public static final FileLockData lockObjectFile(FileLockData newLockData, File lockFile,
      long waitUntil,
      Predicate<FileLockData> lockValidator)
      throws FileLocked {
    long start = System.currentTimeMillis();
    while (true) {
      try {
        if (!lockFile.getParentFile().exists()) {
          lockFile.getParentFile().mkdirs();
        }
        if (!lockFile.exists()) {
          lockFile.createNewFile();
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
            FileChannel fileChannel = randomAccessFile.getChannel();
            FileLock fileLock = fileChannel.lock()) {
          FileLockData lockData = readFileLockData(fileChannel);
          // Check the validity of the lock. If it's valid then initiate wait. If it's invalid or
          // null
          // then we try to initiate the lock file with locking the file itself.
          if (lockData != null && lockValidator.test(lockData)) {
            // We have a valid lock so we have to decide whether to wait or return without locking.
            long currentTimeMillis = System.currentTimeMillis();
            if (waitUntil != -1 && (currentTimeMillis - start) > waitUntil) {
              throw new FileLocked(lockData);
            }
          } else {
            // The data is invalid in the file so we can rewrite the lock file for our own purposes.
            fileChannel.force(true);
            fileChannel.position(0);
            writeFileLockData(fileChannel, newLockData);
            randomAccessFile.getFD().sync();
            return newLockData;
          }
        }

      } catch (IOException e) {
        long currentTimeMillis = System.currentTimeMillis();
        if (!(waitUntil == -1 || (currentTimeMillis - start) < waitUntil)) {
          throw new FileLocked(null);
        }
      }
    }
  }

  /**
   * The unlock use a file system lock via {@link RandomAccessFile} for the lock file. If it fails
   * then we retry until we successfully read the content of the lock file. If we could read it then
   * we check the validity. In case of a valid lock we have to wait and try again.
   *
   * @param lockFile The lock file.
   * @param waitUntil Defines the waiting policy. -1 means wait forever, 0 means try only once, an
   *        exact number means the waiting for this millisecond.
   * @throws FileLocked If the waiting for the lock is finished.
   */
  public static final void unlockObjectFile(File lockFile, long waitUntil)
      throws FileLocked {
    long start = System.currentTimeMillis();
    while (true) {
      try {
        RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        FileLock fileLock = fileChannel.lock();
        try {
          FileLockData lockData = readFileLockData(fileChannel);
          // Check the validity of the lock. If it's valid then initiate wait. If it's invalid or
          // null
          // then we try to initiate the lock file with locking the file itself.
          // if (Objects.deepEquals(myLockData, lockData)) {
          // We are the locking node so we can truncate the lock file.
          fileChannel.truncate(0);
          // }
          return;
        } finally {
          fileLock.release();
          fileChannel.close();
          randomAccessFile.close();
        }
      } catch (IOException e) {
        long currentTimeMillis = System.currentTimeMillis();
        if (!(waitUntil == -1 || (currentTimeMillis - start) < waitUntil)) {
          throw new FileLocked(null);
        }
      }
    }
  }

  /**
   * Utility function to write the {@link FileLockData} object.
   *
   * @param fileChannel The {@link FileChannel} to write into.
   * @param lockData The lock data to write.
   * @throws IOException
   */
  private static void writeFileLockData(FileChannel fileChannel, FileLockData lockData)
      throws IOException {
    if (lockData != null) {
      writeString(fileChannel, lockData.getRuntimeId());
      writeString(fileChannel, lockData.getTransactionId());
    }
  }

  /**
   * Read the {@link FileLockData} object from a lock file.
   *
   * @param fileChannel The {@link FileChannel} to read from.
   * @return The data object.
   */
  private static FileLockData readFileLockData(FileChannel fileChannel) {
    try {
      return new FileLockData(readString(fileChannel), readString(fileChannel));
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Read a String from the {@link FileChannel}. It reads a four byte integer as a length of the
   * following string. Then read the string assuming utf-8 char set.
   *
   * @param fileChannel The {@link FileChannel} to read from.
   * @return The String read from the FileChannel current position.
   * @throws IOException
   */
  private static String readString(FileChannel fileChannel) throws IOException {
    ByteBuffer lenBuffer = ByteBuffer.wrap(new byte[Integer.BYTES]);
    fileChannel.read(lenBuffer);
    lenBuffer.rewind();
    int length = lenBuffer.getInt();
    ByteBuffer stringBuffer = ByteBuffer.wrap(new byte[length]);
    stringBuffer.rewind();
    fileChannel.read(stringBuffer);
    return new String(stringBuffer.array(), StandardCharsets.UTF_8);
  }

  /**
   * Writes a String into the file channel current position. Writes the length into a four byte
   * integer and the String bytes with utf-8 encoding.
   *
   * @param fileChannel The file channel.
   * @param string The string to write out.
   * @throws IOException
   */
  private static void writeString(FileChannel fileChannel, String string) throws IOException {
    ByteBuffer lenBuffer =
        ByteBuffer.allocate(Integer.BYTES).putInt(string == null ? 0 : string.length());
    lenBuffer.rewind();
    fileChannel.write(lenBuffer);
    if (string != null) {
      ByteBuffer stringBuffer = ByteBuffer.wrap(string.getBytes(StandardCharsets.UTF_8));
      stringBuffer.rewind();
      fileChannel.write(stringBuffer);
    }
  }

  /**
   * The read object file tries to read the object file without any error. It will retry until
   * succeeded with longer wait period to avoid collision with other processes.
   *
   * @param file The object file to read.
   * @throws InterruptedException
   */
  public static final <T> T readFileAtomic(File file, Function<InputStream, T> reader)
      throws InterruptedException {
    long waitTime = 2;
    while (true) {
      if (file == null || !file.exists() || !file.isFile()) {
        return null;
      }
      try (FileInputStream fis = new FileInputStream(file)) {
        return reader.apply(fis);
      } catch (IOException e) {
        // We must try again.
        log.debug("Unable to read {}", file);
        waitTime = getNextRandomWaitTime(waitTime);
      }
      Thread.sleep(waitTime);
    }
  }

  /**
   * The finalization means that we execute an atomic move transactionFile -> objectFile. We can use
   * this function if we have a consistent transaction object. The move can fail because of the read
   * from the object file. If it fails then we can try it again until we succeed.
   *
   * @param transactionFile The transaction file that contains the consistent next version. This is
   *        the source of the movement. It must exists to execute this operation.
   * @param objectFile The object file that is the target of the move. This is optional, if this is
   *        the creation of the object then we have no previous version.
   * @throws InterruptedException If the thread is interrupted we get the original exception.
   */
  public static final void finalizeWrite(File transactionFile, File objectFile)
      throws InterruptedException {
    long waitTime = 2;
    while (true) {
      if (transactionFile == null || !transactionFile.exists() || !transactionFile.isFile()) {
        return;
      }
      try {
        Files.move(transactionFile.toPath(), objectFile.toPath(),
            StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        return;
      } catch (IOException e) {
        // We must try again.
        log.debug("Unable to finalize {} -> {}", transactionFile, objectFile);
        waitTime = getNextRandomWaitTime(waitTime);
        Thread.sleep(waitTime);
      }
    }
  }

  public static void move(File sourceObjectFile, File targetObjectFile)
      throws InterruptedException {
    if (sourceObjectFile != null && sourceObjectFile.exists() && targetObjectFile != null) {
      targetObjectFile.getParentFile().mkdirs();
      long waitTime = 2;
      while (true) {
        if (!sourceObjectFile.exists()) {
          return;
        }
        try {
          Files.move(sourceObjectFile.toPath(), targetObjectFile.toPath(),
              StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
          return;
        } catch (IOException e) {
          // We must try again.
          log.debug("Unable to move {} -> {}", sourceObjectFile, targetObjectFile);
          waitTime = getNextRandomWaitTime(waitTime);
          Thread.sleep(waitTime);
        }
      }
    }
  }

  public static void delete(File file)
      throws InterruptedException {
    if (file != null && file.exists()) {
      long waitTime = 2;
      while (true) {
        if (!file.exists()) {
          return;
        }
        try {
          Files.delete(file.toPath());
          return;
        } catch (IOException e) {
          // We must try again.
          log.debug("Unable to delete {}", file);
          waitTime = getNextRandomWaitTime(waitTime);
          Thread.sleep(waitTime);
        }
      }
    }
  }

  public static Boolean checkfileName(String name) {
    if (Strings.isNullOrEmpty(name)) {
      return false;
    }
    for (String character : StringConstant.INVALID_FILE_CHARS) {
      if (name.contains(character)) {
        return false;
      }
    }
    return true;
  }

}
