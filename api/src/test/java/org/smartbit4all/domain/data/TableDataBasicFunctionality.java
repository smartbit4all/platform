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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.security.Address;
import org.smartbit4all.domain.security.AddressDef;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.security.UserAccount;
import org.smartbit4all.domain.security.UserAccountDef;
import org.smartbit4all.domain.security.UserAccountImpl;
import org.smartbit4all.domain.security.UserAccountImpl2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

/**
 * Just for test purposes!!!
 *
 * @author Peter Boros
 *
 */
public class TableDataBasicFunctionality {

  protected static AnnotationConfigApplicationContext ctx;

  @BeforeAll
  static void setup() {
    ctx = new AnnotationConfigApplicationContext();
    ctx.register(PlatformApiConfig.class);
    ctx.register(SecurityEntityConfiguration.class);
    ctx.register(TestFSConfig.class);
    ctx.refresh();
  }

  @AfterAll
  static void tearDown() {
    ctx.close();
  }

  @Test
  void construction()
      throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {

    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
    AddressDef addressDef = ctx.getBean(AddressDef.class);

    TableData<UserAccountDef> tableData = new TableData<>(userAccountDef);
    Set<DataColumn<?>> primaryKeySet = tableData.addColumns(userAccountDef.PRIMARYKEYDEF());
    // Set<DataColumn<?>> mandatories = tableData.addColumns(userAccountDef.MANDATORIES());
    // Set<DataColumn<?>> security = tableData.addColumns(userAccountDef.SECURITY());
    DataColumn<String> name = tableData.addColumn(userAccountDef.name());
    DataColumn<String> zipRef = tableData.addColumn(userAccountDef.primaryAddress().zipcode());

    System.out.println(tableData.toString());
    System.out.println();
    assertEquals("id,name,primaryAddressId,primaryZipcode", tableData.toString());

    // Adding a new row to the data table.
    DataRow row = tableData.addRow();
    tableData.set(name, row, "Gipsz Jakab");

    Reference<UserAccountDef, AddressDef> primaryAddressRef = userAccountDef.primaryAddressRef();
    DataReference<UserAccountDef, AddressDef> addressRef =
        tableData.referenceTo(primaryAddressRef);

    TableData<AddressDef> addressTableData = addressRef.target();
    // RecordSet<UserAccountDef> addressDetails = addressTableData.referredBy(addressRef).rows();
    // RecordSet<AddressDef> addressRecordsByExp = addressTableData.filter(exp);
    DataColumn<Long> addressId = addressTableData.getColumn(addressDef.id());

    DataRow rowAddress = addressTableData.addRow();
    addressTableData.set(addressId, rowAddress, Long.valueOf(1));

    addressRef.set(row, rowAddress);
    // DataRow newAddress = addressRef.add(row);
    DataColumn<String> zip = addressTableData.getColumn(addressDef.zipcode());
    addressTableData.set(zip, rowAddress, "2030");

    System.out.println(tableData);
    System.out.println();
    assertEquals(
        "id,name,primaryAddressId,primaryZipcode" + StringConstant.NEW_LINE
            + "null,Gipsz Jakab,1,2030",
        tableData.toString());

    tableData.set(zipRef, row, "5000");

    System.out.println(tableData);
    System.out.println();
    assertEquals(
        "id,name,primaryAddressId,primaryZipcode" + StringConstant.NEW_LINE
            + "null,Gipsz Jakab,1,5000",
        tableData.toString());


    // addressDef.context().add(TableDataGateway.class, new TableDataGateway() {
    //
    // @Override
    // public Query query() {
    // return new QueryTableData<EntityDefinition>() { @Override
    // public TableData<EntityDefinition> result() {
    // // TODO Auto-generated method stub
    // return new DatT;
    // } };
    // }
    //
    // });
    //
    // TableDataGateway gateway = addressDef.context().get(TableDataGateway.class);
    // gateway.query().all().where().orderBy().asName("alma").save();
    // gateway.query().where(expression(...IN ("alma"));
    // gateway.query().where(expression(...NOT IN ("alma"));
    // gateway.query().where(expression(...IN ("alma"));

    // TableDataGateway<AddressDef> addressGW;
    //
    // Retriever r = addressGW.retriever().into(tableData);
    //
    // r.execute();
    //

    // UserAccountDef userAccountDef = context.get(UserAccountDef.class);
    // ExpressionBooleFormula expression =
    // userAccountDef.PRIMARYADDRESS_ID().in(addressGW.query().where(expression));
    // userGW.query().where(expression).execute();

    // tableData =
    // addressGW.retriever().into(tableData).query(Expression1).query(Expression2).execute().getTable();
    // tableData = addressGW.retriever().into(tableData).executeAsync().get().getTable();
    //
    // Retriever r2 = addressGW.get(Retriever.class);


    // context.get(TableDataGateway.class);
    // TableDataGateway<UserAccountDef> userAccountGW = TableDataGateway.get(UserAccountDef.class);
    //
    // List<Long> ids = userAccountGW.query(expression);
    //
    // service.doSomething(complexData);
    //
    // tableData = userAccountGW.retrieve(tableData, ids);
    // tableData = userAccountGW.query(tableData, expression);
    //
    // // Modify...
    //
    // userAccountGW.applyChanges(tableData);

  }

