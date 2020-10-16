package org.smartbit4all.remote.service.query;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.query.QueryImpl;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the Query function. Sends the query input parameters for processing to a remote
 * server.
 * 
 * @author Zoltan Suller
 *
 */
public class RemoteQuery<E extends EntityDefinition> extends QueryImpl<E> {

  public static final String QUERY_SERVICE_NAME = "/query";

  protected String restUrl;

  protected RestTemplate restTemplate;

  public RemoteQuery(RestTemplate restTemplate, String restUrl) {
    this.restTemplate = restTemplate;
    this.restUrl = restUrl;
  }

  @Override
  public void execute() throws Exception {

    ResponseEntity<QueryOutput> userResponse =
        restTemplate.postForEntity(restUrl + QUERY_SERVICE_NAME, input, QueryOutput.class);
    into(userResponse.getBody().result());

  }

}
