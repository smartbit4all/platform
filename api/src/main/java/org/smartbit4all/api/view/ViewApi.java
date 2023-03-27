package org.smartbit4all.api.view;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.view.bean.CloseResult;
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

  <T> T getComponentModelFromView(Class<T> clazz, UUID viewUuid, String componentId);

  <T> void setComponentModelInView(Class<T> clazz, UUID viewUuid, String componentId,
      T componentModel);

  BinaryData downloadItem(UUID uuid, String item);

}
