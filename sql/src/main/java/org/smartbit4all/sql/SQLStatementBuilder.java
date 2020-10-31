package org.smartbit4all.sql;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import org.smartbit4all.core.utility.EnumSpecificValue;
import org.smartbit4all.core.utility.StringConstant;
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
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.PropertySqlComputed;
import org.smartbit4all.domain.utility.SupportedDatabase;
import org.smartbit4all.types.binarydata.BinaryData;
import org.smartbit4all.types.binarydata.BinaryDataOutputStream;
import com.google.common.io.ByteStreams;

/**
 * The statement builder is a helper class that is instantiated for every statement to support the
 * rendering of the {@link SQLStatementNode}. The builder is supporting the ANSII SQL by default. If
 * we need specific dialects then it must extended by a sub class. The default based class support
 * the main RDBMS brands.
 * 
 * @author Peter Boros
 *
 */
public class SQLStatementBuilder implements SQLStatementBuilderIF {

  /**
   * The string builder that collects the statement textual representation. At the end of the
   * rendering we can use it to log (in case of unbound) or we can use it directly in the JDBC.
   */
  private StringBuilder b = new StringBuilder();

  /**
   * If we need to execute any statement before the main statement then we need to name them and
   * collect in this map. All of these will be executed one by one.
   */
  private Map<String, StringBuilder> beforeStatements = null;

  /**
   * The database system that we render for.
   */
  protected SupportedDatabase target;

  /**
   * The true bound means that we will find ? standing for the values. These ? can be bound later
   * on. The false means that during the rendering the values appears as literals in the statement
   * string. It is useful for logging purposes.
   */
  private boolean bound = true;

  /**
   * The bindPosition defines the next bind position. If we {@link #appendBind()} then this will be
   * incremented.
   */
  private int bindPosition = 1;

  /**
   * In the SQL statement the columns could be qualified if necessary. The join conditions are
   * obviously qualified at both side but the expression not. If we need to qualify the an
   * expression when appending then call qualify() to set this value.
   */
  private String qualifier = StringConstant.EMPTY;

  /**
   * The query limit for the select statement.
   */
  private int queryLimit = -1;

  private boolean hasOrderBy;

  /**
   * Constructs a new builder instance to support the given database. The database can define the
   * dialect differences for the databases.
   */
  public SQLStatementBuilder(SupportedDatabase target) {
    this.target = target;
  }

  /**
   * As a result of the rendering we can access the final textual representation of the SQL
   * statement.
   * 
   * @return The SQL statement that can be used in the
   *         {@link java.sql.Connection#prepareStatement(java.lang.String)} function to create
   *         runnable statement.
   */
  @Override
  public final String getStatement() {
    return b.toString();
  }

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
  @Override
  public PreparedStatement prepareStatement(Connection conn) throws SQLException {
    PreparedStatement result;
    result = conn.prepareStatement(getStatement());
    return result;
  }

  /**
   * @return The bound mean that the given build session will produce a bound statement with the
   *         values as literals.
   */
  @Override
  public final boolean isBound() {
    return bound;
  }

  /**
   * We can change if the rendering is bound or not.
   * 
   * @param bound The true bound means that we will find ? standing for the values. These ? can be
   *        bound later on. The false means that during the rendering the values appears as literals
   *        in the statement string. It is useful for logging purposes.
   */
  @Override
  public final void setBound(boolean bound) {
    this.bound = bound;
  }

  /**
   * Append a simple string to the SQL statement. Used if we have a never changing constant.
   * 
   * @param sqlFragment The fragment of the SQL statement.
   */
  @Override
  public final SQLStatementBuilderIF append(String sqlFragment) {
    b.append(sqlFragment);
    return this;
  }

  /**
   * Append the bind sign (?) to the statement.
   * 
   * @return return the position of the bind variable.
   */
  @Override
  public final int appendBind() {
    b.append(SQLConstant.BINDMARK);
    return bindPosition++;
  }

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
  @Override
  public final void append(List<SQLBindValueLiteral> bindList, Operand<?> operand) {
    if (operand instanceof OperandLiteral<?>) {
      bindList.add(appendLiteral((OperandLiteral<?>) operand));
      return;
    }
    appendProperty((OperandProperty<?>) operand);
  }

