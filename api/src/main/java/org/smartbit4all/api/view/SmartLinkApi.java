package org.smartbit4all.api.view;

import java.net.URI;
import java.util.UUID;
import org.smartbit4all.api.view.bean.SmartLinkData;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectNode;

public interface SmartLinkApi {

  /**
   * A flag set in all published {@link View}s to enable recognition of views opened via SmartLinks.
   * 
   * <p>
   * There is no need to manually set this view parameter, as it is added automatically when
   * invoking {@link #publishView(String, View)}. The expected value is of type {@link Boolean}.
   * 
   * <p>
   * Example usage in a Page API:
   * 
   * <pre>
   * <code>
   * public MyModel initModel(View view) {
   *   final boolean openedFromSmartLink = parameters(view).requireNonNullElse(
   *     SmartLinkApi.PARAM_OPENED_FROM_SMART_LINK,
   *     Boolean.class,
   *     false);
   *   // Initialise model based on the above flag if needed...
   * }
   * </code>
   * </pre>
   */
  String PARAM_OPENED_FROM_SMART_LINK = "p-opened-from-smartlink";

  /**
   * ACL permission required to open the view via a smart link. If the user has this permission,
   * they can access the view, otherwise, a "no permission" page is displayed.
   */
  String ACL_SMART_LINK_ACCESS_PERMISSION = "aclSmartLinkAccessPermission";

  /**
   * Publishes the view in a {@link SmartLinkData} and returns it's URI.
   * 
   * <p>
   * The published {@link View} shall carry the value {@link Boolean#TRUE} under the
   * {@link View#PARAMETERS} named {@link #PARAM_OPENED_FROM_SMART_LINK}.
   *
   * @param channel
   * @param view
   */
  URI publishView(String channel, View view);

  /**
   * Publishes the view in a {@link SmartLinkData}, sets the acl and returns it's URI.
   * 
   * <p>
   * The published {@link View} shall carry the value {@link Boolean#TRUE} under the
   * {@link View#PARAMETERS} named {@link #PARAM_OPENED_FROM_SMART_LINK}.
   *
   * @param channel
   * @param view
   * @param aclUri
   */
  URI publishView(String channel, View view, URI aclUri);

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
