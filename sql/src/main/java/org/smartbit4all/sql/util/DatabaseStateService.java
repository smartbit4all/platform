package org.smartbit4all.sql.util;

import javax.sql.DataSource;

public interface DatabaseStateService {

  boolean isTableEmpty(DataSource dataSource, String tableName);

  boolean areDataTablesEmpty(DataSource dataSource);

}
