package org.smartbit4all.domain.meta.jdbc;

import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the Boolean value based types.
 * 
 * @author Zoltán Suller
 */
public interface JDBCBooleanString extends JDBCDataConverter<Boolean, String> {

  @Override
  default int SQLType() {
    return Types.VARCHAR;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.STRING;
  }

  @Override
  default Class<Boolean> appType() {
    return Boolean.class;
  }

  @Override
  default Class<String> extType() {
    return String.class;
  }

}
