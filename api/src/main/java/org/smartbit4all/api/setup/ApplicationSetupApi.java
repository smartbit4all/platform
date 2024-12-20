package org.smartbit4all.api.setup;

import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.mdm.bean.ApplicationSetupData;

/**
 * The application setup is a contribution api responsible for a setup in an application. One setup
 * can be as long as necessary but be aware of the following. The setup is a special logic that can
 * be called from UI and from {@link ApplicationSetupManagementApi#scheduleSetup()}
 * 
 * @author Peter Boros
 */
public interface ApplicationSetupApi extends ContributionApi {

  /**
   * @return The setup descriptor.
   */
  ApplicationSetupData getData();

  /**
   * The execution of the setup operation. It must be implemented by the given instance.
   */
  void execute();

  /**
   * This function can be implemented and check if it is necessary to run the setup again. In the
   * first execution of the setup it doesn't take into account.
   * 
   * @return By default it is false, so this given setup will be executed only ones. If it is
   *         necessary then implement this function to check the resource if it is necessary to
   *         reapply the setup logic.
   */
  boolean checkRunAgain();

}
