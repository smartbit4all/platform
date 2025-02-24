package org.smartbit4all.api.platformevent;

import org.smartbit4all.api.platformevent.bean.PlatformEvent;

public interface PlatformEventApi {

  public PlatformEventBuilder createEvent(String eventCode);

  public boolean publish(PlatformEvent event) throws Exception;
}
