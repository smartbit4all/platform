package org.smartbit4all.domain.meta.jdbc;

import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the {@link Double} value based types.
 * 
 * @author Peter Boros
 */
public interface JDBCDouble extends JDBCDataConverter<Double, Double> {

  @Override
  default int SQLType() {
    return Types.DECIMAL;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.DOUBLE;
  }

  @Override
  default Class<Double> appType() {
    return Double.class;
  }

  @Override
  default Class<Double> extType() {
    return Double.class;
  }

}
