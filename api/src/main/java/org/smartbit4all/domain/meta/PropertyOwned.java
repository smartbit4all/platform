/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.databasedefinition.bean.ColumnTypeDefinition;
import org.smartbit4all.api.databasedefinition.bean.ColumnTypeDefinition.BaseTypeEnum;
import org.smartbit4all.api.rdbms.DatabaseDefinitionApi;
import org.smartbit4all.core.utility.EnumSpecificValue;
import org.smartbit4all.domain.meta.JDBCDataConverter.JDBCType;
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

  private static final Logger log = LoggerFactory.getLogger(PropertyOwned.class);

  /**
   * The column in the table. It describes the DDL name of the given column at JDBC level. This
   * depends on the database because the reserved keywords in the different databases could result
   * specific table column names.
   */
  private EnumSpecificValue<SupportedDatabase, String> dbExpression;

  /**
   * The owned property in an entity definition is a column in the related table. For the normal
   * operation of the entity definition there is no need to have a precise column type. For the
   * binding only {@link #jdbcConverter()} is necessary that is computed from the java type of the
   * property. To produce proper DDL with {@link DatabaseDefinitionApi} the details of the type must
   * be defined. The length, the precision and the scale parameters.
   */
  private ColumnTypeDefinition columnType;

  /**
   * If the given property is mandatory at the data table.
   */
  private boolean isMandatory = false;

  private static Map<JDBCDataConverter.JDBCType, BaseTypeEnum> columnTypeMap = new HashMap<>();

  static {
    columnTypeMap.put(JDBCType.BIGDECIMAL, BaseTypeEnum.NUMBER);
    columnTypeMap.put(JDBCType.BINARYDATA, BaseTypeEnum.BLOB);
    columnTypeMap.put(JDBCType.DATE, BaseTypeEnum.DATE);
    columnTypeMap.put(JDBCType.DOUBLE, BaseTypeEnum.NUMBER);
    columnTypeMap.put(JDBCType.BINARYDATA, BaseTypeEnum.BLOB);
    columnTypeMap.put(JDBCType.BINARYDATA, BaseTypeEnum.BLOB);
  }

  private ColumnTypeDefinition constructDefaultColumnDefinition() {
    switch (jdbcConverter().bindType()) {
      case BIGDECIMAL:
        return new ColumnTypeDefinition().baseType(BaseTypeEnum.NUMBER).precision(30).scale(10);
      case BINARYDATA:
        return new ColumnTypeDefinition().baseType(BaseTypeEnum.BLOB);
      case DATE:
        return new ColumnTypeDefinition().baseType(BaseTypeEnum.DATE);
      case DOUBLE:
        return new ColumnTypeDefinition().baseType(BaseTypeEnum.NUMBER).precision(18).scale(10);
      case LONG:
        return new ColumnTypeDefinition().baseType(BaseTypeEnum.NUMBER).precision(18);
      case INTEGER:
        return new ColumnTypeDefinition().baseType(BaseTypeEnum.NUMBER).precision(18);
      case STRING:
        return new ColumnTypeDefinition().baseType(BaseTypeEnum.VARCHAR).length(500);
      case TEXT:
        return new ColumnTypeDefinition().baseType(BaseTypeEnum.CLOB);
      case TIME:
        return new ColumnTypeDefinition().baseType(BaseTypeEnum.TIME);
      case TIMESTAMP:
        return new ColumnTypeDefinition().baseType(BaseTypeEnum.DATETIME);
      default:
        return new ColumnTypeDefinition().baseType(BaseTypeEnum.VARCHAR).length(500);
    }
  }

  /**
   * @return The column descriptor of the given attribute. Assigned to the
   */
  public EnumSpecificValue<SupportedDatabase, String> getDbExpression() {
    return dbExpression;
  }

  public void setDbExpression(String defaultDbExpression) {
    this.dbExpression = new EnumSpecificValue<>(defaultDbExpression,
        SupportedDatabase.class);
  }

  public PropertyOwned(String name, Class<T> type, String defaultDbExpression,
      JDBCDataConverterHelper jdbcDataConverterHelper) {
    this(name, type, defaultDbExpression, jdbcDataConverterHelper.from(type));
  }

  public PropertyOwned(String name, Class<T> type, String defaultDbExpression,
      JDBCDataConverter<T, ?> typeHandler) {
    super(name, type, typeHandler);
    setDbExpression(defaultDbExpression);
    columnType = constructDefaultColumnDefinition();
  }

  PropertyOwned(String name, Class<T> type, Comparator<? super T> comparator,
      String defaultDbExpression,
      JDBCDataConverter<T, ?> typeHandler, ColumnTypeDefinition columnType, Boolean isMandatory) {
    super(name, type, typeHandler, comparator);
    setDbExpression(defaultDbExpression);
    this.columnType = columnType;
    this.isMandatory = isMandatory;
  }

  public static <T> PropertyOwned<T> create(String name, Class<T> type, int length,
      boolean isMandatory,
      String defaultDbExpression, JDBCDataConverterHelper jdbcDataConverterHelper) {
    PropertyOwned<T> propertyOwned = new PropertyOwned<>(name, type, defaultDbExpression,
        jdbcDataConverterHelper);
    if (length != -1) {
      propertyOwned.length(length);
    }
    propertyOwned.isMandatory = isMandatory;
    return propertyOwned;
  }

  public static <T> PropertyOwned<T> createFunctionProperty(PropertyOwned<T> baseProperty,
      PropertyFunction function) {
    PropertyOwned<T> funcProp = new PropertyOwned<>(baseProperty.getName(), baseProperty.type(),
        baseProperty.getDbExpression().get(null), baseProperty.jdbcConverter());
    funcProp.setEntityDef(baseProperty.getEntityDef());
    funcProp.setPropertyFunction(function);
    return funcProp;
  }


  public static <R, T> PropertyOwned<R> createFunctionProperty(PropertyOwned<T> baseProperty,
      PropertyFunction function, Class<R> type, JDBCDataConverterHelper jdbcDataConverterHelper) {
    PropertyOwned<R> funcProp =
        new PropertyOwned<>(function.getName(), type, baseProperty.getDbExpression().get(null),
            jdbcDataConverterHelper);
    funcProp.setEntityDef(baseProperty.getEntityDef());
    funcProp.setPropertyFunction(function);
    return funcProp;
  }


  /**
   * Set the length of the database column type. Works on strings only.
   *
   * @param length
   * @return
   */
  public PropertyOwned<T> length(int length) {
    if (columnType == null || columnType.getBaseType() != BaseTypeEnum.VARCHAR) {
      log.error("The {} property is not a string, setting length may be useless.", getName());
    }
    columnType.length(length);
    return this;
  }

  /**
   * Set the precision of the database column type. Works on numbers only.
   *
   * @param precision
   * @return
   */
  public PropertyOwned<T> precison(int precision) {
    if (columnType == null || columnType.getBaseType() != BaseTypeEnum.NUMBER) {
      log.error("The {} property is not a number, setting precison may be useless.", getName());
    }
    columnType.precision(precision);
    return this;
  }

  /**
   * Set the scale of the database column type. Works on numbers only.
   *
   * @param scale
   * @return
   */
  public PropertyOwned<T> scale(int scale) {
    if (columnType == null || columnType.getBaseType() != BaseTypeEnum.NUMBER) {
      log.error("The {} property is not a number, setting scale may be useless.", getName());
    }
    columnType.scale(scale);
    return this;
  }

  public final ColumnTypeDefinition getColumnType() {
    return columnType;
  }

}
