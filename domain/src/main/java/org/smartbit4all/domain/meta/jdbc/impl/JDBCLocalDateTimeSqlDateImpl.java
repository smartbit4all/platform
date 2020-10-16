package org.smartbit4all.domain.meta.jdbc.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.smartbit4all.domain.meta.jdbc.JDBCLocalDateTimeSqlDate;

/**
 * Default implementation of the {@link Date} value based types.
 * 
 */
public class JDBCLocalDateTimeSqlDateImpl implements JDBCLocalDateTimeSqlDate {

  @Override
  public Timestamp app2ext(LocalDateTime appValue) {
    return appValue == null ? null : Timestamp.valueOf(appValue);
  }

  @Override
  public LocalDateTime ext2app(Timestamp extValue) {
    return extValue == null ? null : extValue.toLocalDateTime();
  }

}
