package org.smartbit4all.api.object;

import java.util.List;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.org.bean.ACLOperationReference;

public interface SubscriptionConfigContributionApi extends ContributionApi {

  /**
   * A contribution can manage a list of configurations. These configuration are referred by the
   * {@link ACLOperationReference#CONFIG}.
   * 
   * @return The list of managed configurations.
   */
  List<String> getManagedConfigs();

  /**
   * A contribution can manage the revoke of a list of configurations. These configuration are
   * referred by the {@link ACLOperationReference#CONFIG}.
   * 
   * @return The list of managed configurations.
   */
  List<String> getRevokableConfigs();



}
