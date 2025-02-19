package org.smartbit4all.testing.platformevent;

import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.platformevent.PlatformEventContributionApi;
import org.smartbit4all.api.platformevent.bean.PlatformEvent;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

public class PlatformEventApiTestContributor implements PlatformEventContributionApi {

  public static final String EVENT_LIST = "eventList";
  public static final String TEST_SCHEMA = "testSchema";

  @Autowired
  private CollectionApi collectionApi;
  @Autowired
  private ObjectApi objectApi;

  @Override
  public String getApiName() {
    return "platformevent-test-contributor";
  }

  @Override
  public boolean publish(PlatformEvent event) throws Exception {

    collectionApi.list(TEST_SCHEMA, EVENT_LIST).add(objectApi.saveAsNew(TEST_SCHEMA, event));
    return true;
  }

}
