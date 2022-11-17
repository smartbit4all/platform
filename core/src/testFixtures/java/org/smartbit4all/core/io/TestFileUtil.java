package org.smartbit4all.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import org.springframework.util.StreamUtils;

public class TestFileUtil {

  public static final String SRC_TEST_RESOURCES_TEST_FS = "/test-fs";

  public static String getTestFsPath() {
    return java.nio.file.Paths
        .get("")
        .toAbsolutePath().toString() + SRC_TEST_RESOURCES_TEST_FS;
  }

  public static File testFsRootFolder() {
    return testFsRootFolderPath().toFile();
  }

  public static Path testFsRootFolderPath() {
    return Paths.get(getTestFsPath());
  }

  public static void initTestDirectory() throws IOException {
    clearTestDirectory();
    createTestDirectory();
  }

  public static void createTestDirectory() throws IOException {
    Files.createDirectories(testFsRootFolderPath());
  }

  public static void clearTestDirectory() throws IOException {
    clearGitDir(testFsRootFolder());
    testFsRootFolder().delete();
  }

  private static Collection<String> gitFilesToKeep = Arrays.asList(".keep", ".gitignore");

  public static void clearGitDir(File dir) throws IOException {
    clearDir(dir, gitFilesToKeep);
  }

  public static void clearDir(File dir, Collection<String> filesToKeep) throws IOException {
    if (dir == null || !dir.exists()) {
      return;
    }

    for (File file : dir.listFiles()) {
      if (!filesToKeep.contains(file.getName())) {
        Files.walk(file.toPath())
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      }
    }
  }

  /**
   * Copies the file found in the current project's test resources directory with the specified name
   * to the test file system directory.
   * 
   * <p>
   * This method should be invoked whenever a test relies on the contents of a given file, ensuring
   * the original copy is not affected by executing the test.
   * 
   * @param name the {@code String} name of the file to be copied, not null
   * @return the {@code File} object pointing to the copy result
   */
  public static File copyFileFromTestResourcesToTestFs(String name) {
    String fileToCopy = Paths.get("").toAbsolutePath().toString() + "/src/test/resources/" + name;
    File newFile = new File(new File(getTestFsPath()), name);
    try {
      FileOutputStream fos = new FileOutputStream(newFile);
      InputStream in = new FileInputStream(fileToCopy);
      StreamUtils.copy(in, fos);
      in.close();
      fos.close();
      return newFile;
    } catch (Exception e) {
      throw new IllegalArgumentException("Unable to copy file: " + fileToCopy, e);
    }
  }

}
