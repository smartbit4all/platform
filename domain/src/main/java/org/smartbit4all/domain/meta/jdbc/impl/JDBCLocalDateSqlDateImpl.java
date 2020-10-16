package org.smartbit4all.domain.meta.jdbc.impl;

import java.sql.Date;
import java.time.LocalDate;
import org.smartbit4all.domain.meta.jdbc.JDBCLocalDateSqlDate;

/**
 * Default implementation of the {@link Date} value based types.
 * 
 * @author Attila Mate
 */
public class JDBCLocalDateSqlDateImpl implements JDBCLocalDateSqlDate {

  @Override
  public Date app2ext(LocalDate appValue) {
    return appValue == null ? null : Date.valueOf(appValue);
  }

  @Override
  public LocalDate ext2app(Date extValue) {
    return extValue == null ? null : extValue.toLocalDate();
  }

}
