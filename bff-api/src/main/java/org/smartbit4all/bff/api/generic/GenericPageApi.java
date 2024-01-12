package org.smartbit4all.bff.api.generic;

import java.util.UUID;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewEventHandler;
import org.smartbit4all.bff.api.config.PlatformViewNames;

/**
 * The GenericPageApi doesn't have any specific action, it's actions are handled in via
 * {@link ViewEventHandler}s in {@link View#EVENT_HANDLERS}.
 *
 * This pageApi is always present and registered.
 *
 */
@ViewApi(PlatformViewNames.GENERIC_PAGE)
public interface GenericPageApi extends PageApi<Object> {

  static final String PARAM_MODEL = "PARAM_MODEL";

  static final String ACTION_CLOSE_VIEW = "CLOSE_VIEW";

  @ActionHandler(ACTION_CLOSE_VIEW)
  void closeView(UUID viewUuid, UiActionRequest request);

}
