package org.smartbit4all.domain.meta.jdbc.impl;

import org.smartbit4all.domain.meta.jdbc.JDBCString;

/**
 * Default implementation of the {@link String} value based types.
 * 
 * @author Attila Mate
 */
public class JDBCStringImpl implements JDBCString {

  @Override
  public String app2ext(String appValue) {
    return appValue;
  }

  @Override
  public String ext2app(String extValue) {
    return extValue;
  }

}
