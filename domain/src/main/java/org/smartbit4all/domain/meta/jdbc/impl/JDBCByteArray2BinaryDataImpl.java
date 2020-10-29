package org.smartbit4all.domain.meta.jdbc.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.domain.meta.jdbc.JDBCByteArray2BinaryData;
import org.smartbit4all.types.binarydata.BinaryData;
import com.google.common.io.ByteStreams;

/**
 * Default implementation of the byte[] value based types.
 * 
 * @author Attila Mate
 */
public class JDBCByteArray2BinaryDataImpl implements JDBCByteArray2BinaryData {

  private static final Logger log = LoggerFactory.getLogger(JDBCByteArray2BinaryDataImpl.class);

  @Override
  public BinaryData app2ext(byte[] appValue) {
    return new BinaryData(appValue);
  }

  @Override
  public byte[] ext2app(BinaryData extValue) {
    if (extValue == null) {
      return null;
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ByteStreams.copy(extValue.inputStream(), baos);
    } catch (IOException e) {
      log.error("Unable to read the binary data " + extValue + ". The value will be null.", e);
      return null;
    }
    return baos.toByteArray();
  }

}
