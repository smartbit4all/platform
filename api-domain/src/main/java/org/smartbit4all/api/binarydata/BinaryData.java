/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.api.binarydata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.hash.Funnels;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

/**
 * The BinaryData is responsible for Binary Large Objects that could be stored in temporary files
 * instead of memory. Using this type in the data structure we can avoid the
 * {@link OutOfMemoryError} exceptions at JVM level. We can substitute the
 * {@link ByteArrayOutputStream} and the {@link ByteArrayInputStream} in our code and that's all.
 * There is a limit that remains in the memory but if we have more byte then it switch to temp file.
 * The {@link BinaryData} uses the Java temp file management API.
 * 
 * If the #deleteDataFile is true then the {@link #dataFile} if any is removed after loosing the
 * reference to {@link BinaryData} itself. The removal is scheduled so the deletion of the file is
 * not immediate. Normally the creation of a {@link BinaryData} with {@link BinaryDataOutputStream}
 * set this to true, because in this case a temp file could be created. Any other situation like
 * using an existing file must be managed by the programmer.
 * 
 * The {@link BinaryData} can also encapsulate {@link ByteSource} that is a Guava construction for
 * accessing part of Files. In this way if we have a large file with many {@link BinaryData} inside
 * then we can use the {@link ByteSource} as a pointer to the original content and there is no need
 * to copy it into another format.
 * 
 * @author Peter Boros
 */
public class BinaryData {

  private static final byte[] EMPTY_BYTES = new byte[0];

  /**
   * 8 Kb memory limit by default.
   */
  private static final int MEMORY_LIMIT = 0x2000;

  private static final Logger log = LoggerFactory.getLogger(BinaryData.class);

  /**
   * If the binary data is memory based then this field contains the byte[]. Else this is null.
   */
  private byte[] data;

  /**
   * If the binary data is temp file based then this is the temp file reference.
   */
  private File dataFile;

  /**
   * If the {@link BinaryData} is initiated as {@link ByteSource} then it's an non mutable content
   * as usual and can be accessed via {@link InputStream}.
   */
  private ByteSource byteSource;

  /**
   * If true then at the end of life cycle the data file must be deleted explicitly.
   * 
   */
  private boolean deleteDataFile = false;

  /**
   * The number of bytes stored in the binary data.
   */
  private long length;


  /**
   * The hash of the stored content. Could be used for identifying the same content without
   * comparing them deeply. The hash could be the result of the construction and must be calculated
   * during the streaming process. In HEX format.
   */
  private String hash;

  /**
   * The binary content usually contains the binary data of a file with known mime type. It could be
   * useful when saving, storing or viewing the content. Can be used to identify the extension of
   * the file to save.
   */
  private String mimeType;

  /**
   * The reference queue for queuing the temp files to delete after finalizing the
   * {@link BinaryData} itself.
   */
  public static final BlockingQueue<File> dataFilesPurgeQueue =
      new LinkedBlockingQueue<>();

  public static final int purgeDataFiles() {
    File file;
    int removed = 0;
    while ((file = dataFilesPurgeQueue.poll()) != null) {
      if (file != null && file.exists()) {
        try {
          file.delete();
        } catch (Exception e) {
          log.warn("Unable to delete temp file " + file);
        }
      }
      removed++;
    }
    return removed;
  }

  /**
   * Constructs the binary data based on a byte array.
   * 
   * @param data The content.
   */
  public BinaryData(byte[] data) {
    super();
    this.data = data;
    this.length = data.length;
  }

  /**
   * Constructs the binary data based on a file. Usually this file is a temp file but the
   * {@link BinaryData} can work on normal files also with caution.
   * 
   * @param dataFile The file that contains the binary data.
   */
  public BinaryData(File dataFile) {
    super();
    this.dataFile = dataFile;
    this.length = dataFile.length();
  }

