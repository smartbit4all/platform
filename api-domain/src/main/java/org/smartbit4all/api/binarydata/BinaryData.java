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
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @author Peter Boros
 */
public class BinaryData {

  private static final int MEMORY_LIMIT = 8 * 1024;

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

  @Override
  protected void finalize() throws Throwable {
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
    } else {
      try {
        return new AutoCloseInputStream(new FileInputStream(getDataFile()));
        // return new AutoCloseInputStream(Files.newInputStream(Paths.get(dataFile.toURI())));
        // return new FileInputStream(dataFile);
      } catch (IOException e) {
        log.error(
            "The BinaryData doesn't have the temp file with the content. Assume that the content is empty!",
            e);
        data = new byte[0];
        return new ByteArrayInputStream(data);
      }
    }
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
   * @return
   */
  public String hash() {
    return hash;
  }

  void setHash(String hash) {
    this.hash = hash;
  }

  public byte[] getData() {
    return data;
  }

  public final File getDataFile() {
    return dataFile;
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

}
