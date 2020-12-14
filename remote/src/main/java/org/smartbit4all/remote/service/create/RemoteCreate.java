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
package org.smartbit4all.remote.service.create;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.modify.CreateImpl;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the Query function. Sends the query input parameters for processing to a remote
 * server.
 * 
 * @author Zoltan Suller
 *
 */
public class RemoteCreate<E extends EntityDefinition> extends CreateImpl<E> {

  public static final String CREATE_SERVICE_NAME = "/create";

  protected String restUrl;

  protected RestTemplate restTemplate;

  public RemoteCreate(RestTemplate restTemplate, String restUrl, E entityDef) {
    this.restTemplate = restTemplate;
    this.restUrl = restUrl;
    this.entityDef = entityDef;
  }

  @Override
  public void execute() throws Exception {

    // TODO input and output for Create just like in Query
    // ResponseEntity<QueryOutput> userResponse = restTemplate.postForEntity(restUrl +
    // CREATE_SERVICE_NAME, input, QueryOutput.class);
    // into(userResponse.getBody().result());

  }

}
