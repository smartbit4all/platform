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
