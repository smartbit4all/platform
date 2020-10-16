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
