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
package org.smartbit4all.domain.meta.jdbc;

import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;
import org.smartbit4all.types.binarydata.BinaryData;

/**
 * The interface of the byte[] value based types. The byte array is converted to {@link BinaryData}
 * that is the common data type for the JDBC layer.
 * 
 * @author Peter Boros
 */
public interface JDBCByteArray2BinaryData extends JDBCDataConverter<byte[], BinaryData> {

  @Override
  default int SQLType() {
    return Types.BLOB;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.BINARYDATA;
  }

  @Override
  default Class<byte[]> appType() {
    return byte[].class;
  }

  @Override
  default Class<BinaryData> extType() {
    return BinaryData.class;
  }

}
