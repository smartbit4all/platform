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
package org.smartbit4all.sql.service.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinition.TableDefinition;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.ExpressionBetween;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.ExpressionIsNull;
import org.smartbit4all.domain.meta.ExpressionVisitor;
import org.smartbit4all.domain.meta.Operand;
import org.smartbit4all.domain.meta.OperandProperty;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyComputed;
import org.smartbit4all.domain.meta.PropertyComputed.BasicComputation;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.PropertySqlComputed;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.Reference.Join;
import org.smartbit4all.domain.service.query.QueryImpl;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.smartbit4all.domain.utility.SupportedDatabase;
import org.smartbit4all.sql.SQLComputedColumn;
import org.smartbit4all.sql.SQLGroupByColumn;
import org.smartbit4all.sql.SQLGroupByComputedColumn;
import org.smartbit4all.sql.SQLOrderByColumn;
import org.smartbit4all.sql.SQLSelectAggregateColumn;
import org.smartbit4all.sql.SQLSelectColumn;
import org.smartbit4all.sql.SQLSelectFromNode;
import org.smartbit4all.sql.SQLSelectFromTableNode;
import org.smartbit4all.sql.SQLSelectJoin;
import org.smartbit4all.sql.SQLSelectLock;
import org.smartbit4all.sql.SQLSelectStatement;
import org.smartbit4all.sql.SQLStatementBuilder;
import org.smartbit4all.sql.SQLStatementBuilderIF;
import org.smartbit4all.sql.SQLTableNode;
import org.smartbit4all.sql.SQLWhere;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * Implementation of a query service at SQL level.
 * 
 * 
 * TODO To be continued...
 * 
 * @author Peter Boros
 * @param <E>
 */
public class SQLQuery<E extends EntityDefinition> extends QueryImpl<E> {

  /**
   * The table aliases are indexed by this variable.
   */
  int aliasIndex = 1;

  /**
   * The column aliases are indexed by this variable.
   */
  int columnIndex = 1;

  /**
   * The alias prefix for the tables in the from section of the select.
   */
  public static final String ALIASPREFIX = "T";

  /**
   * The column prefix for the tables in the from section of the select.
   */
  private static final String COLUMNPREFIX = "C";

  /**
   * The select statement for the query.
   */
  SQLSelectStatement select;

  /**
   * The JDBC connection accessor.
   */
  JdbcTemplate jdbcTemplate;

  /**
   * The JDBC has an important parameter during the fetch. The number of rows to fetch at once. The
   * default is 20 row in one fetch operation.
   */
  int fetchRowNumber = 20;

  /**
   * If...
   */
  // SB4CompositeFunction rowFetch;