  @Test
  void testBuilderWithFlexibleProperties() {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);

    TableData<UserAccountDef> expected = TableDatas.of(userAccountDef);
    expected.addColumn(userAccountDef.firstname());
    expected.addColumn(userAccountDef.lastname());
    DataRow row1 = expected.addRow();
    row1.set(userAccountDef.firstname(), "Karoly");
    row1.set(userAccountDef.lastname(), "Nagy");
    DataRow row2 = expected.addRow();
    row2.set(userAccountDef.firstname(), "Robert");
    row2.set(userAccountDef.lastname(), "Kiss");


    TableData<UserAccountDef> result = TableDatas.builder(userAccountDef).addRow()
        .set(userAccountDef.firstname(), "Karoly").set(userAccountDef.lastname(), "Nagy").addRow()
        .set(userAccountDef.firstname(), "Robert").set(userAccountDef.lastname(), "Kiss").build();

    System.out.println(result);
    System.out.println();
    // assertEquals(expected.toString(), result.toString());
    System.out.println(expected);
  }

  @Test
  void testBuilderWithFixProperties() {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);

    TableData<UserAccountDef> expected = TableDatas.of(userAccountDef);
    expected.addColumn(userAccountDef.firstname());
    expected.addColumn(userAccountDef.lastname());
    DataRow row1 = expected.addRow();
    row1.set(userAccountDef.firstname(), "Karoly");
    row1.set(userAccountDef.lastname(), "Nagy");
    DataRow row2 = expected.addRow();
    row2.set(userAccountDef.firstname(), "Robert");
    row2.set(userAccountDef.lastname(), "Kiss");


    List<Property<?>> props = Arrays.asList(userAccountDef.firstname(), userAccountDef.lastname());

    TableData<UserAccountDef> result = TableDatas.builder(userAccountDef, props).addRow()
        .set(userAccountDef.firstname(), "Karoly").set(userAccountDef.lastname(), "Nagy").addRow()
        .set(userAccountDef.firstname(), "Robert").set(userAccountDef.lastname(), "Kiss").build();

    System.out.println(result);
    System.out.println();
    // assertEquals(expected.toString(), result.toString());
    System.out.println(expected);
  }

  @Test
  void testSetNotNullWithBuilderWithFlexibleProperties() {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);

    String firstRowValue = "Karoly";

    TableData<UserAccountDef> result1 = TableDatas.builder(userAccountDef)
        .addRow()
        .setNotNull(userAccountDef.firstname(), firstRowValue)
        .setNotNull(userAccountDef.lastname(), "Nagy")
        .addRow()
        .setNotNull(userAccountDef.firstname(), "Robert")
        .setNotNull(userAccountDef.lastname(), "Kiss")
        .build();

    Assert.notNull(result1.getColumn(userAccountDef.firstname()), "Must not be null");
    assertEquals(firstRowValue, result1.rows().get(0).get(userAccountDef.firstname()));

    String firstRowValueNullCase = null;
    TableData<UserAccountDef> result2 = TableDatas.builder(userAccountDef)
        .addRow()
        .setNotNull(userAccountDef.firstname(), firstRowValueNullCase)
        .setNotNull(userAccountDef.lastname(), "Nagy")
        .addRow()
        .setNotNull(userAccountDef.firstname(), firstRowValueNullCase)
        .setNotNull(userAccountDef.lastname(), "Kiss")
        .build();

    assertEquals(null, result2.getColumn(userAccountDef.firstname()));
  }

  @Test
  void testBuilderWithFixProperties_wrongProp() {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);

    List<Property<?>> props = Arrays.asList(userAccountDef.firstname(), userAccountDef.lastname());

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      TableDatas.builder(userAccountDef, props).addRow().set(userAccountDef.firstname(), "Karoly")
          .set(userAccountDef.lastname(), "Nagy").addRow().set(userAccountDef.firstname(), "Robert")
          .set(userAccountDef.lastname(), "Kiss")
          .set(userAccountDef.primaryAddressId(), Long.valueOf(2l)).build();
    });

    System.out.println(exception.getMessage());

  }

  @Test
  void testBuilderWithFixOrderedProperties() {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);

    TableData<UserAccountDef> expected = TableDatas.of(userAccountDef);
    expected.addColumn(userAccountDef.firstname());
    expected.addColumn(userAccountDef.lastname());
    DataRow row1 = expected.addRow();
    row1.set(userAccountDef.firstname(), "Karoly");
    row1.set(userAccountDef.lastname(), "Nagy");
    DataRow row2 = expected.addRow();
    row2.set(userAccountDef.firstname(), "Robert");
    row2.set(userAccountDef.lastname(), "Kiss");


    List<Property<?>> props = new ArrayList<>();
    props.add(userAccountDef.firstname());
    props.add(userAccountDef.lastname());


    TableData<UserAccountDef> result =
        TableDatas.builderWithOrderedProperties(userAccountDef, props).addRow()
            .setValues("Karoly", "Nagy").addRow().setValues("Robert", "Kiss").build();

    System.out.println(result);
    System.out.println();
    assertEquals(expected.toString(), result.toString());
  }

  @Test
  void testBuilderWithFixOrderedProperties_wrongProp() {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);

    List<Property<?>> props = new ArrayList<>();
    props.add(userAccountDef.firstname());
    props.add(userAccountDef.lastname());
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      TableDatas.builderWithOrderedProperties(userAccountDef, props).addRow()
          .setValues("Karoly", "Nagy").addRow().setValues("Robert", Long.valueOf(42l)).build();
    });

    System.out.println(exception.getMessage());

  }

  @Test
  void testBuilderWithFixOrderedProperties_withLessValues() {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);

    TableData<UserAccountDef> expected = TableDatas.of(userAccountDef);
    expected.addColumn(userAccountDef.firstname());
    expected.addColumn(userAccountDef.lastname());
    DataRow row1 = expected.addRow();
    row1.set(userAccountDef.firstname(), "Karoly");
    DataRow row2 = expected.addRow();
    row2.set(userAccountDef.firstname(), "Robert");


    List<Property<?>> props = new ArrayList<>();
    props.add(userAccountDef.firstname());
    props.add(userAccountDef.lastname());


    TableData<UserAccountDef> result =
        TableDatas.builderWithOrderedProperties(userAccountDef, props).addRow().setValues("Karoly")
            .addRow().setValues("Robert").build();

    System.out.println(result);
    System.out.println();
    System.out.println(expected);
    assertEquals(expected.toString(), result.toString());
  }

  @Test
  void testBeanIterationByRef() {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
    TableData<UserAccountDef> tableData =
        TableDatas.builder(userAccountDef).addRow()
            // .set(UserAccount::firstname)
            .set(userAccountDef.id(), Long.valueOf(1))
            .set(userAccountDef.name(), "Peter Boros")
            .set(userAccountDef.firstname(), "Peter")
            .set(userAccountDef.lastname(), "Boros")
            .set(userAccountDef.primaryAddressId(), Long.valueOf(2))
            .set(userAccountDef.titleCode(), "")
            .set(userAccountDef.title(), "")
            .set(userAccountDef.primaryZipcode(), "2030")
            .set(userAccountDef.fullname(), "Peter Boros")
            .build();
    Iterator<DataRow> rowsIter = tableData.rows().iterator();
    for (UserAccount userAccount : tableData.beansByRef(UserAccount.class)) {
      System.out.println("Name: " + userAccount.getName());
      userAccount.firstname("Tibor").lastname("Titkos");
      DataRow dataRow = rowsIter.next();
      assertEquals(dataRow.get(userAccountDef.firstname()), userAccount.firstname());
      assertEquals(dataRow.get(userAccountDef.lastname()), userAccount.lastname());
      System.out.println(tableData);
    }

  }

  @Test
  void testBeanIterationByVal() {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
    TableData<UserAccountDef> tableData =
        TableDatas.builder(userAccountDef).addRow()
            // .set(UserAccount::firstname)
            .set(userAccountDef.id(), Long.valueOf(1))
            .set(userAccountDef.name(), "Peter Boros")
            .set(userAccountDef.firstname(), "Peter")
            .set(userAccountDef.lastname(), "Boros")
            .set(userAccountDef.primaryAddressId(), Long.valueOf(2))
            .set(userAccountDef.titleCode(), "")
            .set(userAccountDef.title(), "")
            .set(userAccountDef.primaryZipcode(), "2030")
            .set(userAccountDef.fullname(), "Peter Boros")
            .build();
    Iterator<DataRow> rowsIter = tableData.rows().iterator();
    for (UserAccount userAccount : tableData.beansByVal(UserAccount.class)) {
      System.out.println("Name: " + userAccount.getName());
      userAccount.firstname("Tibor").lastname("Titkos");
      DataRow dataRow = rowsIter.next();
      assertEquals(dataRow.get(userAccountDef.firstname()).equals(userAccount.firstname()), false);
      assertEquals(dataRow.get(userAccountDef.lastname()).equals(userAccount.lastname()), false);
      System.out.println(tableData);
    }

  }

  @Test
  void testBeanGetter() throws Exception {
    {
      UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
      TableData<UserAccountDef> tableData =
          TableDatas.builder(userAccountDef).addRow()
              // .set(UserAccount::firstname)
              .set(userAccountDef.id(), Long.valueOf(1))
              .set(userAccountDef.name(), "Peter Boros")
              .set(userAccountDef.firstname(), "Peter")
              .set(userAccountDef.lastname(), "Boros")
              .set(userAccountDef.primaryAddressId(), Long.valueOf(2))
              .set(userAccountDef.titleCode(), "")
              .set(userAccountDef.title(), "")
              .set(userAccountDef.primaryZipcode(), "2030")
              .set(userAccountDef.fullname(), "Peter Boros")
              .build();
      for (DataRow row : tableData.rows()) {
        UserAccountImpl userAccountImpl = row.get(UserAccountImpl.class);
        System.out.println("Name: " + userAccountImpl.fullname());

        assertEquals(row.get(userAccountDef.fullname()).equals(userAccountImpl.fullname()),
            true);

        System.out.println(tableData);
      }

      for (DataRow row : tableData.rows()) {
        UserAccountImpl2 userAccountImpl = row.get(UserAccountImpl2.class);
        System.out.println("Name: " + userAccountImpl.fullname());

        assertEquals(row.get(userAccountDef.fullname()).equals(userAccountImpl.fullname()),
            true);

        System.out.println(tableData);
      }
    }

    {
      AddressDef addressDef = ctx.getBean(AddressDef.class);
      TableData<AddressDef> tableData =
          TableDatas.builder(addressDef).addRow()
              // .set(UserAccount::firstname)
              .set(addressDef.id(), Long.valueOf(1))
              .set(addressDef.zipcode(), "2030")
              .build();
      for (DataRow row : tableData.rows()) {
        Address address = row.get(Address.class);
        System.out.println("Zipcode: " + address.zipCode());

        assertEquals(row.get(addressDef.zipcode()).equals(address.zipCode().toString()),
            true);

        System.out.println(tableData);
      }

      List<Address> addressList = tableData.asList(Address.class);
      ListIterator<Address> addressIter = addressList.listIterator();
      for (DataRow row : tableData.rows()) {
        Address address = addressIter.next();
        assertEquals(row.get(addressDef.zipcode()).equals(address.zipCode().toString()),
            true);
      }

    }

  }

}
