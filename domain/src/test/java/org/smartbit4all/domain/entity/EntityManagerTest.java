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

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.core.object.MapBasedObject;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.filtering.ExpressionEvaluationPlan;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinitionBuilder;
import org.smartbit4all.domain.meta.MetaConfiguration;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.security.UserAccountDef;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.entity.EntityManagerImpl;
import org.smartbit4all.domain.service.entity.EntityUris;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.utility.crud.Crud;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class EntityManagerTest {

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

  protected static AnnotationConfigApplicationContext ctx;

  @BeforeAll
  static void setup() {
    ctx = new AnnotationConfigApplicationContext();
    ctx.register(MetaConfiguration.class);
    ctx.register(SecurityEntityConfiguration.class);
    // ctx.register(EntityManagerTestConfig.class);
    ctx.register(EntityManagerImpl.class);
    ctx.refresh();
  }

  @AfterAll
  static void tearDown() {
    ctx.close();
  }

  @Test
  void testEntityManagerDefinitionGetter() {
    EntityManager entityManager = ctx.getBean(EntityManager.class);

    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);

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
    EntityManager entityManager = ctx.getBean(EntityManager.class);

    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
    Property<String> expectedProperty = userAccountDef.firstname();

    URI propertyUri = EntityUris.createPropertyUri(EntityManagerTestConfig.ENTITY_SOURCE_SEC,
        "userAccountDef", "firstname");
    assertEquals(EXPECTED_PROPERTY_URI, propertyUri.toString());

    Property<?> actualProperty = entityManager.property(propertyUri);
    assertEquals(expectedProperty.hashCode(), actualProperty.hashCode());
  }

  @Test
  void testEntityManagerReferencedPropertyGetter() {
    EntityManager entityManager = ctx.getBean(EntityManager.class);

    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
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

  @Test
  void testPropertyRefrenceCreationByUri() {

    String propertyName = "primaryAddressRef.dontRefThisByUri";
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
    assertNull(userAccountDef.getProperty(propertyName),
        propertyName + " should NOT be defined at this point!");

    EntityManager entityManager = ctx.getBean(EntityManager.class);

    URI propertyUri = EntityUris.createPropertyUri(EntityManagerTestConfig.ENTITY_SOURCE_SEC,
        "userAccountDef", "primaryAddressRef", "dontRefThisByUri");
    assertEquals(EXPECTED_PROPERTY_CREATED_BY_URI, propertyUri.toString());
    String name = entityManager.property(propertyUri).getName();

    assertEquals(propertyName, name);
    assertNotNull(userAccountDef.getProperty(propertyName),
        propertyName + " should be defined at this point!");

  }

  @Test
  void testPropertyRefrenceCreationByRef() {
    String propertyName = "primaryAddressRef.dontRefThisByRef";
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);

    assertNull(userAccountDef.getProperty(propertyName),
        propertyName + " should NOT be defined at this point!");
    String name = userAccountDef.primaryAddress().dontRefThisByRef().getName();

    assertEquals(propertyName, name);
    assertNotNull(userAccountDef.getProperty(propertyName),
        propertyName + " should be defined at this point!");
  }

  /**
   * We know a data structure by having a {@link MapBasedObject} for example.
   */
  @SuppressWarnings("unchecked")
  @Test
  void testGenericEntityDefinition() {
    EntityDefinition myDef1 =
        EntityDefinitionBuilder.of(ctx).name("MyDef1").domain("org.smartbit4all.domain.security")
            .ownedProperty("prop1", String.class).entityDefinition();
    Property<String> property = (Property<String>) myDef1.getProperty("prop1");
    QueryInput queryInput = Crud.read(myDef1).selectAllProperties()
        .where(property.eq("apple")).getQuery();


    TableData<EntityDefinition> tableData = TableDatas.builder(myDef1, myDef1.allProperties())
        .addRow().set(property, "apple")
        .addRow().set(property, "peach")
        .addRow().set(property, "pear").build();

    ExpressionEvaluationPlan evaluationPlan =
        ExpressionEvaluationPlan.of(tableData, null, queryInput.where());

    List<DataRow> result = evaluationPlan.execute();

    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals("apple", result.get(0).get(property));

    System.out.println(queryInput);
    // String propertyName = "primaryAddressRef.dontRefThisByRef";
    // UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
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
