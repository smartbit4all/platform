package org.smartbit4all.core.io.utility;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.binarydata.BinaryData;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

/**
 * Indexed multipart binary store backed by a single file.
 *
 * File layout:
 * 
 * <pre>
 * +----------------------+ 0
 * | Index[0].start  (8B) |
 * | Index[0].length (8B) |
 * | Index[1].start  (8B) |
 * | Index[1].length (8B) |
 * | ...                  |
 * | Index[N-1].start     |
 * | Index[N-1].length    |  -> indexRegionSize = N * 16 bytes
 * +----------------------+ indexRegionSize
 * |   Binary data ...    |  (segments appended sequentially)
 * +----------------------+
 * </pre>
 *
 * Index entry semantics: - startOffset: absolute offset from file start (long) - length: byte
 * length (long) - UNSET = -1 for both fields means "no data"
 *
 * No concurrent writes (as requested).
 */
public final class IndexedMultipartStore implements Closeable {

  public static final long UNSET = -1L;

  private final RandomAccessFile raf;
  private final FileChannel channel;
  private final File file;
  private final int slots;
  private final long indexRegionSize; // bytes
  private long nextAppendOffset; // absolute file offset

  private IndexedMultipartStore(File file, int slots, boolean create) throws IOException {
    if (slots <= 0)
      throw new IllegalArgumentException("slots must be > 0");
    this.file = Objects.requireNonNull(file, "file");
    this.slots = slots;
    this.indexRegionSize = slots * 16L;

    // Ensure parent dirs exist
    File parent = file.getParentFile();
    if (parent != null && !parent.exists() && !parent.mkdirs()) {
      throw new IOException("Cannot create parent directories: " + parent);
    }

    this.raf = new RandomAccessFile(file, "rw");
    this.channel = raf.getChannel();

    if (create) {
      // Truncate and initialize header
      raf.setLength(0);
      raf.setLength(indexRegionSize);
      initIndex();
      this.nextAppendOffset = indexRegionSize;
    } else {
      if (raf.length() < indexRegionSize) {
        raf.setLength(indexRegionSize); // extend header if short
      }
      this.nextAppendOffset = computeNextAppendOffset();
    }
  }

  /** Creates (or truncates) a store and initializes the index with UNSET entries. */
  public static IndexedMultipartStore create(File file, int slots) throws IOException {
    return new IndexedMultipartStore(file, slots, true);
  }

  /** Opens an existing store (or creates header if file is smaller than header). */
  public static IndexedMultipartStore open(File file, int slots) throws IOException {
    return new IndexedMultipartStore(file, slots, false);
  }

  /**
   * Append-write a payload and update index entry {@code index}. If that index already had a
   * segment, it is "moved" (space is not reclaimed).
   *
   * @return the absolute start offset where data was written.
   */
  public long writeAt(int index, BinaryData data) throws IOException {
    Objects.requireNonNull(data, "data");
    checkIndex(index);

    long len = data.length();
    if (len < 0)
      throw new IllegalArgumentException("Negative length");

    long start = nextAppendOffset;

    // Append the payload bytes
    try (InputStream in = data.inputStream2();
        FileOutputStream out = new FileOutputStream(file, true)) {
      // Ensure file pointer is at append offset (out appends; align raf too)
      raf.seek(start);

      byte[] buf = new byte[8192];
      int r;
      long written = 0;
      while ((r = in.read(buf)) >= 0) {
        out.write(buf, 0, r);
        written += r;
      }
      if (written != len) {
        // If BinaryData length was an estimate, this guards index correctness.
        len = written;
      }
      out.getFD().sync();
    }

    // Write index entry: [start, len]
    writeIndexEntry(index, start, len);

    // Advance append pointer
    nextAppendOffset = start + len;
    if (nextAppendOffset < indexRegionSize) {
      nextAppendOffset = indexRegionSize; // never go before data region
    }
    return start;
  }

  /** Reads the segment at index into a BinaryData (zero-copy view via ByteSource slice). */
  public BinaryData readAt(int index) throws IOException {
    checkIndex(index);
    Entry e = readIndexEntry(index);
    if (!e.isPresent())
      return null;

    // Validate bounds
    long fileLen = raf.length();
    long end = e.start + e.length;
    if (end > fileLen)
      throw new IOException("Segment exceeds file length: idx=" + index);

    // Build a ByteSource slice and wrap as BinaryData
    ByteSource slice = Files.asByteSource(file).slice(e.start, e.length);
    return new BinaryData(slice);
  }

  /** Convenience: write a run of contents to indices startIndex..startIndex+contents.length-1. */
  public void writeMultipart(int startIndex, BinaryData... contents) throws IOException {
    if (contents == null || contents.length == 0)
      return;
    if (startIndex < 0)
      throw new IllegalArgumentException("startIndex must be >= 0");
    for (int i = 0; i < contents.length; i++) {
      writeAt(startIndex + i, contents[i]);
    }
  }

  /** Convenience: read {@code count} entries starting at {@code startIndex}. */
  public List<BinaryData> readMultipart(int startIndex, int count) throws IOException {
    if (count <= 0)
      return Collections.emptyList();
    if (startIndex < 0)
      throw new IllegalArgumentException("startIndex must be >= 0");
    List<BinaryData> res = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      BinaryData bd = readAt(startIndex + i);
      res.add(bd); // can be null if UNSET
    }
    return res;
  }

  /** Returns a presence bitset where set bits have data. */
  public BitSet presenceMap() throws IOException {
    BitSet bs = new BitSet(slots);
    for (int i = 0; i < slots; i++) {
      if (readIndexEntry(i).isPresent())
        bs.set(i);
    }
    return bs;
  }

  public int slots() {
    return slots;
  }

  public long indexRegionSize() {
    return indexRegionSize;
  }

  public long nextAppendOffset() {
    return nextAppendOffset;
  }

  @Override
  public void close() throws IOException {
    channel.close();
    raf.close();
  }

  // ---------------- internal helpers ----------------

  private void initIndex() throws IOException {
    raf.seek(0);
    for (int i = 0; i < slots; i++) {
      raf.writeLong(UNSET);
      raf.writeLong(UNSET);
    }
  }

  private void writeIndexEntry(int idx, long start, long length) throws IOException {
    long pos = idx * 16L;
    raf.seek(pos);
    raf.writeLong(start);
    raf.writeLong(length);
  }

  private Entry readIndexEntry(int idx) throws IOException {
    long pos = idx * 16L;
    raf.seek(pos);
    long start = raf.readLong();
    long len = raf.readLong();
    return new Entry(start, len);
  }

  private long computeNextAppendOffset() throws IOException {
    long fileLen = raf.length();
    long max = Math.max(fileLen, indexRegionSize);
    for (int i = 0; i < slots; i++) {
      Entry e = readIndexEntry(i);
      if (e.isPresent()) {
        long candidate = e.start + e.length;
        if (candidate > max)
          max = candidate;
      }
    }
    return Math.max(max, indexRegionSize);
  }

  private void checkIndex(int idx) {
    if (idx < 0 || idx >= slots) {
      throw new IndexOutOfBoundsException("index " + idx + " out of [0.." + (slots - 1) + "]");
    }
  }

  /** Value object for one index entry. */
  private static final class Entry {
    final long start;
    final long length;

    Entry(long s, long l) {
      this.start = s;
      this.length = l;
    }

    boolean isPresent() {
      return start != UNSET && length != UNSET;
    }
  }
}