  /**
   * Creates a bind value.
   * 
   * @param literal The value for the bind.
   * @return
   */
  @Override
  public final SQLBindValueLiteral appendLiteral(OperandLiteral<?> literal) {
    return new SQLBindValueLiteral(appendBind(), literal);
  }

  /**
   * Append an operand as a column.
   * 
   * @param propertyOperand The property operand.
   */
  @Override
  public final void appendProperty(OperandProperty<?> propertyOperand) {
    // The fallback is that we use the name of the property itself.
    String columnName = propertyOperand.property().getName();
    if (propertyOperand.property() instanceof PropertyOwned<?>) {
      PropertyOwned<?> owned = (PropertyOwned<?>) propertyOperand.property();
      columnName = getColumn(owned.getDbExpression());
    } else if (propertyOperand.property() instanceof PropertyRef<?>) {
      // TODO null check: getPropertyOwned will return NULL for computed referedd properties
      PropertyOwned<?> owned =
          ((PropertyRef<?>) propertyOperand.property()).getReferredOwnedProperty();
      columnName = getColumn(owned.getDbExpression());
    }
    if (propertyOperand.getQualifier() != null) {
      columnName = propertyOperand.getQualifier() + StringConstant.DOT + columnName;
    }
    append(columnName);
  }

  /**
   * Append a bind value as a column for the update statement. Contains column name and the value at
   * the same time.
   * 
   * @param value The bind value.
   */
  @Override
  public final void appendUpdateValue(SQLBindValue value, boolean attachSeparator) {
    String columnName = getColumn(value.property.getDbExpression());
    append(qualifyColumn(columnName));
    append(SQLConstant.EQUAL);
    value.render(this);
    if (attachSeparator) {
      append(SQLConstant.SETSEPARATOR);
    }
  }

  /**
   * Append a simple string to the SQL statement. Used if we have a never changing constant.
   * 
   * @param sqlFragment The fragment of the SQL statement.
   */
  @Override
  public final SQLStatementBuilderIF append(BooleanOperator booleanOperator) {
    b.append(booleanOperator.toString());
    return this;
  }

  /**
   * Add the group by section to the statement.
   * 
   * @return Fluid API.
   */
  @Override
  public SQLStatementBuilderIF groupBy() {
    separate();
    b.append(SQLConstant.GROUPBY);
    separate();
    return this;
  }

  /**
   * Add the order by section to the statement.
   * 
   * @return Fluid API.
   */
  @Override
  public SQLStatementBuilderIF orderBy() {
    separate();
    b.append(SQLConstant.ORDERBY);
    separate();
    return this;
  }

  /**
   * Add the update set section.
   * 
   * @return Fluid API.
   */
  @Override
  public SQLStatementBuilderIF updateSet() {
    separate();
    b.append(SQLConstant.SETCLAUSE);
    separate();
    return this;
  }

  /**
   * Add the update set section.
   * 
   * @return Fluid API.
   */
  @Override
  public SQLStatementBuilderIF insertInto(SQLTableNode table) {
    b.append(SQLConstant.INSERTINTO);
    separate();
    append(table);
    return this;
  }

  /**
   * Add the delete from section.
   * 
   * @return Fluid API.
   */
  @Override
  public SQLStatementBuilderIF deleteFrom(SQLTableNode table) {
    b.append(SQLConstant.DELETEFROM);
    separate();
    append(table);
    return this;
  }

  /**
   * Add the update from section.
   * 
   * @return Fluid API.
   */
  @Override
  public SQLStatementBuilderIF update(SQLTableNode table) {
    b.append(SQLConstant.UPDATE);
    separate();
    append(table);
    return this;
  }

  /**
   * Add the select section.
   * 
   * @return Fluid API.
   */
  @Override
  public SQLStatementBuilderIF select() {
    b.append(SQLConstant.SELECT);
    separate();
    if (queryLimit > 0) {
      switch (target) {
        case MSSQL:
          b.append(SQLConstant.TOP).append(StringConstant.SPACE).append(queryLimit)
              .append(StringConstant.SPACE);
          if (hasOrderBy) {
            b.append(SQLConstant.WITH_TIES).append(StringConstant.SPACE);
          }
          break;

        default:
          break;
      }
    }
    return this;
  }

