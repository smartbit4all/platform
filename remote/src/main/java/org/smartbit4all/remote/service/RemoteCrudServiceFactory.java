package org.smartbit4all.remote.service;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.ServiceConfiguration;
import org.smartbit4all.domain.service.CrudService;
import org.smartbit4all.domain.service.CrudServiceFactory;
import org.springframework.web.client.RestTemplate;

public class RemoteCrudServiceFactory extends ServiceConfiguration implements CrudServiceFactory {

  private String restUrl;
  private RestTemplate restTemplate;

  public RemoteCrudServiceFactory(RestTemplate restTemplate, String restUrl) {
    this.restTemplate = restTemplate;
    this.restUrl = restUrl;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends EntityDefinition> CrudService<T> createCrudService(T entityDef, String name) {
    RemoteCrudService<T> crudService = new RemoteCrudService<>(restTemplate, entityDef, name, restUrl);
    return createCrudProxy(CrudService.class, crudService);
  }
}
