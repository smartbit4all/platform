package org.smartbit4all.api.view;

import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.view.bean.CloseResult;
import org.smartbit4all.api.view.bean.MessageData;
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
      List<View> children = getChildrenOfParentView(context, parentViewName);
      if (children.isEmpty()) {
        view.setState(ViewState.TO_OPEN);
      } else {
        view.setState(ViewState.OPEN_PENDING);
        if (context.getOpenPendingData() != null) {
          // current pending will be discarded
          UUID viewToDiscardUuid = context.getOpenPendingData().getViewToOpen();
          View viewToDiscard = ViewContexts.getView(context, viewToDiscardUuid);
          log.warn("OPEN_PENDING already in progress, discarding it ({} - {})",
              viewToDiscard.getViewName(), viewToDiscardUuid);
          viewToDiscard.setState(ViewState.CLOSED);
        }
        context.setOpenPendingData(
            new OpenPendingData()
                .viewToOpen(view.getUuid())
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
   * @param parentViewName
   * @return is there any child views to close
   */
  private List<View> getChildrenOfParentView(ViewContext context, String parentViewName) {
    List<View> activeViews = context.getViews().stream()
        .filter(this::isActiveView)
        .collect(Collectors.toList());
    int startIdx;
    if (Strings.isNullOrEmpty(parentViewName)) {
      // close all active view -> start from first
      startIdx = 0;
    } else {
      // find parent in active views
      List<String> activeViewNames = activeViews.stream()
          .map(View::getViewName)
          .collect(Collectors.toList());
      int parentIdx = activeViewNames.indexOf(parentViewName);
      if (parentIdx != -1) {
        startIdx = parentIdx + 1;
      } else {
        log.error("Parent view ('{}')is not present in ActiveViews!", parentViewName);
        // don't close anything
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
          return viewContextService.getCurrentViewContextEntry();
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
    OpenPendingData data = viewContextService.getCurrentViewContextEntry().getOpenPendingData();
    if (data == null) {
      throw new IllegalArgumentException(
          "View set to OPEN_PENDING but no data associated with it!");
    }
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
                  viewToClose.setModel(null);
                  viewToClose.setConstraint(new ViewConstraint());
                  parentView.getClosedChildrenViews().add(0, viewToClose);
                }
              }
              ViewContexts.updateViewState(context, uuidToClose, ViewState.TO_CLOSE);
            });
            View viewToOpen = ViewContexts.getView(context, data.getViewToOpen());
            viewToOpen.setState(ViewState.TO_OPEN);
            context.setOpenPendingData(null);
            return context;
          });
    } else if (globalResult == CloseResult.REJECTED) {
      // no pending, rejected -> restore original state
      viewContextService.updateCurrentViewContext(
          context -> {
            data.getViewsToClose().forEach(
                v -> ViewContexts.updateViewState(context, v, ViewState.OPENED));
            View viewToOpen = ViewContexts.getView(context, data.getViewToOpen());
            viewToOpen.setState(ViewState.CLOSED);
            context.setOpenPendingData(null);
            return context;
          });
    }
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
      return parents.get(0);
    }
    return null;
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
  public <T> T getComponentModelFromView(Class<T> clazz, UUID viewUuid, String componentId) {
    String[] paths = getPathParts(componentId);
    String location = paths[0];
    String key = paths[1];
    if (View.PARAMETERS.equals(location)) {
      // to make sure view model/params gets initialized
      getModel(viewUuid, null);
      View view = getView(viewUuid);
      Object componentModel = view.getParameters().get(key);
      return objectApi.asType(clazz, componentModel);
    } else if (View.MODEL.equals(location)) {
      Object model = getModel(viewUuid, null);
      return objectApi.getValueFromObject(clazz, model, key);
    } else {
      throw new IllegalArgumentException("Invalid path");
    }
  }

  @Override
  public <T> void setComponentModelInView(Class<T> clazz, UUID viewUuid, String componentId,
      T componentModel) {
    String[] paths = getPathParts(componentId);
    String location = paths[0];
    String key = paths[1];
    if (View.PARAMETERS.equals(location)) {
      View view = getView(viewUuid);
      view.putParametersItem(key, componentModel);
    } else if (View.MODEL.equals(location)) {
      Object model = getModel(viewUuid, null);
      Map<String, Object> modelAsMap = objectApi.definition(model.getClass()).toMap(model);
      modelAsMap.put(key, objectApi.definition(clazz).toMap(componentModel));
      Object updatedModel = objectApi.asType(model.getClass(), modelAsMap);
      getView(viewUuid).setModel(updatedModel);
    } else {
      throw new IllegalArgumentException("Invalid path");
    }
  }

  private String[] getPathParts(String treeId) {
    String[] paths = treeId.split("\\.");
    if (paths == null) {
      throw new IllegalArgumentException("Empty path not allowed");
    }
    if (paths.length != 2) {
      throw new IllegalArgumentException("Invalid path");
    }
    return paths;
  }


}
