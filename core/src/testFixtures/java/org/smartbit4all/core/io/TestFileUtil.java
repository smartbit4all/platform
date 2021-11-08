package org.smartbit4all.core.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  private static Set<String> gitFilesToKeep =
      Stream.of(".keep", ".gitignore")
          .collect(Collectors.toCollection(HashSet::new));

  public static void clearGitDir(File dir) throws IOException {
    clearDir(dir, gitFilesToKeep);
  }

  public static void clearDir(File dir, Set<String> filesToKeep) throws IOException {
    if (dir == null || !dir.exists()) {
      return;
    }

    for (File file : dir.listFiles()) {
      if (!gitFilesToKeep.contains(file.getName())) {
        Files.walk(file.toPath())
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      }
    }
  }

}
