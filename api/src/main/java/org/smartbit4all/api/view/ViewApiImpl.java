package org.smartbit4all.api.view;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.view.bean.CloseResult;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.MessageOption;
import org.smartbit4all.api.view.bean.OpenPendingData;
import org.smartbit4all.api.view.bean.SmartLinkData;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewState;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.base.Strings;
import static java.util.stream.Collectors.toList;

public class ViewApiImpl implements ViewApi {

  private static final Logger log = LoggerFactory.getLogger(ViewApiImpl.class);

  static final String MESSAGE_DATA = "messageData";

  @Autowired
  private ViewContextService viewContextService;

  @Autowired
  private SmartLinkApi smartLinkApi;

  @Autowired
  private ObjectApi objectApi;

  @Value("${view.messageViewName:message-dialog}")
  private String messageViewName;

  @Override
  public UUID showView(View view) {
    Objects.requireNonNull(view, "View must be not null");
    view.setUuid(UUID.randomUUID());
    if (view.getConstraint() == null) {
      view.setConstraint(new ViewConstraint());
    }
    return showViewInternal(view);
  }

  /**
   * Same as showView but doesn't modify UUID, expects it to be non null.
   *
   * @param view
   * @return
   */
  private UUID showViewInternal(View view) {
    Objects.requireNonNull(view.getUuid(), "View.uuid must be not null");
    if (view.getKeepModelOnImplicitClose() == null) {
      view.keepModelOnImplicitClose(
          viewContextService.getKeepModelOnImplicitClose(view.getViewName()));
    }
    viewContextService
        .updateCurrentViewContext(context -> this.addViewToViewContext(context, view));
    if (view.getState() == ViewState.OPEN_PENDING) {
      // view didn't actually opened, need to handle before close handlers
      handleOpenPending();
    }
    return view.getUuid();
  }

  private boolean isActiveView(View view) {
    return view.getType() == ViewType.NORMAL
        && (view.getState() == ViewState.OPENED
            || view.getState() == ViewState.TO_OPEN);
  }

  private ViewContext addViewToViewContext(ViewContext context, View view) {
    if (view.getType() == ViewType.NORMAL) {
      String parentViewName = viewContextService.getParentViewName(view.getViewName());
      // check if parent should exist, open if yes but doesn't
      View parentView = null;
      if (!Strings.isNullOrEmpty(parentViewName)) {
        parentView = context.getViews().stream()
            .filter(this::isActiveView)
            .filter(v -> parentViewName.equals(v.getViewName()))
            .findFirst()
            .orElse(null);
        if (parentView == null) {
          parentView = new View()
              .viewName(parentViewName);
          showView(parentView);
        }
        view.containerUuid(parentView.getUuid());
      }
      List<View> children = getChildrenOfParentView(context, parentView);
      if (children.isEmpty()) {
        if (parentView != null && parentView.getState() == ViewState.OPEN_PENDING) {
          view.setState(ViewState.OPEN_PENDING);
          OpenPendingData data = getOpenPendingData();
          data.addViewsToOpenItem(view.getUuid());
        } else {
          view.setState(ViewState.TO_OPEN);
        }
      } else {
        view.setState(ViewState.OPEN_PENDING);
        if (context.getOpenPendingData() != null) {
          // current pending will be discarded
          List<View> viewsToOpen = context.getOpenPendingData().getViewsToOpen().stream()
              .map(v -> ViewContexts.getView(context, v))
              .collect(toList());
          String message = viewsToOpen.stream()
              .map(v -> v.getViewName() + " - " + v.getUuid())
              .collect(Collectors.joining(","));
          log.warn("OPEN_PENDING already in progress, discarding it ({})",
              message);
          viewsToOpen.forEach(v -> v.setState(ViewState.CLOSED));
        }
        context.setOpenPendingData(
            new OpenPendingData()
                .addViewsToOpenItem(view.getUuid())
                .viewsToClose(children.stream()
                    .map(View::getUuid)
                    .collect(toList())));
      }
    } else {
      view.setState(ViewState.TO_OPEN);
    }
    return context.addViewsItem(view);
  }

