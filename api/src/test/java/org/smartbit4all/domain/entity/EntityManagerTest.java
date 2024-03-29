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
package org.smartbit4all.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.filtering.ExpressionEvaluationPlan;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinitionBuilder;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.security.UserAccountDef;
import org.smartbit4all.domain.service.CrudApi;
import org.smartbit4all.domain.service.dataset.TableDataApi;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.entity.EntityUris;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.smartbit4all.domain.utility.crud.Crud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = {EntityManagerTestConfig.class})
class EntityManagerTest {

  private static final String EXPECTED_USERACCDEF_URI =
      "entity://org.smartbit4all.domain.security/userAccountDef";
  private static final String EXPECTED_PROPERTY_URI =
      "entity://org.smartbit4all.domain.security/userAccountDef#firstname";

  private static final String EXPECTED_PROPERTY_ON_ASSOC_URI =
      "entity://org.smartbit4all.domain.security/userAccountDef#primaryAddressRef.zipcode";
  private static final String EXPECTED_PROPERTY_ON_ASSOC_URI2 =
      "entity://org.smartbit4all.domain.security/userAccountDef#primaryZipcode";

  private static final String EXPECTED_PROPERTY_CREATED_BY_URI =
      "entity://org.smartbit4all.domain.security/userAccountDef#primaryAddressRef.dontRefThisByUri";

  // protected static AnnotationConfigApplicationContext ctx;

  @BeforeAll
  static void setup() {
    // ctx = new AnnotationConfigApplicationContext();
    // ctx.register(MetaConfiguration.class);
    // ctx.register(SecurityEntityConfiguration.class);
    // // ctx.register(EntityManagerTestConfig.class);
    // ctx.register(EntityManagerImpl.class);
    // ctx.refresh();
  }

  @AfterAll
  static void tearDown() {
    // ctx.close();
  }

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private UserAccountDef userAccountDef;

  @Autowired
  private ApplicationContext ctx;

  @Test
  void testEntityManagerDefinitionGetter() {

    URI userAccountUri =
        EntityUris.createEntityUri(EntityManagerTestConfig.ENTITY_SOURCE_SEC, "userAccountDef");
    assertEquals(EXPECTED_USERACCDEF_URI, userAccountUri.toString());

    EntityDefinition entityDefResult = entityManager.definition(userAccountUri);
    if (userAccountDef != entityDefResult) {
      fail();
    }

  }

  @Test
  void testEntityManagerPropertyGetter() {
    Property<String> expectedProperty = userAccountDef.firstname();

    URI propertyUri = EntityUris.createPropertyUri(EntityManagerTestConfig.ENTITY_SOURCE_SEC,
        "userAccountDef", "firstname");
    assertEquals(EXPECTED_PROPERTY_URI, propertyUri.toString());

    Property<?> actualProperty = entityManager.property(propertyUri);
    assertEquals(expectedProperty.hashCode(), actualProperty.hashCode());
  }

  @Test
  void testEntityManagerReferencedPropertyGetter() {
    Property<String> expectedProperty = userAccountDef.primaryZipcode();

    URI propertyUri = EntityUris.createPropertyUri(EntityManagerTestConfig.ENTITY_SOURCE_SEC,
        "userAccountDef", "primaryAddressRef", "zipcode");
    assertEquals(EXPECTED_PROPERTY_ON_ASSOC_URI, propertyUri.toString());

    Property<?> actualProperty = entityManager.property(propertyUri);
    assertEquals(expectedProperty.hashCode(), actualProperty.hashCode());

    propertyUri = EntityUris.createPropertyUri(EntityManagerTestConfig.ENTITY_SOURCE_SEC,
        "userAccountDef", "primaryAddressRef", "city");
    assertEquals(userAccountDef.primaryAddress().city().getUri().toString(),
        propertyUri.toString());

    actualProperty = entityManager.property(propertyUri);
    assertEquals(userAccountDef.primaryAddress().city().hashCode(), actualProperty.hashCode());

    propertyUri = EntityUris.createPropertyUri(EntityManagerTestConfig.ENTITY_SOURCE_SEC,
        "userAccountDef", "primaryZipcode");
    assertEquals(EXPECTED_PROPERTY_ON_ASSOC_URI2, propertyUri.toString());

    actualProperty = entityManager.property(propertyUri);
    assertEquals(expectedProperty.hashCode(), actualProperty.hashCode());
  }

