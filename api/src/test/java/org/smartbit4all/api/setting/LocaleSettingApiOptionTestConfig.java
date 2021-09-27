package org.smartbit4all.api.setting;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiConfig.class})
public class LocaleSettingApiOptionTestConfig {

  @Bean
  public MyModuleLocale myModuleLocale() {
    return new MyModuleLocale();
  }

}
