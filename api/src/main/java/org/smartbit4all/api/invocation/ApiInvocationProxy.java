package org.smartbit4all.api.invocation;

/**
 * The Proxy created from the API with the {@link ApiInvocationHandler} implements this interface.
 * So we can easily access the invocation handler or the original api if we need this.
 * 
 * <pre>
 * 
 * <code>
 *   if (api instanceof ApiInvocationProxy) {
 *     api = ((ApiInvocationProxy) api).getOriginalApi();
 *   }
 * </code>
 *
 * </pre>
 * 
 * @author Peter Boros
 */
public interface ApiInvocationProxy {

  Object getOriginalApi();

  ApiInvocationHandler<?, ?> getInvocationHandler();

}
