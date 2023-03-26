package org.smartbit4all.domain.meta.jdbc;

import java.sql.Types;
import org.smartbit4all.domain.meta.JDBCDataConverter;

public interface JDBCEnumString<E extends Enum<?>> extends JDBCDataConverter<E, String> {

  @Override
  default int SQLType() {
    return Types.VARCHAR;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.STRING;
  }

  @Override
  default Class<String> extType() {
    return String.class;
  }

}