  /**
   * Add the from section.
   * 
   * @return Fluid API.
   */
  @Override
  public SQLStatementBuilderIF from() {
    separate();
    b.append(SQLConstant.FROM);
    separate();
    return this;
  }

  /**
   * Generate start values section.
   * 
   * @return
   */
  @Override
  public SQLStatementBuilderIF startValues() {
    separate();
    b.append(SQLConstant.VALUES);
    separate();
    b.append(StringConstant.LEFT_PARENTHESIS);
    return this;
  }

  @Override
  public SQLStatementBuilderIF postProcessSelect() {
    if (queryLimit <= 0) {
      return this;
    }
    if (target == SupportedDatabase.ORACLE) {
      String currentStmt = b.toString();
      b.setLength(0);
      b.append(SQLConstant.SELECT).append(StringConstant.SPACE).append(StringConstant.ASTERISK)
          .append(StringConstant.SPACE).append(SQLConstant.FROM).append(StringConstant.SPACE)
          .append(StringConstant.LEFT_PARENTHESIS);
      b.append(currentStmt);
      b.append(StringConstant.RIGHT_PARENTHESIS).append(StringConstant.SPACE)
          .append(SQLConstant.WHERE).append(StringConstant.SPACE)
          .append(SQLConstant.ROWNUM_ORA)
          .append(StringConstant.SPACE).append(StringConstant.LESSOREQUAL)
          .append(StringConstant.SPACE).append(queryLimit);
    }
    return this;
  }

  /**
   * Append a value as a literal to the statement.
   * 
   * @param value The value.
   * 
   * @return
   */
  @Override
  public SQLStatementBuilderIF valueLiteral(Object value, JDBCDataConverter<?, ?> converter) {
    if (value == null) {
      append("NULL");
    } else if (value instanceof String) {
      append("\'" + value.toString() + "\'");
    } else if (converter != null) {
      // TODO The type handler can influence the generation of the literal.
      append(value.toString());
    } else {
      append(value.toString());
    }
    return this;
  }

  // Only the JDBCDataConverters are casted based on its bindType
  @SuppressWarnings("unchecked")
  @Override
  public <T> void bind(PreparedStatement stmt, int position, JDBCDataConverter<T, ?> converter,
      T value) throws SQLException {
    if (value != null) {
      switch (converter.bindType()) {
        case BIGDECIMAL:
          stmt.setBigDecimal(position,
              convertFromAppToJdbcType((JDBCDataConverter<T, BigDecimal>) converter, value));
          break;

        case BINARYDATA:
          BinaryData binaryData =
              convertFromAppToJdbcType((JDBCDataConverter<T, BinaryData>) converter, value);
          stmt.setBinaryStream(position, binaryData.inputStream(), binaryData.length());
          break;

        case DATE:
          stmt.setDate(position,
              convertFromAppToJdbcType((JDBCDataConverter<T, Date>) converter, value));
          break;

        case DOUBLE:
          stmt.setDouble(position,
              convertFromAppToJdbcType((JDBCDataConverter<T, Double>) converter, value));
          break;

        case LONG:
          stmt.setLong(position,
              convertFromAppToJdbcType((JDBCDataConverter<T, Long>) converter, value));
          break;

        case STRING:
          stmt.setString(position,
              convertFromAppToJdbcType((JDBCDataConverter<T, String>) converter, value));
          break;

        case TIMESTAMP:
          stmt.setTimestamp(position, convertFromAppToJdbcType(
              (JDBCDataConverter<T, java.sql.Timestamp>) converter, value));
          break;

        case TIME:
          stmt.setTime(position,
              convertFromAppToJdbcType((JDBCDataConverter<T, java.sql.Time>) converter, value));
          break;

      }
    } else {
      stmt.setNull(position, converter.SQLType());
    }
  }

