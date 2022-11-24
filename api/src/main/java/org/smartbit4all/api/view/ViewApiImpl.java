package org.smartbit4all.api.view;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewData;
import org.smartbit4all.api.view.bean.ViewState;
import org.smartbit4all.api.view.bean.ViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.base.Strings;

public class ViewApiImpl implements ViewApi {

  private static final Logger log = LoggerFactory.getLogger(ViewApiImpl.class);

  private static final String MESSAGE_DATA = "messageData";

  @Autowired
  private ViewContextService viewContextService;

  @Value("${view.messageViewName:message-dialog}")
  private String messageViewName;

  @Override
  public UUID showView(ViewData view) {
    Objects.requireNonNull(view, "View must be not null");
    view.setUuid(UUID.randomUUID());
    return showViewInternal(view);
  }

  /**
   * Same as showView but doesn't modify UUID, expects it to be non null.
   * 
   * @param view
   * @return
   */
  private UUID showViewInternal(ViewData view) {
    Objects.requireNonNull(view.getUuid(), "View.uuid must be not null");
    view.setState(ViewState.TO_OPEN);
    viewContextService
        .updateCurrentViewContext(context -> this.addViewToViewContext(context, view));
    return view.getUuid();
  }

  private boolean isActiveView(ViewData view) {
    return view.getType() == ViewType.NORMAL
        && (view.getState() == ViewState.OPENED
            || view.getState() == ViewState.TO_OPEN);
  }

  private ViewContext addViewToViewContext(ViewContext context, ViewData view) {
    if (view.getType() == ViewType.NORMAL) {
      String parentViewName = viewContextService.getParentViewName(view.getViewName());
      closeChildrenOfView(context, parentViewName);
    }
    context.addViewsItem(view);
    return context;
  }

  private void closeChildrenOfView(ViewContext context, String parentViewName) {
    List<ViewData> activeViews = context.getViews().stream()
        .filter(this::isActiveView)
        .collect(Collectors.toList());
    int startIdx;
    if (Strings.isNullOrEmpty(parentViewName)) {
      // close all active view -> start from first
      startIdx = 0;
    } else {
      // find parent in active views
      List<String> activeViewNames = activeViews.stream()
          .map(ViewData::getViewName)
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
    for (int i = startIdx; i < activeViews.size(); i++) {
      // TODO use closeView + navigation events
      activeViews.get(i).setState(ViewState.TO_CLOSE);
    }
  }

  @Override
  public void closeView(UUID viewUuid) {
    Objects.requireNonNull(viewUuid, "UUID must be not null");
    viewContextService.updateCurrentViewContext(c -> {
      c.getViews().stream()
          .filter(v -> viewUuid.equals(v.getUuid()))
          .findFirst()
          .ifPresent(view -> view.setState(ViewState.TO_CLOSE));
      return c;
    });
  }

  @Override
  public ViewData getView(UUID viewUuid) {
    return viewContextService.getViewFromViewContext(null, viewUuid);
  }

  @Override
  public UUID showMessage(MessageData message) {
    Objects.requireNonNull(message, "Message must be not null");
    Objects.requireNonNull(message.getViewUuid(), "Message.viewUuid must be not null");
    message.setUuid(UUID.randomUUID());
    return showViewInternal(new ViewData()
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
  public ViewContext currentViewContext() {
    return viewContextService.getCurrentViewContext();
  }
}
