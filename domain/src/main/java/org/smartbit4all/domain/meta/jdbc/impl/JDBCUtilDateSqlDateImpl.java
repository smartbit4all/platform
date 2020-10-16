package org.smartbit4all.domain.meta.jdbc.impl;

import java.sql.Date;
import org.smartbit4all.domain.meta.jdbc.JDBCUtilDateSqlDate;

/**
 * Default implementation of the {@link Date} value based types.
 * 
 * @author Attila Mate
 */
public class JDBCUtilDateSqlDateImpl implements JDBCUtilDateSqlDate {

  @Override
  public Date app2ext(java.util.Date appValue) {
    return appValue == null ? null : new Date(((java.util.Date) appValue).getTime());
  }

  @Override
  public java.util.Date ext2app(Date extValue) {
    return extValue == null ? null : new java.util.Date(((Date) extValue).getTime());
  }

}
