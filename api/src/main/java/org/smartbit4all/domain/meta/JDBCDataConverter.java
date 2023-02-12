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

/**
 * The {@link JDBCDataConverter} is a specific {@link DataConverter} to fulfill the requirements of
 * the the JDBC type handling. A type handler is responsible for accessor and mutator operations of
 * a property. The data of the {@link Property} is stored in a type at application level and can be
 * stored in another type at database level. It could be as trivial as having String at both side.
 * But could differ in case of Date for example where we would like to use the LocalDate at
 * application level but we should bind a java.sql.Date at database level.
 * 
 * <p>
 * The implementations are named by the following syntax: JDBC + AppTypeName + JDBCTypeName and +
 * Impl in case of the implementation. The converter itself is also a service so it can be defined,
 * implemented and configured at every level of the application.
 * </p>
 * 
 * <p>
 * There is a lookup for the registered implementation where we figure out the implementation by the
 * type of the property. So if we have String at property level then we can get the default
 * JDBCString without any additional configuration. (All the by default working converters are
 * registered at application level.) If we have JDBCDate for example then we will have this
 * converter as default in case of java.util.Date but also LocalDate. The logic of this default
 * types are managed by the from static function.
 * 
 * @author Peter Boros
 *
 * @param <S> It is the application level data type.
 * @param <T> It might be the basic JDBC level type. At JDBC level this handler will convert between
 *        the stored object and the JDBC type.
 */
public interface JDBCDataConverter<S, T> extends DataConverter<S, T> {

  public enum JDBCType {
    BIGDECIMAL, BINARYDATA, TEXT, DATE, DOUBLE, LONG, STRING, TIME, TIMESTAMP
  }

  /**
   * Return the JDBC level type for the given type.
   * 
   * @return
   */
  int SQLType();

  /**
   * The supported JDBC type that must be used during the bind or during the fetch.
   * 
   * @return
   */
  JDBCType bindType();

}
