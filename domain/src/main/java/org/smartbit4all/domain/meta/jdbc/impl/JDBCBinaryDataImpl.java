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

import org.smartbit4all.domain.meta.jdbc.JDBCBinaryData;
import org.smartbit4all.types.binarydata.BinaryData;

/**
 * There is no need to convert. We use the {@link BinaryData} as basic type for blob access at JDBC
 * level.
 * 
 * @author Peter Boros
 *
 */
public class JDBCBinaryDataImpl implements JDBCBinaryData {

  @Override
  public BinaryData app2ext(BinaryData appValue) {
    return appValue;
  }

  @Override
  public BinaryData ext2app(BinaryData extValue) {
    return extValue;
  }

}
