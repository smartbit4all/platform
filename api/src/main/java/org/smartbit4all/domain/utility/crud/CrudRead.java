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
package org.smartbit4all.domain.utility.crud;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.domain.config.DomainAPI;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.DataRowValBeanInvocationHandler;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionContainer;
import org.smartbit4all.domain.meta.LockRequest;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.smartbit4all.domain.service.CrudApis;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.smartbit4all.domain.service.transfer.BeanEntityBinding;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.smartbit4all.domain.utility.serialize.TableDataPager;

public class CrudRead<E extends EntityDefinition> {

  private QueryInput queryInput;
  private QueryOutput queryOutput;

  private E entityDef;

  enum QueryOutputMode {
    INTO, APPEND
  }

  private QueryOutputMode queryOutputMode = QueryOutputMode.INTO;

  CrudRead(E entityDef) {
    this(entityDef, new QueryInput());
  }

  CrudRead(E entityDef, String name) {
    this(entityDef, new QueryInput(name));
  }

  CrudRead(E entityDef, QueryInput query) {
    this.entityDef = entityDef;
    query.from(entityDef);
    this.queryInput = query;
    this.queryOutput = new QueryOutput(queryInput.getName(), entityDef);
  }

  /**
   * The name of the query. If we don't name it directly then it will have a generated name.
   * 
   * @return
   */
  public String name() {
    return queryInput.getName();
  }

  /**
   * Sets the where for the query.
   * 
   * @param where
   * @return Fluid API
   */
  public CrudRead<E> where(Expression where) {
    if (where != null) {
      if (!(where instanceof ExpressionContainer)) {
        queryInput.where(where.BRACKET());
      } else {
        queryInput.where(where);
      }
    }
    return this;
  }


  public void execute() throws Exception {
    Objects.requireNonNull(queryInput, "Can not execute query with null value QueryInput!");

    QueryOutput output = CrudApis.getCrudApi().executeQuery(queryInput);

    if (this.queryOutput == null || !this.queryOutput.hasResult()) {
      this.queryOutput = output;
      return;
    }

    switch (queryOutputMode) {
      case INTO:
        this.queryOutput.copyResult(output);
        break;
      case APPEND:
        this.queryOutput.appendResult(output);
        break;
      default:
        break;
    }

  }

  public QueryInput getQuery() {
    queryInput.from(entityDef);
    return queryInput;
  }

  /**
   * {@link Deprecated Use the #listData() }
   * 
   * @return
   * @throws Exception
   */
  @Deprecated
  public TableData<E> executeIntoTableData() throws Exception {
    return listData();
  }

  public Optional<DataRow> firstRow() throws Exception {
    queryInput.limit(1);

    TableData<E> result = listData();
    List<DataRow> resultRows = result.rows();

    if (resultRows.isEmpty()) {
      return Optional.empty();
    }

    return Optional.ofNullable(resultRows.get(0));
  }

  public List<DataRow> firstRows(int rowNum) throws Exception {
    queryInput.limit(rowNum);
    TableData<E> result = listData();
    return result.rows();
  }

  public <T> Optional<T> firstRowValue(Property<T> property) throws Exception {
    queryInput.limit(1);
    TableData<E> result = listData();
    checkResultProperty(property, result);
    return getValueFromResult(property, result);
  }

  /**
   * In this case we want to run the query and retrieve the first value from the first row.
   * Typically the result of the count, sum avg etc. functions can be executed in this way.
   * 
   * @return
   * @throws Exception
   */
  public Optional<?> singleValue() throws Exception {
    TableData<E> result = listData();
    DataColumn<?> column = result.columns().iterator().next();
    return getValueFromResult(column, result);
  }

