package org.smartbit4all.domain.utility.crud;

import java.util.List;
import java.util.Optional;
import org.smartbit4all.core.SB4CompositeFunction;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.smartbit4all.domain.service.query.Query;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;

public class CrudRead<E extends EntityDefinition> implements Query<E> {

  private Query<E> query;

  private E entityDef;

  @SuppressWarnings("unchecked")
  CrudRead(E entityDef) {
    this.entityDef = entityDef;
    query = (Query<E>) entityDef.services().crud().query();
  }

  @Override
  public CrudRead<E> nameAs(String name) {
    query.nameAs(name);
    return this;
  }

  @Override
  public String name() {
    return query.name();
  }

  @Override
  public CrudRead<E> where(Expression where) {
    query.where(where);
    return this;
  }

  @Override
  public void execute() throws Exception {
    if (result() == null) {
      throw new IllegalStateException(
          "CrudQuery execute can not be called without the result set! Maybe you"
              + " want to use the executeIntoTableData() function instead!");
    }
    query.from(entityDef);
    query.execute();
  }

  public TableData<E> executeIntoTableData() throws Exception {
    if (query.result() == null) {
      TableData<E> result = new TableData<>(entityDef);
      query.into(result);
    }
    this.execute();
    return query.result();
  }

  public Optional<DataRow> firstRow() throws Exception {
    query.limit(1);
    TableData<E> result = executeIntoTableData();
    return Optional.ofNullable(result.rows().get(0));
  }

  public List<DataRow> firstRows(int rowNum) throws Exception {
    query.limit(rowNum);
    TableData<E> result = executeIntoTableData();
    return result.rows();
  }
  
  public <T> Optional<T> firstRowValue(Property<T> property) throws Exception {
    query.limit(1);
    TableData<E> result = executeIntoTableData();
    checkResultProperty(property, result);
    return getValueFromResult(property, result);
  }

  /**
   * @param msg exception message when there are more than one result rows 
   * @return
   * @throws Exception
   */
  public Optional<DataRow> onlyOne(String multipleRowsExceptionMessage) throws Exception {
    TableData<E> result = executeIntoTableData();
    checkResultSize(multipleRowsExceptionMessage, result);
    return Optional.ofNullable(result.rows().get(0));
  }
  
  /**
   * @return the optional first result row
   * @throws IllegalArgumentException when there are more then one result rows. 
   */
  public Optional<DataRow> onlyOne() throws Exception {
    return onlyOne("There are more than one results on read!");
  }

  public <T> Optional<T> onlyOneValue(Property<T> property, String multipleRowsExceptionMessage) throws Exception {
    TableData<E> result = executeIntoTableData();
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

  private void checkResultSize(String multipleRowsExceptionMessage, TableData<E> result)
      throws IllegalAccessError {
    if(result.size() > 1) {
      throw new IllegalAccessError(multipleRowsExceptionMessage);
    }
  }

  private <T> void checkResultProperty(Property<T> property, TableData<E> result) {
    if(result.getColumn(property) == null) {
      throw new IllegalArgumentException("The given property does not occure in the result TableData");
    }
  }

  private <T> Optional<T> getValueFromResult(Property<T> property, TableData<E> result) {
    List<DataRow> resultRows = result.rows();
    if(resultRows.size() > 0) {
      T value = resultRows.get(0).get(property);
      return Optional.ofNullable(value);
    } else {
      return Optional.empty();
    }
  }
  
  public CrudRead<E> selectAllProperties() {
    query.select(entityDef.allProperties());
    return this;
  }

  @Override
  public CrudRead<E> select(List<Property<?>> properties) {
    query.select(properties);
    return this;
  }

  @Override
  public CrudRead<E> select(PropertySet propertySet) {
    query.select(propertySet);
    return this;
  }

  @Override
  public SB4CompositeFunction<QueryInput<E>, QueryOutput<E>> pre() {
    return query.pre();
  }

  @Override
  public CrudRead<E> select(Property<?>... propertySet) {
    query.select(propertySet);
    return this;
  }

  @Override
  public SB4CompositeFunction<QueryInput<E>, QueryOutput<E>> post() {
    return query.post();
  }

  @Override
  public void setInput(QueryInput<E> input) {
    query.setInput(input);
  }

  @Override
  public void setOutput(QueryOutput<E> output) {
    query.setOutput(output);
  }

  @Override
  public CrudRead<E> all() {
    query.all();
    return this;
  }

  @Override
  public CrudRead<E> order(SortOrderProperty order) {
    query.order(order);
    return this;
  }

  @Override
  public CrudRead<E> order(Property<?> property) {
    query.order(property);
    return this;
  }

  @Override
  public CrudRead<E> groupBy(List<Property<?>> groupBy) {
    query.groupBy(groupBy);
    return this;
  }

  @Override
  public CrudRead<E> groupBy(Property<?>... groupBys) {
    query.groupBy(groupBys);
    return this;
  }

  @Override
  public CrudRead<E> limit(int queryLimit) {
    query.limit(queryLimit);
    return this;
  }

  @Override
  public CrudRead<E> count() {
    query.count();
    return this;
  }

  @Override
  public CrudRead<E> min(Property<?> property) {
    query.min(property);
    return this;
  }

  @Override
  public CrudRead<E> max(Property<?> property) {
    query.max(property);
    return this;
  }

  @Override
  public CrudRead<E> avg(Property<?> property) {
    query.avg(property);
    return this;
  }

  @Override
  public CrudRead<E> sum(Property<?> property) {
    query.sum(property);
    return this;
  }

  @Override
  public CrudRead<E> into(TableData<E> result) {
    query.into(result);
    return this;
  }

  @Override
  public CrudRead<E> append(TableData<E> result) {
    query.append(result);
    return this;
  }

  @Override
  public CrudRead<E> merge(TableData<E> result) {
    query.merge(result);
    return this;
  }

  @Override
  public CrudRead<E> from(E entityDef) {
    query.from(entityDef);
    return this;
  }

  @Override
  public CrudRead<E> inputAs(QueryInput<E> input) {
    query.inputAs(input);
    return this;
  }

  @Override
  public QueryInput<E> input() {
    return query.input();
  }

  @Override
  public CrudRead<E> outputAs(QueryOutput<E> output) {
    query.outputAs(output);
    return this;
  }

  @Override
  public QueryOutput<E> output() {
    return query.output();
  }

  @Override
  public CrudRead<E> lock() {
    query.lock();
    return this;
  }

  @Override
  public CrudRead<E> tryLock() {
    query.tryLock();
    return this;
  }

  @Override
  public CrudRead<E> tryLock(long timeOut) {
    query.tryLock(timeOut);
    return this;
  }

  @Override
  public <B> Query<E> select(Class<B> beanClass) throws Exception {
    query.select(beanClass);
    return this;
  }

  @Override
  public TableData<E> result() {
    return query.result();
  }

  @Override
  public TableData<E> listData() throws Exception {
    return query.listData();
  }

  @Override
  public <B> List<B> list(Class<B> beanClass) throws Exception {
    return query.list(beanClass);
  }

}
