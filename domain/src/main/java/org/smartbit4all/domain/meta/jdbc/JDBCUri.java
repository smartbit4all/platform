package org.smartbit4all.domain.meta.jdbc;

import java.net.URI;
import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;

public interface JDBCUri extends JDBCDataConverter<URI, String> {
  
  @Override
  default int SQLType() {
    return Types.VARCHAR;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.STRING;
  }

  @Override
  default Class<URI> appType() {
    return URI.class;
  }

  @Override
  default Class<String> extType() {
    return String.class;
  }
  
}
