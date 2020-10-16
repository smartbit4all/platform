package org.smartbit4all.domain.meta.jdbc;

import java.sql.Time;
import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the {@link Time} value based types.
 * 
 * @author Peter Boros
 */
public interface JDBCSqlTime extends JDBCDataConverter<Time, Time> {

  @Override
  default int SQLType() {
    return Types.TIME;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.TIME;
  }

  @Override
  default Class<Time> appType() {
    return Time.class;
  }

  @Override
  default Class<Time> extType() {
    return Time.class;
  }

}
