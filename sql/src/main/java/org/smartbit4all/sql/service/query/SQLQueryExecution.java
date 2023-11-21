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
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.meta.EntityDefinition.TableDefinition;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.ExpressionBetween;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.ExpressionInDataSet;
import org.smartbit4all.domain.meta.ExpressionIsNull;
import org.smartbit4all.domain.meta.ExpressionVisitor;
import org.smartbit4all.domain.meta.Operand;
import org.smartbit4all.domain.meta.OperandProperty;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyComputed;
import org.smartbit4all.domain.meta.PropertyFunction;
import org.smartbit4all.domain.meta.PropertyObject;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.PropertySqlComputed;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.Reference.Join;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.smartbit4all.domain.service.query.QueryOutputResultAssembler;
import org.smartbit4all.domain.service.query.QueryOutputResultAssemblers;
import org.smartbit4all.sql.SQLComputedColumn;
import org.smartbit4all.sql.SQLGroupByColumn;
import org.smartbit4all.sql.SQLGroupByComputedColumn;
import org.smartbit4all.sql.SQLOrderByColumn;
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
import org.smartbit4all.sql.config.SQLDBParameter;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * Responsible for managing all the information during SQL execution of a query. This will be
 * initiated for every execution and refers to the original {@link QueryInput} object.
 *
 * @author Peter Boros
 */
final class SQLQueryExecution {
  private static final Logger log = LoggerFactory.getLogger(SQLQueryExecution.class);

  /**
   * The alias prefix for the tables in the from section of the select.
   */
  public static final String ALIASPREFIX = "T";

  /**
   * The column prefix for the tables in the from section of the select.
   */
  static final String COLUMNPREFIX = "C";

  /**
   * The table aliases are indexed by this variable.
   */
  int aliasIndex = 1;

  /**
   * The column aliases are indexed by this variable.
   */
  int columnIndex = 1;

  /**
   * The select statement for the query.
   */
  SQLSelectStatement select;

  /**
   * The JDBC has an important parameter during the fetch. The number of rows to fetch at once. The
   * default is 20 row in one fetch operation.
   */
  int fetchRowNumber = 20;

  /**
   * The JDBC connection accessor.
   */
  JdbcTemplate jdbcTemplate;

  final QueryInput queryInput;

  final QueryOutput queryOutput;

  private String schema;

  private SQLDBParameter sqlDBParameter;

  private ObjectApi objectApi;

  public SQLQueryExecution(JdbcTemplate jdbcTemplate, QueryInput query, String schema,
      SQLDBParameter sqlDBParameter, ObjectApi objectApi) {
    super();
    this.jdbcTemplate = jdbcTemplate;
    this.queryInput = query;
    queryOutput = new QueryOutput(query.getName(), query.entityDef());
    select = new SQLSelectStatement();
    aliasIndex = 1;
    columnIndex = 1;
    this.schema = schema;
    this.sqlDBParameter = sqlDBParameter;
    this.objectApi = objectApi;
  }

