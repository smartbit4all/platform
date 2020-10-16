package org.smartbit4all.domain.meta.jdbc.impl;

import org.smartbit4all.domain.meta.jdbc.JDBCLong;

/**
 * Default implementation of the Long value based types.
 * 
 * @author Attila Mate
 */
public class JDBCLongImpl implements JDBCLong {

  @Override
  public Long app2ext(Long appValue) {
    return appValue;
  }

  @Override
  public Long ext2app(Long extValue) {
    return extValue;
  }

}
