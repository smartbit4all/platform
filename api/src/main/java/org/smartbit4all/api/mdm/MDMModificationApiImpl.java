package org.smartbit4all.api.mdm;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.util.ObjectUtils;

public class MDMModificationApiImpl implements MDMModificationApi {

  private final MDMDefinition definition;

  private final MDMDefinitionState state;

  private final MDMModification modification;

  private boolean global = false;

  private final MasterDataManagementApi mdmApi;

  private final ObjectApi objectApi;

  private final SessionApi sessionApi;

  private final BranchApi branchApi;

  private final InvocationApi invocationApi;

  private final LocaleSettingApi localeSettingApi;

  private static class MDMDefitionStateWrapper {
    MDMDefinitionState currentState;
    URI prevState;

    public URI getCurrentStateUri() {
      return currentState.getUri();
    }

    public MDMDefitionStateWrapper(MDMDefinitionState currentState, URI prevState) {
      java.util.Objects.requireNonNull(currentState);
      this.currentState = currentState;
      this.prevState = prevState;
    }
  }

  MDMModificationApiImpl(MDMDefinition definition, MDMDefinitionState state,
      MDMModification modification, boolean global,
      MasterDataManagementApi mdmApi, ObjectApi objectApi, SessionApi sessionApi,
      BranchApi branchApi, InvocationApi invocationApi, LocaleSettingApi localeSettingApi) {
    super();
    this.definition = definition;
    this.state = state;
    this.modification = modification;
    this.global = global;
    this.mdmApi = mdmApi;
    this.objectApi = objectApi;
    this.sessionApi = sessionApi;
    this.branchApi = branchApi;
    this.invocationApi = invocationApi;
    this.localeSettingApi = localeSettingApi;
  }

  @Override
  public URI getBranch() {
    return modification.getBranchUri();
  }

  @Override
  public URI merge() {
    return mergeInner().getCurrentStateUri();
  }

  @Override
  public URI cancel() {
    return null;
  }

  @Override
  public void sendForApproval(URI approver) {
    // TODO Auto-generated method stub

  }

  @Override
  public void approvalAccepted() {
    // TODO Auto-generated method stub

  }

  @Override
  public void approvalRejected(String reason) {
    // TODO Auto-generated method stub

  }

  protected MDMDefitionStateWrapper mergeInner() {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definition.getName(), state -> {
      URI branch = getBranch();
      if (sessionApi != null) {
        UserActivityLog merged = sessionApi.createActivityLog();
        mdmApi.getDefinition(definition.getName()).getDescriptors().keySet().stream()
            .map(descriptorName -> mdmApi.getApi(definition.getName(), descriptorName, branch))
            .filter(entryApi -> entryApi.getBranchingList().stream()
                .anyMatch(e -> e.getBranchingState() != BranchingStateEnum.NOP))
            .forEach(entryApi -> entryApi.setBranchedEntriesMerged(merged));
      }

      branchApi.merge(branch);
      return state
          .globalModification(null);
    }, state -> noBranchValidation(state));
    fireModificationEvent(MasterDataManagementApi.MODIFICATION_FINALIZED, null,
        mdmApi.getDefinition(definition.getName()).getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState);
    MDMDefinitionState state =
        objectApi.load(stateWrapper.prevState).getObject(MDMDefinitionState.class);
    Map<String, MDMEntryDescriptor> descriptors = state.getGlobalModification().getDescriptors();
    if (!ObjectUtils.isEmpty(descriptors)) {
      MDMDefinitionOption option =
          new MDMDefinitionOption(mdmApi.getDefinition(definition.getName()));
      descriptors.entrySet()
          .forEach(
              entry -> mdmApi.addDescriptorToDefinition(option.getDefinition(), entry.getValue()));
      mdmApi.addNewEntries(option, null);
    }

    return stateWrapper;
  }

  @SafeVarargs
  final MDMDefitionStateWrapper modifyDefinitionState(String definitionName,
      UnaryOperator<MDMDefinitionState> modification, Consumer<MDMDefinitionState>... validations) {
    MDMDefinition definition = mdmApi.getDefinition(definitionName);
    Lock lock = objectApi.getLock(definition.getUri());
    lock.lock();
    try {
      URI stateUri =
          objectApi.loadLatest(definition.getUri()).ref(MDMDefinition.STATE).getObjectUri();
      ObjectNode stateNode = objectApi.loadLatest(stateUri);
      MDMDefinitionState state = stateNode.getObject(MDMDefinitionState.class);
      if (validations != null) {
        for (int i = 0; i < validations.length; i++) {
          validations[i].accept(state);
        }
      }
      stateNode.modify(MDMDefinitionState.class,
          modification);
      objectApi.save(stateNode);
      return new MDMDefitionStateWrapper(stateNode.getObject(MDMDefinitionState.class),
          stateNode.getObjectUri());
    } finally {
      lock.unlock();
    }
  }

  void fireModificationEvent(String event, String scope, URI definition, URI state,
      URI prevState) {
    invocationApi
        .publisher(
            MasterDataManagementApi.class,
            MDMSubscriberApi.class,
            MasterDataManagementApi.STATE_CHANGED)
        .publish(api -> api.stateChanged(event, scope, definition, state, prevState));
  }

  private void noBranchValidation(MDMDefinitionState pState) {
    if (global && pState.getGlobalModification() == null) {
      throw new IllegalStateException(MessageFormat.format(
          localeSettingApi.get("mdm.globalbranch.empty"),
          definition.getName()));
    }
  }

}
