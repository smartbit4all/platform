package org.smartbit4all.domain.meta.jdbc;

import java.sql.Date;
import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the {@link Date} value based types.
 * 
 * @author Peter Boros
 */
public interface JDBCUtilDateSqlDate extends JDBCDataConverter<java.util.Date, Date> {

  @Override
  default int SQLType() {
    return Types.DATE;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.DATE;
  }

  @Override
  default Class<java.util.Date> appType() {
    return java.util.Date.class;
  }

  @Override
  default Class<Date> extType() {
    return Date.class;
  }

}
