package org.smartbit4all.api.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.toolbar.bean.ActionDefinition;
import org.smartbit4all.api.toolbar.bean.ViewEvaluationContext;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class ActionDefinitionApiImpl implements ActionDefinitionApi {

  private static final Logger log = LoggerFactory.getLogger(ActionDefinitionApiImpl.class);

  /**
   * All the action providers available in the current runtime. As a result all these providers are
   * going to be saved into the registry of the action definitions.
   */
  @Autowired(required = false)
  private List<ActionProviderApi> providers;

  @Autowired
  @Lazy
  private MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  @Lazy
  private InvocationApi invocationApi;

  @Override
  public ActionDefinition getAction(String qualifiedName) {
    return null;
  }

  private final void refreshCache() {

  }

  @Override
  public String getDefinitionToSetup() {
    return MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION;
  }

  @Override
  public List<String> getEntriesToSetup() {
    List<String> result = new ArrayList<>();
    result.add(MDM_ACTION_DEFINITIONS);
    return result;
  }

  @Override
  public void setupEntries(Map<String, MDMEntryApi> entries) {
    if (providers != null) {
      // Iterate on every provider to get the available actions and save them into the MDM for
      // further management.
      MDMEntryApi entryApi = entries.get(MDM_ACTION_DEFINITIONS);
      if (entryApi != null) {
        for (ActionProviderApi providerApi : providers) {
          List<ActionDefinition> actionDefinitions = providerApi.getActionDefinitions();
          for (ActionDefinition actionDefinition : actionDefinitions) {
            // TODO: Properly examine every incoming action definition!
            try {
              entryApi.updateList(SCHEMA, Arrays.asList(actionDefinition));
            } catch (Exception e) {
              log.error("Unable to setup the action definition entries.", e);
            }
          }
        }
      }
    }
  }

  @Override
  public List<ActionDefinition> getActionsForView(ViewEvaluationContext viewEvaluationContext) {
    final MDMEntryApi actionDefinitionApi = masterDataManagementApi.getApiSafe(
        MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
        ActionDefinitionApi.MDM_ACTION_DEFINITIONS);
    final Collection<ActionDefinition> actionsToRender = actionDefinitionApi.getList()
        .uris().stream()
        .map(objectApi::loadLatest)
        .map(it -> it.getObject(ActionDefinition.class))
        .filter(it -> canBePresentOnView(it, viewEvaluationContext))
        .collect(Collectors.toMap(
            it -> it.getAction().getCode(),
            Function.identity(),
            (a, b) -> {
              final int aPrecedence = unbox(a.getPrecedenceOrder(), 0);
              final int bPrecedence = unbox(b.getPrecedenceOrder(), 0);
              return ((aPrecedence - bPrecedence) > 0) ? b : a;
            }))
        .values();
    return new ArrayList<>(actionsToRender);
  }

  private boolean canBePresentOnView(ActionDefinition actionDefinition,
      ViewEvaluationContext viewEvaluationContext) {
    final List<InvocationRequest> contextChecks = actionDefinition.getContextChecks();
    if (contextChecks == null || contextChecks.isEmpty()) {
      return true;
    }

    for (InvocationRequest contextCheck : contextChecks) {
      final boolean passed = tryInvoke(contextCheck, viewEvaluationContext)
          .map(this::returnedTrue)
          .orElse(true); // if we didn't manage to invoke the check, we consider it passed.
      if (!passed) {
        return false;
      }
    }

    return true;
  }

  private Optional<InvocationParameter> tryInvoke(InvocationRequest req, Object... args) {
    try {
      return Optional.ofNullable(invocationApi.invoke(req, args));
    } catch (ApiNotFoundException e) {
      log.error(e.getMessage(), e);
      return Optional.empty();
    }
  }

  private boolean returnedTrue(InvocationParameter result) {
    if (result == null) {
      return false;
    }

    final Object value = result.getValue();
    return Boolean.TRUE.equals(value);
  }

  private int unbox(Integer i, int fallback) {
    return i == null ? fallback : i;
  }

}
