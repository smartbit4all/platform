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

  public final long getReadTime() {
    return readTime;
  }

  public final void setReadTime(long readTime) {
    this.readTime = readTime;
  }

  public final long getReadNumber() {
    return readNumber;
  }

  public final void setReadNumber(long readNumber) {
    this.readNumber = readNumber;
  }

  public final long getWriteTime() {
    return writeTime;
  }

  public final void setWriteTime(long writeTime) {
    this.writeTime = writeTime;
  }

  public final long getWriteNumber() {
    return writeNumber;
  }

  public final void setWriteNumber(long writeNumber) {
    this.writeNumber = writeNumber;
  }

}
