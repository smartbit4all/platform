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
