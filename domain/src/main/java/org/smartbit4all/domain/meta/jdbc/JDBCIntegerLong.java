package org.smartbit4all.domain.meta.jdbc;

import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the Long value based types.
 * 
 * @author Peter Boros
 */
public interface JDBCIntegerLong extends JDBCDataConverter<Integer, Long> {

  @Override
  default int SQLType() {
    return Types.DECIMAL;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.LONG;
  }

  @Override
  default Class<Integer> appType() {
    return Integer.class;
  }

  @Override
  default Class<Long> extType() {
    return Long.class;
  }

}
