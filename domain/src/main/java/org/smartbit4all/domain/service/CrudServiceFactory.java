package org.smartbit4all.domain.service;

import org.smartbit4all.domain.meta.EntityDefinition;

public interface CrudServiceFactory {

  static final String SERVICE_NAME = "crudServiceFactory";

  <T extends EntityDefinition> CrudService<T> createCrudService(T entityDef, String name);

}
