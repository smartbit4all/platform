package org.smartbit4all.storage.fs;

public class StoragePerformanceRecord {

  private long readTime = 0;

  private long readNumber = 0;

  private long writeTime = 0;

  private long writeNumber = 0;

  public void addRead(long time) {
    readTime += time;
    readNumber++;
  }

  public long getReadAvg() {
    if (readNumber != 0) {
      return readTime / readNumber;
    }
    return -1;
  }

  public void addWrite(long time) {
    writeTime += time;
    writeNumber++;
  }

  public long getWriteAvg() {
    if (writeNumber != 0) {
      return writeTime / writeNumber;
    }
    return -1;
  }

}
