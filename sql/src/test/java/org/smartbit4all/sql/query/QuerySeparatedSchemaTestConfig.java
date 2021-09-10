package org.smartbit4all.sql.query;

import java.net.URI;
import javax.sql.DataSource;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterConfig;
import org.smartbit4all.domain.service.query.QueryApiImpl.QueryExecutorConfig;
import org.smartbit4all.sql.config.SQLConfig;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.smartbit4all.sql.config.SQLDBParameterBase;
import org.smartbit4all.sql.config.SQLDBParameterH2;
import org.smartbit4all.sql.service.identifier.SQLIdentifierService;
import org.smartbit4all.sql.service.query.SQLQueryExecutionApi;
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
    TestmodelEntityConfig.class
})
public class QuerySeparatedSchemaTestConfig {

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

  @Bean
  @Primary
  public JdbcTemplate dataConnection1(@Qualifier("dataSource1") DataSource dataSource) {
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
        new ClassPathResource("script/separatedschema_query_schema.sql"),
        new ClassPathResource("script/separatedschema_query_data.sql"));
    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);

    return dataSourceInitializer;
  }

  @Bean
  public SQLQueryExecutionApi queryExecutionApi1(
      @Qualifier("dataConnection1") JdbcTemplate jdbcTemplate) {
    
    SQLQueryExecutionApi sqlQueryExecutionApi = new SQLQueryExecutionApi(jdbcTemplate);
    sqlQueryExecutionApi.setSchema("test1");
    
    return sqlQueryExecutionApi;
  }

  @Bean
  public SQLQueryExecutionApi queryExecutionApi2(
      @Qualifier("dataConnection1") JdbcTemplate jdbcTemplate) {
    
    SQLQueryExecutionApi sqlQueryExecutionApi = new SQLQueryExecutionApi(jdbcTemplate);
    sqlQueryExecutionApi.setSchema("test2");
    
    return sqlQueryExecutionApi;
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
