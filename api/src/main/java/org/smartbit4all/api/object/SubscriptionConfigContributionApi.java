package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.org.bean.ACLOperationReference;

public interface SubscriptionConfigContributionApi extends ContributionApi {

  /**
   * Returns true if the contribution supports the config.
   * 
   * @param config
   * @return
   */
  boolean supports(String config);

  /**
   * Returns true if the contribution supports revoke action for the config.
   * 
   * @param config
   * @return
   */
  boolean supportsRevoke(String config);

  /**
   * A contribution can manage the revoke of a list of configurations. These configuration are
   * referred by the {@link ACLOperationReference#CONFIG}.
   * 
   * @return The list of managed configurations.
   */
  List<String> getRevokableConfigs();

  /**
   * This function constructs the entity summary based on the configuration and the the referred
   * entity.
   * 
   * @param config The name of the configuration to use.
   * @param entityUri the uri of the entity.
   * @return
   */
  String constructEntitySummary(String config, URI entityUri);

}