  /**
   *
   * @param context
   * @param parentView
   * @return is there any child views to close
   */
  private List<View> getChildrenOfParentView(ViewContext context, View parentView) {
    List<View> activeViews = context.getViews().stream()
        .filter(this::isActiveView)
        .collect(Collectors.toList());
    int startIdx;
    if (parentView == null) {
      // close all active view -> start from first
      startIdx = 0;
    } else {
      // find parent in active views
      List<UUID> activeViewUuids = activeViews.stream()
          .map(View::getUuid)
          .collect(Collectors.toList());
      int parentIdx = activeViewUuids.indexOf(parentView.getUuid());
      if (parentIdx != -1) {
        startIdx = parentIdx + 1;
      } else {
        // not an active parent, don't close anything
        startIdx = activeViews.size();
      }
    }
    return activeViews
        .subList(startIdx, activeViews.size());
  }

  @Override
  public void closeView(UUID viewUuid) {
    Objects.requireNonNull(viewUuid, "UUID must be not null");
    viewContextService.updateCurrentViewContext(
        context -> {
          ViewContexts.updateViewState(context, viewUuid, ViewState.TO_CLOSE);
          View viewToClose = ViewContexts.getView(context, viewUuid);
          if (viewToClose != null && viewToClose.getType() == ViewType.NORMAL) {
            View parentView = getParentView(viewToClose);
            while (parentView != null && !parentView.getClosedChildrenViews().isEmpty()) {
              View viewToShow = parentView.getClosedChildrenViews().remove(0);
              if (viewToShow != null) {
                showViewInternal(viewToShow);
              }
              parentView = viewToShow;
            }
          }
          return context;
        });

  }

  @Override
  public View getView(UUID viewUuid) {
    return viewContextService.getViewFromCurrentViewContext(viewUuid);
  }

  @Override
  public <M> M getModel(UUID viewUuid, Class<M> clazz) {
    return viewContextService.getModel(viewUuid, clazz);
  }

  @Override
  public List<View> getViews(String viewName) {
    Objects.requireNonNull(viewName, "viewName must be not null");
    return viewContextService.getCurrentViewContextEntry()
        .getViews().stream()
        .filter(v -> viewName.equals(v.getViewName()))
        .collect(toList());
  }

  @Override
  public UUID showMessage(MessageData message) {
    Objects.requireNonNull(message, "Message must be not null");
    Objects.requireNonNull(message.getViewUuid(), "Message.viewUuid must be not null");
    message.setUuid(UUID.randomUUID());
    if (message.getOptions() == null || message.getOptions().isEmpty()) {
      // an OK button is must have, otherwise client won't be able to close it
      message.addOptionsItem(new MessageOption()
          .code("OK")
          .label("Ok"));
    }
    return showViewInternal(new View()
        .uuid(message.getUuid())
        .containerUuid(message.getViewUuid())
        .viewName(messageViewName)
        .type(ViewType.DIALOG)
        .putParametersItem(MESSAGE_DATA, message));
  }

  @Override
  public void closeMessage(UUID messageUuid) {
    closeView(messageUuid);
  }

  @Override
  public UUID currentViewContextUuid() {
    return viewContextService.getCurrentViewContextUuid();
  }

