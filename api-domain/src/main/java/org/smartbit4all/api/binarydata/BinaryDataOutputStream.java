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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.io.BaseEncoding;

/**
 * The {@link BinaryData} can be constructed using this {@link OutputStream} with the same pattern
 * as the byte[] can be constructed using the {@link ByteArrayOutputStream}. Instead of using byte
 * array to store large amount of bytes use this to avoid memory consumption problems.
 * 
 * @author Peter Boros
 */
public class BinaryDataOutputStream extends OutputStream {

  private static final Logger log = LoggerFactory.getLogger(BinaryDataOutputStream.class);

  /**
   * The {@link BinaryData} store the content in memory till this limit. If we reach it then it
   * switch to temp file and copy the currently available content into this new temp file. The 0
   * means that from the first byte it uses the temp file as storage. The -1 means that all of the
   * content will be stored in memory without any temp file.
   * 
   * 
   * TODO Parameterization!
   */
  private int memoryLimit = 0;

  /**
   * The actively used {@link OutputStream}.
   */
  private OutputStream osActive;

  /**
   * If we write to byte array then this output stream is the active one.
   */
  private ByteArrayOutputStream osByteArray;

  /**
   * The temp file.
   */
  private File dataFile;

  /**
   * If a temp file initiated then we have a .
   */
  private boolean tempFile;

  /**
   * If we fill the data into a temp file then this output stream is the active one with a
   * {@link BufferedOutputStream} in front of it. The {@link #osBuffered} will be {@link #osActive}
   * but it will be inner stream.
   */
  private FileOutputStream osFile;

  /**
   * If we write into the temp file then this is {@link #osActive}.
   */
  private BufferedOutputStream osBuffered;

  /**
   * The number of bytes written onto the stream.
   */
  private int counter = 0;

  /**
   * Indicates if the {@link OutputStream} is already closed. If true then no more bytes are
   * accepted.
   */
  private boolean closed = false;

  /**
   * If we cann't create the temp file then we set this true to avoid trying again and again.
   */
  private boolean tempFileCreationFailed = false;

  /**
   * The message digest used for hash calculation. If we have an initialized instance that during
   * the construction of the {@link BinaryData} we always update it. At the end the result is
   */
  private MessageDigest messageDigest;

  /**
   * If we set this {@link Hasher} then the crc checksum is calculated while writing and set to the
   * {@link BinaryData#setCrcCheckSum(int, HashFunction)}
   */
  private Hasher crcCheckSumHasher;

  /**
   * If we set this {@link HashFunction} then the crc checksum is calculated while writing and set
   * to the {@link BinaryData#setCrcCheckSum(int, HashFunction)}
   */
  private HashFunction crcCheckSumHasherFunction;

  /**
   * The result of the construction stream after the {@link #close()}.
   */
  private BinaryData data = null;

  /**
   * The {@link BinaryData} store the content in memory till this limit. If we reach it then it
   * switch to temp file and copy the currently available content into this new temp file. The 0
   * means that from the first byte it uses the temp file as storage. The -1 means that all of the
   * content will be stored in memory without any temp file.
   * 
   */
  public BinaryDataOutputStream() {
    this(-1, null);
  }

  /**
   * The {@link BinaryData} store the content in memory till this limit. If we reach it then it
   * switch to temp file and copy the currently available content into this new temp file. The 0
   * means that from the first byte it uses the temp file as storage. The -1 means that all of the
   * content will be stored in memory without any temp file.
   * 
   * @param memoryLimit
   */
  public BinaryDataOutputStream(int memoryLimit) {
    this(memoryLimit, null);
  }

  /**
   * The {@link BinaryData} store the content in memory till this limit. If we reach it then it
   * switch to temp file and copy the currently available content into this new temp file. The 0
   * means that from the first byte it uses the temp file as storage. The -1 means that all of the
   * content will be stored in memory without any temp file.
   * 
   * @param memoryLimit
   */
  public BinaryDataOutputStream(int memoryLimit, MessageDigest messageDigest) {
    super();
    this.memoryLimit = memoryLimit;
    this.messageDigest = messageDigest;
    if (memoryLimit == 0) {
      // We start in a temp file from the first byte.
      try {
        initTempFile();
      } catch (Exception e) {
        throw new IllegalArgumentException("Unable to init the temp file", e);
      }
    } else {
      // Start in the memory.
      osByteArray = new ByteArrayOutputStream();
      osActive = osByteArray;
    }
  }

  public BinaryDataOutputStream crcCheckSum(HashFunction crcCheckSumFunction) {
    this.crcCheckSumHasherFunction = crcCheckSumFunction;
    // We initiate it for the subsequent write operations.
    this.crcCheckSumHasher = crcCheckSumFunction.newHasher();
    return this;
  }

