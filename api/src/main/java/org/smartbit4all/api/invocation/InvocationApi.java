package org.smartbit4all.api.invocation;

import java.net.URI;
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

}
