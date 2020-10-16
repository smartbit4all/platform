package org.smartbit4all.domain.meta.jdbc;

import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the {@link LocalDate} value based types.
 */
public interface JDBCLocalTimeSqlDate extends JDBCDataConverter<LocalTime, Time> {

  @Override
  default int SQLType() {
    return Types.TIME;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.TIME;
  }

  @Override
  default Class<LocalTime> appType() {
    return LocalTime.class;
  }

  @Override
  default Class<Time> extType() {
    return Time.class;
  }

}
