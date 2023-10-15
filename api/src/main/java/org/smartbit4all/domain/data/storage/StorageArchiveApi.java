package org.smartbit4all.domain.data.storage;

public interface StorageArchiveApi {
  public static final String SLASH = "/";
  public static final String BACKSLASH = "\\";
  public static final String ZERO = "0";

  void startArchive(String archiveConfigFile);
}
