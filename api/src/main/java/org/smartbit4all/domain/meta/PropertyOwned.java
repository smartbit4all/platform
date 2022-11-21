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

  public void setDbExpression(String defaultDbExpression) {
    this.dbExpression = new EnumSpecificValue<SupportedDatabase, String>(defaultDbExpression,
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
  }

  public static <T> PropertyOwned<T> create(String name, Class<T> type, boolean isMandatory, 
      String defaultDbExpression, JDBCDataConverterHelper jdbcDataConverterHelper) {
    // FIXME isMandatory is never used. Is it intentional?
    return new PropertyOwned<T>(name, type, defaultDbExpression,
        jdbcDataConverterHelper);
  }

  public static <T> PropertyOwned<T> createFunctionProperty(PropertyOwned<T> baseProperty,
      PropertyFunction function) {
    PropertyOwned<T> funcProp = new PropertyOwned<T>(baseProperty.getName(), baseProperty.type(),
        baseProperty.getDbExpression().get(null), baseProperty.jdbcConverter());
    funcProp.setEntityDef(baseProperty.getEntityDef());
    funcProp.setPropertyFunction(function);
    return funcProp;
  }

}