  /**
   * Constructs the binary data based on a file. Usually this file is a temp file but the
   * {@link BinaryData} can work on normal files also with caution.
   * 
   * @param byteSource The {@link ByteSource} that contains all the data. A valid ByteSource with
   *        known size must be used to avoid unnecessary reading and {@link IOException}.
   * @throws IOException If the {@link ByteSource} is not available and / or the size is not
   *         available.
   */
  public BinaryData(ByteSource byteSource) throws IOException {
    super();
    this.byteSource = byteSource;
    this.length = byteSource.size();
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    if (deleteDataFile) {
      dataFilesPurgeQueue.add(dataFile);
    }
  }

  public static final class AutoCloseInputStream extends InputStream {

    private InputStream is = null;

    public AutoCloseInputStream(InputStream is) {
      super();
      this.is = is;
    }

    public final InputStream getInnerStream() {
      return is;
    }

    @Override
    public final int read() throws IOException {
      if (is == null) {
        return -1;
      }
      int result = is.read();
      if (result == -1) {
        is.close();
        is = null;
      }
      return result;
    }

    @Override
    public final int read(byte[] b) throws IOException {
      if (is == null) {
        return -1;
      }
      int result = is.read(b);
      if (result == -1) {
        is.close();
        is = null;
      }
      return result;
    }

    @Override
    public final int read(byte[] b, int off, int len) throws IOException {
      if (is == null) {
        return -1;
      }
      int result = is.read(b, off, len);
      if (result == -1) {
        is.close();
        is = null;
      }
      return result;
    }

    @Override
    public final int available() throws IOException {
      return is != null ? is.available() : 0;
    }

    @Override
    public final void close() throws IOException {
      if (is != null) {
        is.close();
        is = null;
      }
    }

    @Override
    public synchronized void mark(int readlimit) {
      if (is != null) {
        is.mark(readlimit);
      }
    }

    @Override
    public final boolean markSupported() {
      return is != null ? is.markSupported() : false;
    }

    @Override
    public synchronized void reset() throws IOException {
      if (is != null) {
        is.read();
      }
    }

  }


  /**
   * If we would like to read the whole binary content then we can do this using this input stream.
   * 
   * @return
   */
  public InputStream inputStream() {
    if (data != null) {
      // In this case we have all the data in memory. We can use the ByteArrayInputStream
      return new ByteArrayInputStream(data);
    } else if (byteSource != null) {
      try {
        return new AutoCloseInputStream(byteSource.openStream());
      } catch (IOException e) {
        return constructEmptyData(
            "The BinaryData doesn't have the a valid ByteSource with the content. Assume that the content is empty!",
            e);
      }
    } else {
      try {
        return new AutoCloseInputStream(new FileInputStream(getDataFile()));
      } catch (IOException e) {
        return constructEmptyData(
            "The BinaryData doesn't have the temp file with the content. Assume that the content is empty!",
            e);
      }
    }
  }

  /**
   * 
   * @param message
   * @param e
   * @return
   */
  private final InputStream constructEmptyData(String message, IOException e) {
    log.error(message, e);
    data = EMPTY_BYTES;
    return new ByteArrayInputStream(data);
  }

  /**
   * The binary content usually contains the binary data of a file with known mime type. It could be
   * useful when saving, storing or viewing the content. Can be used to identify the extension of
   * the file to save.
   * 
   * @return
   */
  public String mimeType() {
    return mimeType;
  }

  /**
   * The binary content usually contains the binary data of a file with known mime type. It could be
   * useful when saving, storing or viewing the content. Can be used to identify the extension of
   * the file to save.
   * 
   * @param mimeType The Guava or Spring MediaType can be used to fill this value.
   */
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * The number of bytes in content.
   * 
   * @return
   */
  public long length() {
    return length;
  }

  /**
   * The hash of the content if it was calculated during the construction.
   * 
   * @return The hash if available. Else we get null!
   */
  public synchronized String hash() throws IOException {
    // temporary solution
    if (hash == null) {
      Hasher hasher = Hashing.sha256().newHasher();
      ByteStreams.copy(inputStream(), Funnels.asOutputStream(hasher));
      hash = hasher.hash().toString();
    }
    return hash;
  }

