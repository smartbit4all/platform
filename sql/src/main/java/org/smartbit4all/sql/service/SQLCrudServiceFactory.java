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
import org.smartbit4all.domain.meta.ServiceConfiguration;
import org.smartbit4all.domain.service.CrudService;
import org.smartbit4all.domain.service.CrudServiceFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLCrudServiceFactory extends ServiceConfiguration implements CrudServiceFactory {

  private JdbcTemplate jdbcTemplate;

  public SQLCrudServiceFactory(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends EntityDefinition> CrudService<T> createCrudService(T entityDef, String name) {
    SQLCrudService<T> crudService = new SQLCrudService<>(jdbcTemplate, entityDef, name);
    return createCrudProxy(CrudService.class, crudService);
  }
}
