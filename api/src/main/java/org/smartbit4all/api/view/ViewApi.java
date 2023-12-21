package org.smartbit4all.api.view;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.view.bean.CloseResult;
import org.smartbit4all.api.view.bean.DownloadedFile;
import org.smartbit4all.api.view.bean.Link;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewState;

/**
 * This interface is used in stateless BFF APIs, and provides generic UI handling functionality.
 * ViewApi works on current {@link ViewContext}
 *
 * @author matea
 *
 */
public interface ViewApi {

  /**
   * Adds view to current session, with state = {@link ViewState#TO_OPEN}. View.uuid will be
   * overridden.
   *
   * @param view
   * @return Identifier of View
   */
  UUID showView(View view);

  /**
   * Existing views state will be update to {@link ViewState#TO_CLOSE}.
   *
   * @param viewUuid
   */
  void closeView(UUID viewUuid);

  /**
   * Returns viewData from current viewContext.
   *
   * @param viewUuid
   * @return
   */
  View getView(UUID viewUuid);

  /**
   * Return model of the view identified by viewUuid. This object can be used to manipulate the
   * model, it is saved at the end of server call.
   *
   * @param <M>
   * @param viewUuid
   * @param clazz
   * @return
   */
  <M> M getModel(UUID viewUuid, Class<M> clazz);

  // TODO setModel should be here..

  /**
   * Returns viewDatas from current viewContext with specified viewName.
   *
   * @param viewName
   * @return
   */
  List<View> getViews(String viewName);

  /**
   * Creates and shows a view based on MessageData parameter. MessageData.UUID will be overridden.
   *
   * @param message
   * @return
   */
  UUID showMessage(MessageData message);

  /**
   * View created from message specified by parameter will be updated to state
   * {@link ViewState#TO_CLOSE}.
   *
   * @param messageUuid
   */
  void closeMessage(UUID messageUuid);

  /**
   * Returns current ViewContext's UUID.
   *
   * @return
   */
  UUID currentViewContextUuid();

  /**
   * Set the closing result of a view, which should be closed because another view is opening.
   *
   * @param viewToClose UUID of the view which sets the close result.
   * @param result
   */
  void setClosePendingResult(UUID viewToClose, CloseResult result);

  /**
   * Updates the given viewData.
   *
   * @param viewUuid
   * @param update
   */
  void updateView(UUID viewUuid, UnaryOperator<View> update);

  /**
   * The
   *
   * @param channel
   * @param smartLinkUuid
   * @return
   */
  UUID showPublishedView(String channel, UUID smartLinkUuid);

  <T> T getWidgetModelFromView(Class<T> clazz, UUID viewUuid, String widgetId);

  <T> T getWidgetServerModelFromView(Class<T> clazz, UUID viewUuid, String widgetId);

  <T> void setWidgetModelInView(Class<T> clazz, UUID viewUuid, String widgetId, T widgetModel);

  <T> void setWidgetServerModelInView(Class<T> clazz, UUID viewUuid, String widgetId,
      T widgetModel);

  BinaryData downloadItem(UUID uuid, String item);

  /**
   * Add a single callback to View. Subsequent adds will override previous callback. Retrieving this
   * callback should be done via {@link #getCallback(UUID, String)}.
   *
   * @param viewUuid
   * @param requestId
   * @param request
   */
  void setCallback(UUID viewUuid, String requestId, InvocationRequest request);

  /**
   * Add a callback to a View with requestId as key. Subsequent calls will add callbacks to a list,
   * so retrieving them should be done via {@link #getCallbacks(UUID, String)}.
   *
   * @param viewUuid
   * @param requestId
   * @param request
   */
  void addCallback(UUID viewUuid, String requestId, InvocationRequest request);

  /**
   * Returns a callback registered to this View specified by requestId.
   *
   * @param viewUuid
   * @param requestId
   * @return
   */
  InvocationRequest getCallback(UUID viewUuid, String requestId);

  /**
   * Returns all callbacks in a List registered to this View specified by requestId.
   *
   * @param viewUuid
   * @param requestId
   * @return
   */
  List<InvocationRequest> getCallbacks(UUID viewUuid, String requestId);

  /**
   * Retrieve a {@link ViewEventApi} instance for the given view. It can be used for one action
   * execution cycle. Typically one server call because it refers to the actual loaded View
   * instance.
   *
   * @param viewUuid The uuid of the view.
   * @return Always return a new instance from the api. Use cautiously!
   */
  ViewEventApi events(UUID viewUuid);

  /**
   * Retrieve a {@link ViewEventApi} instance for the given view. It can be used for one action
   * execution cycle. Typically one server call because it refers to the actual loaded View
   * instance.
   *
   * @param view The view instance to manage.
   * @return Always return a new instance from the api. Use cautiously!
   */
  ViewEventApi events(View view);

  /**
   * Opens a link in the browser.
   *
   * @param link
   */
  void openLink(Link link);

  void downloadFile(DownloadedFile file);

}