  // @Test
  // void testPropertyRefrenceCreationByUri() {
  //
  // String propertyName = "primaryAddressRef.dontRefThisByUri";
  // assertNull(userAccountDef.getProperty(propertyName),
  // propertyName + " should NOT be defined at this point!");
  //
  // URI propertyUri = EntityUris.createPropertyUri(EntityManagerTestConfig.ENTITY_SOURCE_SEC,
  // "userAccountDef", "primaryAddressRef", "dontRefThisByUri");
  // assertEquals(EXPECTED_PROPERTY_CREATED_BY_URI, propertyUri.toString());
  // String name = entityManager.property(propertyUri).getName();
  //
  // assertEquals(propertyName, name);
  // assertNotNull(userAccountDef.getProperty(propertyName),
  // propertyName + " should be defined at this point!");
  //
  // }

  @Test
  void testPropertyRefrenceCreationByRef() {
    String propertyName = "primaryAddressRef.dontRefThisByRef";

    Property<?> namePropertyByName = userAccountDef.getProperty(propertyName);
    assertNotNull(namePropertyByName,
        propertyName + " should NOT be defined at this point!");
    Property<String> namePropertyByFluidApi = userAccountDef.primaryAddress().dontRefThisByRef();
    assertSame(namePropertyByName, namePropertyByFluidApi);

    String name = namePropertyByFluidApi.getName();
    assertEquals(propertyName, name);

    Property<?> namePropertyByNameAfterFLuidApi = userAccountDef.getProperty(propertyName);
    assertNotNull(namePropertyByNameAfterFLuidApi,
        propertyName + " should be defined at this point!");
    assertSame(namePropertyByFluidApi, namePropertyByNameAfterFLuidApi);

  }

  /**
   *
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  void testGenericEntityDefinition() throws Exception {
    EntityDefinition myDef1 =
        EntityDefinitionBuilder.of(ctx).name("MyDef1").domain("org.smartbit4all.domain.security")
            .ownedProperty("prop1", String.class).build();
    entityManager.registerEntityDef(myDef1);
    Property<String> property = (Property<String>) myDef1.getProperty("prop1");


    TableData<EntityDefinition> tableData = TableDatas.builder(myDef1, myDef1.allProperties())
        .addRow().set(property, "apple")
        .addRow().set(property, "peach")
        .addRow().set(property, "pear").build();
    TableDataApi tableDataApi = ctx.getBean(TableDataApi.class);
    URI tdUri = tableDataApi.save(tableData);

    QueryInput queryInput = Crud.read(myDef1).selectAllProperties()
        .where(property.eq("apple")).fromTableData(tdUri).getQuery();
    ExpressionEvaluationPlan evaluationPlan =
        ExpressionEvaluationPlan.of(tableData, null, queryInput.where());

    List<DataRow> result = evaluationPlan.execute();

    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals("apple", result.get(0).get(property));

    System.out.println(queryInput);

    CrudApi queryApi = ctx.getBean(CrudApi.class);

    QueryOutput queryOutput = queryApi.executeQuery(queryInput);

    Assertions.assertEquals(1, queryOutput.getTableData().size());
    Assertions.assertEquals("apple", queryOutput.getTableData().rows().get(0).get(property));

    // String propertyName = "primaryAddressRef.dontRefThisByRef";
    // UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);Ezt én
    //
    // assertNull(userAccountDef.getProperty(propertyName),
    // propertyName + " should NOT be defined at this point!");
    // String name = userAccountDef.primaryAddress().dontRefThisByRef().getName();
    //
    // assertEquals(propertyName, name);
    // assertNotNull(userAccountDef.getProperty(propertyName),
    // propertyName + " should be defined at this point!");
  }

}
