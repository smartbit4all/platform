package org.smartbit4all.domain.service.query;

import java.util.List;
import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.config.DomainAPI;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.LockRequest;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.smartbit4all.domain.service.transfer.BeanEntityBinding;

public abstract class QueryImpl<E extends EntityDefinition>
    extends SB4FunctionImpl<QueryInput<E>, QueryOutput<E>> implements Query<E> {

  public QueryImpl() {
    input = new QueryInput<>();
    output = new QueryOutput<>();
  }

  @Override
  public Query<E> nameAs(String name) {
    input.nameAs(name);
    return this;
  }

  @Override
  public String name() {
    return input.name();
  }

  @Override
  public Query<E> select(List<Property<?>> properties) {
    input.select(properties);
    return this;
  }

  @Override
  public Query<E> select(PropertySet propertySet) {
    input.select(propertySet);
    return this;
  }

  @Override
  public Query<E> select(Property<?>... propertySet) {
    input.select(propertySet);
    return this;
  }

  @Override
  public <B> Query<E> select(Class<B> beanClass) throws Exception {
    BeanEntityBinding binding =
        DomainAPI.get().transferService().binding(beanClass, input.entityDef());
    input.select(binding.bindingsByProperties().keySet());
    return this;
  }

  @Override
  public Query<E> where(Expression where) {
    input.where(where);
    return this;
  }

  @Override
  public Query<E> limit(int queryLimit) {
    input.limit(queryLimit);
    return this;
  }

  @Override
  public Query<E> count() {
    input.count();
    return this;
  }

  @Override
  public Query<E> min(Property<?> property) {
    input.min(property);
    return this;
  }

  @Override
  public Query<E> max(Property<?> property) {
    input.max(property);
    return this;
  }

  @Override
  public Query<E> avg(Property<?> property) {
    input.avg(property);
    return this;
  }

  @Override
  public Query<E> sum(Property<?> property) {
    input.sum(property);
    return this;
  }

  @Override
  public Query<E> all() {
    input.all();
    return this;
  }

  @Override
  public Query<E> groupBy(List<Property<?>> groupBy) {
    input.groupBy(groupBy);
    return this;
  }

  @Override
  public Query<E> groupBy(Property<?>... groupBys) {
    input.groupBy(groupBys);
    return this;
  }

  @Override
  public Query<E> into(TableData<E> result) {
    output.into(result);
    return this;
  }

  @Override
  public Query<E> append(TableData<E> result) {
    output.append(result);
    return this;
  }

  @Override
  public Query<E> merge(TableData<E> result) {
    output.append(result);
    return this;
  }

  @Override
  public TableData<E> result() {
    return output.result();
  }

  @Override
  public Query<E> from(E entityDef) {
    input.from(entityDef);
    return this;
  }

  @Override
  public QueryInput<E> input() {
    return input;
  }

  @Override
  public Query<E> inputAs(QueryInput<E> input) {
    this.input = input;
    return this;
  }

  @Override
  public QueryOutput<E> output() {
    return output;
  }

  @Override
  public Query<E> outputAs(QueryOutput<E> output) {
    this.output = output;
    return this;
  }

  @Override
  public Query<E> lock() {
    return initLock(-1);
  }

  @Override
  public Query<E> tryLock() {
    return initLock(0);
  }

  @Override
  public Query<E> tryLock(long timeOut) {
    return initLock(timeOut);
  }

  private final Query<E> initLock(long timeout) {
    if (input != null) {
      input.lockRequest = new LockRequest(timeout);
    }
    return this;
  }

  @Override
  public Query<E> order(SortOrderProperty order) {
    input.sortOrders.add(order);
    return this;
  }

  @Override
  public Query<E> order(Property<?> property) {
    input.sortOrders.add(property.asc());
    return this;
  }

  @Override
  public TableData<E> listData() throws Exception {
    if (output().tableData == null) {
      into(new TableData<>(input().entityDef()));
    }
    execute();
    return output().tableData;
  }

  @Override
  public <B> List<B> list(Class<B> beanClass) throws Exception {
    TableData<E> tableData = listData();
    return tableData.asList(beanClass);
  }

}
