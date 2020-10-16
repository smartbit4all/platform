package org.smartbit4all.remote.service.update;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.modify.UpdateImpl;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the Query function. Sends the query input parameters for processing to a remote
 * server.
 * 
 * @author Zoltan Suller
 *
 */
public class RemoteUpdate<E extends EntityDefinition> extends UpdateImpl<E> {

  public static final String UPDATE_SERVICE_NAME = "/update";

  protected String restUrl;

  protected RestTemplate restTemplate;

  public RemoteUpdate(RestTemplate restTemplate, String restUrl, E entityDef) {
    this.restTemplate = restTemplate;
    this.restUrl = restUrl;
    this.entityDef = entityDef;
  }

  @Override
  public void execute() throws Exception {

    // TODO input and output for Update just like in Query
    // ResponseEntity<QueryOutput> userResponse = restTemplate.postForEntity(restUrl +
    // CREATE_SERVICE_NAME, input, QueryOutput.class);
    // into(userResponse.getBody().result());

  }

}
