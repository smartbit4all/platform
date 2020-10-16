package org.smartbit4all.domain.meta;

import java.util.EnumMap;
import org.smartbit4all.core.utility.EnumSpecificValue;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterHelper;
import org.smartbit4all.domain.utility.SupportedDatabase;

/**
 * This property is stored in the table of the entity in a simple column or at least it's computed
 * at SQL level during the SQL select execution. In this case the stored property is read only. It
 * means that you cann't change its value in any entity data table.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public class PropertyOwned<T> extends Property<T> {

  /**
   * The column in the table. It describes the DDL name of the given column at JDBC level. This
   * depends on the database because the reserved keywords in the different databases could result
   * specific table column names.
   */
  private EnumSpecificValue<SupportedDatabase, String> dbExpression;

  /**
   * @return The column descriptor of the given attribute. Assigned to the
   */
  public EnumSpecificValue<SupportedDatabase, String> getDbExpression() {
    return dbExpression;
  }

  public void setDbExpression(String defaultDbExpression,
      EnumMap<SupportedDatabase, String> dbExpressions) {
    this.dbExpression = new EnumSpecificValue<SupportedDatabase, String>(defaultDbExpression,
        SupportedDatabase.class);
  }

  public PropertyOwned(String name, Class<T> type, String defaultDbExpression,
      EnumMap<SupportedDatabase, String> dbExpressions,
      JDBCDataConverterHelper jdbcDataConverterHelper) {
    this(name, type, defaultDbExpression, dbExpressions, jdbcDataConverterHelper.from(type));
  }

  public PropertyOwned(String name, Class<T> type, String defaultDbExpression,
      EnumMap<SupportedDatabase, String> dbExpressions, JDBCDataConverter<T, ?> typeHandler) {
    super(name, type, typeHandler);
    setDbExpression(defaultDbExpression, dbExpressions);
  }
  
  public static <T> PropertyOwned<T> create(String name, Class<T> type, String defaultDbExpression,
      EnumMap<SupportedDatabase, String> dbExpressions,
      JDBCDataConverterHelper jdbcDataConverterHelper) {
    return new PropertyOwned<T>(name, type, defaultDbExpression, dbExpressions, jdbcDataConverterHelper);
  }

}
