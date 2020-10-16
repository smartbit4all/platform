package org.smartbit4all.domain.service.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.LockRequest;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.SortOrderProperty;

public class QueryInput<E extends EntityDefinition> {
  /**
   * The name of the query if we would like to refer it from another expression.
   */
  protected String name;

  /**
   * The properties to query.
   */
  protected List<Property<?>> properties = new ArrayList<>();

  /**
   * The condition of the query.
   */
  protected Expression where;

  /**
   * The sort order of the query.
   */
  protected List<SortOrderProperty> sortOrders = new ArrayList<>(5);

  /**
   * The list of properties for the grouping.
   */
  protected List<Property<?>> groupByProperties = new ArrayList<>(2);

  /**
   * The lock request for the query statement. Implemented as "for update" or similar in the SQL
   * layer.
   */
  protected LockRequest lockRequest;

  /**
   * Unlimited query by default.
   */
  protected int limit = -1;

  /**
   * The {@link EntityDefinition} that is the root for the query. All the related entities must be
   * attached with {@link Reference} as we add {@link PropertyRef} properties to the query.
   */
  protected E entityDef;

  public void nameAs(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void select(List<Property<?>> properties) {
    this.properties.addAll(properties);
  }

  public void select(PropertySet propertySet) {
    this.properties.addAll(propertySet);
  }

  public void select(Property<?>... propertySet) {
    this.properties.addAll(Arrays.asList(propertySet));
  }

  public void select(Set<Property<?>> propertySet) {
    this.properties.addAll(propertySet);
  }

  public void where(Expression where) {
    this.where = where;
  }

  public void limit(int queryLimit) {
    this.limit = queryLimit;
  }

  public int limit() {
    return this.limit;
  }

  public void count() {
    properties.add(entityDef.count());
  }

  public void min(Property<?> property) {
    properties.add(property.min());
  }

  public void max(Property<?> property) {
    properties.add(property.max());
  }

  public void avg(Property<?> property) {
    properties.add(property.avg());
  }

  public void sum(Property<?> property) {
    properties.add(property.sum());
  }

  public void all() {
    properties.addAll(entityDef.allProperties());
  }

  public void groupBy(List<Property<?>> groupBy) {
    this.groupByProperties.addAll(groupBy);
  }

  public void groupBy(Property<?>... groupBys) {
    groupByProperties.addAll(Arrays.asList(groupBys));
  }

  public void from(E entityDef) {
    this.entityDef = entityDef;
  }

  public E entityDef() {
    return entityDef;
  }

  public List<Property<?>> properties() {
    return properties;
  }

  public Expression where() {
    return where;
  }

  public List<SortOrderProperty> orderBys() {
    return sortOrders;
  }

  public List<Property<?>> groupByProperties() {
    return groupByProperties;
  }

  public LockRequest lockRequest() {
    return lockRequest;
  }

}
