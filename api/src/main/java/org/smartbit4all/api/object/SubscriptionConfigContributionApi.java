package org.smartbit4all.api.object;

import java.util.List;
import org.smartbit4all.api.contribution.ContributionApi;

public interface SubscriptionConfigContributionApi extends ContributionApi {

  /**
   * A contribution can manage a list of configurations.
   * 
   * @return
   */
  List<String> getManagedConfigs();

  List<String> getRevokableConfigs();

}
