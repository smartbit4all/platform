package org.smartbit4all.api.contribution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.invocation.ApiInvocationHandler;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The {@link PrimaryApi} implementation is responsible for collection all the
 * {@link ContributionApi}s from the Spring context and register them. On the other hand it's
 * responsible for offering a remote registration api for the collaborating integration parties.
 * They will be registered as remote proxies in this api. The default implementations of the
 * {@link PrimaryApi}s will extend this class to implement the basic functionality.
 * 
 * @author Peter Boros
 *
 * @param <A>
 */
public class PrimaryApiImpl<A extends ContributionApi> implements PrimaryApi<A>, InitializingBean {

  @Autowired(required = false)
  List<A> apis;

  @Autowired
  InvocationApi invocationApi;

  /**
   * The active registry of the api instances managed by the primary api.
   */
  Map<String, A> apiByName = new HashMap<>();

  private final Class<?> primaryApiClass;

  private final Class<A> innerApiClass;

  public PrimaryApiImpl(Class<?> primaryApiClass, Class<A> innerApiClass) {
    super();
    this.primaryApiClass = primaryApiClass;
    this.innerApiClass = innerApiClass;
  }

  @Override
  public A findApiByName(String apiName) {
    return apiByName.get(apiName);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (apis != null) {
      for (A api : apis) {
        apiByName.put(api.getApiName(),
            ApiInvocationHandler.createProxyInner(primaryApiClass, this, innerApiClass, api,
                invocationApi, Invocations.LOCAL));
      }
    }
  }

}
