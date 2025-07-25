package org.smartbit4all.sql.storage;

import javax.sql.DataSource;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.service.identifier.IdentifierService;
import org.smartbit4all.sql.config.DynamicDatasourceSQLConfig;
import org.smartbit4all.sql.config.SQLConfig;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.smartbit4all.sql.config.SQLDBParameterBase;
import org.smartbit4all.sql.config.SQLDBParameterH2;
import org.smartbit4all.sql.config.SQLObjectStorageEntityConfiguration;
import org.smartbit4all.sql.service.identifier.SQLIdentifierServiceH2;
import org.smartbit4all.sql.util.EmptyDatabasePopulator;
import org.smartbit4all.storage.fs.StorageTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({
    DynamicDatasourceSQLConfig.class,
    StorageTestConfig.class,
    SQLConfig.class,
    SQLObjectStorageEntityConfiguration.class
})
@EnableTransactionManagement
public class StorageSQLConnectionPoolTestConfig {

  @Bean
  public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new JdbcTransactionManager(dataSource);
  }

  @Bean(name = SQLDBParameterBase.DEFAULT)
  public SQLDBParameter h2Parameter() {
    SQLDBParameter result = new SQLDBParameterH2();
    return result;
  }

  @Bean
  public IdentifierService identifierService(JdbcTemplate jdbcTemplate) {
    SQLIdentifierServiceH2 result = new SQLIdentifierServiceH2(jdbcTemplate);
    return result;
  }

  @Bean
  public DataSourceInitializer dataSourceInitializer(DataSource dataSourceH2) {

    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
    dataSourceInitializer.setDataSource(dataSourceH2);

    ResourceDatabasePopulator resourceDatabasePopulator = new EmptyDatabasePopulator();
    // Transfer table scripts:
    Resource[] schemaScriptResources =
        {(new ClassPathResource("script/objectstorage_oracle.sql")),
            (new ClassPathResource("script/sb4tables_oracle.sql"))};
    resourceDatabasePopulator.addScripts(schemaScriptResources);

    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);

    return dataSourceInitializer;
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
