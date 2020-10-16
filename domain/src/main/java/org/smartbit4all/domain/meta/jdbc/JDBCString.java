package org.smartbit4all.domain.meta.jdbc;

import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the {@link String} value based types.
 * 
 * @author Peter Boros
 */
public interface JDBCString extends JDBCDataConverter<String, String> {

  @Override
  default int SQLType() {
    return Types.VARCHAR;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.STRING;
  }

  @Override
  default Class<String> appType() {
    return String.class;
  }

  @Override
  default Class<String> extType() {
    return String.class;
  }

}
