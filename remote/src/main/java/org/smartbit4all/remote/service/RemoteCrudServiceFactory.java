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
