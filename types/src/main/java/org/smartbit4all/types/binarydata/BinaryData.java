/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.types.binarydata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BinaryData is responsible for Binary Large Objects that could be stored in temporary files
 * instead of memory. Using this type in the data structure we can avoid the
 * {@link OutOfMemoryError} exceptions at JVM level. We can substitute the
 * {@link ByteArrayOutputStream} and the {@link ByteArrayInputStream} in our code and that's all.
 * There is a limit that remains in the memory but if we have more byte then it switch to temp file.
 * The {@link BinaryData} uses the Java temp file management API.
 * 
 * @author Peter Boros
 */
public class BinaryData {

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
   * TODO Implement with PhantomReference and Spring TaskExecutor.
   */
  //private boolean deleteDataFile = true;

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

  // private static class PhantomRef extends PhantomReference<BinaryData> {
  //
  // /**
  // * The file that must be deleted at the cleanup session.
  // */
  // File file;
  //
  // public PhantomRef(BinaryData referent, File file, ReferenceQueue<? super BinaryData> q) {
  // super(referent, q);
  // this.file = file;
  // }
  //
  // }
  //
  // /**
  // * The reference queue for disposing the phantom reachable file based {@link BinaryData}.
  // */
  // private static final ReferenceQueue<PhantomRef> referenceQueue = new ReferenceQueue<>();
  //
  // private static final TaskExecutor cleanupExecutor;
  //
  // static {
  // cleanupExecutor.submit(new Runnable() {
  //
  // @Override
  // public void run() {
  // // TODO Auto-generated method stub
  //
  // }
  // });
  // }

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
   * @param deleteOn Indicates whether delete or not the file at the finalize of the Object.
   */
  public BinaryData(File dataFile, boolean deleteOn) {
    super();
    this.dataFile = dataFile;
    this.length = dataFile.length();
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
        return new FileInputStream(dataFile);
      } catch (FileNotFoundException e) {
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

}
