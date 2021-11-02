package org.smartbit4all.core.io.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.utility.StringConstant;
import com.google.common.io.ByteSource;
import com.google.common.primitives.Longs;

/**
 * The basic file IO for the file system related operations based on objects.
 * 
 * @author Peter Boros
 */
public class FileIO {

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
    try (InputStream in = content.inputStream()) {

      newFile.mkdirs();
      Files.copy(in, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      in.close();

    } catch (Exception e) {
      throw new IllegalArgumentException("Cannot write file: " + newFile, e);
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
    for (BinaryData binaryData : contents) {
      // Write the length first and the content next.
      byteSources.add(ByteSource.wrap(Longs.toByteArray(binaryData.length())));
      byteSources.add(new ByteSource() {

        @Override
        public InputStream openStream() throws IOException {
          return binaryData.inputStream();
        }
      });
    }
    try (InputStream in = ByteSource.concat(byteSources).openStream()) {

      newFile.getParentFile().mkdirs();
      System.out.println(newFile);
      Files.copy(in, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

    } catch (Exception e) {
      throw new IllegalArgumentException("Cannot write file: " + newFile, e);
    }
  }

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
        ByteSource sourceBinaryData = byteSource.slice(offset, length);
        BinaryData binaryData = BinaryData.of(sourceBinaryData.openStream());
        if (result == null) {
          result = new ArrayList<>();
        }
        result.add(binaryData);
        offset += length;
        length = Long.BYTES;
        sliceLen = byteSource.slice(offset, length);
      }
    } catch (IOException e) {
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

}
