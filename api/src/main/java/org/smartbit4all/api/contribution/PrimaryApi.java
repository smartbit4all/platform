package org.smartbit4all.api.contribution;

import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

/**
 * The primary API collects a kind Apis to serve as a switch selecting the appropriate Api for a
 * request. This interface is used by the {@link InvocationApi} to identify these Apis and to be
 * able to access the contributed Apis by name. In the {@link InvocationRequest} we can name a
 * primary api and we can add the name of the contributed api also.
 * 
 * @author Peter Boros
 */
public interface PrimaryApi<A extends ContributionApi> {

  /**
   * Find the registered api by name.
   * 
   * @param apiName
   * @return The api if it's available.
   */
  A findApiByName(String apiName);

}