  private void handleOpenPending() {
    OpenPendingData data = getOpenPendingData();
    CloseResult globalResult = CloseResult.APPROVED;
    for (UUID view : data.getViewsToClose()) {
      CloseResult result = data.getResults()
          .computeIfAbsent(
              view.toString(),
              k -> viewContextService.callBeforeClose(view, data));
      if (result == CloseResult.PENDING || result == CloseResult.REJECTED) {
        // pending or rejected -> don't call others
        globalResult = result;
        break;
      }
    }
    if (globalResult == CloseResult.APPROVED) {
      // no pending, approved -> finish view open
      viewContextService.updateCurrentViewContext(
          context -> {
            data.getViewsToClose().forEach(uuidToClose -> {
              View parentView = getParentView(uuidToClose);
              if (parentView != null) {
                View viewToClose = getView(uuidToClose);
                if (viewToClose != null) {
                  if (!Boolean.TRUE.equals(viewToClose.getKeepModelOnImplicitClose())) {
                    // TODO clearView()?
                    viewToClose.setModel(null);
                    if (viewToClose.getActions() != null) {
                      viewToClose.getActions().clear();
                    }
                    if (viewToClose.getCallbacks() != null) {
                      viewToClose.getCallbacks().clear();
                    }
                    if (viewToClose.getValueSets() != null) {
                      viewToClose.getValueSets().clear();
                    }
                    if (viewToClose.getWidgetModels() != null) {
                      viewToClose.getWidgetModels().clear();
                    }
                    if (viewToClose.getVariables() != null) {
                      viewToClose.getVariables().clear();
                    }
                  }
                  viewToClose.setConstraint(new ViewConstraint());
                  parentView.getClosedChildrenViews().add(0, viewToClose);
                }
              }
              ViewContexts.updateViewState(context, uuidToClose, ViewState.TO_CLOSE);
            });
            data.getViewsToOpen().stream()
                .map(v -> ViewContexts.getView(context, v))
                .forEach(v -> v.setState(ViewState.TO_OPEN));
            context.setOpenPendingData(null);
            return context;
          });
    } else if (globalResult == CloseResult.REJECTED) {
      // no pending, rejected -> restore original state
      viewContextService.updateCurrentViewContext(
          context -> {
            data.getViewsToClose().forEach(
                v -> ViewContexts.updateViewState(context, v, ViewState.OPENED));
            data.getViewsToOpen().stream()
                .map(v -> ViewContexts.getView(context, v))
                .forEach(v -> v.setState(ViewState.CLOSED));
            context.setOpenPendingData(null);
            return context;
          });
    }
  }

  private OpenPendingData getOpenPendingData() {
    OpenPendingData data = viewContextService.getCurrentViewContextEntry().getOpenPendingData();
    if (data == null) {
      throw new IllegalArgumentException(
          "View set to OPEN_PENDING but no data associated with it!");
    }
    return data;
  }

  private View getParentView(UUID viewUuid) {
    return getParentView(getView(viewUuid));
  }

  private View getParentView(View view) {
    if (view == null) {
      return null;
    }
    String parentViewName =
        viewContextService.getParentViewName(view.getViewName());
    List<View> parents = getViews(parentViewName);
    if (parents.size() == 1) {
      // exact match
      View parentView = parents.get(0);
      if (!Objects.equals(view.getContainerUuid(), parentView.getUuid())) {
        log.warn("parentView.getUuid({}) != view.getContainerUuid({})",
            parentView.getUuid(), view.getContainerUuid());
      }
      return parentView;
    }
    return parents.stream()
        .filter(v -> Objects.equals(view.getContainerUuid(), v.getUuid()))
        .findFirst()
        .orElse(null);
  }

  @Override
  public void setClosePendingResult(UUID viewToClose, CloseResult result) {
    Objects.requireNonNull(viewToClose, "viewToClose must be specified");
    Objects.requireNonNull(result, "result must be specified");
    viewContextService.updateCurrentViewContext(
        c -> {
          c.getOpenPendingData().putResultsItem(viewToClose.toString(), result);
          return c;
        });
    handleOpenPending();
  }

  @Override
  public void updateView(UUID viewUuid, UnaryOperator<View> update) {
    viewContextService.updateCurrentViewContext(
        c -> ViewContexts.updateViewData(c, viewUuid, update));
  }

  @Override
  public UUID showPublishedView(String channel, UUID smartLinkUuid) {
    ObjectNode linkNode = smartLinkApi.getSmartLink(channel, smartLinkUuid);
    if (linkNode == null) {
      return null;
    }
    return showView(linkNode.getValue(View.class, SmartLinkData.VIEW));
  }

