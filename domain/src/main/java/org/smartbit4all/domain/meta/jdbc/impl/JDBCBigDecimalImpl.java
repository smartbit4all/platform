package org.smartbit4all.domain.meta.jdbc.impl;

import java.math.BigDecimal;
import org.smartbit4all.domain.meta.jdbc.JDBCBigDecimal;

/**
 * Default implementation of the {@link BigDecimal} value based types.
 * 
 * @author Attila Mate
 */
public class JDBCBigDecimalImpl implements JDBCBigDecimal {

  @Override
  public BigDecimal app2ext(BigDecimal appValue) {
    return appValue;
  }

  @Override
  public BigDecimal ext2app(BigDecimal extValue) {
    return extValue;
  }

}
