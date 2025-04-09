package org.smartbit4all.sql.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smartbit4all.core.utility.StringConstant.joinDot;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBoolOperator;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.sample.bean.SampleCompany;
import org.smartbit4all.api.sample.bean.SampleDepartment;
import org.smartbit4all.api.sample.bean.SampleEmployee;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    SearchIndexReferenceTestConfig.class
})
@TestInstance(Lifecycle.PER_CLASS)
public class SearchIndexReferenceTest {

  public static final String SCHEMA = "sample";
  public static final String SAMPLE_EMPLOYEE = "sampleEmployee";
  public static final String SAMPLE_EMPLOYEE_DB = "sampleEmployeeDb";
  public static final String SAMPLE_DEPARTMENT = "sampleDepartment";
  public static final String SAMPLE_DEPARTMENT_DB = "sampleDepartmentDb";
  public static final String SAMPLE_DEPARTMENT_REF = "department";
  public static final String SAMPLE_COMPANY = "sampleCompany";
  public static final String SAMPLE_COMPANY_DB = "sampleCompanyDb";
  public static final String SAMPLE_COMPANY_REF = "company";

  public static final String COL_SUMMARY = "summary";

  public static final String DEPARTMENT_COMPANY_NAME =
      joinDot(SampleDepartment.COMPANY, SampleCompany.NAME);

  public static final String EMPLOYEE_DEPARTMENT_ID =
      joinDot(SampleEmployee.DEPARTMENT, SampleDepartment.ID);

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  private List<URI> employees = new ArrayList<>();
  private List<URI> departments = new ArrayList<>();
  private List<URI> companies = new ArrayList<>();


  @BeforeAll
  void init() {
    // Company
    SampleCompany company1 =
        new SampleCompany().name("TeszCompany1").id("0");
    URI company1Uri = objectApi.saveAsNew(SCHEMA, company1);
    SampleCompany company2 =
        new SampleCompany().name("TeszCompany2").id("1");
    URI company2Uri = objectApi.saveAsNew(SCHEMA, company2);
    companies.add(company1Uri);
    companies.add(company2Uri);

    // Department
    SampleDepartment department1 =
        new SampleDepartment().name("TeszDepartment1").id("0").company(company1Uri);
    URI department1Uri = objectApi.saveAsNew(SCHEMA, department1);
    SampleDepartment department2 =
        new SampleDepartment().name("TeszDepartment2").id("1").company(company1Uri);
    URI department2Uri = objectApi.saveAsNew(SCHEMA, department2);
    SampleDepartment department3 =
        new SampleDepartment().name("TeszDepartment3").id("2").company(company2Uri);
    URI department3Uri = objectApi.saveAsNew(SCHEMA, department3);
    departments.add(department1Uri);
    departments.add(department2Uri);
    departments.add(department3Uri);

    SampleEmployee employee1 =
        new SampleEmployee().name("Teszt Jakab").id("0").department(department1Uri);
    URI employee1Uri = objectApi.saveAsNew(SCHEMA, employee1);
    SampleEmployee employee2 =
        new SampleEmployee().name("Teszt Géza").id("1").department(department1Uri);
    URI employee2Uri = objectApi.saveAsNew(SCHEMA, employee2);
    SampleEmployee employee3 =
        new SampleEmployee().name("Teszt Emese").id("2").department(department2Uri);
    URI employee3Uri = objectApi.saveAsNew(SCHEMA, employee3);
    SampleEmployee employee4 =
        new SampleEmployee().name("Teszt Helga").id("3").department(department3Uri);
    URI employee4Uri = objectApi.saveAsNew(SCHEMA, employee4);
    employees.add(employee1Uri);
    employees.add(employee2Uri);
    employees.add(employee3Uri);
    employees.add(employee4Uri);

    // In-memory
    SearchIndex<SampleCompany> companySearchIndex =
        collectionApi.searchIndex(SCHEMA, SearchIndexReferenceTest.SAMPLE_COMPANY,
            SampleCompany.class);
    companySearchIndex
        .updateIndex(companies);
    SearchIndex<SampleDepartment> departmentSearchIndex = collectionApi.searchIndex(SCHEMA,
        SearchIndexReferenceTest.SAMPLE_DEPARTMENT, SampleDepartment.class);
    departmentSearchIndex
        .updateIndex(departments);

    SearchIndex<SampleEmployee> employeeSearchIndex =
        collectionApi.searchIndex(SCHEMA, SearchIndexReferenceTest.SAMPLE_EMPLOYEE,
            SampleEmployee.class);
    employeeSearchIndex
        .updateIndex(employees);

    // DB
    SearchIndex<SampleCompany> companyDbSearchIndex =
        collectionApi.searchIndex(SCHEMA, SearchIndexReferenceTest.SAMPLE_COMPANY_DB,
            SampleCompany.class);
    companyDbSearchIndex
        .updateIndex(companies);
    SearchIndex<SampleDepartment> departmentDbSearchIndex = collectionApi.searchIndex(SCHEMA,
        SearchIndexReferenceTest.SAMPLE_DEPARTMENT_DB, SampleDepartment.class);
    departmentDbSearchIndex
        .updateIndex(departments);

    SearchIndex<SampleEmployee> employeeDbSearchIndex =
        collectionApi.searchIndex(SCHEMA, SearchIndexReferenceTest.SAMPLE_EMPLOYEE_DB,
            SampleEmployee.class);
    employeeDbSearchIndex
        .updateIndex(employees);
  }

