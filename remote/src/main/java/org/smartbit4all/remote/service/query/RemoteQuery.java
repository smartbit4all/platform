/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.remote.service.query;

import org.smartbit4all.domain.meta.EntityDefinition;

/**
 * Implementation of the Query function. Sends the query input parameters for processing to a remote
 * server.
 * 
 * @author Zoltan Suller
 *
 */
public class RemoteQuery<E extends EntityDefinition> { //extends QueryImpl<E> {

//  public static final String QUERY_SERVICE_NAME = "/query";
//
//  protected String restUrl;
//
//  protected RestTemplate restTemplate;
//
//  public RemoteQuery(RestTemplate restTemplate, String restUrl) {
//    this.restTemplate = restTemplate;
//    this.restUrl = restUrl;
//  }
//
//  @Override
//  public void execute() throws Exception {
//
//    ResponseEntity<QueryOutput> userResponse =
//        restTemplate.postForEntity(restUrl + QUERY_SERVICE_NAME, input, QueryOutput.class);
//    into(userResponse.getBody().result());
//
//  }
//
//  @Override
//  public QueryRequest<E> copy() {
//    // TODO It will be unnecessary
//    return null;
//  }
//
//  @Override
//  public <T extends EntityDefinition> QueryRequest<T> copyTranslated(T entityDef,
//      List<Reference<?, ?>> joinPath) {
//    // TODO It will be unnecessary
//    return null;
//  }

}
