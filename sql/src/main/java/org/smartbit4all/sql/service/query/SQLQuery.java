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
package org.smartbit4all.sql.service.query;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.query.QueryImpl;
import org.smartbit4all.domain.service.query.QueryResult;
import org.smartbit4all.sql.SQLSelectStatement;
import org.springframework.jdbc.core.JdbcTemplate;

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
    SQLQueryExecution execution = new SQLQueryExecution(jdbcTemplate, this);
    execution.execute();
  }

}
