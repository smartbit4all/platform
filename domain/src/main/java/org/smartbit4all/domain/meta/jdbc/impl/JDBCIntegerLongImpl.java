package org.smartbit4all.domain.meta.jdbc.impl;

import org.smartbit4all.domain.meta.jdbc.JDBCIntegerLong;

/**
 * Default implementation of the Long value based types.
 * 
 * @author Attila Mate
 */
public class JDBCIntegerLongImpl implements JDBCIntegerLong {

  @Override
  public Long app2ext(Integer appValue) {
    return appValue == null ? null : Long.valueOf(((Integer) appValue).longValue());
  }

  @Override
  public Integer ext2app(Long extValue) {
    return extValue == null ? null : Integer.valueOf(((Long) extValue).intValue());
  }

}
