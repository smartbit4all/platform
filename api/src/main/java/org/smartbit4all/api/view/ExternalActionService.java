package org.smartbit4all.api.view;

import java.util.UUID;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public interface ExternalActionService {

  enum ActionExecutionResult {
    OK, UNKNOWN, FAIL, REFRESH_REQUIRED
  }

  /**
   * Injects externally configured actions to a live view.
   * 
   * @param view the {@link View} to add actions to
   * @param model the initial data model of the view, assembled in {@code initModel(View)}
   */
  void initActions(View view, Object model);

  /**
   * Performs an externally configured action.
   * 
   * @param <M> the type parameter describing the corresponding PageApi's data model class
   * @param viewUuid the {@link UUID} of the {@link View} to perform the action on, not null
   * @param request the {@link UiActionRequest} to satisfy, not null
   * @param modelClass the {@link Class} of the {@link View}'s data model, not null
   * @return true if the action was performed, false if it failed
   */
  <M> ActionExecutionResult performAction(UUID viewUuid, UiActionRequest request,
      Class<M> modelClass);

}
