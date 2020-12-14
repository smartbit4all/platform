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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.smartbit4all.core.SB4Service;
import org.smartbit4all.core.utility.EnumSpecificValue;
import org.smartbit4all.domain.meta.BooleanOperator;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.ExpressionBetween;
import org.smartbit4all.domain.meta.ExpressionBoolean;
import org.smartbit4all.domain.meta.ExpressionBracket;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.ExpressionIsNull;
import org.smartbit4all.domain.meta.JDBCDataConverter;
import org.smartbit4all.domain.meta.Operand;
import org.smartbit4all.domain.meta.OperandLiteral;
import org.smartbit4all.domain.meta.OperandProperty;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyComputed;
import org.smartbit4all.domain.meta.PropertySqlComputed;
import org.smartbit4all.domain.utility.SupportedDatabase;


public interface SQLStatementBuilderIF extends SB4Service {

  /**
   * If we don't know the value of a non negative index then we can use this value instead to see
   * that the value is missing.
   */
  int UNKNOWNINDEX = -1;

  /**
   * As a result of the rendering we can access the final textual representation of the SQL
   * statement.
   * 
   * @return The SQL statement that can be used in the
   *         {@link java.sql.Connection#prepareStatement(java.lang.String)} function to create
   *         runnable statement.
   */
  String getStatement();

  /**
   * As a best practice the SQL layer almost always uses so called prepared statements in the JDBC.
   * It ensures that the database can optimize the SQL parsing because the same SQL looks the same
   * in every executing. After preparing the SQL statement we can bind the values with this builder
   * also.
   * 
   * @param conn The database connection.
   * @return If we succeeded then we get back the {@link PreparedStatement}.
   * @throws SQLException If we cann't create the SQL statement.
   */
  PreparedStatement prepareStatement(Connection conn) throws SQLException;

  /**
   * @return The bound mean that the given build session will produce a bound statement with the
   *         values as literals.
   */
  boolean isBound();

  /**
   * We can change if the rendering is bound or not.
   * 
   * @param bound The true bound means that we will find ? standing for the values. These ? can be
   *        bound later on. The false means that during the rendering the values appears as literals
   *        in the statement string. It is useful for logging purposes.
   */
  void setBound(boolean bound);

  /**
   * Append a simple string to the SQL statement. Used if we have a never changing constant.
   * 
   * @param sqlFragment The fragment of the SQL statement.
   */
  SQLStatementBuilderIF append(String sqlFragment);

  /**
   * Append the bind sign (?) to the statement.
   * 
   * @return return the position of the bind variable.
   */
  int appendBind();

  /**
   * Append the bind sign (?) to the statement and construct the {@link SQLBindValueLiteral} at the
   * same time.
   *
   * @param operand The operand to append. If it's an {@link OperandProperty} then it will be
   *        appended as column. But if it's an {@link OperandLiteral} then it will create the bind
   *        value {@link SQLBindValueLiteral}.
   * @return The {@link SQLBindValueLiteral} if it has been created or null if it was property and
   *         added as a column.
   */
  void append(List<SQLBindValueLiteral> bindList, Operand<?> operand);

  /**
   * Creates a bind value.
   * 
   * @param literal The value for the bind.
   * @return
   */
  SQLBindValueLiteral appendLiteral(OperandLiteral<?> literal);

  /**
   * Append an operand as a column.
   * 
   * @param propertyOperand The property operand.
   */
  void appendProperty(OperandProperty<?> propertyOperand);

  /**
   * Append a bind value as a column for the update statement. Contains column name and the value at
   * the same time.
   * 
   * @param value The bind value.
   */
  void appendUpdateValue(SQLBindValue value, boolean attachSeparator);

  /**
   * Append a simple string to the SQL statement. Used if we have a never changing constant.
   * 
   * @param sqlFragment The fragment of the SQL statement.
   */
  SQLStatementBuilderIF append(BooleanOperator booleanOperator);

  /**
   * Add the group by section to the statement.
   * 
   * @return Fluid API.
   */
  SQLStatementBuilderIF groupBy();

  /**
   * Add the order by section to the statement.
   * 
   * @return Fluid API.
   */
  SQLStatementBuilderIF orderBy();

  /**
   * Add the update set section.
   * 
   * @return Fluid API.
   */
  SQLStatementBuilderIF updateSet();

  /**
   * Add the update set section.
   * 
   * @return Fluid API.
   */
  SQLStatementBuilderIF insertInto(SQLTableNode table);

  /**
   * Add the delete from section.
   * 
   * @return Fluid API.
   */
  SQLStatementBuilderIF deleteFrom(SQLTableNode table);

  /**
   * Add the update from section.
   * 
   * @return Fluid API.
   */
  SQLStatementBuilderIF update(SQLTableNode table);

