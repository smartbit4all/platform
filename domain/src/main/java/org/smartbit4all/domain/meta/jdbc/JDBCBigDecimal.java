package org.smartbit4all.domain.meta.jdbc;

import java.math.BigDecimal;
import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the {@link BigDecimal} value based types.
 * 
 * @author Peter Boros
 */
public interface JDBCBigDecimal extends JDBCDataConverter<BigDecimal, BigDecimal> {

  @Override
  default int SQLType() {
    return Types.DECIMAL;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.BIGDECIMAL;
  }

  default Class<BigDecimal> appType() {
    return BigDecimal.class;
  }

  @Override
  default Class<BigDecimal> extType() {
    return BigDecimal.class;
  }

}
