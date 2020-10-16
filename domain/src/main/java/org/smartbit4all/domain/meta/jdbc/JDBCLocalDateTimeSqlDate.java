package org.smartbit4all.domain.meta.jdbc;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the {@link LocalDate} value based types.
 */
public interface JDBCLocalDateTimeSqlDate extends JDBCDataConverter<LocalDateTime, Timestamp> {

  @Override
  default int SQLType() {
    return Types.TIMESTAMP;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.TIMESTAMP;
  }

  @Override
  default Class<LocalDateTime> appType() {
    return LocalDateTime.class;
  }

  @Override
  default Class<Timestamp> extType() {
    return Timestamp.class;
  }

}
