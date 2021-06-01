package org.smartbit4all.sql.storage;

import org.smartbit4all.domain.meta.EntityConfiguration;
import org.springframework.context.annotation.Bean;

public class TestSearchDefConfig extends EntityConfiguration {

  @Bean("test")
  public TestSearchDef userAccountDef() {
    TestSearchDef userAccDef = createEntityProxy(TestSearchDef.class);
    return userAccDef;
  }
  
}
