package org.smartbit4all.api.setup;

import org.smartbit4all.api.contribution.PrimaryApi;

/**
 * This api is scheduled and run all the new setup logics if they appear as methods in an
 * ApplicationSetupApi. The contribution apis are offering functions to call by the central setup
 * logic typically only one time to ensure that the setup is commited into the given application.
 * 
 * @author Peter Boros
 */
public interface ApplicationSetupManagementApi extends PrimaryApi<ApplicationSetupApi> {

  /**
   * The map of the already executed setups.
   */
  static String SETUP_MAP = "applicationSetupMap";

  void scheduleSetup();

}
