package org.smartbit4all.api.mdm;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.MasterDataManagementApiImpl.MDMDefitionStateWrapper;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.mdm.bean.MDMModificationArchive;
import org.smartbit4all.api.mdm.bean.MDMModificationItem;
import org.smartbit4all.api.mdm.bean.MDMModificationItem.StateEnum;
import org.smartbit4all.api.mdm.bean.MDMModificationNote;
import org.smartbit4all.api.mdm.bean.MDMModificationState;
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
  public MDMModification getModification() {
    return modification;
  }

  @Override
  public URI merge() {
    return mergeInner().getCurrentStateUri();
  }

  @Override
  public URI cancel() {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definition.getName(),
        state -> {
          modification.state(MDMModificationState.DISPOSED);
          removeModification(state);
          this.definitionState = state;
          return state;
        },
        this::getBranchFromPrevState,
        this::noBranchValidation);
    fireModificationEvent(MasterDataManagementApi.MODIFICATION_CANCELLED, null,
        definition.getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState, stateWrapper.branchUri);
    return stateWrapper.getCurrentStateUri();
  }

  @Override
  public void sendForApproval(URI approver) {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definition.getName(),
        state -> {
          MDMModification m = getModification(state);
          if (m != null) {
            UserActivityLog activityLog = sessionApi.createActivityLog();
            m
                .approver(approver)
                .sentToApproval(activityLog)
                .updated(activityLog);
            m.state(MDMModificationState.APPROVING);
            this.modification = m;
          }
          this.definitionState = state;
          return state;
        },
        this::getBranchFromPrevState,
        this::noBranchValidation,
        this::branchUnderApprovalValidation);
    fireModificationEvent(MasterDataManagementApi.MODIFICATION_SENT_FOR_APPROVAL, null,
        definition.getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState, stateWrapper.branchUri);
  }

  @Override
  public void approvalAccepted() {
    MDMDefitionStateWrapper stateWrapper = mergeInner();
    fireModificationEvent(MasterDataManagementApi.MODIFICATION_APPROVED, null,
        definition.getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState, stateWrapper.branchUri);
  }

  @Override
  public void approvalRejected(String reason) {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definition.getName(),
        state -> {
          MDMModification m = getModification(state);
          UserActivityLog activityLog = sessionApi.createActivityLog();
          m.updated(activityLog)
              .addNotesItem(new MDMModificationNote()
                  .created(activityLog)
                  .note(reason));
          m.approver(null);
          m.state(MDMModificationState.REJECTED);
          this.definitionState = state;
          return state;
        },
        this::getBranchFromPrevState,
        this::noBranchValidation);
    fireModificationEvent(MasterDataManagementApi.MODIFICATION_REJECTED, null, definition.getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState, stateWrapper.branchUri);
  }

  @Override
  public void addComment(String comment) {
    modifyDefinitionState(definition.getName(),
        state -> {
          MDMModification m = getModification(state);
          UserActivityLog activityLog = sessionApi.createActivityLog();
          m.addNotesItem(new MDMModificationNote()
              .created(activityLog)
              .note(comment));
          this.definitionState = state;
          return state;
        },
        this::getBranchFromPrevState);
  }

  @Override
  public void addComment(URI objectUri, String comment) {
    modifyDefinitionState(definition.getName(),
        state -> {
          MDMModification m = getModification(state);
          UserActivityLog activityLog = sessionApi.createActivityLog();
          MDMModificationNote note = new MDMModificationNote()
              .created(activityLog)
              .note(comment);

          if (m.getModificationItems() == null
              || !m.getModificationItems().containsKey(objectUri.toString())) {
            m.putModificationItemsItem(objectUri.toString(),
                new MDMModificationItem().objectUri(objectUri).addNotesItem(note));
          } else {
            m.getModificationItems().compute(objectUri.toString(),
                (key, v) -> v.addNotesItem(note));
          }

          this.definitionState = state;
          return state;
        },
        this::getBranchFromPrevState);
  }

  @Override
  public void updateItemState(URI objectUri, StateEnum itemState) {
    modifyDefinitionState(definition.getName(),
        state -> {

          MDMModification m = getModification(state);

          StateEnum prevState = null;
          if (m.getModificationItems() != null
              && m.getModificationItems().containsKey(objectUri.toString())) {
            prevState = m.getModificationItems().get(objectUri.toString()).getState();
          }
          if (itemState == prevState) {
            throw new IllegalArgumentException(
                localeSettingApi.get("mdm.modification.update.matchwithprevstate"));
          }
          if (MDMModificationState.APPROVING == m.getState()
              && (itemState != StateEnum.APPROVED && itemState != StateEnum.REJECTED)) {
            throw new IllegalArgumentException(
                localeSettingApi.get("mdm.modification.update.approveduringnotapprovingstate"));
          }
          if ((MDMModificationState.ACTIVE == m.getState()
              || MDMModificationState.REJECTED == m.getState()) && (itemState != StateEnum.FIXED)) {
            throw new IllegalArgumentException(
                localeSettingApi.get("mdm.modification.update.fixduringnotactivestate"));
          }
          if (MDMModificationState.DISPOSED == m.getState()
              || MDMModificationState.APPROVED == m.getState()) {
            throw new IllegalArgumentException(
                localeSettingApi.get("mdm.modification.update.finishedModification"));
          }

          if (m.getModificationItems() == null
              || !m.getModificationItems().containsKey(objectUri.toString())) {
            m.putModificationItemsItem(objectUri.toString(),
                new MDMModificationItem().state(itemState));
          } else {
            m.getModificationItems().compute(objectUri.toString(),
                (key, v) -> v.state(itemState));
          }

          this.definitionState = state;
          return state;
        },
        this::getBranchFromPrevState);
  }

  private final Stream<MDMModification> getAllModifications(MDMDefinitionState state) {
    return Stream
        .concat(state.getGlobalModification() != null ? Stream.of(state.getGlobalModification())
            : Stream.empty(), state.getActiveModifications().stream());
  }

  @Override
  public void startEditing() {
    URI userUri = sessionApi.getUserUri();
    modifyDefinitionState(definition.getName(),
        state -> {
          // Remove all other editing entry for the user.
          getAllModifications(state).forEach(
              m -> m.getCurrentEditors().removeIf(u -> objectApi.equalsIgnoreVersion(u, userUri)));
          MDMModification m = getModification(state);
          m.addCurrentEditorsItem(userUri);
          this.definitionState = state;
          return state;
        },
        this::getBranchFromPrevState);
  }

  @Override
  public void renameEditing(String name) {
    modifyDefinitionState(definition.getName(),
        state -> {
          MDMModification m = getModification(state);
          m.name(name);
          return state;
        },
        this::getBranchFromPrevState,
        state -> {
          if (state.getActiveModifications().stream()
              .anyMatch(mod -> Objects.equals(name, mod.getName()))) {
            throw new IllegalStateException(MessageFormat.format(
                localeSettingApi.get("mdm.branch.alreadyexists"),
                name));
          }
        });
  }

  @Override
  public void stopEditing() {
    modifyDefinitionState(definition.getName(),
        state -> {
          MDMModification m = getModification(state);
          URI userUri = objectApi.getLatestUri(sessionApi.getUserUri());
          m.getCurrentEditors().removeIf(u -> objectApi.equalsIgnoreVersion(u, userUri));
          this.definitionState = state;
          return state;
        },
        this::getBranchFromPrevState);
  }

  private final MDMModification getModification(MDMDefinitionState s) {
    MDMModification m;
    if (global) {
      m = s.getGlobalModification();
    } else {
      m = s.getActiveModifications().stream()
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
    archiveModification(state, modification);
  }

  // TODO duplication! MasterDataManagementApiImpl and MDMModificationApiImpl.archiveModification
  private void archiveModification(MDMDefinitionState state, MDMModification modification) {
    MDMModificationArchive archiveObject = new MDMModificationArchive()
        .modification(modification)
        .archival(sessionApi.createActivityLog());
    URI archiveUri = state.getArchive();
    if (archiveUri == null) {
      archiveUri = objectApi.saveAsNew(MasterDataManagementApi.SCHEMA, archiveObject);
    } else {
      ObjectNode archiveNode = objectApi.loadLatest(archiveUri);
      archiveNode.modify(MDMModificationArchive.class, arch -> archiveObject);
      archiveUri = objectApi.save(archiveNode);
    }
    state.archive(archiveUri);
  }

  private final MDMDefitionStateWrapper mergeInner() {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definition.getName(),
        state -> {
          URI branch = getModification().getBranchUri();
          if (sessionApi != null) {
            UserActivityLog merged = sessionApi.createActivityLog();
            mdmApi.getDefinition(definition.getName()).getDescriptors().keySet().stream()
                .map(descriptorName -> mdmApi.getApi(definition.getName(), descriptorName, branch))
                .filter(entryApi -> entryApi.getBranchingList().stream()
                    .anyMatch(e -> e.getBranchingState() != BranchingStateEnum.NOP))
                .forEach(entryApi -> entryApi.setBranchedEntriesMerged(merged));
          }
          branchApi.merge(branch);
          modification
              .state(MDMModificationState.APPROVED)
              .approved(sessionApi.createActivityLog());
          removeModification(state);
          this.definitionState = state;
          return state;
        },
        this::getBranchFromPrevState,
        this::noBranchValidation, this::hasRejectedItemValidation);
    fireModificationEvent(MasterDataManagementApi.MODIFICATION_FINALIZED, null,
        mdmApi.getDefinition(definition.getName()).getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState, stateWrapper.branchUri);
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
      UnaryOperator<MDMDefinitionState> modification,
      BiFunction<MDMDefinitionState, MDMDefinitionState, URI> branchUriProducer,
      Consumer<MDMDefinitionState>... validations) {
    MDMDefinition definition = mdmApi.getDefinition(definitionName);
    Lock lock = objectApi.getLock(definition.getUri());
    lock.lock();
    try {
      URI stateUri =
          objectApi.loadLatest(definition.getUri()).ref(MDMDefinition.STATE).getObjectUri();
      ObjectNode stateNode = objectApi.loadLatest(stateUri);
      URI prevStateUri = stateNode.getObjectUri();
      ObjectNode prevStateNode = objectApi.load(prevStateUri);
      MDMDefinitionState state = stateNode.getObject(MDMDefinitionState.class);
      if (validations != null) {
        for (int i = 0; i < validations.length; i++) {
          validations[i].accept(state);
        }
      }
      stateNode.modify(MDMDefinitionState.class,
          modification);
      objectApi.save(stateNode);
      MDMDefinitionState currentState = stateNode.getObject(MDMDefinitionState.class);
      MDMDefinitionState prevState = prevStateNode.getObject(MDMDefinitionState.class);
      URI branchUri = branchUriProducer.apply(prevState, currentState);
      return new MDMDefitionStateWrapper(currentState, prevStateUri, branchUri);
    } finally {
      lock.unlock();
    }
  }

  void fireModificationEvent(String event, String scope, URI definition, URI state,
      URI prevState, URI branchUri) {
    invocationApi
        .publisher(
            MasterDataManagementApi.class,
            MDMSubscriberApi.class,
            MasterDataManagementApi.STATE_CHANGED)
        .publish(api -> api.stateChanged(event, scope, definition, state, prevState, branchUri));
  }

  private void noBranchValidation(MDMDefinitionState pState) {
    if (global && pState.getGlobalModification() == null) {
      throw new IllegalStateException(MessageFormat.format(
          localeSettingApi.get("mdm.globalbranch.empty"),
          definition.getName()));
    }
  }

  private void hasRejectedItemValidation(MDMDefinitionState pState) {
    MDMModification m = getModification(pState);
    if (m != null && m.getModificationItems() != null
        && m.getModificationItems().entrySet().stream()
            .anyMatch(e -> e.getValue().getState() == StateEnum.REJECTED)) {
      throw new IllegalStateException(
          localeSettingApi.get("mdm.hasRejectedmodificationitem"));
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

  private URI getBranchFromPrevState(MDMDefinitionState prevState, MDMDefinitionState currState) {
    MDMModification mod = getModification(prevState);
    return mod == null ? null : mod.getBranchUri();
  }
}
