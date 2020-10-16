package org.smartbit4all.domain.meta.jdbc;

import java.sql.Timestamp;
import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the {@link Timestamp} value based types.
 * 
 * @author Peter Boros
 */
public interface JDBCSqlTimestamp extends JDBCDataConverter<Timestamp, Timestamp> {

  @Override
  default int SQLType() {
    return Types.TIMESTAMP;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.TIMESTAMP;
  }

  @Override
  default Class<Timestamp> appType() {
    return Timestamp.class;
  }

  @Override
  default Class<Timestamp> extType() {
    return Timestamp.class;
  }

}
