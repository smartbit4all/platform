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
