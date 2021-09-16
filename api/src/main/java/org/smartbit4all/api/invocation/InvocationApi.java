package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.UUID;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;

/**
 * The {@link InvocationApi} is a generic api to call other api function.
 * 
 * @author Peter Boros
 */
public interface InvocationApi {

  /**
   * The generic api call.
   * 
   * @param request
   * @throws Exception
   */
  InvocationParameter invoke(InvocationRequest request) throws Exception;

  /**
   * We can save an invocation template.
   * 
   * @param requestTemplateThe template.
   * @return Generates an URI for the saved request template.
   */
  URI save(InvocationRequestTemplate requestTemplate);

  /**
   * We can load the invocation template with the URI we have.
   * 
   * @param templateUri
   * @return The request template.
   */
  InvocationRequestTemplate load(URI templateUri);

  /**
   * This function register the given API instance into the execution registry of the
   * {@link InvocationApi}. Generates a unique identifier that can be used in the
   * {@link #invoke(InvocationRequest)}.
   * 
   * @param apiInstance The api object as an instance.
   * @return The UUID for the given instance.
   */
  UUID register(Object apiInstance);

  /**
   * Find the api instance registered by {@link #register(Object)}. If the instance is not available
   * any more or not registered then we get null.
   * 
   * @param instanceId
   * @return
   */
  Object find(UUID instanceId);

}
