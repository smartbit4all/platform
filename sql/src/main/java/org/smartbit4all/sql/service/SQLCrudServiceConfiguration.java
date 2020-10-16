package org.smartbit4all.sql.service;

import org.smartbit4all.domain.meta.ServiceConfiguration;
import org.smartbit4all.domain.service.CrudServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SQLCrudServiceConfiguration extends ServiceConfiguration {

  @Bean(value = CrudServiceFactory.SERVICE_NAME)
  public SQLCrudServiceFactory crudServiceFactory(JdbcTemplate jdbcTemplate) {
    return new SQLCrudServiceFactory(jdbcTemplate);
  }

}
