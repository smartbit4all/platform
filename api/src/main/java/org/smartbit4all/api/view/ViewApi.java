package org.smartbit4all.api.view;

import java.util.UUID;
import org.smartbit4all.api.view.bean.CloseResult;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewData;
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
  UUID showView(ViewData view);

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
  ViewData getView(UUID viewUuid);

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
   * Returns current ViewContext.
   * 
   * @return
   */
  ViewContext currentViewContext();

  /**
   * Set the closing result of a view, which should be closed because another view is opening.
   * 
   * @param viewToClose UUID of the view which sets the close result.
   * @param result
   */
  void setClosePendingResult(UUID viewToClose, CloseResult result);

}
