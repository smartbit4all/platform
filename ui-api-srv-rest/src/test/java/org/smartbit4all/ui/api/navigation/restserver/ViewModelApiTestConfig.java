package org.smartbit4all.ui.api.navigation.restserver;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.object.ObservablePublisherWrapper;
import org.smartbit4all.ui.api.navigation.restserver.config.UIApiSrvRestConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({
    PlatformApiConfig.class,
    UIApiSrvRestConfig.class
})
public class ViewModelApiTestConfig {

  @Bean
  @ConditionalOnMissingBean(RestTemplate.class)
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate;
  }

  @Bean
  @Scope("prototype")
  TestViewModel testViewModel(ObservablePublisherWrapper publisherWrapper) {
    return new TestViewModelImpl(publisherWrapper);
  }

}