  public SQLQuery(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * This execute constructs the {@link SQLSelectStatement} and execute it. It uses the
   * {@link QueryResult} to fetch into.
   * 
   * @throws Exception
   */
  @Override
  public void execute() throws Exception {
    init();
    // That time the entity is based on a table.
    TableDefinition table = input.entityDef().tableDefinition();
    SQLTableNode tableNode = new SQLTableNode(table.getSchema(), table.getName());
    SQLSelectFromTableNode rootTable = new SQLSelectFromTableNode(tableNode, nextTableAlias());
    // This map must be a list based map to preserve the order.
    Map<String, Property<?>> columnMap = new HashMap<>();
    // SQLStatementBuilderIF builder = entityDef.context().get(SQLStatementBuilderIF.class);
    SQLStatementBuilderIF builder = new SQLStatementBuilder(SupportedDatabase.ORACLE);
    select.setQueryLimit(input.limit());
    for (Property<?> property : input.properties()) {
      SQLSelectColumn column = setupProperty(rootTable, property, builder);
      // It's a column in the select add this column.
      if (column != null) {
        select.addColumn(column);
      }
      columnMap.put(column.getAlias().toUpperCase(), property);
    }
    // Adding the expression to the select. It means that we visit the expression and add the
    // properties.
    if (input.where() != null) {
      input.where().accept(new ExpressionVisitor() {

        @Override
        public <T> void visit2Operand(Expression2Operand<T> expression) {
          prepareOperand(expression.getOp(), rootTable, builder);
        }

        @Override
        public void visitBetween(ExpressionBetween<?> expression) {
          prepareOperand(expression.getOperand(), rootTable, builder);
          prepareOperand(expression.getLowerBound(), rootTable, builder);
          prepareOperand(expression.getUpperBound(), rootTable, builder);
        }

        @Override
        public <T> void visitIn(ExpressionIn<T> expression) {
          prepareOperand(expression.getOperand(), rootTable, builder);
        }

        @Override
        public void visitIsNull(ExpressionIsNull expression) {
          prepareOperand(expression.getOp(), rootTable, builder);
        }

      });
      select.setWhere(new SQLWhere(input.where()));
    }

    for (SortOrderProperty sortOrder : input.orderBys()) {
      // Add them to the select like any other property.
      SQLSelectColumn column = setupProperty(rootTable, sortOrder.property, builder);
      select.addOrderBy(new SQLOrderByColumn(column.from(), column.getColumnName(), sortOrder.asc,
          sortOrder.nullsFirst));
    }

    // Adding the group by to the SQLSelectStatemenet
    for (Property<?> property : input.groupByProperties()) {
      // Add them to the select like any other property.
      SQLSelectColumn column = setupProperty(rootTable, property, builder);
      if (column instanceof SQLComputedColumn) {
        select.addGroupBy(new SQLGroupByComputedColumn(column.getAlias()));
      } else {
        columnMap.put(column.getAlias().toUpperCase(), property);
        select.addGroupBy(new SQLGroupByColumn(column.from(), column.getColumnName()));
      }
    }
    select.setFrom(rootTable);
    // Add the lock if we have lock instruction in the input.
    if (input.lockRequest() != null) {
      select.setLock(new SQLSelectLock(input.lockRequest().timeOut()));
    }
    // Run the select against the JDBC connection and fetch it into the result.
    // The statement builder to use when rendering the SQL. It can be specified for the given
    // database dialect or application specific purposes.
    select.render(builder);

    // Execute the before statements if there are any.
    for (Entry<String, StringBuilder> entry : builder.beforeStatements().entrySet()) {
      String beforeStatement = entry.getValue().toString();
      if (beforeStatement != null) {
        jdbcTemplate.execute(beforeStatement);
      }
    }

    jdbcTemplate.query(new PreparedStatementCreator() {

      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(builder.getStatement());

        select.bind(builder, stmt);
        return stmt;
      }
    }, new ResultSetExtractor<QueryOutput>() {

      @Override
      public QueryOutput extractData(ResultSet rs) throws SQLException, DataAccessException {
        fetchResult(rs, columnMap, builder);
        return null;
      }

    });

  }

  private void init() {
    select = new SQLSelectStatement();
    aliasIndex = 1;
    columnIndex = 1;
  }

  /**
   * Examine if the given operand is an {@link OperandProperty} and if it is then
   * {@link #setupProperty(SQLSelectFromTableNode, Property, SQLStatementBuilderIF)} and set the
   * qualifier for the operand.
   * 
   * @param rootTable
   * @param builder
   */
  private final void prepareOperand(Operand<?> operand, SQLSelectFromNode rootTable,
      SQLStatementBuilderIF builder) {
    if (operand instanceof OperandProperty<?>) {
      OperandProperty<?> operandProperty = (OperandProperty<?>) operand;
      SQLSelectColumn column = setupProperty(rootTable, operandProperty.property(), builder);
      operandProperty.setQualifier(column.from().alias());
    }
  }

  private final void fetchResult(ResultSet resultSet, Map<String, Property<?>> columnMap,
      SQLStatementBuilderIF builder) throws SQLException, DataAccessException {
    // For the faster fetch result we need to have an index to index translation between the JDBC
    // ResultSet and the QueryResult.
    ResultSetMetaData metaData = resultSet.getMetaData();
    int indexTrans[] = new int[metaData.getColumnCount()];
    Property<?> propertyTrans[] = new Property<?>[metaData.getColumnCount()];
    output().start();
    for (int i = 0; i < metaData.getColumnCount(); i++) {
      String columnName = builder.getColumnAssignedColumnName(metaData, i + 1).toUpperCase();
      Property<?> property = columnMap.get(columnName);
      indexTrans[i] = output().accept(property);
      propertyTrans[i] = property;
    }
    // Iterate the JDBC result set.
    while (resultSet.next()) {
      output().startRow();
      for (int colIdx = 0; colIdx < indexTrans.length; colIdx++) {
        if (indexTrans[colIdx] != QueryOutput.COLUMNNOTACCEPTED) {
          try {
            output().setValue(indexTrans[colIdx],
                builder.getResultValue(resultSet, colIdx + 1, propertyTrans[colIdx]));
          } catch (Exception e) {
            throw new DataRetrievalFailureException("Error occured while fetching data", e);
          }
        }
      }
      // TODO Execute rowComputation.
      output().finishRow();
    }
    output().finish();
  }

