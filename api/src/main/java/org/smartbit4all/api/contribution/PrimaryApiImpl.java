package org.smartbit4all.api.contribution;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private List<A> apis;

  /**
   * The active registry of the api instances managed by the primary api.
   */
  protected Map<String, A> apiByName = new HashMap<>();

  private final Class<A> innerApiClass;

  public PrimaryApiImpl(Class<A> innerApiClass) {
    super();
    this.innerApiClass = innerApiClass;

  }

  @Override
  public A getContributionApi(String apiName) {
    return apiByName.get(apiName);
  }

  @Override
  public void registerApi(A api, String name) {
    addContributionApi(api, name);
  }

  @Override
  public void unregisterApi(String apiName) {
    removeContributionApi(apiName);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (apis != null) {
      for (A api : apis) {
        addContributionApi(api, api.getApiName());
      }
    }
  }

  /**
   * This function can be overridden to add more setup for the contribution.
   * 
   * @param api
   * @param apiName
   */
  protected void addContributionApi(A api, String apiName) {
    apiByName.put(apiName, api);
    if (api instanceof ContributionApiImpl) {
      ((ContributionApiImpl) api).setPrimaryApi(this);
    }
  }

  /**
   * This function can be overridden to add more setup for the contribution.
   * 
   * @param apiName
   */
  protected void removeContributionApi(String apiName) {
    apiByName.remove(apiName);
  }

  @Override
  public Class<A> getContributionApiClass() {
    return innerApiClass;
  }

  @Override
  public Map<String, A> getContributionApis() {
    return Collections.unmodifiableMap(apiByName);
  }

}