  @Test
  void testExecuteSearchWithIndexReference() {
    executeSearchOnCompany(SearchIndexReferenceTest.SAMPLE_COMPANY);
    executeSearchOnEmployeeAndReferences(SearchIndexReferenceTest.SAMPLE_EMPLOYEE);

    executeSearchOnCompany(SearchIndexReferenceTest.SAMPLE_COMPANY_DB);
    executeSearchOnEmployeeAndReferences(SearchIndexReferenceTest.SAMPLE_EMPLOYEE_DB);
  }

  protected void executeSearchOnEmployeeAndReferences(String sampleEmployeeSi) {
    FilterExpressionList filter = new FilterExpressionList()
        .addExpressionsItem(
            new FilterExpressionData()
                .currentOperation(FilterExpressionOperation.EQUAL)
                .operand1(new FilterExpressionOperandData()
                    .isDataName(true)
                    .valueAsString(SampleEmployee.ID))
                .operand2(new FilterExpressionOperandData()
                    .isDataName(false)
                    .valueAsString("1"))
                .boolOperator(FilterExpressionBoolOperator.OR));

    SearchIndex<SampleEmployee> employeeSearchIndex =
        collectionApi.searchIndex(SCHEMA, sampleEmployeeSi,
            SampleEmployee.class);
    EntityDefinition employeeEntityDef = employeeSearchIndex.getDefinition()
        .getDefinition();

    String departmentIdColumn = EMPLOYEE_DEPARTMENT_ID;
    String companyNameColumn =
        joinDot(SampleEmployee.DEPARTMENT, SampleDepartment.COMPANY, SampleCompany.NAME);
    String companyUri = joinDot(SampleEmployee.DEPARTMENT, SampleDepartment.COMPANY);
    String departmentSummaryColumn = joinDot(SampleEmployee.DEPARTMENT, COL_SUMMARY);
    Property<String> departmentIdProperty =
        (Property<String>) employeeEntityDef.getProperty(departmentIdColumn);
    Property<String> companyNameColumnProperty =
        (Property<String>) employeeEntityDef.getProperty(companyNameColumn);
    Property<String> departmentSummaryProperty =
        (Property<String>) employeeEntityDef.getProperty(departmentSummaryColumn);
    // In memory search
    TableData<?> result =
        employeeSearchIndex.executeSearch(filter,
            null,
            Arrays.asList(SampleEmployee.NAME, departmentIdColumn, companyNameColumn,
                departmentSummaryColumn, companyUri));

    Assertions.assertEquals(1, result.size());

    DataRow row = result.rows().get(0);
    String departmentId = row.get(departmentIdProperty);
    String companyName = row.get(companyNameColumnProperty);
    String departmentSummary = row.get(departmentSummaryProperty);

    Assertions.assertEquals("0", departmentId);
    Assertions.assertEquals("TeszCompany1", companyName);
    Assertions.assertEquals("TeszDepartment1 department of the TeszCompany1 company.",
        departmentSummary);

    // result columns size should equal executeSearch.fields size
    Assertions.assertEquals(5, result.columns().size());
    // calculated property's source property won't be in search result
    assertThat(Arrays.asList(SampleEmployee.NAME, departmentIdColumn, companyNameColumn,
        departmentSummaryColumn, companyUri))
            .hasSameElementsAs(result.columns().stream()
                .map(DataColumn::getName)
                .collect(Collectors.toList()));
  }

