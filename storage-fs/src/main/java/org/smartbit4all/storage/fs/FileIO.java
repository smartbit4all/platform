package org.smartbit4all.storage.fs;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.smartbit4all.types.binarydata.BinaryData;

public class FileIO {

  public static BinaryData read(File rootFolder, URI uri) {
    if (uri == null || uri.getPath() == null) {
      return null;
    }
    File documentFile = new File(rootFolder, uri.getPath());
    if (documentFile.exists() && documentFile.isFile()) {
      return new BinaryData(documentFile, false);
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

  public static boolean delete(File rootFolder, URI uri) {
    File file = getFileByUri(rootFolder, uri);
    return file.delete();
  }
  
  public static File getFileByUri(File rootFolder, URI uri) {
    return new File(rootFolder, uri.getPath());
  }
  
}
