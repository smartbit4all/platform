package org.smartbit4all.core.io.utility;

public class FileLocked extends Exception {

  private FileLockData lockData;

  public FileLocked(FileLockData lockData) {
    super("The given object is locked.");
    this.lockData = lockData;
  }

}