  /**
   * Add the select section.
   * 
   * @return Fluid API.
   */
  SQLStatementBuilderIF select();

  /**
   * Add the from section.
   * 
   * @return Fluid API.
   */
  SQLStatementBuilderIF from();

  /**
   * Generate start values section.
   * 
   * @return
   */
  SQLStatementBuilderIF startValues();

  /**
   * Generate start values section.
   * 
   * @return
   */
  SQLStatementBuilderIF postProcessSelect();

  /**
   * Append a value as a literal to the statement.
   * 
   * @param value The value.
   * 
   * @return
   */
  SQLStatementBuilderIF valueLiteral(Object value, JDBCDataConverter<?, ?> typeHandler);

  <T> void bind(PreparedStatement stmt, int position, JDBCDataConverter<T, ?> typeHandler, T value)
      throws SQLException;

  // void bind(PreparedStatement stmt, int position, JDBCDataConverter<?, ?> typeHandler, Object
  // value)
  // throws SQLException;

  void appendOpenParenthesis(ExpressionBracket bracket);

  /**
   * Append a 2 operand expression to the statement. Can be equal, less than etc.
   * 
   * @param expression
   * @return
   */
  List<SQLBindValueLiteral> append(Expression2Operand<?> expression);

  /**
   * Append a between to the statement.
   * 
   * @param expression Manages the between with symmetric option.
   * @return
   */
  List<SQLBindValueLiteral> append(ExpressionBetween<?> expression);

  List<SQLBindValueLiteral> append(ExpressionIn<?> expression);

  List<SQLBindValueLiteral> append(ExpressionIsNull expression);

  void append(ExpressionBoolean expression);

  void append(SQLSelectColumn column);

  void append(SQLGroupByColumn column);

  void append(SQLOrderByColumn column);

  void append(SQLTableNode table);

  void append(SQLSelectFromNode source, SQLSelectFromNode target, SQLSelectJoin join);

  void appendLock(long timeoutInMillis);

  /**
   * In the SQL statement the columns could be qualified if necessary. The join conditions are
   * obviously qualified at both side but the expression not. If wee need to qualify the an
   * expression when appending then call qualify() to set this value.
   * 
   * @param qualifier The qualifier doesn't contain the . as the separator.
   * @return Fluid API
   */
  SQLStatementBuilderIF qualify(String qualifier);

  /**
   * In the SQL statement the columns could be qualified if necessary. The join conditions are
   * obviously qualified at both side but the expression not. If wee need to qualify the an
   * expression when appending then call qualify() to set this value. With this call we can empty
   * the qualifier.
   * 
   * @return Fluid API
   */
  SQLStatementBuilderIF unqualify();

  /**
   * This method is responsible for constructing the column name from the JDBC
   * {@link ResultSetMetaData} given column index. It can be varied by different databases.
   * 
   * @param metaData The meta.
   * @param i The index of the column.
   * @return The name of the column to search in the result.
   * @throws SQLException
   */
  String getColumnAssignedColumnName(ResultSetMetaData metaData, int i) throws SQLException;

  /**
   * The parameter contains an sqlExpression that should be translated to match with the given
   * target database requirements and dialect.
   * 
   * @param sqlExpression The sql expression of the column.
   * @return
   */
  String getColumn(EnumSpecificValue<SupportedDatabase, String> sqlExpression);

  /**
   * Read and return the value of the a column from a {@link ResultSet}.
   * 
   * @param <T>
   * 
   * @param resultSet The {@link ResultSet} to read from.
   * @param colIdx The column index.
   * @param property The {@link Property} to use.
   * @return The value of the given column.
   */
  <T> T getResultValue(ResultSet resultSet, int colIdx, Property<T> property) throws Exception;

  /**
   * Returns the aggregate function as a column.
   *
   * @param property The property to add.
   * @param requiredColumns The list of the already prepared columns in the same order then the
   *        {@link PropertyComputed#getRequiredProperties()} list.
   * @return
   */
  String getFunctionColumn(PropertyComputed<?> property, List<SQLSelectColumn> requiredColumns);

  String getSqlComputedColumn(PropertySqlComputed<?> property,
      ArrayList<SQLSelectColumn> requiredColumns);

  ArrayList<Property<?>> getRequiredPropertiesOfSqlColumn(PropertySqlComputed<?> property);

  Map<String, StringBuilder> beforeStatements();

  void setQueryLimit(int queryLimit);

  void setHasOrderBy(boolean hasOrderBy);

  /**
   * Before the where we can add tokens or whatever.
   */
  void preProcessWhere();

  /**
   * Before the a where in a select statement we can add tokens or whatever.
   */
  void preProcessSelectWhere();

  /**
   * After the where we can add tokens or whatever.
   */
  void postProcessWhere();

  /**
   * If we don't have any where then this function can add alternative stuffs.
   */
  void insteadOfWhere();

}
