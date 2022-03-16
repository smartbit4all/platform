package org.smartbit4all.sql.util;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class SimpleDatabaseConnectionConfig {

  @Autowired
  private Environment env;

  @Bean
  public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
    dataSourceInitializer.setDataSource(dataSource);

    ResourceDatabasePopulator resourceDatabasePopulator = new EmptyDatabasePopulator();
    resourceDatabasePopulator
        .addScript(new ClassPathResource(env.getProperty("smartbit4all.sql.populator.script")));
    resourceDatabasePopulator.setContinueOnError(true);
    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);

    return dataSourceInitializer;
  }

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();

    dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
    dataSource.setUrl(env.getProperty("spring.datasource.url"));
    dataSource.setUsername(env.getProperty("spring.datasource.username"));
    dataSource.setPassword(env.getProperty("spring.datasource.password"));

    return dataSource;
  }

  @Bean
  public JdbcTemplate dataConnection(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }


}
