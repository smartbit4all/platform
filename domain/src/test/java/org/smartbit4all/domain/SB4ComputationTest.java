package org.smartbit4all.domain;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.meta.MetaConfiguration;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.security.UserAccountDef;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SB4ComputationTest {

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
  void testTwoComputationScenario() {
    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);
    // ComputationFramework cf = new ComputationFrameworkImpl() {
    //
    // @Override
    // protected void setup(ComputationLogic<?> logic) {
    // if (logic instanceof UserAccountFullNameImpl) {
    // }
    // }
    //
    // };
    // cf.install(UserAccountFullName.class);
    // cf.install(UserTitle.class);
    // cf.start();
    // cf.fireChangeEvent(userAccountDef);
  }

}
