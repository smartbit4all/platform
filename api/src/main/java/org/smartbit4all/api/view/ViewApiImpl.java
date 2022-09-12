package org.smartbit4all.api.view;

import java.util.Objects;
import java.util.UUID;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.ViewData;
import org.smartbit4all.api.view.bean.ViewState;
import org.smartbit4all.api.view.bean.ViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class ViewApiImpl implements ViewApi {

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
    viewContextService.updateCurrentViewContext(c -> c.addViewsItem(view));
    return view.getUuid();
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
    return viewContextService.getCurrentViewContext().getViews().stream()
        .filter(v -> viewUuid.equals(v.getUuid()))
        .findFirst()
        .orElse(null);
  }

  @Override
  public UUID showMessage(MessageData message) {
    Objects.requireNonNull(message, "View must be not null");
    message.setUuid(UUID.randomUUID());
    return showViewInternal(new ViewData()
        .uuid(message.getUuid())
        .viewName(messageViewName)
        .type(ViewType.DIALOG)
        .putParametersItem(MESSAGE_DATA, message));
  }

  @Override
  public void closeMessage(UUID messageUuid) {
    closeView(messageUuid);
  }

}
