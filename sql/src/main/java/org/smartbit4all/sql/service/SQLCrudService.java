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
package org.smartbit4all.sql.service;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.CrudService;
import org.smartbit4all.domain.service.modify.Create;
import org.smartbit4all.domain.service.modify.Delete;
import org.smartbit4all.domain.service.modify.Update;
import org.smartbit4all.domain.service.query.Query;
import org.smartbit4all.sql.service.modify.SQLCreate;
import org.smartbit4all.sql.service.modify.SQLDelete;
import org.smartbit4all.sql.service.modify.SQLUpdate;
import org.smartbit4all.sql.service.query.SQLQuery;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Return the implementation of the CRUD functions at SQL level.
 * 
 * @author Zoltan Suller
 */
public class SQLCrudService<E extends EntityDefinition> implements CrudService<E> {

  protected E entityDef;
  protected JdbcTemplate jdbcTemplate;
  protected String name;

  public SQLCrudService(JdbcTemplate jdbcTemplate, E entityDef, String name) {
    super();
    this.jdbcTemplate = jdbcTemplate;
    this.entityDef = entityDef;
    this.name = name;
  }

  @Override
  public Query<E> query() {
    SQLQuery<E> query = new SQLQuery<>(jdbcTemplate);
    return query.from(entityDef);
  }

  @Override
  public Create<E> create() {
    return new SQLCreate<>(jdbcTemplate, entityDef);
  }


  @Override
  public Update update() {
    return new SQLUpdate(jdbcTemplate, entityDef);
  }

  @Override
  public Delete delete() {
    return new SQLDelete(jdbcTemplate, entityDef);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public E entityDef() {
    return entityDef;
  }

}
