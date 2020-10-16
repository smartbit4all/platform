package org.smartbit4all.domain.meta.jdbc;

import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the {@link LocalDate} value based types.
 */
public interface JDBCLocalDateSqlDate extends JDBCDataConverter<LocalDate, Date> {

  @Override
  default int SQLType() {
    return Types.DATE;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.DATE;
  }

  @Override
  default Class<LocalDate> appType() {
    return LocalDate.class;
  }

  @Override
  default Class<Date> extType() {
    return Date.class;
  }

}