  @Override
  public void write(int b) throws IOException {
    if (closed) {
      throw new IOException("BinaryDataOutputStream is already closed");
    }
    control(1);
    osActive.write(b);
    counter++;
    if (messageDigest != null) {
      messageDigest.update((byte) (b & 0xFF));
    }
    if (crcCheckSumHasher != null) {
      crcCheckSumHasher.putInt(b);
    }
  }

  @Override
  public final void write(byte[] b, int off, int len) throws IOException {
    if (closed) {
      throw new IOException("BinaryDataOutputStream is already closed");
    }
    control(len);
    osActive.write(b, off, len);
    counter += len;
    if (messageDigest != null) {
      messageDigest.update(b, off, len);
    }
    if (crcCheckSumHasher != null) {
      crcCheckSumHasher.putBytes(b, off, len);
    }
  }

  @Override
  public final void write(byte[] b) throws IOException {
    if (closed) {
      throw new IOException("BinaryDataOutputStream is already closed");
    }
    control(b.length);
    osActive.write(b);
    counter += b.length;
    if (messageDigest != null) {
      messageDigest.update(b);
    }
    if (crcCheckSumHasher != null) {
      crcCheckSumHasher.putBytes(b);
    }
  }

  @Override
  public final void flush() throws IOException {
    osActive.flush();
  }

  @Override
  public final synchronized void close() throws IOException {
    if (closed) {
      return;
    }
    // Construct the Binary Data.
    if (osByteArray != null) {
      osByteArray.close();
      // In this case we have a byte array as binary data.
      data = new BinaryData(osByteArray.toByteArray());
      osByteArray = null;
    } else {
      // In this case we have a temp file with the content.
      closeFileStreams();
      data = new BinaryData(dataFile);
      data.setDeleteDataFile(true);
    }
    osActive = null;
    if (messageDigest != null) {
      // Using Guava
      data.setHash(BaseEncoding.base16().lowerCase().encode(messageDigest.digest()));
    }
    if (crcCheckSumHasher != null) {
      data.setCrcCheckSum(crcCheckSumHasher.hash().asInt(), crcCheckSumHasherFunction);
    }
    closed = true;
  }

  /**
   * This function will analyze the situation and decide if it's necessary to change from memory
   * target to temp file.
   * 
   * @param bytesToWrite
   */
  private final void control(int bytesToWrite) {
    if (osByteArray != null && memoryLimit > 0 && !tempFileCreationFailed) {
      // Check if we exceed the limit.
      if ((osByteArray.size() + bytesToWrite) > memoryLimit) {
        // In this case we write the currently in memory content into a temp file and continue the
        // streaming with this new target.
        // TODO Parameterization
        try {
          initTempFile();
          osFile.write(osByteArray.toByteArray());
          osByteArray = null;
        } catch (Throwable e) {
          log.error("Unable to create or use temp file. Using memory byte[] instead.", e);
          closeFileStreams();
          tempFileCreationFailed = true;
        }
      }
    }
  }

  private void initTempFile() throws IOException, FileNotFoundException {
    dataFile = File.createTempFile("binarydata", ".sb4");
    osFile = new FileOutputStream(dataFile);
    osBuffered = new BufferedOutputStream(osFile);
    // Write the currently in memory content into this new file.
    osActive = osBuffered;
  }

  private void closeFileStreams() {
    if (osBuffered != null) {
      try {
        osBuffered.close();
      } catch (IOException e1) {
        // Already closed my be.
      }
      osBuffered = null;
    }
    if (osFile != null) {
      try {
        osFile.close();
      } catch (IOException e1) {
        // Already closed my be.
      }
      osFile = null;
    }
  }

  /**
   * The {@link BinaryData} store the content in memory till this limit. If we reach it then it
   * switch to temp file and copy the currently available content into this new temp file. The 0
   * means that from the first byte it uses the temp file as storage. The -1 means that all of the
   * content will be stored in memory without any temp file.
   * 
   * @return
   */
  public int getMemoryLimit() {
    return memoryLimit;
  }

  /**
   * The {@link BinaryData} store the content in memory till this limit. If we reach it then it
   * switch to temp file and copy the currently available content into this new temp file. The 0
   * means that from the first byte it uses the temp file as storage. The -1 means that all of the
   * content will be stored in memory without any temp file.
   * 
   * @param memoryLimit
   */
  public void setMemoryLimit(int memoryLimit) {
    this.memoryLimit = memoryLimit;
  }

  /**
   * @return The result content of the Stream containing all the bytes that were written earlier.
   *         Use only after calling the {@link #close()}.
   */
  public BinaryData data() {
    return data;
  }

  /**
   * The number of bytes that were written into the stream.
   * 
   * @return
   */
  public long counter() {
    return counter;
  }

}