  // Only the JDBCDataConverters are casted based on its bindType
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getResultValue(ResultSet resultSet, int colIdx, Property<T> property)
      throws Exception {
    JDBCDataConverter<T, ?> typeHandler = property.jdbcConverter();
    switch (typeHandler.bindType()) {
      case BIGDECIMAL:
        return convertFromJdbcToAppType((JDBCDataConverter<T, BigDecimal>) typeHandler,
            () -> resultSet.getBigDecimal(colIdx));

      case BINARYDATA:
        Blob blob = resultSet.getBlob(colIdx);
        BinaryData result;
        if (blob == null) {
          result = null;
        } else {
          BinaryDataOutputStream bdos = new BinaryDataOutputStream(-1, null);
          ByteStreams.copy(blob.getBinaryStream(), bdos);
          bdos.close();
          result = bdos.data();
        }
        return convertFromJdbcToAppType((JDBCDataConverter<T, BinaryData>) typeHandler,
            () -> result);

      case DATE:
        return convertFromJdbcToAppType((JDBCDataConverter<T, Date>) typeHandler,
            () -> resultSet.getDate(colIdx));

      case DOUBLE:
        return convertFromJdbcToAppType((JDBCDataConverter<T, Double>) typeHandler,
            () -> {
              double res = resultSet.getDouble(colIdx);
              if (resultSet.wasNull()) {
                return null;
              }
              return res;
            });

      case LONG:
        return convertFromJdbcToAppType((JDBCDataConverter<T, Long>) typeHandler,
            () -> {
              long res = resultSet.getLong(colIdx);
              if (resultSet.wasNull()) {
                return null;
              }
              return res;
            });

      case STRING:
        return convertFromJdbcToAppType((JDBCDataConverter<T, String>) typeHandler,
            () -> resultSet.getString(colIdx));

      case TIMESTAMP:
        return convertFromJdbcToAppType((JDBCDataConverter<T, java.sql.Timestamp>) typeHandler,
            () -> resultSet.getTimestamp(colIdx));

      case TIME:
        return convertFromJdbcToAppType((JDBCDataConverter<T, java.sql.Time>) typeHandler,
            () -> resultSet.getTime(colIdx));

    }
    return null;
  }

  private <APPTYPE, JDBCTYPE> JDBCTYPE convertFromAppToJdbcType(
      JDBCDataConverter<APPTYPE, JDBCTYPE> typeHandler, APPTYPE value) {
    return typeHandler.app2ext(value);
  }

  private <APPTYPE, JDBCTYPE> APPTYPE convertFromJdbcToAppType(
      JDBCDataConverter<APPTYPE, JDBCTYPE> typeHandler,
      ThrowingSupplier<JDBCTYPE, Exception> getter) throws Exception {
    return typeHandler.ext2app(getter.get());
  }

  @FunctionalInterface
  public interface ThrowingSupplier<T, E extends Exception> {
    T get() throws E;
  }

  @Override
  public void appendOpenParenthesis(ExpressionBracket bracket) {
    separate();
    if (bracket.isNegate()) {
      b.append(SQLConstant.NOT);
      separate();
    }
    b.append(StringConstant.LEFT_PARENTHESIS);
  }

  /**
   * Append a 2 operand expression to the statement. Can be equal, less than etc.
   * 
   * @param expression
   * @return
   */
  @Override
  public List<SQLBindValueLiteral> append(Expression2Operand<?> expression) {
    List<SQLBindValueLiteral> result = new ArrayList<>();
    separate();
    if (expression.isNegate()) {
      b.append(SQLConstant.NOT);
      separate();
      b.append(StringConstant.LEFT_PARENTHESIS);
    }

    append(result, expression.getOp());

    separate();

    append(expression.operator().text());

    separate();

    append(result, expression.getLiteral());

    if (expression.isNegate()) {
      b.append(StringConstant.RIGHT_PARENTHESIS);
    }
    return result;
  }

