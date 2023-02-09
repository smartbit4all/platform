package org.smartbit4all.api.view;

import java.net.URI;
import java.util.UUID;
import org.smartbit4all.api.view.bean.SmartLinkData;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectNode;

public interface SmartLinkApi {

  /**
   * Publishes the view in a {@link SmartLinkData} and returns it's URI.
   *
   * @param channel
   * @param view
   */
  URI publishView(String channel, View view);

  /**
   * Finds the SmartLinkData in the channel, identified by it's UUID, and returns it as an
   * {@link ObjectNode}. If link is not found, returns null.
   *
   * @param channel
   * @param smartLinkUuid
   * @return
   */
  ObjectNode getSmartLink(String channel, UUID smartLinkUuid);

}
