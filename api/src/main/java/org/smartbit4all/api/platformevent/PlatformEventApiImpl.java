package org.smartbit4all.api.platformevent;

import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.platformevent.bean.PlatformEvent;

public class PlatformEventApiImpl
    extends PrimaryApiImpl<PlatformEventContributionApi>
    implements PlatformEventApi {

  public PlatformEventApiImpl() {
    super(PlatformEventContributionApi.class);
  }

  @Override
  public boolean publish(PlatformEvent event) throws Exception {

    boolean result = true;
    for (PlatformEventContributionApi api : getContributionApis().values()) {
      result = api.publish(event);
    }

    return result;
  }

}
