package org.smartbit4all.api.view;

import java.util.UUID;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.view.bean.CloseResult;
import org.smartbit4all.api.view.bean.MessageResult;
import org.smartbit4all.api.view.bean.OpenPendingData;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
import org.smartbit4all.api.view.bean.ViewData;

public interface ViewContextService {

  public static final String SCHEMA = "viewcontext-sv";

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
   * Returns current ViewContext's UUID.
   * 
   * @return
   */
  UUID getCurrentViewContextUuid();

  /**
   * Sets the current viewContext specified by uuid, locks it and executes the given process.
   * 
   * @param uuid
   * @param process
   * @throws Exception
   */
  void execute(UUID uuid, ViewContextCommand process) throws Exception;

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
   * Returns viewData from the current viewContext.
   * 
   * @param viewUuid
   * @return
   */
  ViewData getViewFromCurrentViewContext(UUID viewUuid);

  /**
   * Finds ViewData by viewUuid, and calls it's API's message handler method which is registered for
   * messageResult.selectedOption.code value.
   * 
   * @param viewUuid
   * @param messageUuid
   * @param messageResult
   */
  void handleMessage(UUID viewUuid, UUID messageUuid, MessageResult messageResult);

  /**
   * If the API associated with view identified by viewToCloseUuid has a method with
   * {@link BeforeClose} annotation, it will be called and it's return value will be returned,
   * otherwise it will always return Approved.
   * 
   * @return
   * 
   */
  CloseResult callBeforeClose(UUID viewToCloseUuid, OpenPendingData data);

}