  /**
   * The hash of the content if it was calculated during the construction.
   * 
   * @return The hash if available. Else we get null!
   */
  public String hashIfPresent() {
    return hash;
  }

  /**
   * Set the hash of the binary data. Doesn't check the value! Use it with caution.
   * 
   * @param hash The hash key.
   */
  void setHash(String hash) {
    this.hash = hash;
  }

  /**
   * Use it with caution it will contain the data if and only if it is stored in the memory. The
   * file or byte source is not processed to produce the byte array! USE THE {@link #inputStream()}
   * INSTEAD! {@link ByteStreams#toByteArray(InputStream)} can be used as utility to do so.
   * 
   * @return The {@link #data} array if any!
   */
  public byte[] getData() {
    return data;
  }

  /**
   * Use it with caution it will contain the data file if and only if it is stored in data file. The
   * byte array or byte source is not processed to produce the data file! USE THE
   * {@link #inputStream()} INSTEAD!
   * 
   * @return The {@link #dataFile} if any!
   */
  public final File getDataFile() {
    return dataFile;
  }

  /**
   * Use it with caution it will contain the {@link ByteSource} if and only if it is stored in
   * {@link ByteSource}. The byte array or file is not processed to produce the data file! USE THE
   * {@link #inputStream()} INSTEAD!
   * 
   * @return The {@link #byteSource} if any!
   */
  public final ByteSource getByteSource() {
    return byteSource;
  }

  /**
   * Constructs a {@link BinaryData} instance by copying the content.
   * 
   * @param is The input stream.
   * @return Null of the is is null or not readable for any reason.
   */
  public static final BinaryData of(InputStream is) {
    if (is == null) {
      return null;
    }
    BinaryDataOutputStream bdos;
    try {
      bdos = new BinaryDataOutputStream(MEMORY_LIMIT);
      ByteStreams.copy(is, bdos);
      bdos.close();
      is.close();
      return bdos.data();
    } catch (Exception e) {
      log.error("Unable to process the given InputSream", e);
      return null;
    }
  }

  /**
   * The BinaryData itself is not object of the StorageApi because the lack of URI. It doesn't have
   * identity just a programming concept.
   * 
   * @return The object for save into a Storage.
   */
  public BinaryDataObject asObject() {
    return new BinaryDataObject(this);
  }

  /**
   * If it's true then the {@link #dataFile} if any is removed after loosing the reference to
   * {@link BinaryData} itself. The removal is scheduled so the deletion of the file is not
   * immediate.
   * 
   * @return
   */
  final boolean isDeleteDataFile() {
    return deleteDataFile;
  }

  /**
   * If it's true then the {@link #dataFile} if any is removed after loosing the reference to
   * {@link BinaryData} itself. The removal is scheduled so the deletion of the file is not
   * immediate. Normally the creation of a {@link BinaryData} with {@link BinaryDataOutputStream}
   * set this to true, because in this case a temp file could be created. Any other situation like
   * using an existing file must be managed by the programmer.
   * 
   * @param deleteDataFile
   */
  final void setDeleteDataFile(boolean deleteDataFile) {
    this.deleteDataFile = deleteDataFile;
  }

  public RandomAccessFile asRandomAccessFile() throws FileNotFoundException {
    if (dataFile == null || !dataFile.exists()) {
      throw new IllegalStateException(
          "asRandomAccessFile() can be called only on file based BinaryData!");
    }
    return new RandomAccessFile(dataFile, "r");
  }

  /**
   * Load the data into memory if the size is under the limit. It works when the data is located in
   * a {@link #byteSource}.
   * 
   * @param limit The limit of the load.
   * @return true if we succeed and the {@link #byteSource} was reseted.
   */
  public boolean loadIntoMemory(long limit) {
    if (byteSource != null && limit < length) {
      try {
        data = byteSource.read();
        byteSource = null;
        return true;
      } catch (IOException e) {
        data = EMPTY_BYTES;
      }
    }
    return false;
  }

}