  protected void executeSearchOnCompany(String sampleCompanySi) {
    FilterExpressionList filter = new FilterExpressionList()
        .addExpressionsItem(
            new FilterExpressionData()
                .currentOperation(FilterExpressionOperation.EQUAL)
                .operand1(new FilterExpressionOperandData()
                    .isDataName(true)
                    .valueAsString(SampleCompany.NAME))
                .operand2(new FilterExpressionOperandData()
                    .isDataName(false)
                    .valueAsString("TeszCompany2"))
                .boolOperator(FilterExpressionBoolOperator.OR));

    SearchIndex<SampleCompany> companySearchIndex =
        collectionApi.searchIndex(SCHEMA, sampleCompanySi,
            SampleCompany.class);
    Property<String> companyIdProperty =
        (Property<String>) companySearchIndex.getDefinition()
            .getDefinition().getProperty(SampleCompany.ID);
    TableData<?> result =
        companySearchIndex.executeSearch(filter, null,
            Arrays.asList(SampleCompany.NAME, SampleCompany.ID));

    Assertions.assertEquals(1, result.size());

    DataRow row = result.rows().get(0);
    String id = row.get(companyIdProperty);
    Assertions.assertEquals("1", id);

    Assertions.assertEquals(2, result.columns().size());
    assertThat(Arrays.asList(SampleCompany.NAME, SampleCompany.ID))
        .hasSameElementsAs(result.columns().stream()
            .map(DataColumn::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void testTableDataOfUriWithIndexReference() {
    tableDataOfEmployeeUris(SAMPLE_EMPLOYEE);
    tableDataOfEmployeeUris(SAMPLE_EMPLOYEE_DB);
    tableDataOfDepartmentUris(SAMPLE_DEPARTMENT);
    tableDataOfDepartmentUris(SAMPLE_DEPARTMENT_DB);
  }

  @Test
  void testTableDataOfObjectWithIndexReference() {
    tableDataOfEmployeeObject(SAMPLE_EMPLOYEE);
    tableDataOfEmployeeObject(SAMPLE_EMPLOYEE_DB);
    tableDataOfDepartmentObjects(SAMPLE_DEPARTMENT);
    tableDataOfDepartmentObjects(SAMPLE_DEPARTMENT_DB);
  }

  protected void tableDataOfEmployeeUris(String employeeSi) {
    SearchIndex<SampleEmployee> employeeSearchIndex =
        collectionApi.searchIndex(SCHEMA, employeeSi,
            SampleEmployee.class);

    TableData<?> employeeTableData = employeeSearchIndex.tableDataOfUris(employees.stream());

    Assertions.assertEquals(4, employeeTableData.size());
    Assertions.assertEquals(4, employeeTableData.columns().size());
    assertThat(Arrays.asList(SampleEmployee.ID, SampleEmployee.NAME, SampleEmployee.URI,
        SampleEmployee.DEPARTMENT))
            .hasSameElementsAs(employeeTableData.columns().stream()
                .map(DataColumn::getName)
                .collect(Collectors.toList()));
  }

  protected void tableDataOfDepartmentUris(String departmentSi) {
    SearchIndex<SampleDepartment> departmentSearchIndex =
        collectionApi.searchIndex(SCHEMA, departmentSi,
            SampleDepartment.class);

    TableData<?> departmentTableData = departmentSearchIndex.tableDataOfUris(departments.stream());

    Assertions.assertEquals(3, departmentTableData.size());
    Assertions.assertEquals(6, departmentTableData.columns().size());
    assertThat(Arrays.asList(SampleDepartment.ID, SampleDepartment.NAME, SampleDepartment.URI,
        SampleDepartment.COMPANY, COL_SUMMARY,
        joinDot(SampleDepartment.COMPANY, SampleCompany.NAME)))
            .hasSameElementsAs(departmentTableData.columns().stream()
                .map(DataColumn::getName)
                .collect(Collectors.toList()));
  }

  protected void tableDataOfEmployeeObject(String employeeSi) {
    SearchIndex<SampleEmployee> employeeSearchIndex =
        collectionApi.searchIndex(SCHEMA, employeeSi,
            SampleEmployee.class);

    Stream<SampleEmployee> departmentObjects = employees.stream()
        .map(objectApi::load)
        .map(node -> node.getObject(SampleEmployee.class));

    TableData<?> employeeTableData = employeeSearchIndex.tableDataOfObjects(departmentObjects);

    Assertions.assertEquals(4, employeeTableData.size());
    Assertions.assertEquals(4, employeeTableData.columns().size());
    assertThat(Arrays.asList(SampleEmployee.ID, SampleEmployee.NAME, SampleEmployee.URI,
        SampleEmployee.DEPARTMENT))
            .hasSameElementsAs(employeeTableData.columns().stream()
                .map(DataColumn::getName)
                .collect(Collectors.toList()));
  }

  protected void tableDataOfDepartmentObjects(String departmentSi) {
    SearchIndex<SampleDepartment> departmentSearchIndex =
        collectionApi.searchIndex(SCHEMA, departmentSi,
            SampleDepartment.class);
    Stream<SampleDepartment> departmentObjects = departments.stream()
        .map(objectApi::load)
        .map(node -> node.getObject(SampleDepartment.class));

    TableData<?> departmentTableData = departmentSearchIndex.tableDataOfObjects(departmentObjects);

    Assertions.assertEquals(3, departmentTableData.size());
    Assertions.assertEquals(6, departmentTableData.columns().size());
    assertThat(Arrays.asList(SampleDepartment.ID, SampleDepartment.NAME, SampleDepartment.URI,
        SampleDepartment.COMPANY, COL_SUMMARY,
        joinDot(SampleDepartment.COMPANY, SampleCompany.NAME)))
            .hasSameElementsAs(departmentTableData.columns().stream()
                .map(DataColumn::getName)
                .collect(Collectors.toList()));
  }
}