  @Override
  public <T> T getWidgetModelFromView(Class<T> clazz, UUID viewUuid, String widgetId) {
    String[] paths = getPathParts(widgetId);
    String location = paths[0];
    String key = paths[1];
    if (View.PARAMETERS.equals(location)) {
      // to make sure view model/params gets initialized
      getModel(viewUuid, null);
      View view = getView(viewUuid);
      Object componentModel = view.getParameters().get(key);
      T widgetModel = objectApi.asType(clazz, componentModel);
      view.getParameters().put(key, widgetModel);
      return widgetModel;
    } else if (View.WIDGET_MODELS.equals(location)) {
      View view = getView(viewUuid);
      T widgetModel = objectApi.asType(clazz, view.getWidgetModels().get(key));
      view.getWidgetModels().put(key, widgetModel);
      return widgetModel;
    } else if (View.MODEL.equals(location)) {
      Object model = getModel(viewUuid, null);
      return objectApi.getValueFromObject(clazz, model, key);
    } else {
      throw new IllegalArgumentException("Invalid path");
    }
  }

  @Override
  public <T> void setWidgetModelInView(Class<T> clazz, UUID viewUuid, String widgetId,
      T widgetModel) {
    String[] paths = getPathParts(widgetId);
    String location = paths[0];
    String key = paths[1];
    View view = getView(viewUuid);
    if (View.PARAMETERS.equals(location)) {
      view.putParametersItem(key, widgetModel);
    } else if (View.MODEL.equals(location)) {
      Object model = getModel(viewUuid, null);
      Map<String, Object> modelAsMap = objectApi.definition(model.getClass()).toMap(model);
      modelAsMap.put(key, objectApi.definition(clazz).toMap(widgetModel));
      Object updatedModel = objectApi.asType(model.getClass(), modelAsMap);
      view.setModel(updatedModel);
    } else if (View.WIDGET_MODELS.equals(location)) {
      view.getWidgetModels().put(key, widgetModel);
    } else {
      throw new IllegalArgumentException("Invalid path");
    }
  }

  private String[] getPathParts(String componentId) {
    String[] paths = componentId.split("\\.");
    if (paths == null) {
      throw new IllegalArgumentException("Empty path not allowed");
    }
    if (paths.length == 1) {
      paths = new String[2];
      paths[0] = View.WIDGET_MODELS;
      paths[1] = componentId;
    }
    if (paths.length != 2) {
      throw new IllegalArgumentException("Invalid path");
    }
    return paths;
  }

  @Override
  public BinaryData downloadItem(UUID uuid, String item) {
    View view = viewContextService.getViewFromCurrentSession(uuid);
    URI uri = view.getDownloadableItems().get(item);
    if (uri != null) {
      BinaryDataObject data = objectApi.read(uri, BinaryDataObject.class);
      return data.getBinaryData();
    }
    return null;
  }

  @Override
  public void setCallback(UUID viewUuid, String requestId, InvocationRequest request) {
    updateView(viewUuid, view -> view.putCallbacksItem(requestId, request));
  }

  @Override
  public void addCallback(UUID viewUuid, String requestId, InvocationRequest request) {
    updateView(viewUuid, view -> {
      getViewCallbackList(view, requestId).add(request);
      return view;
    });
  }

  private List<InvocationRequest> getViewCallbackList(View view, String requestId) {
    Object callbacks = view.getCallbacks().computeIfAbsent(requestId, id -> new ArrayList<>());
    if (!(callbacks instanceof List)) {
      throw new IllegalStateException("CallbackList is not a List for " + requestId);
    }
    List<InvocationRequest> list = objectApi.asList(InvocationRequest.class, (List<?>) callbacks);
    // put back typed list
    view.putCallbacksItem(requestId, list);
    return list;
  }

  @Override
  public InvocationRequest getCallback(UUID viewUuid, String requestId) {
    View view = getView(viewUuid);
    InvocationRequest request =
        objectApi.asType(InvocationRequest.class, view.getCallbacks().get(requestId));
    // put back typed object
    view.putCallbacksItem(requestId, request);
    return request;
  }

  @Override
  public List<InvocationRequest> getCallbacks(UUID viewUuid, String requestId) {
    return getViewCallbackList(getView(viewUuid), requestId);
  }

  @Override
  public ViewEventApi events(UUID viewUuid) {
    return new ViewEventApiImpl(getView(viewUuid));
  }

  @Override
  public ViewEventApi events(View view) {
    return new ViewEventApiImpl(view);
  }

}
