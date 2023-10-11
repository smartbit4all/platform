package org.smartbit4all.sql.queryexecution;

import java.net.URI;
import javax.sql.DataSource;
import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterConfig;
import org.smartbit4all.domain.service.CrudApiImpl.QueryExecutorConfig;
import org.smartbit4all.sql.config.SQLConfig;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.smartbit4all.sql.config.SQLDBParameterBase;
import org.smartbit4all.sql.config.SQLDBParameterH2;
import org.smartbit4all.sql.service.identifier.SQLIdentifierService;
import org.smartbit4all.sql.service.query.SQLCrudExecutionApi;
import org.smartbit4all.sql.testmodel.TestmodelEntityConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Here we set up two H2 databases. Both databases use the exists_schema.sql and exists_data_01.sql
 * scripts, but we drop the ADDRESSES table from db #1 and the TICKET table from db #2. </br>
 * After, we configure two QueryExecutionApi-s to use each database. </br>
 * Finally, we create a {@link QueryExecutorConfig} where we set up the AddressDef EntityDefinition
 * to use the QueryExecutionApi #2 and the TicketDef to use the #1 so they connect to the fitting
 * databases (the ones that still contains the corresponding tables).
 *
 */
@Configuration
@Import({
    SQLConfig.class,
    SQLIdentifierService.class,
    JDBCDataConverterConfig.class,
    TestmodelEntityConfig.class,
    TestFSConfig.class
})
public class QueryExecutionTestConfig {

  @Bean(name = SQLDBParameterBase.DEFAULT)
  public SQLDBParameter h2Parameter() {
    SQLDBParameter result = new SQLDBParameterH2();
    return result;
  }

  @Bean()
  @Primary
  public DataSource dataSource1() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:mem:testdb1;MODE=Oracle;DB_CLOSE_DELAY=-1");
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    return dataSource;
  }

  @Bean()
  public DataSource dataSource2() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:mem:testdb2;MODE=Oracle;DB_CLOSE_DELAY=-1");
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    return dataSource;
  }


  @Bean
  @Primary
  public JdbcTemplate dataConnection1(@Qualifier("dataSource1") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public JdbcTemplate dataConnection2(@Qualifier("dataSource2") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  @Primary
  public DataSourceInitializer dataSourceInitializer1(
      @Qualifier("dataSource1") DataSource datasource) {

    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
    dataSourceInitializer.setDataSource(datasource);

    ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
    resourceDatabasePopulator.addScripts(
        new ClassPathResource("script/exists_schema.sql"),
        new ClassPathResource("script/exists_data_01.sql"),
        new ClassPathResource("script/exists_drop_address.sql"));
    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);

    return dataSourceInitializer;
  }

  private static boolean hasBean2ndDbInitialized = false;

  @Bean
  public DataSourceInitializer dataSourceInitializer2(
      @Qualifier("dataSource2") DataSource datasource) {

    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
    dataSourceInitializer.setDataSource(datasource);

    if (hasBean2ndDbInitialized) {
      return dataSourceInitializer;
    }
    hasBean2ndDbInitialized = true;

    ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
    resourceDatabasePopulator.addScripts(
        new ClassPathResource("script/exists_schema.sql"),
        new ClassPathResource("script/exists_data_01.sql"),
        new ClassPathResource("script/exists_drop_ticket.sql"));
    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);

    return dataSourceInitializer;
  }

  @Bean
  public SQLCrudExecutionApi queryExecutionApi1(
      @Qualifier("dataConnection1") JdbcTemplate jdbcTemplate, ObjectApi objectApi) {
    return new SQLCrudExecutionApi(jdbcTemplate, objectApi);
  }

  @Bean
  public SQLCrudExecutionApi queryExecutionApi2(
      @Qualifier("dataConnection2") JdbcTemplate jdbcTemplate, ObjectApi objectApi) {
    return new SQLCrudExecutionApi(jdbcTemplate, objectApi);
  }

  @Bean
  public QueryExecutorConfig executorConfig() {
    return QueryExecutorConfig.create()
        .addExecutionApiForEntityUri(
            URI.create("entity://org.smartbit4all.sql.testmodel/ticketDef"), "queryExecutionApi1")
        .addExecutionApiForEntityUri(
            URI.create("entity://org.smartbit4all.sql.testmodel/personDef"), "queryExecutionApi1")
        .addExecutionApiForEntityUri(
            URI.create("entity://org.smartbit4all.sql.testmodel/addressDef"), "queryExecutionApi2");
  }

}