  /**
   * @param multipleRowsExceptionMessage exception message when there are more than one result rows
   * @return
   * @throws Exception
   */
  public Optional<DataRow> onlyOne(String multipleRowsExceptionMessage) throws Exception {
    TableData<E> result = listData();
    checkResultSize(multipleRowsExceptionMessage, result);
    if (result.rows().isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(result.rows().get(0));
  }

  /**
   * @return the optional first result row
   * @throws IllegalArgumentException when there are more then one result rows.
   */
  public Optional<DataRow> onlyOne() throws Exception {
    return onlyOne("There are more than one results on read!");
  }

  public <T> Optional<T> onlyOneValue(Property<T> property, String multipleRowsExceptionMessage)
      throws Exception {
    TableData<E> result = listData();
    checkResultSize(multipleRowsExceptionMessage, result);
    checkResultProperty(property, result);
    return getValueFromResult(property, result);
  }

  /**
   * 
   * @param <T> the value type of the property
   * @param property
   * @return
   * @throws Exception
   */
  public <T> Optional<T> onlyOneValue(Property<T> property) throws Exception {
    return onlyOneValue(property, "There are more than one results on read!");
  }

  private void checkResultSize(String multipleRowsExceptionMessage, TableData<E> result) {
    if (result.size() > 1) {
      throw new IllegalArgumentException(multipleRowsExceptionMessage);
    }
  }

  private <T> void checkResultProperty(Property<T> property, TableData<E> result) {
    if (result.getColumn(property) == null) {
      throw new IllegalArgumentException(
          "The given property does not occure in the result TableData");
    }
  }

  private <T> Optional<T> getValueFromResult(Property<T> property, TableData<E> result) {
    List<DataRow> resultRows = result.rows();
    if (!resultRows.isEmpty()) {
      T value = resultRows.get(0).get(property);
      return Optional.ofNullable(value);
    } else {
      return Optional.empty();
    }
  }

  private Optional<?> getValueFromResult(DataColumn<?> column, TableData<E> result) {
    List<DataRow> resultRows = result.rows();
    if (!resultRows.isEmpty()) {
      Object value = result.get(column, resultRows.get(0));
      return Optional.ofNullable(value);
    } else {
      return Optional.empty();
    }
  }

  /**
   * We can add all columns to the query.
   * 
   * @return Fluid API
   */
  public CrudRead<E> selectAllProperties() {
    return all();
  }

  /**
   * We can set the properties to list as the result of the query. By default the result contains
   * only the identifiers.
   * 
   * @return Fluid API
   */
  public CrudRead<E> select(List<Property<?>> properties) {
    queryInput.select(properties);
    return this;
  }

  /**
   * Add the properties to the query.
   * 
   * @param propertySet
   * @return
   */
  public CrudRead<E> select(PropertySet propertySet) {
    queryInput.select(propertySet);
    return this;
  }

  /**
   * Add the properties to the query.
   * 
   * @param propertySet varargs to add properties in a convenient way.
   * @return
   */
  public CrudRead<E> select(Property<?>... propertySet) {
    queryInput.select(propertySet);
    return this;
  }

  // public void setInput(QueryInput input) {
  // query.setInput(input);
  // }
  //
  //
  // public void setOutput(QueryOutput output) {
  // query.setOutput(output);
  // }


  /**
   * We can add all columns to the query.
   * 
   * @deprecated use {@link #selectAllProperties()} instead!
   * @return Fluid API
   */
  @Deprecated
  public CrudRead<E> all() {
    queryInput.all();
    return this;
  }

  /**
   * Defines a sort order for the query.
   * 
   * @param order
   * @return Fluid API
   */
  public CrudRead<E> order(SortOrderProperty order) {
    queryInput.orderBys().add(order);
    return this;
  }

  /**
   * Defines a sort order for the query with ascending parameter.
   * 
   * @param property
   * @return Fluid API
   */
  public CrudRead<E> order(Property<?> property) {
    queryInput.orderBys().add(property.asc());
    return this;
  }

  /**
   * @param groupBy The list of properties to use for grouping by.
   * @return Fluid API
   */
  public CrudRead<E> groupBy(List<Property<?>> groupBy) {
    queryInput.groupBy(groupBy);
    return this;
  }

  /**
   * @param groupBys The list of properties to use for grouping by.
   * @return Fluid API
   */
  public CrudRead<E> groupBy(Property<?>... groupBys) {
    queryInput.groupBy(groupBys);
    return this;
  }

  /**
   * Set the limit of the results at the level of SQL. Preserve the JVM from the flood of byte in
   * case an SQL select with unsatisfying conditions.
   * 
   * @param queryLimit
   * @return
   */
  public CrudRead<E> limit(int queryLimit) {
    queryInput.limit(queryLimit);
    return this;
  }

  /**
   * Set the distinct for the query. In this case every result will be different in the result set.
   * In case of SQL queries it will add the distinct clause for the select.
   * 
   * @return
   */
  public CrudRead<E> distinct() {
    queryInput.setDistinct(true);
    return this;
  }

  /**
   * We can remove the distinct flag from the query.
   * 
   * @return
   */
  public CrudRead<E> distinctNot() {
    queryInput.setDistinct(false);
    return this;
  }

  /**
   * Requires the count of the result records.
   * 
   * @return Fluid API
   */
  public CrudRead<E> count() {
    queryInput.count();
    return this;
  }

  /**
   * Requires the minimum value of the given property.
   * 
   * @param property
   * @return Fluid API
   */
  public CrudRead<E> min(Property<?> property) {
    queryInput.min(property);
    return this;
  }

  /**
   * Requires the maximum value of the given property.
   * 
   * @param property
   * @return Fluid API
   */
  public CrudRead<E> max(Property<?> property) {
    queryInput.max(property);
    return this;
  }

  /**
   * Requires the average value of the given property.
   * 
   * @param property
   * @return Fluid API
   */
  public CrudRead<E> avg(Property<?> property) {
    queryInput.avg(property);
    return this;
  }

  /**
   * Requires the summary of the given property.
   * 
   * @param property
   * @return Fluid API
   */
  public CrudRead<E> sum(Property<?> property) {
    queryInput.sum(property);
    return this;
  }

  /**
   * Query result will appear in result parameter. All existing rows will be deleted.
   * 
   * @param result
   * @return Fluid API
   */
  public CrudRead<E> into(TableData<E> result) {
    this.queryOutput.setTableData(result);
    this.queryOutputMode = QueryOutputMode.INTO;
    return this;
  }

  /**
   * Query result will be appended to the result parameter. Existing rows will NOT be deleted.
   * 
   * @param result
   * @return Fluid API
   */
  public CrudRead<E> append(TableData<E> result) {
    this.queryOutput.setTableData(result);
    this.queryOutputMode = QueryOutputMode.APPEND;
    return this;
  }

  /**
   * Query result will appear in result parameter. All existing rows will be deleted.
   * 
   * @param resultData
   * @return Fluid API
   */
  public CrudRead<E> into(BinaryData resultData) {
    this.queryInput.setResultSerialized(true);
    this.queryOutput.setSerializedTableData(resultData);
    this.queryOutputMode = QueryOutputMode.INTO;
    return this;
  }

  /**
   * Query result will be appended to the result parameter. Existing rows will NOT be deleted.
   * 
   * @param resultData
   * @return Fluid API
   */
  public CrudRead<E> append(BinaryData resultData) {
    this.queryOutput.setSerializedTableData(resultData);
    this.queryOutputMode = QueryOutputMode.APPEND;
    return this;
  }


  /**
   * The from sets the {@link EntityDefinition} that is the root for the query. All the related
   * entities must be attached with {@link Reference} as we add {@link PropertyRef} properties to
   * the query.
   * 
   * @param entityDef
   * @return
   */
  public CrudRead<E> from(E entityDef) {
    queryInput.from(entityDef);
    return this;
  }

  /**
   * The from sets the {@link EntityDefinition} that is the root for the query. All the related
   * entities must be attached with {@link Reference} as we add {@link PropertyRef} properties to
   * the query.
   * 
   * @param tableDataUri The table data the query is executed against.
   * @return
   */
  public CrudRead<E> fromTableData(URI tableDataUri) {
    queryInput.setTableDataUri(tableDataUri);
    return this;
  }

  /**
   * Set the input of the Query. The {@link QueryInput}'s name and EntityDefinition must match the
   * input's name and EntityDefinition!
   * 
   * @param input
   * @return Fluid API
   */
  public CrudRead<E> inputAs(QueryInput input) {
    this.queryInput = input;
    return this;
  }


  public QueryInput input() {
    return queryInput;
  }

  /**
   * Sets the output of the Query. The {@link QueryOutput}'s name and EntityDefinition must match
   * the input's name and EntityDefinition!
   * 
   * @param output
   * @return Fluid API
   */
  public CrudRead<E> outputAs(QueryOutput output) {
    this.queryOutput = output;
    return this;
  }

  /**
   * Returns the output of the Query.
   * 
   * @return Fluid API
   */
  public QueryOutput output() {
    return queryOutput;
  }

  /**
   * Initiate the lock on the query. It results a "FOR UPDATE" or similar statement at database
   * level. It waits until gets the lock or interrupted by someone.
   * 
   * @return
   */
  public CrudRead<E> lock() {
    initLock(-1);
    return this;
  }

  /**
   * Initiate the lock on the query. It results a "FOR UPDATE" or similar statement at database
   * level. If it cann't get the lock immediately then the query throws an exception.
   * 
   * @return
   */
  public CrudRead<E> tryLock() {
    initLock(0);
    return this;
  }

  /**
   * Initiate the lock on the query. It results a "FOR UPDATE" or similar statement at database
   * level. If it cann't get the lock during the given timeout period then the query throws an
   * exception.
   * 
   * @param timeOut The timeout for the lock trial in millisecond.
   * @return
   */
  public CrudRead<E> tryLock(long timeOut) {
    initLock(timeOut);
    return this;
  }

  private final CrudRead<E> initLock(long timeout) {
    if (queryInput != null) {
      queryInput.setLockRequest(new LockRequest(timeout));
    }
    return this;
  }

  /**
   * Add the properties to the query by a bean.
   * 
   * @param beanClass The bean that has fields translatable to entity properties
   * @return
   * @throws Exception
   */
  public <B> CrudRead<E> select(Class<B> beanClass) throws Exception {
    BeanEntityBinding binding =
        DomainAPI.get().transferService().binding(beanClass, queryInput.entityDef());
    queryInput.select(binding.bindingsByProperties().keySet());
    return this;
  }

  /**
   * Executes the query and returns the {@link TableData} as a result.
   * 
   * @return Return the result as the typed table data of the given entity.
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public TableData<E> listData() throws Exception {
    execute();
    return (TableData<E>) queryOutput.getTableData();
  }

  /**
   * Executes the query into a serialized {@link TableData} then builds a dynamic pager on it. This
   * {@link TableDataPager} can be used to fetch result fragments so only the required information
   * will be loaded into memory.
   */
  public TableDataPager<E> pageData(Class<E> entityDefClazz, EntityManager entityManager)
      throws Exception {
    if (!queryInput.isResultSerialized()) {
      queryInput.setResultSerialized(true);
    }
    execute();
    BinaryData resultFile = queryOutput.getSerializedTableData();

    return TableDataPager.create(entityDefClazz, resultFile, entityManager);
  }

  /**
   * Executes the query and returns the list of bean as a result. The result will be available later
   * on in the {@link #output()} object as {@link TableData} but it's more convenient to call this
   * if we would like to execute and retrieve the result at the same time. Better for fluid API
   * because the whole query can be included in line. The conversion to the bean is executed after
   * the query creates the inner {@link TableData} representation. So we can use the
   * {@link TableData} later on to retrieve other data formats from the same query result.
   * 
   * @param beanClass The bean class that must have registered transfer binding in the
   *        {@link TransferService}. It can be an interface also but in this case the instances will
   *        be proxies with {@link DataRowValBeanInvocationHandler}. Inside the invocation handler
   *        it's a Map so be careful with the performance!
   * @return The list with the bean instances for every result row.
   * @throws Exception
   */
  public <B> List<B> list(Class<B> beanClass) throws Exception {
    TableData<E> tableData = listData();
    return tableData.asList(beanClass);
  }

}
