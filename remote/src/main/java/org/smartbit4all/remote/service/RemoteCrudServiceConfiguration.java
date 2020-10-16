package org.smartbit4all.remote.service;

import org.smartbit4all.domain.meta.ServiceConfiguration;
import org.smartbit4all.domain.service.CrudServiceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RemoteCrudServiceConfiguration extends ServiceConfiguration {

  @Value("${remote.crud.rest.url}")
  private String restUrl;
  
  @Bean(value = CrudServiceFactory.SERVICE_NAME)
  public RemoteCrudServiceFactory crudServiceFactory(RestTemplate restTemplate) {
    return new RemoteCrudServiceFactory(restTemplate, restUrl);
  }

}