  /**
   * This execute constructs the {@link SQLSelectStatement} and execute it. It uses the
   * {@link #queryOutput} to fetch into.
   *
   */
  public void execute() {
    // That time the entity is based on a table.
    TableDefinition table = queryInput.getForcedTableName() != null
        ? new TableDefinition(queryInput.getForcedTableName())
        : queryInput.entityDef().tableDefinition();
    SQLTableNode tableNode = new SQLTableNode(schema, table.getName());
    SQLSelectFromTableNode rootTable = new SQLSelectFromTableNode(tableNode, nextTableAlias());
    // This map must be a list based map to preserve the order.
    Map<String, Property<?>> columnMap = new HashMap<>();
    SQLStatementBuilderIF builder = new SQLStatementBuilder(sqlDBParameter);
    select.setQueryLimit(queryInput.limit());
    select.setDistinctQuery(queryInput.isDistinct());
    for (Property<?> property : queryInput.properties()) {
      SQLSelectColumn column = setupProperty(rootTable, property, builder);
      // It's a column in the select add this column.
      if (column != null) {
        select.addColumn(column);
        columnMap.put(column.getAlias().toUpperCase(), property);
      }
    }
    // Adding the expression to the select. It means that we visit the expression and add the
    // properties.
    if (queryInput.where() != null) {
      queryInput.where().accept(new ExpressionVisitor() {

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

        @Override
        public void visitInDataSet(ExpressionInDataSet expressionInDataSet) {
          prepareOperand(expressionInDataSet.getOperand(), rootTable, builder);
        }

      });
      select.setWhere(new SQLWhere(queryInput.where()));
    }

    for (SortOrderProperty sortOrder : queryInput.orderBys()) {
      // Add them to the select like any other property.
      SQLSelectColumn column = setupProperty(rootTable, sortOrder.property, builder);
      select.addOrderBy(new SQLOrderByColumn(column.from(), column.getColumnName(), sortOrder.asc,
          sortOrder.nullsFirst));
    }

    // Adding the group by to the SQLSelectStatemenet
    for (Property<?> property : queryInput.groupByProperties()) {
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
    if (queryInput.lockRequest() != null) {
      select.setLock(new SQLSelectLock(queryInput.lockRequest().timeOut()));
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
        if (log.isDebugEnabled()) {
          log.debug(stmt.toString());
        }
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

  /**
   * Examine if the given operand is an {@link OperandProperty} and if it is then
   * {@link #setupProperty(SQLSelectFromNode, Property, SQLStatementBuilderIF)} and set the
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

      // set the qualifiers of the required operand properties too
      if (column instanceof SQLComputedColumn) {
        ArrayList<SQLSelectColumn> requiredColumns =
            ((SQLComputedColumn) column).getRequiredColumns();
        List<OperandProperty<?>> requiredOperandProperties =
            operandProperty.getRequiredOperandProperties();
        if (requiredOperandProperties != null && requiredColumns != null) {
          // no checks on the size of the lists: these MUST match at this point!
          for (int i = 0; i < requiredOperandProperties.size(); i++) {
            requiredOperandProperties.get(i).setQualifier(requiredColumns.get(i).from().alias());
          }
        }
      }
    }
  }

  private final void fetchResult(ResultSet resultSet, Map<String, Property<?>> columnMap,
      SQLStatementBuilderIF builder) throws SQLException, DataAccessException {
    // For the faster fetch result we need to have an index to index translation between the JDBC
    // ResultSet and the QueryResult.
    ResultSetMetaData metaData = resultSet.getMetaData();
    int indexTrans[] = new int[metaData.getColumnCount()];
    Property<?> propertyTrans[] = new Property<?>[metaData.getColumnCount()];
    QueryOutputResultAssembler resultAssembler =
        QueryOutputResultAssemblers.create(this.queryInput, this.queryOutput, this.objectApi);
    resultAssembler.start();
    for (int i = 0; i < metaData.getColumnCount(); i++) {
      String columnName = builder.getColumnAssignedColumnName(metaData, i + 1).toUpperCase();
      Property<?> property = columnMap.get(columnName);
      indexTrans[i] = resultAssembler.accept(property);
      propertyTrans[i] = property;
    }
    resultAssembler.finishColumns();
    // Iterate the JDBC result set.
    while (resultSet.next()) {
      resultAssembler.startRow();
      for (int colIdx = 0; colIdx < indexTrans.length; colIdx++) {
        if (indexTrans[colIdx] != QueryOutputResultAssembler.COLUMNNOTACCEPTED) {
          try {
            resultAssembler.setValue(indexTrans[colIdx],
                builder.getResultValue(resultSet, colIdx + 1, propertyTrans[colIdx]));
          } catch (Exception e) {
            throw new DataRetrievalFailureException("Error occured while fetching data", e);
          }
        }
      }
      // TODO Execute rowComputation.
      resultAssembler.finishRow();
    }
    resultAssembler.finish();
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
    if (property instanceof PropertyObject) {
      Property<?> basic = ((PropertyObject) property).getBasic();
      return setupProperty(table, basic, builder);
    }
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
          SQLTableNode targetTable = new SQLTableNode(schema, tableDef.getName());
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
      // computed properties will be skipped.

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

      column = new SQLComputedColumn(table, sqlComputedColumn, propertySqlComputed.getName(),
          requiredColumns);
    }

    PropertyFunction propertyFunction = property.getPropertyFunction();
    if (propertyFunction != null) {
      /*
       * In case the property has applied functions on it, we need to create an SQLComputedColumn A
       * property like this can has other dependent properties and these are also need to be set up
       * with the right sql aliases, so we call the setupProperty method recursively. These computed
       * SQLSelectColumns then will be added as required columns to the result.
       */

      if (sqlDBParameter != null) {
        propertyFunction = sqlDBParameter.convertPropertyFunction(propertyFunction);
      }
      List<Property<?>> requiredProperties = propertyFunction.getRequiredProperties();
      ArrayList<SQLSelectColumn> requiredColumns = new ArrayList<>();
      if (requiredProperties != null && !requiredProperties.isEmpty()) {
        for (Property<?> requiredProperty : requiredProperties) {
          if (requiredProperty instanceof PropertyComputed) {
            // TODO only after rowFetchFunction evaluation?
          } else {
            SQLSelectColumn requiredColumn = setupProperty(table, requiredProperty, builder);
            requiredColumns.add(requiredColumn);
          }
        }
      }

      List<String> requiredParamSqlStrings = requiredColumns.stream()
          .map(rc -> rc.getNameWithFrom())
          .collect(Collectors.toList());
      String functionSqlString = builder.getFunctionSqlString(propertyFunction,
          column.getNameWithFrom(), requiredParamSqlStrings);
      column = new SQLComputedColumn(column.from(), functionSqlString, nextColumnAlias(),
          requiredColumns);
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
