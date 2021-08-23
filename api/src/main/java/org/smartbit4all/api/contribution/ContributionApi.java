package org.smartbit4all.api.contribution;

/**
 * The contribution api is a marker interface for the api implementations contributed into a
 * {@link PrimaryApi}. The have a unique identified inside the {@link PrimaryApi} registry to help
 * routing.
 * 
 * @author Peter Boros
 */
public interface ContributionApi {

  /**
   * The api name must be unique inside a {@link PrimaryApi} to ensure the routing.
   * 
   * @return
   */
  String getApiName();

}
