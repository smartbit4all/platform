package org.smartbit4all.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.meta.MetaConfiguration;
import org.smartbit4all.domain.security.AddressDef;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.security.UserAccountDef;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SB4MetaInitializeTest {

  protected static AnnotationConfigApplicationContext ctx;

  @BeforeAll
  static void setup() {
    ctx = new AnnotationConfigApplicationContext();
    ctx.register(MetaConfiguration.class);
    ctx.register(SecurityEntityConfiguration.class);
    ctx.refresh();
  }

  @AfterAll
  static void tearDown() {
    ctx.close();
  }

  @Test
  void metaCreationWithEntityDefInvocationHandler() {

    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);

    // PropertyOwned
    assertEquals(UserAccountDef.ID, userAccountDef.id().getName());
    assertEquals(Long.class, userAccountDef.id().type());

    // Reference
    assertEquals(UserAccountDef.PRIMARYADDRESS_REF, userAccountDef.primaryAddressRef().getName());

    // Referenced entity
    assertEquals(AddressDef.ENTITY_NAME, userAccountDef.primaryAddress().entityDefName());

    // PropertyRef - defined
    assertEquals(UserAccountDef.PRIMARYZIPCODE, userAccountDef.primaryZipcode().getName());

    // PropertyRef - on demand
    assertEquals(UserAccountDef.PRIMARYADDRESS_REF + "." + AddressDef.CITY,
        userAccountDef.primaryAddress().city().getName());

    // PropertyRef - defined equals on demand
    assertEquals(userAccountDef.primaryZipcode(), userAccountDef.primaryAddress().zipcode());

    // PropertyComupted
    assertEquals(UserAccountDef.FULLNAME, userAccountDef.fullname().getName());
    assertEquals(String.class, userAccountDef.fullname().type());

    assertEquals(9, userAccountDef.allProperties().size());

  }
}
