package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.smartbit4all.core.utility.EnumSpecificValue;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterHelper;
import org.smartbit4all.domain.utility.SupportedDatabase;

public class PropertySqlComputed<T> extends Property<T> {

  private EnumSpecificValue<SupportedDatabase, String> sqlExpression;

  private Pattern paramPattern = Pattern.compile(":([a-zA-Z0-9_\\.]+)");

  private PropertySqlComputed(String name, Class<T> type, JDBCDataConverter<T, ?> jdbcConverter,
      String defaultSqlExpression) {
    super(name, type, jdbcConverter);
    if (defaultSqlExpression == null || defaultSqlExpression.isEmpty()) {
      throw new IllegalArgumentException(
          "defaultSqlExpression parameter can not be null nor empty!");
    }
    this.sqlExpression = new EnumSpecificValue<>(defaultSqlExpression, SupportedDatabase.class);
  }

  private PropertySqlComputed(String name, Class<T> type, JDBCDataConverter<T, ?> jdbcConverter,
      String defaultSqlExpression, Map<SupportedDatabase, String> expressionsByDialect) {
    super(name, type, jdbcConverter);
    if (expressionsByDialect == null || expressionsByDialect.isEmpty()) {
      throw new IllegalArgumentException(
          "expressionsByDialect parameter can not be null nor empty!");
    }
    this.sqlExpression = new EnumSpecificValue<>(defaultSqlExpression, expressionsByDialect);
  }

  public String getSqlExpression(SupportedDatabase dialect) {
    return sqlExpression.get(dialect);
  }

  public ArrayList<Property<?>> getRequiredProperties(SupportedDatabase dialect) {
    String expression = sqlExpression.get(dialect);
    return getRequiredPropertiesFromExpression(expression);
  }

  private ArrayList<Property<?>> getRequiredPropertiesFromExpression(String expression) {
    ArrayList<Property<?>> properties = new ArrayList<>();
    Matcher m = paramPattern.matcher(expression);
    while (m.find()) {
      String propertyName = m.group(1);
      Property<?> foundProperty = getEntityDef().getProperty(propertyName);
      if (foundProperty != null) {
        properties.add(foundProperty);
      } else {
        throw new NoSuchElementException(
            "There was no property found with the given SqlComputed expression parameter: "
                + propertyName);
      }
    }
    return properties;
  }

  public Pattern getParamPattern() {
    return paramPattern;
  }

  public static <T> PropertySqlComputed<T> create(String name, Class<T> type,
      JDBCDataConverterHelper jdbcDataConverterHelper, String defaultSqlExpression) {
    return new PropertySqlComputed<>(name, type, jdbcDataConverterHelper.from(type),
        defaultSqlExpression);
  }

  public static <T> PropertySqlComputed<T> create(String name, Class<T> type,
      JDBCDataConverterHelper jdbcDataConverterHelper, String defaultSqlExpression,
      Map<SupportedDatabase, String> expressionsByDialect) {
    return new PropertySqlComputed<>(name, type, jdbcDataConverterHelper.from(type),
        defaultSqlExpression, expressionsByDialect);
  }

}
