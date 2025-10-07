package org.smartbit4all.sql.collection;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import javax.sql.DataSource;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.sample.bean.SampleCompany;
import org.smartbit4all.api.sample.bean.SampleDepartment;
import org.smartbit4all.api.sample.bean.SampleEmployee;
import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.core.object.ObjectReferenceConfigs;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterConfig;
import org.smartbit4all.sql.config.SQLConfig;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.smartbit4all.sql.config.SQLDBParameterBase;
import org.smartbit4all.sql.config.SQLDBParameterH2;
import org.smartbit4all.sql.service.identifier.SQLIdentifierService;
import org.smartbit4all.sql.testmodel.TestmodelEntityConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Import({PlatformApiConfig.class,
    SQLConfig.class,
    SQLIdentifierService.class,
    JDBCDataConverterConfig.class,
    TestmodelEntityConfig.class,
    TestFSConfig.class})
public class SearchIndexReferenceTestConfig {


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
  @Primary
  public DataSourceInitializer dataSourceInitializer1(DataSource datasource) {

    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
    dataSourceInitializer.setDataSource(datasource);

    ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
    resourceDatabasePopulator.addScripts(
        new ClassPathResource("script/search_index_schema.sql"));
    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);

    return dataSourceInitializer;
  }

  @Bean
  public SearchIndex<SampleEmployee> sampleEmployee() {
    SearchIndexImpl<SampleEmployee> index = new SearchIndexImpl<>(
        SearchIndexReferenceTest.SCHEMA,
        SearchIndexReferenceTest.SAMPLE_EMPLOYEE, SearchIndexReferenceTest.SCHEMA,
        SampleEmployee.class, false)
            .map(SampleEmployee.NAME, String.class, SampleEmployee.NAME)
            .map(SampleEmployee.ID, String.class, SampleEmployee.ID)
            .map(SampleEmployee.DEPARTMENT, URI.class, SampleEmployee.DEPARTMENT)
            .map(SampleEmployee.URI, SampleEmployee.URI);

    index.reference(SearchIndexReferenceTest.SAMPLE_DEPARTMENT_REF,
        SearchIndexReferenceTest.SCHEMA,
        SearchIndexReferenceTest.SAMPLE_DEPARTMENT,
        SampleEmployee.DEPARTMENT,
        SampleDepartment.URI);

    return index;
  }

  @Bean
  public SearchIndex<SampleDepartment> sampleDepartment() {
    SearchIndexImpl<SampleDepartment> index = new SearchIndexImpl<>(
        SearchIndexReferenceTest.SCHEMA,
        SearchIndexReferenceTest.SAMPLE_DEPARTMENT, SearchIndexReferenceTest.SCHEMA,
        SampleDepartment.class, false)
            .map(SampleDepartment.NAME, String.class, SampleDepartment.NAME)
            .map(SampleDepartment.ID, String.class, SampleDepartment.ID)
            .map(SampleDepartment.COMPANY, URI.class, SampleDepartment.COMPANY)
            .map(SampleDepartment.URI, SampleDepartment.URI)
            .mapCalculated(SearchIndexReferenceTest.COL_SUMMARY,
                Arrays.asList(SampleDepartment.NAME,
                    SearchIndexReferenceTest.DEPARTMENT_COMPANY_NAME),
                String.class,
                row -> {
                  String departmentName = (String) row.getValue(SampleCompany.NAME);
                  String companyName = (String) row
                      .getValue(SearchIndexReferenceTest.DEPARTMENT_COMPANY_NAME);
                  return MessageFormat.format(
                      "{0} department of the {1} company.",
                      departmentName, companyName);
                });
    index.reference(SearchIndexReferenceTest.SAMPLE_COMPANY_REF,
        SearchIndexReferenceTest.SCHEMA,
        SearchIndexReferenceTest.SAMPLE_COMPANY,
        SampleDepartment.COMPANY,
        SampleCompany.URI);
    return index;
  }

  @Bean
  public SearchIndex<SampleCompany> sampleCompany() {
    SearchIndexImpl<SampleCompany> index = new SearchIndexImpl<>(
        SearchIndexReferenceTest.SCHEMA,
        SearchIndexReferenceTest.SAMPLE_COMPANY, SearchIndexReferenceTest.SCHEMA,
        SampleCompany.class, false)
            .map(SampleCompany.NAME, String.class, SampleCompany.NAME)
            .map(SampleCompany.ID, String.class, SampleCompany.ID)
            .map(SampleCompany.URI, SampleCompany.URI);
    return index;
  }

  @Bean
  public SearchIndex<SampleEmployee> sampleEmployeeDb() {
    SearchIndexImpl<SampleEmployee> index = new SearchIndexImpl<>(
        SearchIndexReferenceTest.SCHEMA,
        SearchIndexReferenceTest.SAMPLE_EMPLOYEE_DB, SearchIndexReferenceTest.SCHEMA,
        SampleEmployee.class, true)
            .map(SampleEmployee.NAME, String.class, SampleEmployee.NAME)
            .map(SampleEmployee.ID, String.class, SampleEmployee.ID)
            .map(SampleEmployee.DEPARTMENT, URI.class, SampleEmployee.DEPARTMENT)
            .map(SampleEmployee.URI, SampleEmployee.URI);
    index.reference(SearchIndexReferenceTest.SAMPLE_DEPARTMENT_REF,
        SearchIndexReferenceTest.SCHEMA,
        SearchIndexReferenceTest.SAMPLE_DEPARTMENT_DB,
        SampleEmployee.DEPARTMENT,
        SampleDepartment.URI);

    return index;
  }

  @Bean
  public SearchIndex<SampleDepartment> sampleDepartmentDb() {
    SearchIndexImpl<SampleDepartment> index = new SearchIndexImpl<>(
        SearchIndexReferenceTest.SCHEMA,
        SearchIndexReferenceTest.SAMPLE_DEPARTMENT_DB, SearchIndexReferenceTest.SCHEMA,
        SampleDepartment.class, true)
            .map(SampleDepartment.NAME, String.class, SampleDepartment.NAME)
            .map(SampleDepartment.ID, String.class, SampleDepartment.ID)
            .map(SampleDepartment.COMPANY, URI.class, SampleDepartment.COMPANY)
            .map(SampleDepartment.URI, SampleDepartment.URI)
            .mapCalculated(SearchIndexReferenceTest.COL_SUMMARY,
                Arrays.asList(SampleDepartment.NAME,
                    SearchIndexReferenceTest.DEPARTMENT_COMPANY_NAME),
                String.class,
                row -> {
                  String departmentName = (String) row.getValue(SampleCompany.NAME);
                  String companyName = (String) row
                      .getValue(SearchIndexReferenceTest.DEPARTMENT_COMPANY_NAME);
                  return MessageFormat.format(
                      "{0} department of the {1} company.",
                      departmentName, companyName);
                });
    index.reference(SearchIndexReferenceTest.SAMPLE_COMPANY_REF,
        SearchIndexReferenceTest.SCHEMA,
        SearchIndexReferenceTest.SAMPLE_COMPANY_DB,
        SampleDepartment.COMPANY,
        SampleCompany.URI);
    return index;
  }

  @Bean
  public SearchIndex<SampleCompany> sampleCompanyDb() {
    SearchIndexImpl<SampleCompany> index = new SearchIndexImpl<>(
        SearchIndexReferenceTest.SCHEMA,
        SearchIndexReferenceTest.SAMPLE_COMPANY_DB, SearchIndexReferenceTest.SCHEMA,
        SampleCompany.class, true)
            .map(SampleCompany.NAME, String.class, SampleCompany.NAME)
            .map(SampleCompany.ID, String.class, SampleCompany.ID)
            .map(SampleCompany.URI, SampleCompany.URI);
    return index;
  }

  @Bean
  public ObjectReferenceConfigs refDefs() {
    return new ObjectReferenceConfigs()
        .ref(SampleEmployee.class,
            SampleEmployee.DEPARTMENT,
            SampleDepartment.class,
            ReferencePropertyKind.REFERENCE)
        .ref(SampleDepartment.class,
            SampleDepartment.COMPANY,
            SampleCompany.class,
            ReferencePropertyKind.REFERENCE);
  }

}
