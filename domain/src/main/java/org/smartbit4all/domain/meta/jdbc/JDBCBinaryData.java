package org.smartbit4all.domain.meta.jdbc;

import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;
import org.smartbit4all.types.binarydata.BinaryData;

/**
 * The interface of the {@link BinaryData} application type. There is no conversion we use the same
 * type in both layer.
 * 
 * @author Peter Boros
 */
public interface JDBCBinaryData extends JDBCDataConverter<BinaryData, BinaryData> {

  @Override
  default int SQLType() {
    return Types.BLOB;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.BINARYDATA;
  }

  @Override
  default Class<BinaryData> appType() {
    return BinaryData.class;
  }

  @Override
  default Class<BinaryData> extType() {
    return BinaryData.class;
  }

}
