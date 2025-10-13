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
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.DeflateCompressingInputStream;
import org.springframework.util.ObjectUtils;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.primitives.Longs;

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

  private IndexedMultipartStore(File file, int slots, boolean initiate) throws IOException {
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

    boolean newFile = false;
    if (!file.exists()) {
      newFile = true;
    }
    this.raf = new RandomAccessFile(file, "rw");
    this.channel = raf.getChannel();

    if (initiate || newFile) {
      // Truncate and initialize header
      raf.setLength(0);
      raf.setLength(indexRegionSize);
      initIndex();
      this.nextAppendOffset = indexRegionSize;
    } else {
      // Not necessary for this kind of usage. There is no way to extend the index after start using
      // the indexed file.
      // if (raf.length() < indexRegionSize) {
      // raf.setLength(indexRegionSize); // extend header if short
      // }
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

  /** Opens an existing store (or creates header if file is smaller than header). */
  public static IndexedMultipartStore openOrCreate(File file, int slots) throws IOException {
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


  /**
   * Append-write a payload and update index entry {@code index}. If that index already had a
   * segment, it is "moved" (space is not reclaimed).
   *
   * @return the absolute start offset where data was written.
   */
  // public long writeAtMultipart(int index, BinaryData... contents) throws IOException {
  // if (ObjectUtils.isEmpty(contents)) {
  // throw new IllegalArgumentException("The contents to write is empty.");
  // }
  // checkIndex(index);
  //
  // List<BinaryData> contentList = Arrays.asList(contents);
  // long start = nextAppendOffset;
  // long nextStart = start;
  //
  // List<BinaryDataCRCRecord> crcRecords = new ArrayList<>(contents.length);
  //
  // long len = 0;
  //
  // for (BinaryData binaryData : contents) {
  // // Write the length first and the content next.
  // ByteSource byteSource = new ByteSource() {
  //
  // @Override
  // public InputStream openStream() throws IOException {
  // InputStream inputStream = binaryData.inputStream2();
  // if (inputStream instanceof HashingInputStream) {
  // crcRecords.add(new BinaryDataCRCRecord(binaryData, (HashingInputStream) inputStream));
  // }
  // return new DeflateCompressingInputStream(inputStream, Deflater.BEST_COMPRESSION, true);
  // }
  // };
  // // Append the payload bytes
  // try (InputStream in = byteSource.openStream();
  // FileOutputStream out = new FileOutputStream(file, true)) {
  // // Ensure file pointer is at append offset (out appends; align raf too)
  // raf.seek(nextStart + Longs.BYTES);
  //
  // byte[] buf = new byte[8192];
  // int r;
  // @SuppressWarnings("resource")
  // CountingOutputStream cntOut = new CountingOutputStream(out);
  // while ((r = in.read(buf)) >= 0) {
  // cntOut.write(buf, 0, r);
  // }
  // long count = cntOut.getCount();
  //
  // out.flush();
  // out.getFD().sync();
  // out.getChannel().position(nextStart);
  // out.write(Longs.toByteArray(count));
  // out.getFD().sync();
  // len += count + Longs.BYTES;
  // nextStart += count + Longs.BYTES;
  // }
  // }
  //
  //
  // for (BinaryDataCRCRecord binaryDataCRCRecord : crcRecords) {
  // binaryDataCRCRecord.check();
  // }
  //
  // // Write index entry: [start, len]
  // writeIndexEntry(index, start, len);
  //
  // // Advance append pointer
  // nextAppendOffset = start + len;
  // if (nextAppendOffset < indexRegionSize) {
  // nextAppendOffset = indexRegionSize; // never go before data region
  // }
  // return start;
  // }

  public long writeAtMultipart(int index, BinaryData... contents) throws IOException {
    if (ObjectUtils.isEmpty(contents)) {
      throw new IllegalArgumentException("The contents to write is empty.");
    }
    checkIndex(index);

    final long start = nextAppendOffset;
    long nextStart = start; // where the next [len + payload] block will start
    long totalLen = 0L; // sum of all written bytes in this call (including headers)

    final List<BinaryDataCRCRecord> crcRecords = new ArrayList<>(contents.length);

    try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
      for (BinaryData binaryData : contents) {

        // Build the (possibly hashing) input stream and wrap with deflate-on-read
        InputStream payloadIn = new ByteSource() {
          @Override
          public InputStream openStream() throws IOException {
            InputStream in = binaryData.inputStream2();
            if (in instanceof HashingInputStream) {
              crcRecords.add(new BinaryDataCRCRecord(binaryData, (HashingInputStream) in));
            }
            return new DeflateCompressingInputStream(in, Deflater.BEST_COMPRESSION, true);
          }
        }.openStream();

        long payloadCount = 0L;

        // 1) Write payload starting at (nextStart + 8)
        raf.seek(nextStart + Long.BYTES);
        byte[] buf = new byte[8192];
        int r;
        try (InputStream in = payloadIn) {
          while ((r = in.read(buf)) >= 0) {
            raf.write(buf, 0, r);
            payloadCount += r;
          }
        }

        // 2) Seek back and write the length header (number of payload bytes)
        raf.seek(nextStart);
        raf.writeLong(payloadCount); // big-endian; same shape as Guava Longs.toByteArray

        // 3) Advance cursors
        long writtenThisBlock = Long.BYTES + payloadCount;
        totalLen += writtenThisBlock;
        nextStart += writtenThisBlock;
      }

      // Ensure durability (optional, but mirrors your original flush+sync)
      raf.getFD().sync();
    }

    // Verify CRCs (if any)
    for (BinaryDataCRCRecord rec : crcRecords) {
      rec.check();
    }

    // Write index entry for this multipart block
    writeIndexEntry(index, start, totalLen);

    // Advance global append pointer, but never before the data region
    nextAppendOffset = start + totalLen;
    if (nextAppendOffset < indexRegionSize) {
      nextAppendOffset = indexRegionSize;
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

  /** Reads the segment at index into a BinaryData (zero-copy view via ByteSource slice). */
  public List<BinaryData> readMultipart(int index) throws IOException {
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
    ByteSource byteSource = Files.asByteSource(file).slice(e.start, e.length);

    long offset = 0;
    long length = Long.BYTES;
    ByteSource sliceLen = byteSource.slice(offset, length);
    List<BinaryData> result = null;
    try {
      while (!sliceLen.isEmpty()) {
        offset += length;
        length = Longs.fromByteArray(sliceLen.read());
        long myOffset = offset;
        long myLength = length;
        ByteSource byteSourceSlice = new ByteSource() {
          @Override
          public InputStream openStream() throws IOException {
            return new InflaterInputStream(
                byteSource.slice(myOffset, myLength).openBufferedStream(),
                new Inflater(true), 4 * 1024);
          }
        };
        BinaryData binaryData = new BinaryData(byteSourceSlice);
        if (result == null) {
          result = new ArrayList<>();
        }
        result.add(binaryData);
        offset += length;
        length = Long.BYTES;
        sliceLen = byteSource.slice(offset, length);
      }
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to read multipart file " + file, ex);
    }
    return result != null ? result : Collections.emptyList();
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
  public List<BinaryData> readBatch(int startIndex, int count) throws IOException {
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

  public File getFile() {
    return file;
  }

}
