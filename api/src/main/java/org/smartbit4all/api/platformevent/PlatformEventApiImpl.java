package org.smartbit4all.api.platformevent;

import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.platformevent.bean.PlatformEvent;
import org.smartbit4all.api.session.SessionApi;
import org.springframework.beans.factory.annotation.Autowired;

public class PlatformEventApiImpl
    extends PrimaryApiImpl<PlatformEventContributionApi>
    implements PlatformEventApi {

  public PlatformEventApiImpl() {
    super(PlatformEventContributionApi.class);
  }

  @Autowired
  private OrgApi orgApi;
  @Autowired
  private SessionApi sessionApi;

  @Override
  public boolean publish(PlatformEvent event) throws Exception {

    boolean result = true;
    for (PlatformEventContributionApi api : getContributionApis().values()) {
      result = api.publish(event);
    }

    return result;
  }

  @Override
  public PlatformEventBuilder createEvent(String eventCode) {

    PlatformEventBuilder platformEventBuilder = new PlatformEventBuilder(orgApi, sessionApi, this);
    platformEventBuilder.create(eventCode);

    return platformEventBuilder;
  }
}
