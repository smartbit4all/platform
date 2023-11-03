package org.smartbit4all.api.view;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.view.ViewContextServiceImpl.ViewCall;
import org.smartbit4all.api.view.annotation.BeforeClose;
import org.smartbit4all.api.view.bean.CloseResult;
import org.smartbit4all.api.view.bean.ComponentModel;
import org.smartbit4all.api.view.bean.DataChange;
import org.smartbit4all.api.view.bean.MessageResult;
import org.smartbit4all.api.view.bean.OpenPendingData;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextChange;
import org.smartbit4all.api.view.bean.ViewContextData;
import org.smartbit4all.api.view.bean.ViewContextUpdate;

public interface ViewContextService {

  public static final String SCHEMA = "viewcontext-sv";

  /**
   * Creates a new viewContext and registers it in the Session.
   *
   * @return
   */
  ViewContextData createViewContext();

  /**
   * Returns current ViewContext.
   *
   * @return
   */
  ViewContextData getCurrentViewContext();

  /**
   * Returns current ViewContext.
   *
   * @return
   */
  ViewContext getCurrentViewContextEntry();

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
   * Returns ViewContext by UUID.
   *
   * @return
   */
  ViewContextData getViewContext(UUID uuid);

  void updateCurrentViewContext(UnaryOperator<ViewContext> update);

  void updateViewContext(ViewContextUpdate viewContextUpdate);

  String getParentViewName(String viewName);

  Boolean getKeepModelOnImplicitClose(String viewName);

  /**
   * Returns viewData from the current viewContext.
   *
   * @param viewUuid
   * @return
   */
  View getViewFromCurrentViewContext(UUID viewUuid);

  /**
   * Returns viewData from the current session.
   *
   * @param viewUuid
   * @return
   */
  View getViewFromCurrentSession(UUID viewUuid);

  /**
   * Return model of the view identified by viewUuid. This object can be used to manipulate the
   * model, it is saved at the end of server call (see {@link #execute(UUID, ViewContextCommand)}.
   *
   * @param <M>
   * @param viewUuid
   * @param clazz
   * @return
   */
  <M> M getModel(UUID viewUuid, Class<M> clazz);

  /**
   * Finds ViewData by viewUuid, and calls it's API's message handler method which is registered for
   * messageResult.selectedOption.code value.
   *
   * @param viewUuid
   * @param messageUuid
   * @param messageResult
   */
  ViewContextChange handleMessage(UUID viewUuid, UUID messageUuid, MessageResult messageResult);

  /**
   * If the API associated with view identified by viewToCloseUuid has a method with
   * {@link BeforeClose} annotation, it will be called and it's return value will be returned,
   * otherwise it will always return Approved.
   *
   * @return
   *
   */
  CloseResult callBeforeClose(UUID viewToCloseUuid, OpenPendingData data);

  /**
   * Returns a ComponentModel assembled from the View specified by viewUuid.
   *
   * @param viewUuid
   * @return
   */
  ComponentModel getComponentModel(UUID viewUuid);

  /**
   * Returns a ComponentModel assembled from the View specified by viewUuid, wrapped in a
   * ViewContextChange, so all other view changes can be handled by the client..
   *
   * @param viewUuid
   * @return
   */
  ViewContextChange getComponentModel2(UUID viewUuid);

  /**
   * Performs an action based on request. Finds View by viewUuid, and calls it's API's action
   * handler method which is registered for request.code value.
   *
   * @param viewUuid
   * @param request
   * @return
   */
  ViewContextChange performAction(UUID viewUuid, UiActionRequest request);

  ViewContextChange performWidgetAction(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  /**
   * Performs a data change event on specified component/page. Event's change will be written into
   * ComponentModel.data/View.model, and all registered DataChangeListeners will be called. Result
   * will be calculated the same way as with other BFF calls.
   *
   * @param viewUuid
   * @return
   */
  ViewContextChange performDataChanged(UUID viewUuid, DataChange event);

  ViewContextChange performViewCall(ViewCall viewCall, String methodName);


  /**
   * The view returns the uuid of the opened child views
   */
  List<UUID> getChildrenOfView(UUID viewUuid);

}
