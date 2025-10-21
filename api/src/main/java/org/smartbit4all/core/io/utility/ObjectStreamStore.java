package org.smartbit4all.core.io.utility;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.DeflateCompressingInputStream;
import org.springframework.util.ObjectUtils;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.primitives.Longs;

/**
 * Provides a low-level appendable file storage abstraction that stores arbitrary binary objects
 * ({@link BinaryData}) in a compressed, length-prefixed binary stream format.
 * <p>
 * Each object written into this store is serialized as a segment of the following structure:
 * 
 * <pre>
 *   [8 bytes: payload length][payload bytes (deflated)][8 bytes: payload length]
 * </pre>
 * 
 * This symmetric layout allows both forward and backward traversal of the file for incremental
 * reads or recovery of objects.
 * </p>
 * <p>
 * Objects are written sequentially using a tracked file offset ({@link #currentOffset}), which
 * defines where the next write will occur. The store supports reading back individual entries by
 * specifying their starting offset using {@link #readFrom(long)}.
 * </p>
 *
 * <p>
 * Compression is performed using the {@link Deflater#BEST_COMPRESSION} level, and decompression is
 * handled transparently when reading. Optionally, if the {@link BinaryData} provides a
 * {@link HashingInputStream}, CRC verification is also performed to ensure data integrity.
 * </p>
 *
 * <p>
 * Typical use cases include incremental binary object storage, versioned or snapshot data
 * archiving, or large blob persistence with integrity checks and efficient deflate compression.
 * </p>
 *
 * <h3>Thread Safety</h3> This class is <strong>not thread-safe</strong>. External synchronization
 * is required when accessed concurrently.
 *
 * <h3>Resource Management</h3> Always close instances explicitly or use try-with-resources to
 * ensure file descriptors are released.
 *
 * @see BinaryData
 * @see DeflateCompressingInputStream
 */
public final class ObjectStreamStore implements Closeable {

  private final RandomAccessFile raf;
  private final FileChannel channel;
  private final File file;

  private static final Logger log = LoggerFactory.getLogger(ObjectStreamStore.class);

  /**
   * The absolute file offset within the underlying file where the next object will be appended.
   * This pointer advances automatically after each {@link #write(BinaryData)} call.
   */
  private long currentOffset;

  /**
   * Creates a new {@code ObjectStreamStore} bound to the specified file and initializes it for
   * writing and reading binary segments. The constructor ensures that the target directory exists
   * and that the file is open in read/write mode.
   *
   * @param file the target file that will back this store (must not be {@code null})
   * @param nextAppendOffset the offset position in the file where the next append should begin;
   *        must be greater than zero
   * @throws IOException if the file cannot be created, opened, or truncated
   * @throws IllegalArgumentException if {@code nextAppendOffset <= 0}
   */
  private ObjectStreamStore(File file, long nextAppendOffset) throws IOException {
    if (nextAppendOffset < 0)
      throw new IllegalArgumentException("appending position must be > 0");
    this.file = Objects.requireNonNull(file, "file");
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

    if (newFile) {
      // Truncate and initialize header
      raf.setLength(0);
    }
  }

  /**
   * Creates a new (or truncates an existing) object stream store and initializes it for writing
   * from the beginning of the file.
   *
   * @param file the target file to create or truncate
   * @return a new {@code ObjectStreamStore} ready for writing
   * @throws IOException if the file cannot be created, truncated, or initialized
   */
  public static ObjectStreamStore create(File file) throws IOException {
    return new ObjectStreamStore(file, 0);
  }

  /**
   * Opens an existing store for reading or appending, or creates a new one if the file does not yet
   * exist.
   * <p>
   * This method allows specifying a known append offset (typically recovered from metadata) so that
   * new entries can be appended without overwriting previous data.
   * </p>
   *
   * @param file the store file to open
   * @param nextAppendOffset the next write offset, representing the end of the last written segment
   * @return an initialized {@code ObjectStreamStore} instance
   * @throws IOException if the file cannot be opened or created
   */
  public static ObjectStreamStore openOrCreate(File file, long nextAppendOffset)
      throws IOException {
    return new ObjectStreamStore(file, nextAppendOffset);
  }

  /**
   * Writes a single {@link BinaryData} object into the store at the current append offset.
   * <p>
   * The method compresses the provided binary content using a {@link Deflater} at
   * {@link Deflater#BEST_COMPRESSION} level and writes the following structure:
   * </p>
   * 
   * <pre>
   * [8 bytes: payload length][payload bytes (deflated)][8 bytes: payload length]
   * </pre>
   * <ul>
   * <li>The first {@code long} stores the compressed payload length.</li>
   * <li>The payload bytes follow immediately after the header.</li>
   * <li>A final {@code long} repeats the payload length for reverse iteration or validation.</li>
   * </ul>
   * <p>
   * The file is synced after the write operation to ensure durability, and any available CRC checks
   * from {@link HashingInputStream} wrappers are verified upon completion.
   * </p>
   *
   * @param content the binary content to be written (must not be {@code null} or empty)
   * @return the starting offset (in bytes) of the written segment
   * @throws IOException if a write, compression, or synchronization error occurs
   * @throws IllegalArgumentException if {@code content} is empty
   */
  public long write(BinaryData content) throws IOException {
    if (ObjectUtils.isEmpty(content)) {
      throw new IllegalArgumentException("The contents to write is empty.");
    }

    final long start = currentOffset;
    long nextStart = start;
    long totalLen = 0L;
    final List<BinaryDataCRCRecord> crcRecords = new ArrayList<>();

    try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
      InputStream payloadIn = new ByteSource() {
        @Override
        public InputStream openStream() throws IOException {
          InputStream in = content.inputStream2();
          if (in instanceof HashingInputStream) {
            crcRecords.add(new BinaryDataCRCRecord(content, (HashingInputStream) in));
          }
          return new DeflateCompressingInputStream(in, Deflater.BEST_COMPRESSION, true);
        }
      }.openStream();

      long payloadCount = 0L;
      raf.seek(nextStart + Long.BYTES);

      byte[] buf = new byte[8192];
      int r;
      try (InputStream in = payloadIn) {
        while ((r = in.read(buf)) >= 0) {
          raf.write(buf, 0, r);
          payloadCount += r;
        }
      }

      raf.writeLong(payloadCount);
      raf.seek(nextStart);
      raf.writeLong(payloadCount);

      long writtenThisBlock = Long.BYTES * 2 + payloadCount;
      totalLen += writtenThisBlock;
      nextStart += writtenThisBlock;

      raf.getFD().sync();
    }

