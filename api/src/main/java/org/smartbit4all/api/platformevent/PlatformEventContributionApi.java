package org.smartbit4all.api.platformevent;

import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.platformevent.bean.PlatformEvent;

public interface PlatformEventContributionApi extends ContributionApi {

  boolean publish(PlatformEvent event) throws Exception;

}
