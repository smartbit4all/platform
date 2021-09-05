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

  /**
   * Reads an object uri based given extension file if exists.
   * 
   * @param rootFolder
   * @param uri
   * @param extension The specific extension as post fix for the file.
   * @return
   */
  public static BinaryData read(File rootFolder, URI uri, String extension) {
    if (uri == null || uri.getPath() == null) {
      return null;
    }
    File file = new File(rootFolder, uri.getPath() + extension);
    return getFileBinaryData(file);
  }

  private static BinaryData getFileBinaryData(File file) {
    if (file.exists() && file.isFile()) {
      return new BinaryData(file, false);
    }
    return null;
  }

  public static void write(File rootFolder, URI uri, BinaryData content) {
    File newFile = getFileByUri(rootFolder, uri);
    write(newFile, content);
  }

  /**
   * Write the file with specific extension based on the uri.
   * 
   * @param rootFolder
   * @param uri
   * @param extension
   * @param content
   */
  public static void write(File rootFolder, URI uri, String extension, BinaryData content) {
    File newFile = getFileByUri(rootFolder, uri, extension);
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

}
