/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.data.filtering.ExpressionEvaluationPlan;
import org.smartbit4all.domain.data.index.StorageLoaderTableData;
import org.smartbit4all.domain.data.index.TableDataIndexSet;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.MetaConfiguration;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.security.AddressDef;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.security.UserAccountDef;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Expression Evaluaton test
 * 
 * @author Zoltán Suller
 *
 */
public class ExpressionEvaluationBasicFunctionality {

  protected static AnnotationConfigApplicationContext ctx;

  private static TableData<UserAccountDef> tableData;
  private static UserAccountDef userAccountDef;
  private static TableDataIndexSet indexset;
  private static DataRow row1;
  private static DataRow row2;
  private static DataRow row3;
  private static DataRow row4;
  private static DataRow row5;
  private static DataRow row6;
  private static DataRow row7;

  /**
   * Creating TableData:
   * 
   * <pre>
   * +--------------+------+--------------+--------------+--------------+----------------------+------------------------+--------------------------+
   * |    ACTION    |  ID  |  FIRSTNAME   |   LASTNAME   |  TITLE_CODE  |  PRIMARYADDRESS_ID   |  PRIMARYADDRESS.CITY   |  PRIMARYADDRESS.ZIPCODE  |
   * +--------------+------+--------------+--------------+--------------+----------------------+------------------------+--------------------------+
   * |  UNTOUCHED   |  1   |    Jakab     |    Gipsz     |     Dr.      |                      |        Budapest        |           1047           |
   * +--------------+------+--------------+--------------+--------------+----------------------+------------------------+--------------------------+
   * |  UNTOUCHED   |  2   |    Eszter    |    Varga     |     Dr.      |                      |        Budapest        |           1047           |
   * +--------------+------+--------------+--------------+--------------+----------------------+------------------------+--------------------------+
   * |  UNTOUCHED   |  3   |    Jakab     |  Keresztes   |     Mr.      |                      |        Budapest        |           1022           |
   * +--------------+------+--------------+--------------+--------------+----------------------+------------------------+--------------------------+
   * |  UNTOUCHED   |  4   |   Veronika   |    Varga     |     Mrs.     |                      |         Sopron         |           4400           |
   * +--------------+------+--------------+--------------+--------------+----------------------+------------------------+--------------------------+
   * |  UNTOUCHED   |  5   |    Péter     |    Gipsz     |     Dr.      |                      |      Nyíregyháza       |           3300           |
   * +--------------+------+--------------+--------------+--------------+----------------------+------------------------+--------------------------+
   * |  UNTOUCHED   |  6   |    Gyula     |    Gipsz     |              |                      |        Szolnok         |           2200           |
   * +--------------+------+--------------+--------------+--------------+----------------------+------------------------+--------------------------+
   * |  UNTOUCHED   |  7   |     Géza     |    Varga     |     Dr.      |                      |          Pécs          |           3456           |
   * +--------------+------+--------------+--------------+--------------+----------------------+------------------------+--------------------------+
   * 
   * </pre>
   */
  @BeforeAll
  private static void init() {
    ctx = new AnnotationConfigApplicationContext();
    ctx.register(MetaConfiguration.class);
    ctx.register(SecurityEntityConfiguration.class);
    ctx.refresh();

    userAccountDef = ctx.getBean(UserAccountDef.class);
    AddressDef addressDef = ctx.getBean(AddressDef.class);

    // Creating the TableData
    tableData = new TableData<>(userAccountDef);
    tableData.addColumns(userAccountDef.PRIMARYKEYDEF());
    DataColumn<Long> idColumn = tableData.getColumn(userAccountDef.id());
    DataColumn<String> firstNameColumn = tableData.addColumn(userAccountDef.firstname());
    DataColumn<String> lastNameColumn = tableData.addColumn(userAccountDef.lastname());
    DataColumn<String> titleColumn = tableData.addColumn(userAccountDef.titleCode());
    tableData.addColumn(userAccountDef.primaryAddress().city());
    tableData.addColumn(userAccountDef.primaryAddress().zipcode());

    DataReference<UserAccountDef, AddressDef> addressRef = tableData
        .referenceTo((Reference<UserAccountDef, AddressDef>) ((PropertyRef<String>) userAccountDef
            .primaryZipcode()).getJoinPath().last());
    TableData<AddressDef> addressTableData = addressRef.target();
    DataColumn<String> cityColumn = addressTableData.getColumn(addressDef.city());
    DataColumn<String> zipcodeColumn = addressTableData.getColumn(addressDef.zipcode());

    // Adding a new rows to the data table.
    row1 = tableData.addRow();
    tableData.set(idColumn, row1, 1l);
    tableData.set(firstNameColumn, row1, "Jakab");
    tableData.set(lastNameColumn, row1, "Gipsz");
    tableData.set(titleColumn, row1, "Dr.");
    DataRow rowAddress = addressTableData.addRow();
    addressTableData.set(cityColumn, rowAddress, "Budapest");
    addressTableData.set(zipcodeColumn, rowAddress, "1047");
    addressRef.set(row1, rowAddress);

    row2 = tableData.addRow();
    tableData.set(idColumn, row2, 2l);
    tableData.set(firstNameColumn, row2, "Eszter");
    tableData.set(lastNameColumn, row2, "Varga");
    tableData.set(titleColumn, row2, "Dr.");
    addressRef.set(row2, rowAddress);

    row3 = tableData.addRow();
    tableData.set(idColumn, row3, 3l);
    tableData.set(firstNameColumn, row3, "Jakab");
    tableData.set(lastNameColumn, row3, "Keresztes");
    tableData.set(titleColumn, row3, "Mr.");
    rowAddress = addressTableData.addRow();
    addressTableData.set(cityColumn, rowAddress, "Budapest");
    addressTableData.set(zipcodeColumn, rowAddress, "1022");
    addressRef.set(row3, rowAddress);

    row4 = tableData.addRow();
    tableData.set(idColumn, row4, 4l);
    tableData.set(firstNameColumn, row4, "Veronika");
    tableData.set(lastNameColumn, row4, "Varga");
    tableData.set(titleColumn, row4, "Mrs.");
    rowAddress = addressTableData.addRow();
    addressTableData.set(cityColumn, rowAddress, "Sopron");
    addressTableData.set(zipcodeColumn, rowAddress, "4400");
    addressRef.set(row4, rowAddress);

    row5 = tableData.addRow();
    tableData.set(idColumn, row5, 5l);
    tableData.set(firstNameColumn, row5, "Péter");
    tableData.set(lastNameColumn, row5, "Gipsz");
    tableData.set(titleColumn, row5, "Dr.");
    rowAddress = addressTableData.addRow();
    addressTableData.set(cityColumn, rowAddress, "Nyíregyháza");
    addressTableData.set(zipcodeColumn, rowAddress, "3300");
    addressRef.set(row5, rowAddress);

    row6 = tableData.addRow();
    tableData.set(idColumn, row6, 6l);
    tableData.set(firstNameColumn, row6, "Gyula");
    tableData.set(lastNameColumn, row6, "Gipsz");
    rowAddress = addressTableData.addRow();
    addressTableData.set(cityColumn, rowAddress, "Szolnok");
    addressTableData.set(zipcodeColumn, rowAddress, "2200");
    addressRef.set(row6, rowAddress);

    row7 = tableData.addRow();
    tableData.set(idColumn, row7, 7l);
    tableData.set(firstNameColumn, row7, "Géza");
    tableData.set(lastNameColumn, row7, "Varga");
    tableData.set(titleColumn, row7, "Dr.");
    rowAddress = addressTableData.addRow();
    addressTableData.set(cityColumn, rowAddress, "Pécs");
    addressTableData.set(zipcodeColumn, rowAddress, "3456");
    addressRef.set(row7, rowAddress);

    // Creating the IndexSet for the TableData
    indexset = new TableDataIndexSet(tableData);
    indexset.unique(idColumn);
    indexset.nonUnique(lastNameColumn);

    System.out.println(tableData.toString());
  }

