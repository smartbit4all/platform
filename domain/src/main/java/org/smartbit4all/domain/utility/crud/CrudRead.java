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

import java.util.List;
import java.util.Optional;
import org.smartbit4all.core.SB4CompositeFunction;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntitySetup;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.smartbit4all.domain.service.query.Query;
import org.smartbit4all.domain.service.query.QueryApi;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;

public class CrudRead<E extends EntityDefinition> {

  private Query<E> query;

  private E entityDef;

  @SuppressWarnings("unchecked")
  CrudRead(E entityDef) {
    this(entityDef, (Query<E>) entityDef.services().crud().query());
  }

  CrudRead(E entityDef, Query<E> query) {
    this.entityDef = entityDef;
    this.query = query;
  }

  public CrudRead<E> nameAs(String name) {
    query.nameAs(name);
    return this;
  }

  public String name() {
    return query.name();
  }


  public CrudRead<E> where(Expression where) {
    if (where != null) {
      query.where(where);
    }
    return this;
  }


  public void execute() throws Exception {
    if (result() == null) {
      throw new IllegalStateException(
          "CrudQuery execute can not be called without the result set! Maybe you"
              + " want to use the listData() or list(BeanClass) function instead!");
    }

    QueryApi queryApi = null;
    if (entityDef instanceof EntitySetup) {
      queryApi = ((EntitySetup) entityDef).getQueryApi();
    }

    if (queryApi == null) {
      getQuery().execute();
    } else {
      queryApi.execute(queryApi.prepare(getQuery()));
    }

  }

  public Query<E> getQuery() {
    query.from(entityDef);
    return query;
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
    query.limit(1);

    TableData<E> result = listData();
    List<DataRow> resultRows = result.rows();

    if (resultRows.isEmpty()) {
      return Optional.empty();
    }

    return Optional.ofNullable(resultRows.get(0));
  }

  public List<DataRow> firstRows(int rowNum) throws Exception {
    query.limit(rowNum);
    TableData<E> result = listData();
    return result.rows();
  }

  public <T> Optional<T> firstRowValue(Property<T> property) throws Exception {
    query.limit(1);
    TableData<E> result = listData();
    checkResultProperty(property, result);
    return getValueFromResult(property, result);
  }

  /**
   * In this case we want to run the query and retrieve the first value from the first row.
   * Typically the result of the count, sum avg etc. functions can be executed in this way.
   * 
   * @param <T>
   * @return
   * @throws Exception
   */
  public Optional<?> singleValue() throws Exception {
    TableData<E> result = listData();
    DataColumn<?> column = result.columns().iterator().next();
    return getValueFromResult(column, result);
  }

  /**
   * @param msg exception message when there are more than one result rows
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

  public CrudRead<E> selectAllProperties() {
    query.select(entityDef.allProperties());
    return this;
  }


  public CrudRead<E> select(List<Property<?>> properties) {
    query.select(properties);
    return this;
  }


  public CrudRead<E> select(PropertySet propertySet) {
    query.select(propertySet);
    return this;
  }


  public SB4CompositeFunction<QueryInput<E>, QueryOutput<E>> pre() {
    return query.pre();
  }


  public SB4CompositeFunction<QueryInput<E>, QueryOutput<E>> getPreSection() {
    return query.getPreSection();
  }


  public CrudRead<E> select(Property<?>... propertySet) {
    query.select(propertySet);
    return this;
  }


  public SB4CompositeFunction<QueryInput<E>, QueryOutput<E>> post() {
    return query.post();
  }


  public SB4CompositeFunction<QueryInput<E>, QueryOutput<E>> getPostSection() {
    return query.getPostSection();
  }


  public void setInput(QueryInput<E> input) {
    query.setInput(input);
  }


  public void setOutput(QueryOutput<E> output) {
    query.setOutput(output);
  }


  public CrudRead<E> all() {
    query.all();
    return this;
  }


  public CrudRead<E> order(SortOrderProperty order) {
    query.order(order);
    return this;
  }


  public CrudRead<E> order(Property<?> property) {
    query.order(property);
    return this;
  }


  public CrudRead<E> groupBy(List<Property<?>> groupBy) {
    query.groupBy(groupBy);
    return this;
  }


  public CrudRead<E> groupBy(Property<?>... groupBys) {
    query.groupBy(groupBys);
    return this;
  }


  public CrudRead<E> limit(int queryLimit) {
    query.limit(queryLimit);
    return this;
  }


  public CrudRead<E> distinct() {
    query.distinct();
    return this;
  }


  public CrudRead<E> distinctNot() {
    query.distinctNot();
    return this;
  }


  public CrudRead<E> count() {
    query.count();
    return this;
  }


  public CrudRead<E> min(Property<?> property) {
    query.min(property);
    return this;
  }


  public CrudRead<E> max(Property<?> property) {
    query.max(property);
    return this;
  }


  public CrudRead<E> avg(Property<?> property) {
    query.avg(property);
    return this;
  }


  public CrudRead<E> sum(Property<?> property) {
    query.sum(property);
    return this;
  }


  public CrudRead<E> into(TableData<E> result) {
    query.into(result);
    return this;
  }


  public CrudRead<E> append(TableData<E> result) {
    query.append(result);
    return this;
  }


  public CrudRead<E> merge(TableData<E> result) {
    query.merge(result);
    return this;
  }


  public CrudRead<E> from(E entityDef) {
    query.from(entityDef);
    return this;
  }


  public CrudRead<E> inputAs(QueryInput<E> input) {
    query.inputAs(input);
    return this;
  }


  public QueryInput<E> input() {
    return query.input();
  }


  public CrudRead<E> outputAs(QueryOutput<E> output) {
    query.outputAs(output);
    return this;
  }


  public QueryOutput<E> output() {
    return query.output();
  }


  public CrudRead<E> lock() {
    query.lock();
    return this;
  }


  public CrudRead<E> tryLock() {
    query.tryLock();
    return this;
  }


  public CrudRead<E> tryLock(long timeOut) {
    query.tryLock(timeOut);
    return this;
  }


  public <B> CrudRead<E> select(Class<B> beanClass) throws Exception {
    query.select(beanClass);
    return this;
  }


  public TableData<E> result() {
    return query.result();
  }


  public TableData<E> listData() throws Exception {
    if (query.result() == null) {
      TableData<E> result = new TableData<>(entityDef);
      query.into(result);
    }
    execute();
    return query.result();
  }


  public <B> List<B> list(Class<B> beanClass) throws Exception {
    TableData<E> tableData = listData();
    return tableData.asList(beanClass);
  }

}
