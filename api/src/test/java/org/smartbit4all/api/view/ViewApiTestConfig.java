package org.smartbit4all.api.view;

import org.smartbit4all.api.object.ObjectApiTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ObjectApiTestConfig.class})
public class ViewApiTestConfig {

  public static final String MENU1 = "MENU1";

  public static final String MENU2 = "MENU2";

  public static final String MENU3 = "MENU3";

  @Bean
  ActionSupplierApi actionSupplier1() {
    return new ActionSupplier1();
  }

}
