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
package org.smartbit4all.domain.service.query;

import java.util.List;
import org.smartbit4all.core.SB4Function;
import org.smartbit4all.domain.data.DataRowValBeanInvocationHandler;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.smartbit4all.domain.service.transfer.TransferService;

/**
 * The query is service for collecting the data by an {@link Expression}. This functional object is
 * created for every run so it contains all the parameters for the execution. The queries can
 * collaborate with other {@link Query} functions. In this cases we must set a name for the given
 * query to be able to refer its result from the expression of the other query.
 * 
 * @author Peter Boros
 */
public interface Query<E extends EntityDefinition>
    extends SB4Function<QueryInput<E>, QueryOutput<E>> {

  /**
   * The name of the query if we would like to refer it from another expression.
   * 
   * @param name The name of query.
   * @return Fluid API
   */
  Query<E> nameAs(String name);

  /**
   * The name of the query. If we don't name it directly then it will have a generated name.
   * 
   * @return
   */
  String name();

  /**
   * Set the where for the query.
   * 
   * @param where
   * @return Fluid API
   */
  Query<E> where(Expression where);

  /**
   * We can set the properties to list as the result of the query. By default the result contains
   * only the identifiers.
   * 
   * @return Fluid API
   */
  Query<E> select(List<Property<?>> properties);

  /**
   * Add the properties to the query.
   * 
   * @param propertySet
   * @return
   */
  Query<E> select(PropertySet propertySet);

  /**
   * Add the properties to the query.
   * 
   * @param propertySet varargs to add properties in a convenient way.
   * @return
   */
  Query<E> select(Property<?>... propertySet);

  /**
   * Add the properties to the query.
   * 
   * @param propertySet varargs to add properties in a convenient way.
   * @return
   * @throws Exception
   */
  <B> Query<E> select(Class<B> beanClass) throws Exception;

  /**
   * We can add all columns to the query.
   * 
   * @return Fluid API
   */
  Query<E> all();

  /**
   * Defines a sort order for the query.
   * 
   * @param sortOrder
   * @return Fluid API
   */
  Query<E> order(SortOrderProperty order);

  /**
   * Defines a sort order for the query with ascending parameter.
   * 
   * @param sortOrder
   * @return Fluid API
   */
  Query<E> order(Property<?> property);

  /**
   * @param groupBy The list of properties to use for grouping by.
   * @return Fluid API
   */
  Query<E> groupBy(List<Property<?>> groupBy);

  /**
   * @param groupBy The list of properties to use for grouping by.
   * @return Fluid API
   */
  Query<E> groupBy(Property<?>... groupBys);

  /**
   * Set the limit of the results at the level of SQL. Preserve the JVM from the flood of byte in
   * case an SQL select with unsatisfying conditions.
   * 
   * @param queryLimit
   * @return
   */
  Query<E> limit(int queryLimit);

  /**
   * Requires the count of the result records.
   * 
   * @return Fluid API
   */
  Query<E> count();

  /**
   * Requires the minimum value of the given property.
   * 
   * @param property
   * @return Fluid API
   */
  Query<E> min(Property<?> property);

  /**
   * Requires the maximum value of the given property.
   * 
   * @param property
   * @return Fluid API
   */
  Query<E> max(Property<?> property);

  /**
   * Requires the average value of the given property.
   * 
   * @param property
   * @return Fluid API
   */
  Query<E> avg(Property<?> property);

  /**
   * Requires the summary of the given property.
   * 
   * @param property
   * @return Fluid API
   */
  Query<E> sum(Property<?> property);

  /**
   * Query result will appear in result parameter. All existing rows will be deleted.
   * 
   * @param property
   * @return Fluid API
   */
  Query<E> into(TableData<E> result);

  /**
   * Query result will be appended to the result parameter. Existing rows will NOT be deleted.
   * 
   * @param property
   * @return Fluid API
   */
  Query<E> append(TableData<E> result);

  /**
   * Query result will be merged into the result parameter, based on entity's primary keys. Existing
   * rows will NOT be deleted.
   * 
   * @param property
   * @return Fluid API
   */
  Query<E> merge(TableData<E> result);

  /**
   * Return the result of the Query.
   * 
   * @return
   */
  TableData<E> result();

  /**
   * The from sets the {@link EntityDefinition} that is the root for the query. All the related
   * entities must be attached with {@link Reference} as we add {@link PropertyRef} properties to
   * the query.
   * 
   * @param entityDef
   * @return
   */
  Query<E> from(E entityDef);

  /**
   * Set the input of the Query.
   * 
   * @param input
   * @return Fluid API
   */
  Query<E> inputAs(QueryInput<E> input);

  /**
   * Returns the input of the Query.
   * 
   * @return
   */
  @Override
  QueryInput<E> input();

  /**
   * Set the output of the Query.
   * 
   * @param output
   * @return Fluid API
   */
  Query<E> outputAs(QueryOutput<E> output);

  /**
   * Returns the output of the Query.
   * 
   * @return
   */
  @Override
  QueryOutput<E> output();

  /**
   * Initiate the lock on the query. It results a "FOR UPDATE" or similar statement at database
   * level. It waits until gets the lock or interrupted by someone.
   * 
   * @return
   */
  Query<E> lock();

  /**
   * Initiate the lock on the query. It results a "FOR UPDATE" or similar statement at database
   * level. If it cann't get the lock immediately then the query throws an exception.
   * 
   * @return
   */
  Query<E> tryLock();

  /**
   * Initiate the lock on the query. It results a "FOR UPDATE" or similar statement at database
   * level. If it cann't get the lock during the given timeout period then the query throws an
   * exception.
   * 
   * @param timeOut The timeout for the lock trial in millisecond.
   * @return
   */
  Query<E> tryLock(long timeOut);

  /**
   * Executes the query and returns the {@link TableData} as a result. The result will be available
   * later on in the {@link #output()} object but it's more convenient to call this if we would like
   * to execute and retrieve the result at the same time. Better for fluid API because the whole
   * query can be included in line.
   * 
   * @return Return the result as the typed table data of the given entity.
   * @throws Exception
   */
  TableData<E> listData() throws Exception;

  /**
   * Executes the query and returns the list of bean as a result. The result will be available later
   * on in the {@link #output()} object as {@link TableData} but it's more convenient to call this
   * if we would like to execute and retrieve the result at the same time. Better for fluid API
   * because the whole query can be included in line. The conversion to the bean is executed after
   * the query creates the inner {@link TableData} representation. So we can use the
   * {@link TableData} later on to retrieve other data formats from the same query result.
   * 
   * @param <B> The bean class that must have registered transfer binding in the
   *        {@link TransferService}.
   * @param beanClass The bean class that can be interface also but in this case the instances will
   *        be proxies with {@link DataRowValBeanInvocationHandler}. Inside the invocation handler
   *        it's a Map so be careful with the performance!
   * @return The list with the bean instances for every result row.
   * @throws Exception
   */
  <B> List<B> list(Class<B> beanClass) throws Exception;

}
