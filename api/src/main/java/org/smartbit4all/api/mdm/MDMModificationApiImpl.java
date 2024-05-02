package org.smartbit4all.api.mdm;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.mdm.bean.MDMModificationNote;
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

  private MDMDefinitionState definitionState;

  private MDMModification modification;

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
    this.definitionState = state;
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
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definition.getName(), state -> {
      removeModification(state);
      this.definitionState = state;
      return state;
    }, this::noBranchValidation);
    fireModificationEvent(MasterDataManagementApi.MODIFICATION_CANCELLED, null,
        definition.getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState);
    return stateWrapper.getCurrentStateUri();
  }

  @Override
  public void sendForApproval(URI approver) {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definition.getName(), state -> {
      MDMModification m = getModification(state);
      if (m != null) {
        m.approver(approver).updated(sessionApi.createActivityLog());
        this.modification = m;
      }
      this.definitionState = state;
      return state;
    }, this::noBranchValidation,
        this::branchUnderApprovalValidation);
    fireModificationEvent(MasterDataManagementApi.MODIFICATION_SENT_FOR_APPROVAL, null,
        definition.getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState);
  }

  @Override
  public void approvalAccepted() {
    MDMDefitionStateWrapper stateWrapper = mergeInner();
    fireModificationEvent(MasterDataManagementApi.MODIFICATION_APPROVED, null,
        definition.getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState);
  }

  @Override
  public void approvalRejected(String reason) {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definition.getName(), state -> {
      MDMModification m = getModification(state);
      UserActivityLog activityLog = sessionApi.createActivityLog();
      m.updated(activityLog)
          .addNotesItem(new MDMModificationNote()
              .created(activityLog)
              .note(reason));
      m.approver(null);
      this.definitionState = state;
      return state;
    }, this::noBranchValidation);
    fireModificationEvent(MasterDataManagementApi.MODIFICATION_REJECTED, null, definition.getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState);
  }

  @Override
  public void addComment(String comment) {
    modifyDefinitionState(definition.getName(), state -> {
      MDMModification m = getModification(state);
      UserActivityLog activityLog = sessionApi.createActivityLog();
      m.addNotesItem(new MDMModificationNote()
          .created(activityLog)
          .note(comment));
      this.definitionState = state;
      return state;
    });
  }

  @Override
  public void startEditing() {
    modifyDefinitionState(definition.getName(), state -> {
      MDMModification m = getModification(state);
      m.addCurrentEditorsItem(sessionApi.getUserUri());
      this.definitionState = state;
      return state;
    });
  }

  @Override
  public void stopEditing() {
    modifyDefinitionState(definition.getName(), state -> {
      MDMModification m = getModification(state);
      URI userUri = objectApi.getLatestUri(sessionApi.getUserUri());
      m.getCurrentEditors().removeIf(u -> objectApi.equalsIgnoreVersion(u, userUri));
      this.definitionState = state;
      return state;
    });
  }

  private final MDMModification getModification(MDMDefinitionState s) {
    MDMModification m;
    if (global) {
      m = s.getGlobalModification();
    } else {
      m =
          s.getActiveModifications().stream()
              .filter(mod -> Objects.equals(modification.getId(), mod.getId()))
              .findFirst().orElse(null);
    }
    return m;
  }

  private final void removeModification(MDMDefinitionState state) {
    if (global) {
      state.globalModification(null);
    } else {
      state.getActiveModifications()
          .removeIf(m -> Objects.equals(modification.getId(), m.getId()));
    }
  }

  private final MDMDefitionStateWrapper mergeInner() {
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
      removeModification(state);
      this.definitionState = state;
      return state;
    }, this::noBranchValidation);
    fireModificationEvent(MasterDataManagementApi.MODIFICATION_FINALIZED, null,
        mdmApi.getDefinition(definition.getName()).getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState);
    MDMDefinitionState state =
        objectApi.load(stateWrapper.prevState).getObject(MDMDefinitionState.class);
    MDMModification m = getModification(state);
    Map<String, MDMEntryDescriptor> descriptors = m.getDescriptors();
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

  private void branchUnderApprovalValidation(MDMDefinitionState state) {
    if (state.getGlobalModification() != null
        && state.getGlobalModification().getApprover() != null) {
      throw new IllegalStateException(MessageFormat.format(
          localeSettingApi.get("mdm.globalbranch.underapproval"),
          definition.getName()));
    }
  }


}
