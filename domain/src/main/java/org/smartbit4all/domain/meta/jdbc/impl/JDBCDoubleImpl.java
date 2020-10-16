package org.smartbit4all.domain.meta.jdbc.impl;

import org.smartbit4all.domain.meta.jdbc.JDBCDouble;

/**
 * Default implementation of the {@link Double} value based types.
 * 
 * @author Attila Mate
 */
public class JDBCDoubleImpl implements JDBCDouble {

  @Override
  public Double app2ext(Double appValue) {
    return appValue;
  }

  @Override
  public Double ext2app(Double extValue) {
    return extValue;
  }

}