    for (BinaryDataCRCRecord crcRecord : crcRecords) {
      crcRecord.check();
    }

    currentOffset = start + totalLen;
    return currentOffset;
  }

  /**
   * Reads and decompresses the next or previous binary object segment relative to the current
   * cursor position within the stream store.
   * <p>
   * Depending on the value of the {@code forward} flag, this method navigates either forward or
   * backward in the file and reconstructs the corresponding {@link BinaryData} entry.
   * </p>
   *
   * <p>
   * Each segment in the file follows the format:
   * </p>
   * 
   * <pre>
   * [8 bytes: payload length][payload bytes (deflated)][8 bytes: payload length]
   * </pre>
   * <p>
   * When {@code forward} is {@code true}, the method:
   * </p>
   * <ol>
   * <li>Reads the 8-byte length header located at the current offset.</li>
   * <li>Reads and inflates the subsequent compressed payload.</li>
   * <li>Returns a {@link BinaryData} instance backed by an {@link InflaterInputStream} that
   * transparently provides decompressed content.</li>
   * </ol>
   *
   * <p>
   * When {@code forward} is {@code false}, the method performs the reverse operation: it locates
   * the length footer immediately before the current offset, determines the payload size, and reads
   * the preceding compressed segment.
   * </p>
   *
   * <p>
   * The returned {@link BinaryData} object provides a lazy, stream-based view of the decompressed
   * bytes without fully loading them into memory.
   * </p>
   *
   * @param forward if {@code true}, reads the next segment after the current offset; if
   *        {@code false}, reads the previous segment before the current offset
   * @return a {@link BinaryData} instance representing the decompressed object content, or
   *         {@code null} if the cursor is already at the end (for forward reads) or at the
   *         beginning (for backward reads)
   * @throws IOException if the file cannot be accessed, read, or decompressed
   * @throws IllegalArgumentException if the current offset exceeds the file boundaries
   * @throws IllegalStateException if the length header or footer is invalid (e.g., negative or
   *         inconsistent with file length)
   */
  public BinaryData read(boolean forward) throws IOException {

    long fileLen = raf.length();
    if (forward && currentOffset >= fileLen) {
      return null;
    } else if (!forward && currentOffset <= 0) {
      return null;
    }

    ByteSource byteSource = Files.asByteSource(file);
    ByteSource sliceLen = forward ? byteSource.slice(currentOffset, Long.BYTES)
        : byteSource.slice(currentOffset - Long.BYTES, Long.BYTES);
    long length = Longs.fromByteArray(sliceLen.read());
    if (length < 0) {
      throw new IllegalStateException("The data length is less then zero: length=" + length);
    }
    if (length > fileLen) {
      throw new IllegalStateException(
          "The data segment is invalid, overflow the file length: startFrom=" + currentOffset
              + ", length=" + length);
    }

    final long myOffset = currentOffset;
    ByteSource byteSourceSlice = new ByteSource() {
      @Override
      public InputStream openStream() throws IOException {
        return new InflaterInputStream(
            byteSource.slice(
                forward ? (myOffset + Long.BYTES) : (myOffset - Long.BYTES - length),
                length).openBufferedStream(),
            new Inflater(true), 4 * 1024);
      }
    };
    currentOffset += (forward ? 1 : -1) * (Long.BYTES * 2 + length);
    return new BinaryData(byteSourceSlice);
  }

  /**
   * Returns the next append offset in the file.
   * <p>
   * This value represents the absolute file position where the next {@link #write(BinaryData)}
   * operation will start writing.
   * </p>
   *
   * @return the absolute offset of the next append position
   */
  public long nextAppendOffset() {
    return currentOffset;
  }

  public long length() throws IOException {
    return raf.length();
  }

  /**
   * Closes the underlying file channel and {@link RandomAccessFile}.
   * <p>
   * This releases all system resources associated with this store. Once closed, the store cannot be
   * used again for reading or writing.
   * </p>
   *
   * @throws IOException if an I/O error occurs while closing the underlying file handles
   */
  @Override
  public void close() throws IOException {
    channel.close();
    raf.close();
  }

  public long getCurrentOffset() {
    return currentOffset;
  }

  public void setCurrentOffset(long currentOffset) {
    this.currentOffset = currentOffset;
  }

}
