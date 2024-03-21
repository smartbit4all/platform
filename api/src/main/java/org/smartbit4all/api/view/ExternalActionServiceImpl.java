package org.smartbit4all.api.view;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.toolbar.bean.ActionDefinition;
import org.smartbit4all.api.toolbar.bean.ViewEvaluationContext;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

public class ExternalActionServiceImpl implements ExternalActionService {

  private static final Logger log = LoggerFactory.getLogger(ExternalActionServiceImpl.class);

  private static final String VAR_ACTION_DEFINITIONS = "v-action-definitions";

  @Autowired
  private ActionDefinitionApi actionDefinitionApi;
  @Autowired(required = false)
  private SessionApi sessionApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;
  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private ViewApi viewApi;
  @Autowired
  private InvocationApi invocationApi;

  @Override
  public void initActions(View view, Object model) {
    final List<ActionDefinition> actionDefinitions = actionDefinitionApi
        .getActionsForView(new ViewEvaluationContext()
            .view(view)
            .user(sessionApi == null ? null : sessionApi.getUserUri())
            .model(model));
    if (actionDefinitions.isEmpty()) {
      return;
    }

    final Map<String, URI> actionDefinitionsByUri = new HashMap<>();
    actionDefinitions.forEach(it -> {
      final UiAction action = ActionDefinitionApi.definitionAsAction(it, localeSettingApi);
      UiActions.add(view, action);
      actionDefinitionsByUri.put(action.getCode(), it.getUri());
    });
    view.putVariablesItem(VAR_ACTION_DEFINITIONS, actionDefinitionsByUri);
  }

  @Override
  public <M> boolean performAction(UUID viewUuid, UiActionRequest request, Class<M> modelClass) {
    final View view = viewApi.getView(viewUuid);
    final ActionDefinition actionDefinition = Optional
        .ofNullable(view.getVariables().get(VAR_ACTION_DEFINITIONS))
        .map(it -> (Map<String, ?>) it)
        .map(it -> objectApi.asMap(URI.class, it))
        .flatMap(it -> Optional.ofNullable(it.get(request.getCode())))
        .map(objectApi::loadLatest)
        .map(it -> it.getObject(ActionDefinition.class))
        .orElse(null);
    return (actionDefinition != null)
        && performActionInternal(viewUuid, request, modelClass, actionDefinition);
  }

  private <M> boolean performActionInternal(UUID viewUuid, UiActionRequest request,
      Class<M> modelClass,
      ActionDefinition actionDefinition) {
    final List<InvocationRequest> invocations = actionDefinition.getInvocations();
    if (invocations == null || invocations.isEmpty()) {
      return false;
    }

    ViewEvaluationContext context = new ViewEvaluationContext()
        .user(sessionApi.getUserUri())
        .model(viewApi.getModel(viewUuid, modelClass))
        .view(viewApi.getView(viewUuid));
    for (InvocationRequest invocation : invocations) {
      try {
        final InvocationParameter result = invocationApi.invoke(invocation, context, request);
        final Object value = result.getValue();
        context = objectApi.asType(ViewEvaluationContext.class, value);
      } catch (ApiNotFoundException e) {
        log.error(e.getMessage(), e);
        return false;
      }
    }

    viewApi.getView(viewUuid).setModel(objectApi.asType(modelClass, context.getModel()));
    return true;
  }

}
