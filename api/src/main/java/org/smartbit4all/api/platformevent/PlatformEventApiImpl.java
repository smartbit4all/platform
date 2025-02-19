package org.smartbit4all.api.platformevent;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.platformevent.bean.PlatformEvent;
import org.smartbit4all.api.session.SessionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

public class PlatformEventApiImpl
    extends PrimaryApiImpl<PlatformEventContributionApi>
    implements PlatformEventApi {

  public PlatformEventApiImpl() {
    super(PlatformEventContributionApi.class);
  }

  private static final Logger log = LoggerFactory.getLogger(PlatformEventApiImpl.class);

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Override
  public boolean publish(PlatformEvent event) throws Exception {

    boolean result = false;
    Collection<PlatformEventContributionApi> apis = getContributionApis().values();
    if (ObjectUtils.isEmpty(apis)) {
      log.info("Warn - No PlatformEventContributionApi instances found.");
    }

    for (PlatformEventContributionApi api : apis) {
      result = api.publish(event);
    }

    return result;
  }

  @Override
  public PlatformEventBuilder createEvent(String eventCode) {

    PlatformEventBuilder platformEventBuilder = new PlatformEventBuilder(sessionApi, this);
    platformEventBuilder.create(eventCode);

    return platformEventBuilder;
  }
}