  /**
   * Append a between to the statement.
   * 
   * @param expression Manages the between with symmetric option.
   * @return
   */
  @Override
  public List<SQLBindValueLiteral> append(ExpressionBetween<?> expression) {
    List<SQLBindValueLiteral> result = new ArrayList<>();
    separate();

    if (expression.isNegate()) {
      b.append(SQLConstant.NOT);
      separate();
    }

    if (expression.isSymmetric()) {
      b.append(StringConstant.LEFT_PARENTHESIS);
    }

    appendBetween(expression, result, expression.getLowerBound(), expression.getUpperBound());

    if (expression.isSymmetric()) {
      b.append(StringConstant.RIGHT_PARENTHESIS);
      b.append(SQLConstant.OR);
      b.append(StringConstant.LEFT_PARENTHESIS);
      appendBetween(expression, result, expression.getUpperBound(), expression.getLowerBound());
      b.append(StringConstant.RIGHT_PARENTHESIS);
    }
    return result;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public List<SQLBindValueLiteral> append(ExpressionIn<?> expression) {
    List<SQLBindValueLiteral> result = new ArrayList<>();
    separate();

    // If we have a property then the values must match it's type. Else if have a value here then
    // the rest of the values must match with it.
    JDBCDataConverter<?, ?> converter;
    if (expression.getOperand() instanceof OperandProperty<?>) {
      converter = ((OperandProperty<?>) expression.getOperand()).property().jdbcConverter();
    } else {
      converter = ((OperandLiteral<?>) expression.getOperand()).typeHandler();
    }

    append(result, expression.getOperand());

    separate();
    if (expression.isNegate()) {
      b.append(SQLConstant.NOT);
      separate();
    }
    b.append(SQLConstant.IN);
    separate();

    b.append(StringConstant.LEFT_PARENTHESIS);
    for (Object value : expression.values()) {
      if (!result.isEmpty()) {
        b.append(StringConstant.COMMA);
      }
      // Here we creates the literal operands. We need them to bind the values.
      result.add(appendLiteral(new OperandLiteral(value, converter)));
    }
    b.append(StringConstant.RIGHT_PARENTHESIS);

    return result;
  }

  @Override
  public List<SQLBindValueLiteral> append(ExpressionIsNull expression) {
    List<SQLBindValueLiteral> result = new ArrayList<>();
    separate();

    append(result, expression.getOp());

    if (expression.isNegate()) {
      b.append(SQLConstant.ISNOTNULL);
    } else {
      b.append(SQLConstant.ISNULL);
    }

    return result;
  }

  @Override
  public void append(ExpressionBoolean expression) {
    separate();

    if (expression.evaluate()) {
      b.append(SQLConstant.TRUE);
    } else {
      b.append(SQLConstant.FALSE);
    }
  }

  @Override
  public void append(SQLSelectColumn column) {
    SQLSelectFromNode fromNode = column.from.get();
    b.append(fromNode.alias());
    b.append(StringConstant.DOT);
    b.append(column.columnName);
    separate();
    b.append(column.alias);
  }

  @Override
  public void append(SQLGroupByColumn column) {
    SQLSelectFromNode fromNode = column.from.get();
    b.append(fromNode.alias());
    b.append(StringConstant.DOT);
    b.append(column.columnName);
  }

  @Override
  public void append(SQLOrderByColumn column) {
    SQLSelectFromNode fromNode = column.from.get();
    b.append(fromNode.alias());
    b.append(StringConstant.DOT);
    b.append(column.columnName);
    separate();
    b.append(column.asc ? SQLConstant.ASC : SQLConstant.DESC);
    if (column.nullsFirst) {
      separate();
      b.append(SQLConstant.NULLSFIRST);
    }
  }

  @Override
  public void append(SQLTableNode table) {
    if (table.schema != null) {
      b.append(table.schema);
      b.append(StringConstant.DOT);
    }
    b.append(table.name);
  }

  @Override
  public void append(SQLSelectFromNode source, SQLSelectFromNode target, SQLSelectJoin join) {
    separate();
    if (join.outer) {
      b.append(SQLConstant.LEFTJOIN);
    } else {
      b.append(SQLConstant.JOIN);
    }
    separate();
    target.render(this);
    separate();
    b.append(SQLConstant.ON);
    separate();
    boolean first = true;
    for (Entry<String, String> joinEntry : join.joinColumns.entrySet()) {
      if (!first) {
        b.append(SQLConstant.AND);
      }
      b.append(source.alias());
      b.append(StringConstant.DOT);
      b.append(joinEntry.getKey());
      b.append(SQLConstant.EQUAL);
      b.append(target.alias());
      b.append(StringConstant.DOT);
      b.append(joinEntry.getValue());
      first = false;
    }
    separate();
  }

  private void separate() {
    b.append(SQLConstant.SEGMENTSEPARATOR);
  }

  @Override
  public void appendLock(long timeoutInMillis) {
    switch (target) {
      case ORACLE:
        separate();
        b.append(SQLConstant.FOR_UPDATE);
        // TODO Columns for the lock.
        if (timeoutInMillis == 0) {
          separate();
          b.append(SQLConstant.NOWAIT);
        } else if (timeoutInMillis > 0) {
          separate();
          b.append(SQLConstant.WAIT);
          separate();
          b.append(SQLConstant.convertWaitTimeout2Second(timeoutInMillis));
        }
        break;

      case POSTGRESQL:
        separate();
        b.append(SQLConstant.FOR_UPDATE);
        // TODO Columns for the lock.
        if (timeoutInMillis == 0) {
          separate();
          b.append(SQLConstant.NOWAIT);
        } else if (timeoutInMillis > 0) {
          StringBuilder before = before(SQLConstant.WAIT);
          before.append(SQLConstant.LOCK_TIMEOUT_POSTGRESQL);
          before.append(SQLConstant.convertWaitTimeout2Second(timeoutInMillis));
          before.append(SQLConstant.LOCK_TIMEOUT_POSTFIX_POSTGRESQL);
        }
        break;

      case MSSQL:
        // At the SQLServer the lock request is assigned to the table at the from section as hint.
        if (timeoutInMillis > 0) {
          StringBuilder before = before(SQLConstant.WAIT);
          before.append(SQLConstant.LOCK_TIMEOUT_SQLSERVER);
          before.append(SQLConstant.SEGMENTSEPARATOR);
          before.append(timeoutInMillis);
        }
        break;

      default:
        break;
    }
  }

  protected void appendBetween(ExpressionBetween<?> expression, List<SQLBindValueLiteral> result,
      Operand<?> lowerBound, Operand<?> upperBound) {
    append(result, expression.getOperand());

    separate();
    b.append(SQLConstant.BETWEEN);
    separate();

    append(result, lowerBound);

    b.append(SQLConstant.AND);

    append(result, upperBound);
  }

  /**
   * Constructs the qualified name for the column.
   * 
   * @param column The name of the column,
   * @return The qualified name.
   */
  private String qualifyColumn(String column) {
    if (qualifier == null || qualifier.isEmpty()) {
      return column;
    }
    return qualifier + StringConstant.DOT + column;
  }

  /**
   * In the SQL statement the columns could be qualified if necessary. The join conditions are
   * obviously qualified at both side but the expression not. If wee need to qualify the an
   * expression when appending then call qualify() to set this value.
   * 
   * @param qualifier The qualifier doesn't contain the . as the separator.
   * @return Fluid API
   */
  @Override
  public SQLStatementBuilderIF qualify(String qualifier) {
    this.qualifier = qualifier;
    return this;
  }

  /**
   * In the SQL statement the columns could be qualified if necessary. The join conditions are
   * obviously qualified at both side but the expression not. If wee need to qualify the an
   * expression when appending then call qualify() to set this value. With this call we can empty
   * the qualifier.
   * 
   * @return Fluid API
   */
  @Override
  public SQLStatementBuilderIF unqualify() {
    this.qualifier = StringConstant.EMPTY;
    return this;
  }

  @Override
  public String getColumnAssignedColumnName(ResultSetMetaData metaData, int i) throws SQLException {
    // The database type can influence it.
    return target == SupportedDatabase.SQLMX ? metaData.getColumnName(i)
        : metaData.getColumnLabel(i);
  }

  @Override
  public String getColumn(EnumSpecificValue<SupportedDatabase, String> sqlExpression) {
    // By default it's simply the value registered for the supported database.
    return sqlExpression.get(target);
  }

  @Override
  public String getFunctionColumn(PropertyComputed<?> property,
      List<SQLSelectColumn> requiredColumns) {
    switch (property.getBasicType()) {
      case COUNT:
        return SQLConstant.COUNT;
      case MIN:
        return SQLConstant.MIN + StringConstant.LEFT_PARENTHESIS + requiredColumns.get(0).columnName
            + StringConstant.RIGHT_PARENTHESIS;
      case MAX:
        return SQLConstant.MAX + StringConstant.LEFT_PARENTHESIS + requiredColumns.get(0).columnName
            + StringConstant.RIGHT_PARENTHESIS;
      case AVG:
        return SQLConstant.AVG + StringConstant.LEFT_PARENTHESIS + requiredColumns.get(0).columnName
            + StringConstant.RIGHT_PARENTHESIS;
      case SUM:
        return SQLConstant.SUM + StringConstant.LEFT_PARENTHESIS + requiredColumns.get(0).columnName
            + StringConstant.RIGHT_PARENTHESIS;

      default:
        return null;

    }
  }

  @Override
  public String getSqlComputedColumn(PropertySqlComputed<?> property,
      ArrayList<SQLSelectColumn> requiredColumns) {
    String sqlExpression = property.getSqlExpression(target);
    Matcher m = property.getParamPattern().matcher(sqlExpression);
    String result = sqlExpression;
    int idx = 0;
    while (m.find()) {
      String propertyName = m.group(1);
      result = result.replaceFirst(":" + propertyName, requiredColumns.get(idx++).columnName);
    }
    return result;
  }

  @Override
  public ArrayList<Property<?>> getRequiredPropertiesOfSqlColumn(PropertySqlComputed<?> property) {
    return property.getRequiredProperties(target);
  }

  /**
   * Get the before statement section by name
   * 
   * @param beforeStatementName
   * @return
   */
  private final StringBuilder before(String beforeStatementName) {
    if (beforeStatements == null) {
      beforeStatements = new HashMap<String, StringBuilder>();
    }
    StringBuilder result = beforeStatements.get(beforeStatementName);
    if (result == null) {
      result = new StringBuilder();
      beforeStatements.put(beforeStatementName, result);
    }
    return result;
  }

  /**
   * @return The before statements in a map. If empty then we get back an empty map not a null.
   */
  public Map<String, StringBuilder> beforeStatements() {
    return beforeStatements == null ? Collections.emptyMap() : beforeStatements;
  }

  public void setQueryLimit(int queryLimit) {
    this.queryLimit = queryLimit;
  }

  @Override
  public void preProcessWhere() {
    if (queryLimit > 0) {
      switch (target) {
        case ORACLE:
          if (!hasOrderBy) {
            b.append(StringConstant.LEFT_PARENTHESIS);
          }
          break;

        default:
          break;
      }
    }
  }

  @Override
  public void postProcessWhere() {
    if (queryLimit > 0) {
      switch (target) {
        case ORACLE:
          if (!hasOrderBy) {
            b.append(StringConstant.RIGHT_PARENTHESIS).append(StringConstant.SPACE)
                .append(SQLConstant.AND).append(StringConstant.SPACE)
                .append(SQLConstant.ROWNUM_ORA).append(StringConstant.SPACE)
                .append(StringConstant.LESSOREQUAL).append(StringConstant.SPACE).append(queryLimit)
                .append(StringConstant.SPACE);
          }
          break;

        case POSTGRESQL:
          b.append(StringConstant.SPACE)
              .append(SQLConstant.LIMIT).append(StringConstant.SPACE).append(queryLimit)
              .append(StringConstant.SPACE);
          break;
        default:
          break;
      }
    }
  }

  @Override
  public void insteadOfWhere() {
    if (queryLimit > 0) {
      switch (target) {
        case ORACLE:
          if (!hasOrderBy) {
            b.append(SQLConstant.WHERE).append(StringConstant.SPACE)
                .append(SQLConstant.ROWNUM_ORA).append(StringConstant.SPACE)
                .append(StringConstant.LESSOREQUAL).append(StringConstant.SPACE).append(queryLimit)
                .append(StringConstant.SPACE);
          }
          break;

        default:
          break;
      }
    }
  }

  @Override
  public void setHasOrderBy(boolean hasOrderBy) {
    this.hasOrderBy = hasOrderBy;
  }

}
