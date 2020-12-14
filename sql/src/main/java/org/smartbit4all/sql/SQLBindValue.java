/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.smartbit4all.domain.meta.JDBCDataConverter;
import org.smartbit4all.domain.meta.PropertyOwned;

/**
 * Contains a single value to set into the {@link PreparedStatement} as input value. There is a 1
 * based indexing in the JDBC statement so the {@link #position} must be proper.
 * 
 * @author Peter Boros
 */
public class SQLBindValue implements SQLCommonValueNode {

  /**
   * The value to bind in the SQL expression. This is rendered as a question mark and bound by the
   * position.
   */
  private Object value;

  /**
   * The 1 based position inside the whole SQL.
   */
  private int position;

  /**
   * The meta of the column.
   */
  PropertyOwned<?> property;

  SQLBindValue(PropertyOwned<?> property) {
    super();
    this.property = property;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {
    if (b.isBound()) {
      position = b.appendBind();
    } else {
      b.valueLiteral(value, property.jdbcConverter());
    }
  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
//    b.bind(stmt, position, property.jdbcConverter(), value);
    bind(b, stmt, property.type());
  }
  
  private <T> void bind(SQLStatementBuilderIF b, PreparedStatement stmt, Class<T> sourceType) throws SQLException {
    b.bind(stmt, position, (org.smartbit4all.domain.meta.JDBCDataConverter<T, ?>)property.jdbcConverter(), (T) value);
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public PropertyOwned<?> getProperty() {
    return property;
  }

}
