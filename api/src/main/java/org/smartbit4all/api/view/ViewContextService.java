package org.smartbit4all.api.view;

import java.util.UUID;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.view.bean.MessageResult;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
import org.smartbit4all.api.view.bean.ViewData;

public interface ViewContextService {

  /**
   * Creates a new viewContext and registers it in the Session.
   * 
   * @return
   */
  ViewContext createViewContext();

  /**
   * Returns current ViewContext.
   * 
   * @return
   */
  ViewContext getCurrentViewContext();

  /**
   * Sets current viewContext (by uuid).
   * 
   * @param uuid
   */
  void setCurrentViewContext(UUID uuid);

  /**
   * Returns ViewContext by URI.
   * 
   * @return
   */
  ViewContext getViewContext(UUID uuid);

  void updateCurrentViewContext(UnaryOperator<ViewContext> update);

  void updateViewContext(ViewContextUpdate viewContextUpdate);

  String getParentViewName(String viewName);

  /**
   * Returns viewData from specified viewContext. If viewContextUuid is null, current viewContext is
   * used.
   * 
   * @param viewContextUuid
   * @param viewUuid
   * @return
   */
  ViewData getViewFromViewContext(UUID viewContextUuid, UUID viewUuid);

  /**
   * Finds ViewData by viewUuid, and calls it's API's message handler method which is registered for
   * messageResult.selectedOption.code value.
   * 
   * @param viewUuid
   * @param messageUuid
   * @param messageResult
   */
  void handleMessage(UUID viewUuid, UUID messageUuid, MessageResult messageResult);

}
