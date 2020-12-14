/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.net.URI;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.MetaConfiguration;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.security.UserAccountDef;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.entity.EntityUris;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class EntityManagerTest {

  private static final String EXPECTED_USERACCDEF_URI = "entity://security/userAccountDef";
  private static final String EXPECTED_PROPERTY_URI = "entity://security/userAccountDef#firstname";
  protected static AnnotationConfigApplicationContext ctx;

  @BeforeAll
  static void setup() {
    ctx = new AnnotationConfigApplicationContext();
    ctx.register(MetaConfiguration.class);
    ctx.register(SecurityEntityConfiguration.class);
    ctx.register(EntityManagerTestConfig.class);
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
    
    URI userAccountUri = EntityUris.createEntityUri(EntityManagerTestConfig.ENTITY_SOUCE_SEC, "userAccountDef");
    assertEquals(EXPECTED_USERACCDEF_URI, userAccountUri.toString());
    
    EntityDefinition entityDefResult = entityManager.definition(userAccountUri);
    if(userAccountDef != entityDefResult) {
      fail();
    }
    
  }
  
  @Test
  void testEntityManagerPropertyGetter() {
    EntityManager entityManager = ctx.getBean(EntityManager.class);
    
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
    Property<String> expectedProperty = userAccountDef.firstname();
    
    URI propertyUri = EntityUris.createPropertyUri(EntityManagerTestConfig.ENTITY_SOUCE_SEC, "userAccountDef", "firstname");
    assertEquals(EXPECTED_PROPERTY_URI, propertyUri.toString());
    
    Property<?> actualProperty = entityManager.property(propertyUri);
    assertEquals(expectedProperty, actualProperty);
  }
  
}