  /**
   * This function setup the query for querying the given property. A recursive function to add the
   * given property to the SQL select statement.
   * 
   * @param table
   * @param property This accept only the {@link PropertyRef} and {@link PropertyOwned} properties.
   * @return We get back the column for the select. We can decide if we add it to the select or not.
   * 
   */
  private final SQLSelectColumn setupProperty(SQLSelectFromNode table, Property<?> property,
      SQLStatementBuilderIF builder) {
    SQLSelectColumn column = null;
    if (property instanceof PropertyRef<?>) {
      PropertyRef<?> propertyRef = (PropertyRef<?>) property;
      SQLSelectFromNode currentTable = table;
      List<Reference<?, ?>> joinPath = propertyRef.getJoinReferences();
      // We need the list of joins as a path to the given property at the end of the path.
      for (Reference<?, ?> reference : joinPath) {
        // Find the target join from the current table
        SQLSelectJoin join = currentTable.findJoin(reference.getName());
        // If we haven' found the join then we have to create it here to have the path here.
        if (join == null) {
          // Create the target table in the select.
          TableDefinition tableDef = reference.getTarget().tableDefinition();
          SQLTableNode targetTable = new SQLTableNode(tableDef.getSchema(), tableDef.getName());
          SQLSelectFromTableNode target = new SQLSelectFromTableNode(targetTable, nextTableAlias());
          join = new SQLSelectJoin(currentTable, target, !reference.isMandatory());
          join.setReferenceName(reference.getName());
          for (Join<?> refJoin : reference.joins()) {
            // TODO handle case when join is not based on owned properties
            join.addJoin(
                builder
                    .getColumn(((PropertyOwned<?>) refJoin.getSourceProperty()).getDbExpression()),
                builder
                    .getColumn(((PropertyOwned<?>) refJoin.getTargetProperty()).getDbExpression()));
          }
          currentTable.joins().add(join);
        }
        currentTable = join.target();
      }
      // TODO If we have computed or referred property at the end then we must recurse!
      // At the end we can add the referred column to the currentTable.
      // TODO null check: getPropertyOwned will return NULL for computed referedd properties
      column = new SQLSelectColumn(currentTable,
          builder.getColumn(propertyRef.getReferredOwnedProperty().getDbExpression()),
          nextColumnAlias());
    } else if (property instanceof PropertyOwned<?>) {
      PropertyOwned<?> propertyOwned = (PropertyOwned<?>) property;
      column = new SQLSelectColumn(table, builder.getColumn(propertyOwned.getDbExpression()),
          nextColumnAlias());
    } else if (property instanceof PropertyComputed<?>) {
      // There are some privileged computation that are supported by the SQL layer. Any other
      // computed properties will be skipped.
      PropertyComputed<?> propertyComputed = (PropertyComputed<?>) property;
      if (propertyComputed.getBasicType() != BasicComputation.NONE) {
        // We need to setup our required properties. The result is a list that contains the columns
        // for every required property at the same index.
        List<SQLSelectColumn> requiredColumns =
            new ArrayList<>(propertyComputed.getRequiredProperties().size());
        for (Property<?> requiredProperty : propertyComputed.getRequiredProperties()) {
          requiredColumns.add(setupProperty(table, requiredProperty, builder));
        }
        // We add the count with the name of the property as alias.
        column = new SQLSelectAggregateColumn(table,
            builder.getFunctionColumn(propertyComputed, requiredColumns),
            propertyComputed.getName());
      }
      // Here comes the rowFetchFunction.add(Computation...)
    } else if (property instanceof PropertySqlComputed<?>) {
      PropertySqlComputed<?> propertySqlComputed = (PropertySqlComputed<?>) property;

      ArrayList<Property<?>> requiredProperties =
          builder.getRequiredPropertiesOfSqlColumn(propertySqlComputed);

      ArrayList<SQLSelectColumn> requiredColumns = new ArrayList<>();
      for (Property<?> requiredProperty : requiredProperties) {
        if (requiredProperty instanceof PropertyComputed) {
          // TODO only after rowFetchFunction evaluation?
        } else {
          requiredColumns.add(setupProperty(table, requiredProperty, builder));
        }
      }

      String sqlComputedColumn = builder.getSqlComputedColumn(propertySqlComputed, requiredColumns);

      column = new SQLComputedColumn(table, sqlComputedColumn, propertySqlComputed.getName());
    }
    return column;
  }

  /**
   * Constructs the next alias for a column in the select.
   * 
   * @return
   */
  private final String nextColumnAlias() {
    return COLUMNPREFIX + columnIndex++;
  }

  /**
   * Constructs the next table alias.
   * 
   * @return
   */
  private final String nextTableAlias() {
    return ALIASPREFIX + aliasIndex++;
  }

}