  @AfterAll
  static void tearDown() {
    ctx.close();
  }

  /**
   * Expressions: A && TRUE; A && FALSE; A || TRUE ; A || FALSE
   */
  @Test
  void expressionBoolean() {
    // A && TRUE
    Expression expression =
        userAccountDef.primaryAddress().city().eq("Budapest").AND(Expression.TRUE());
    System.out.println("\nExpression: " + expression.toString());
    ExpressionEvaluationPlan plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    ExpressionEvaluationPlan planStorage = ExpressionEvaluationPlan
        .of(new StorageLoaderTableData(userAccountDef, tableData, userAccountDef.id()), expression);

    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row1, row2, row3)), new HashSet<>(filteredRows));

    // A && FALSE
    expression = userAccountDef.primaryAddress().city().eq("Budapest").AND(Expression.FALSE());
    System.out.println("\nExpression: " + expression.toString());
    plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(), new HashSet<>(filteredRows));

    // A || TRUE
    expression = userAccountDef.primaryAddress().city().eq("Győr").OR(Expression.TRUE());
    System.out.println("\nExpression: " + expression.toString());
    plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row1, row2, row3, row4, row5, row6, row7)),
        new HashSet<>(filteredRows));

    // A || FALSE
    expression = userAccountDef.primaryAddress().city().eq("Budapest").OR(Expression.FALSE());
    System.out.println("\nExpression: " + expression.toString());
    plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row1, row2, row3)), new HashSet<>(filteredRows));
  }

  /**
   * Expression:
   */
  @Test
  void expressionBetween() {
    // TODO Create properties to test the between expression
  }

  /**
   * Expression: A is null
   */
  @Test
  void expressionIsNull() {
    Expression expression = userAccountDef.titleCode().isNull();
    System.out.println("\nExpression: " + expression.toString());
    ExpressionEvaluationPlan plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row6)), new HashSet<>(filteredRows));
  }

  /**
   * Expression: A in (2, 3, 5)
   */
  @Test
  void expressionIn() {
    Expression expression = userAccountDef.id().in(Arrays.asList(2l, 3l, 5l));
    System.out.println("\nExpression: " + expression.toString());
    ExpressionEvaluationPlan plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row2, row3, row5)), new HashSet<>(filteredRows));
  }

  /**
   * Expression: A && B && C
   */
  @Test
  void expressionAndWithoutIndex() {
    Expression expression = userAccountDef.primaryAddress().city().eq("Budapest")
        .AND(userAccountDef.titleCode().eq("Dr."))
        .AND(userAccountDef.primaryAddress().zipcode().eq("1047"));
    System.out.println("\nExpression: " + expression.toString());
    ExpressionEvaluationPlan plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row1, row2)), new HashSet<>(filteredRows));
  }

  /**
   * Expression: A && B && C && D ; index on the property of A and B
   */
  @Test
  void expressionAndWithIndex() {
    Expression expression =
        userAccountDef.lastname().eq("Gipsz").AND(userAccountDef.id().gt(3l)).AND(userAccountDef
            .titleCode().eq("Dr.").AND(userAccountDef.primaryAddress().zipcode().like("%00%")));
    System.out.println("Expression: " + expression.toString());
    ExpressionEvaluationPlan plan =
        ExpressionEvaluationPlan.of(tableData, indexset, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row5)), new HashSet<>(filteredRows));
  }

  /**
   * Expression: A || B || C
   */
  @Test
  void expressionOrWithoutIndex() {
    Expression expression = userAccountDef.primaryAddress().city().eq("Budapest")
        .OR(userAccountDef.titleCode().eq("Dr."))
        .OR(userAccountDef.primaryAddress().city().eq("Szolnok"));
    System.out.println("\nExpression: " + expression.toString());
    ExpressionEvaluationPlan plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row1, row2, row3, row5, row6, row7)),
        new HashSet<>(filteredRows));
  }

  /**
   * Expression: A || B || C || D ; index on the property of A and B
   */
  @Test
  void expressionOrWithIndex() {
    Expression expression = userAccountDef.lastname().eq("Gipsz").OR(userAccountDef.id().eq(2l))
        .OR(userAccountDef.primaryAddress().city().eq("Szolnok"))
        .OR(userAccountDef.firstname().eq("Jakab"));
    System.out.println("\nExpression: " + expression.toString());
    ExpressionEvaluationPlan plan =
        ExpressionEvaluationPlan.of(tableData, indexset, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row1, row2, row3, row5, row6)),
        new HashSet<>(filteredRows));
  }

  /**
   * Expression: A || B && C
   */
  @Test
  void expressionOrAndWithoutIndex() {
    Expression expression = userAccountDef.primaryAddress().city().eq("Budapest")
        .OR(userAccountDef.titleCode().eq("Dr."))
        .AND(userAccountDef.primaryAddress().city().eq("Pécs"));
    System.out.println("\nExpression: " + expression.toString());
    ExpressionEvaluationPlan plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row1, row2, row3, row7)), new HashSet<>(filteredRows));
  }

  /**
   * Expression: A && B && C || D ; index on the property of A and B
   */
  @Test
  void expressionOrAndWithIndex() {
    Expression expression = userAccountDef.lastname().eq("Gipsz").AND(userAccountDef.id().gt(2l))
        .AND(userAccountDef.titleCode().eq("Dr."))
        .OR(userAccountDef.primaryAddress().city().eq("Pécs"));
    System.out.println("\nExpression: " + expression.toString());
    ExpressionEvaluationPlan plan =
        ExpressionEvaluationPlan.of(tableData, indexset, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row5, row7)), new HashSet<>(filteredRows));
  }

  /**
   * Expression: A && B && (C || D)
   */
  @Test
  void expressionWithBracket() {
    Expression expression = userAccountDef.lastname().eq("Gipsz").AND(userAccountDef.id().gt(2l))
        .AND(userAccountDef.primaryAddress().city().eq("Szolnok")
            .OR(userAccountDef.primaryAddress().city().eq("Nyíregyháza")).BRACKET());
    System.out.println("Expression: " + expression.toString());
    ExpressionEvaluationPlan plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row5, row6)), new HashSet<>(filteredRows));
  }

  /**
   * Expression: A || (B && C) && D
   */
  @Test
  void expressionWithBracket2() {
    Expression expression = userAccountDef.lastname().eq("Varga")
        .OR(userAccountDef.lastname().eq("Gipsz")
            .AND(userAccountDef.primaryAddress().zipcode().like("%00%")).BRACKET())
        .AND(userAccountDef.titleCode().eq("Dr."));
    System.out.println("Expression: " + expression.toString());
    ExpressionEvaluationPlan plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row2, row4, row5, row7)), new HashSet<>(filteredRows));
  }

  /**
   * Expression: A || (B || C || D) && D
   */
  @Test
  void expressionWithBracket3() {
    Expression expression = userAccountDef.lastname().eq("Varga")
        .OR(userAccountDef.lastname().eq("Gipsz")
            .OR(userAccountDef.lastname().eq("Perger").OR(userAccountDef.id().lt(3l))).BRACKET())
        .AND(userAccountDef.titleCode().eq("Dr."));
    System.out.println("Expression: " + expression.toString());
    ExpressionEvaluationPlan plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row1, row2, row4, row5, row7)),
        new HashSet<>(filteredRows));
  }

  /**
   * Expression: ((A || B || C) || D) && E
   */
  @Test
  void expressionWithBracket4() {
    Expression expression = userAccountDef.lastname().eq("Varga")
        .OR(userAccountDef.id().lt(3l).OR(userAccountDef.lastname().eq("Kulcsár"))).BRACKET()
        .OR(userAccountDef.lastname().eq("Gipsz")).BRACKET()
        .AND(userAccountDef.titleCode().eq("Dr."));
    System.out.println("Expression: " + expression.toString());
    ExpressionEvaluationPlan plan = ExpressionEvaluationPlan.of(tableData, null, expression);
    System.out.println(plan.toString());
    List<DataRow> filteredRows = plan.execute(tableData.rows());
    // System.out.println(tableData.toString(filteredRows));

    assertEquals(new HashSet<>(Arrays.asList(row1, row2, row5, row7)), new HashSet<>(filteredRows));
  }

}
