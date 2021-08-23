package org.smartbit4all.api.contribution;

import java.lang.ref.WeakReference;

/**
 * The basic implementation of the {@link ContributionApi} managing the name and the reference for
 * the {@link PrimaryApi}.
 * 
 * @author Peter Boros
 *
 */
public class ContributionApiImpl implements ContributionApi {

  /**
   * The name of the api inside the {@link PrimaryApi}.
   */
  private String apiName;

  /**
   * The reference for the {@link PrimaryApi} managing this contribution.
   */
  WeakReference<PrimaryApi<?>> primaryApiRef;

  protected ContributionApiImpl(String apiName) {
    this.apiName = apiName;
  }

  @Override
  public String getApiName() {
    return apiName;
  }

  protected void setApiName(String apiName) {
    this.apiName = apiName;
  }

}
