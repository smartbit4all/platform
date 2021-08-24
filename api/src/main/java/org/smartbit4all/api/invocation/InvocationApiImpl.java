package org.smartbit4all.api.invocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The implementation of the {@link InvocationApi}. It collects all the
 * {@link InvocationExecutionApi} we have. If we call the {@link #invoke(InvocationRequest)} then
 * there is routing to the appropriate execution api. We always have a local execution api
 * 
 * @author Peter Boros
 */
public final class InvocationApiImpl implements InvocationApi, InitializingBean {

  /**
   * The list of {@link InvocationExecutionApi} from the Spring context.
   */
  @Autowired(required = false)
  List<InvocationExecutionApi> apis;

  Map<String, InvocationExecutionApi> apiByName = new HashMap<>();

  @Override
  public InvocationParameter invoke(InvocationRequest request) throws ClassNotFoundException {
    InvocationExecutionApi api = api(request.getExecutionApi());
    if (api != null) {
      return api.invoke(request);
    } else {
      throw new IllegalArgumentException(
          request.getExecutionApi() + " execution api is not available for the " + request);
    }
  }

  public InvocationExecutionApi api(String name) {
    return apiByName.get(name);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // The local implementation is always set plus the apis from the SpringContext.
    if (apis != null) {
      for (InvocationExecutionApi executionApi : apis) {
        apiByName.put(executionApi.getName(), executionApi);
      }
    }
  }

}
