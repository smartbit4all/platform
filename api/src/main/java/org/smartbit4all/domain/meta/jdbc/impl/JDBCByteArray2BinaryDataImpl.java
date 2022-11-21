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
package org.smartbit4all.domain.meta.jdbc.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.domain.meta.jdbc.JDBCByteArray2BinaryData;
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
