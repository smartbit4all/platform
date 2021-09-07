package org.smartbit4all.api.invocation;

/**
 * The {@link InvocationApi} is a generic api to call other api function.
 * 
 * TODO Separate The InvocationApi classes and the Api contribution mechanism classes. -->
 * contribution package
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

}
