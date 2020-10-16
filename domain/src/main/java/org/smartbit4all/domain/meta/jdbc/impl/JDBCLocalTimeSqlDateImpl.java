package org.smartbit4all.domain.meta.jdbc.impl;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import org.smartbit4all.domain.meta.jdbc.JDBCLocalTimeSqlDate;

/**
 * Default implementation of the {@link Date} value based types.
 * 
 */
public class JDBCLocalTimeSqlDateImpl implements JDBCLocalTimeSqlDate {

  @Override
  public Time app2ext(LocalTime appValue) {
    return appValue == null ? null : Time.valueOf(appValue);
  }

  @Override
  public LocalTime ext2app(Time extValue) {
    return extValue == null ? null : extValue.toLocalTime();
  }

}
