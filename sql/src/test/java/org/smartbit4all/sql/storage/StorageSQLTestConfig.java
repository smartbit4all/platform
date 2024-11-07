package org.smartbit4all.sql.storage;

import javax.sql.DataSource;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.sql.config.SQLConfig;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.smartbit4all.sql.config.SQLDBParameterBase;
import org.smartbit4all.sql.config.SQLDBParameterH2;
import org.smartbit4all.sql.config.SQLObjectStorageEntityConfiguration;
import org.smartbit4all.sql.service.identifier.SQLIdentifierService;
import org.smartbit4all.sql.util.EmptyDatabasePopulator;
import org.smartbit4all.storage.fs.StorageTestConfig;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
@Import({
    SQLConfig.class,
    SQLIdentifierService.class,
    SQLObjectStorageEntityConfiguration.class,
    StorageTestConfig.class
})
public class StorageSQLTestConfig {

  @Bean(Storage.STORAGETX)
  public StorageTransactionManagerFS transactionManager() {
    return new StorageTransactionManagerFS(null);
  }

  @Bean(name = SQLDBParameterBase.DEFAULT)
  public SQLDBParameter h2Parameter() {
    SQLDBParameter result = new SQLDBParameterH2();
    return result;
  }

  @Bean
  public DataSourceInitializer dataSourceInitializer(DataSource dataSourceH2) {

    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
    dataSourceInitializer.setDataSource(dataSourceH2);

    ResourceDatabasePopulator resourceDatabasePopulator = new EmptyDatabasePopulator();
    // Transfer table scripts:
    Resource[] schemaScriptResources =
        {(new ClassPathResource("script/objectstorage_oracle.sql"))};
    resourceDatabasePopulator.addScripts(schemaScriptResources);

    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);

    return dataSourceInitializer;
  }

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:mem:testdb;MODE=Oracle;DB_CLOSE_DELAY=-1");
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    return dataSource;
  }

  @Bean
  public JdbcTemplate applicationDataConnection(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public StorageSQL defaultStorage(ObjectDefinitionApi objectDefinitionApi) {
    return new StorageSQL(objectDefinitionApi);
  }
}
