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
import org.smartbit4all.domain.service.CrudService;
import org.smartbit4all.domain.service.modify.Create;
import org.smartbit4all.domain.service.modify.Delete;
import org.smartbit4all.domain.service.modify.Update;
import org.smartbit4all.domain.service.query.Query;
import org.smartbit4all.remote.service.create.RemoteCreate;
import org.smartbit4all.remote.service.query.RemoteQuery;
import org.smartbit4all.remote.service.update.RemoteUpdate;
import org.springframework.web.client.RestTemplate;

/**
 * Remote implementation of the CrudService
 * 
 * @author Zoltan Suller
 */
public class RemoteCrudService<E extends EntityDefinition> implements CrudService<E> {

  protected RestTemplate restTemplate;

  protected E entityDef;

  protected String name;

  protected String restUrl;

  public RemoteCrudService(RestTemplate restTemplate, E entityDef, String name, String restUrl) {
    super();
    this.entityDef = entityDef;
    this.restTemplate = restTemplate;
    this.name = name;
    this.restUrl = restUrl;
  }

  @Override
  public Query<E> query() {
    return new RemoteQuery<E>(restTemplate, restUrl).from(entityDef);
  }

  @Override
  public Create<E> create() {
    return new RemoteCreate<>(restTemplate, restUrl, entityDef);
  }

  @Override
  public Update<E> update() {
    return new RemoteUpdate<>(restTemplate, restUrl, entityDef);
  }

  @Override
  public Delete<E> delete() {
    // TODO RemoteDelete
    return null;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public E entityDef() {
    return entityDef;
  }

}
