package org.smartbit4all.api.view;

import java.net.URI;
import java.util.Collection;
import java.util.List;
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
   * @param channel the {@code String} logical grouping where the {@link SmartLinkData} is inserted
   *        into persistent storage, not null
   * @param view the {@link View} to publish, not null
   * @return the {@link URI} uniquely identifying the newly created {@link SmartLinkData} in
   *         persistent storage
   */
  URI publishView(String channel, View view);

  /**
   * Publishes the view in a {@link SmartLinkData}, sets the acl and returns it's URI.
   * 
   * <p>
   * The published {@link View} shall carry the value {@link Boolean#TRUE} under the
   * {@link View#PARAMETERS} named {@link #PARAM_OPENED_FROM_SMART_LINK}.
   *
   * @param channel the {@code String} logical grouping where the {@link SmartLinkData} is inserted
   *        into persistent storage, not null
   * @param view the {@link View} to publish, not null
   * @param aclUri the {@code ACL} {@link URI} to set for the created {@link SmartLinkData}, if
   *        provided; nullable
   * @return the {@link URI} uniquely identifying the newly created {@link SmartLinkData} in
   *         persistent storage
   */
  URI publishView(String channel, View view, URI aclUri);

  /**
   * Publishes the provided {@link View} as a smart link.
   * 
   * <p>
   * The published {@link View} shall carry the value {@link Boolean#TRUE} under the
   * {@link View#PARAMETERS} named {@link #PARAM_OPENED_FROM_SMART_LINK}.
   * 
   * @param channel the {@code String} logical grouping where the {@link SmartLinkData} is inserted
   *        into persistent storage, not null
   * @param view the {@link View} to publish, not null
   * @param aclUri the {@code ACL} {@link URI} to set for the created {@link SmartLinkData}, if
   *        provided; nullable
   * @param smartLinkId the {@link UUID} uniquely identifying the created {@link SmartLinkData};
   *        nullable: if unspecified, a new random value shall be generated
   * @return the {@link URI} uniquely identifying the newly created {@link SmartLinkData} in
   *         persistent storage
   */
  URI publishView(String channel, View view, URI aclUri, UUID smartLinkId);

  /**
   * Finds the SmartLinkData in the channel, identified by it's UUID, and returns it as an
   * {@link ObjectNode}. If link is not found, returns null.
   *
   * @param channel
   * @param smartLinkUuid
   * @return
   */
  ObjectNode getSmartLink(String channel, UUID smartLinkUuid);

  /**
   * Do the migration for a channel of smart link.
   * 
   * @param channel The name of the channel to migrate.
   */
  void migrate(String channel);


  /**
   * Deletes the {@link SmartLinkData} identified by the provided {@link UUID}s from persistent
   * storage.
   * 
   * @param smartLinkUuids a {@link Collection} of {@link UUID}s belonging to {@link SmartLinkData},
   *        nullable
   * @return the {@link List} of {@link URI}s of operation managed to remove from persistent
   *         storage, never null
   */
  List<URI> remove(Collection<? extends UUID> smartLinkUuids);

  /**
   * Deletes the stored collections backing the provided channel's legacy persistence implementation
   * 
   * @param channels the {@link String} names of the channels to clear, nullable
   */
  void removeLegacyChannels(Collection<? extends String> channels);


}
