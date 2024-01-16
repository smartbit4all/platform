package org.smartbit4all.sql.util;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.ObjectUtils;

/**
 * Makes possible to add a custom database populator.<br/>
 * Usage: add a resource path to an .sql script in the {@value #SCRIPT_PROPERTY} property. <br/>
 * E.g.: in application.properties add: {@value #SCRIPT_PROPERTY}=scripts/init.sql where
 * src/main/resources/scripts/init.sql contains the database population script
 *
 */
@Configuration
public class SimpleDatabaseConnectionConfig {

  private static final String SCRIPT_PROPERTY = "smartbit4all.sql.populator.script";

  @Bean
  @ConditionalOnProperty(SCRIPT_PROPERTY)
  public DataSourceInitializer dataSourceInitializer(DataSource dataSource,
      @Value("${" + SCRIPT_PROPERTY + ":}") String scriptPath) throws Exception {

    if (ObjectUtils.isEmpty(scriptPath)) {
      throw new Exception("Missing property parameter: " + SCRIPT_PROPERTY);
    }

    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
    dataSourceInitializer.setDataSource(dataSource);

    ResourceDatabasePopulator resourceDatabasePopulator = new EmptyDatabasePopulator();
    resourceDatabasePopulator.addScript(new ClassPathResource(scriptPath));
    resourceDatabasePopulator.setContinueOnError(true);
    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);

    return dataSourceInitializer;
  }

}
