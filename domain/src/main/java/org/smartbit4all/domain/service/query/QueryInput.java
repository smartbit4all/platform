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
package org.smartbit4all.domain.service.query;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.LockRequest;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.smartbit4all.domain.service.dataset.TableDataApi;
import org.springframework.util.StringUtils;

public class QueryInput {
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
  private LockRequest lockRequest;

  /**
   * Unlimited query by default.
   */
  protected int limit = -1;

  /**
   * The global distinct flag for the query. The default is false because in case of the normal
   * database design there is no need to add distinct.
   */
  protected boolean distinct = false;

  /**
   * The {@link EntityDefinition} that is the root for the query. All the related entities must be
   * attached with {@link Reference} as we add {@link PropertyRef} properties to the query.
   */
  protected EntityDefinition entityDef;

  /**
   * The result is stored in memory or is serialized based on thid flag.
   */
  private boolean isResultSerialized;

  /**
   * If the query is running on a table data managed by the {@link TableDataApi}.
   */
  private URI tableDataUri;

  public QueryInput() {
    this(UUID.randomUUID().toString());
  }

  public QueryInput(String name) {
    if (StringUtils.isEmpty(name)) {
      throw new IllegalArgumentException("The name parameter can not be null!");
    }
    this.name = name;
  }

  public String getName() {
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

  public void from(EntityDefinition entityDef) {
    this.entityDef = entityDef;
  }

  public EntityDefinition entityDef() {
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
    return getLockRequest();
  }

  public final boolean isDistinct() {
    return distinct;
  }

  public final void setDistinct(boolean distinct) {
    this.distinct = distinct;
  }

  public LockRequest getLockRequest() {
    return lockRequest;
  }

  public void setLockRequest(LockRequest lockRequest) {
    this.lockRequest = lockRequest;
  }

  public boolean isResultSerialized() {
    return isResultSerialized;
  }

  public void setResultSerialized(boolean isResultSerialized) {
    this.isResultSerialized = isResultSerialized;
  }

  protected final URI getTableDataUri() {
    return tableDataUri;
  }

  protected final void setTableDataUri(URI tableDataUri) {
    this.tableDataUri = tableDataUri;
  }

}
