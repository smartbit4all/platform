package org.smartbit4all.bff.api.mdm;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.mdm.MDMApprovalApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.UiActions.UiActionBuilder;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.bff.api.mdm.utility.MDMActions;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class MDMEntryChangesPageApiImpl extends PageApiImpl<Object>
    implements MDMEntryChangesPageApi {

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private SessionApi sessionApi;

  @Autowired(required = false)
  private MDMApprovalApi mdmApprovalApi;

  /**
   * The page context is a useful object to encapsulate all the parameters necessary to execute the
   * actions of the page.
   */
  protected class PageContext {

    View view;
    public MDMDefinition definition;

    PageContext loadByView() {
      ObjectMapHelper parameters = parameters(view);
      definition = masterDataManagementApi.getDefinition(getDefinition(parameters));

      return this;
    }

    private final String getDefinition(ObjectMapHelper parameters) {
      return parameters.require(PARAM_MDM_DEFINITION, String.class);
    }

    public boolean checkAdmin() {
      return OrgUtils.securityPredicate(sessionApi, definition.getAdminGroupName());
    }

    public boolean getBranchActive() {
      ObjectNode definitionNode = objectApi.loadLatest(definition.getUri());
      ObjectNode defitionState = definitionNode.ref(MDMDefinition.STATE).get();
      MDMDefinitionState mdmDefinitionState = defitionState.getObject(MDMDefinitionState.class);
      return mdmDefinitionState.getGlobalModification() != null;
    }

    public URI getApprover() {
      ObjectNode definitionNode = objectApi.loadLatest(definition.getUri());
      ObjectNode defitionState = definitionNode.ref(MDMDefinition.STATE).get();
      MDMDefinitionState mdmDefinitionState = defitionState.getObject(MDMDefinitionState.class);
      if (mdmDefinitionState.getGlobalModification() != null) {
        return mdmDefinitionState.getGlobalModification().getApprover();
      } else {
        return null;
      }

    }

    public View getView() {
      return view;
    }

  }

  public MDMEntryChangesPageApiImpl() {
    super(Object.class);
  }

  @Override
  public Object initModel(View view) {
    PageContext pageContext = new PageContext();
    pageContext.view = view;
    pageContext.loadByView();
    refreshActions(pageContext);

    return new HashMap<String, Object>();
  }

  protected void refreshActions(PageContext ctx) {
    if (ctx.definition.getBranchingStrategy() == MDMBranchingStrategy.GLOBAL) {
      boolean isAdmin = ctx.checkAdmin();
      boolean branchActive = ctx.getBranchActive();
      // if API present, approving enabled
      boolean approvingEnabled = mdmApprovalApi != null;
      UiActionBuilder uiActions = UiActions.builder();
      if (approvingEnabled) {
        URI approver = ctx.getApprover();
        boolean underApproval = approver != null;
        boolean isApprover = approver != null && approver.equals(sessionApi.getUserUri());
        boolean canEdit = canEdit(isAdmin, underApproval, isApprover);

        uiActions
            .addIf(new UiAction().code(MDMActions.ACTION_START_EDITING).confirm(true),
                isAdmin, !branchActive)
            .addIf(new UiAction().code(MDMActions.ACTION_CANCEL_CHANGES).confirm(true),
                canEdit, branchActive)
            .addIf(new UiAction().code(MDMActions.ACTION_SEND_FOR_APPROVAL).confirm(true),
                isAdmin, !underApproval, branchActive)
            .addIf(new UiAction().code(MDMActions.ACTION_ADMIN_APPROVE_OK).confirm(true),
                isApprover, underApproval, branchActive)
            .addIf(
                new UiAction().code(MDMActions.ACTION_ADMIN_APPROVE_NOT_OK).confirm(true)
                    .input2Type(UiActionInputType.TEXTAREA),
                isApprover, underApproval, branchActive);
      } else {
        uiActions
            .addIf(MDMActions.ACTION_START_EDITING, isAdmin, !branchActive)
            .addIf(MDMActions.ACTION_FINALIZE_CHANGES, isAdmin, branchActive)
            .addIf(MDMActions.ACTION_CANCEL_CHANGES, isAdmin, branchActive);
      }

      ctx.view.actions(uiActions.build());
    }
  }

  protected boolean canEdit(boolean isAdmin, boolean underApproval, boolean isApprover) {
    return (isAdmin && !underApproval) || (isApprover && underApproval);
  }

  @Override
  public void startEditing(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    masterDataManagementApi.initiateGlobalBranch(context.definition.getName(),
        String.valueOf(System.currentTimeMillis()));
    refreshActions(context);
  }

  protected PageContext getContextByViewUUID(UUID viewUuid) {
    PageContext result = new PageContext();
    result.view = viewApi.getView(viewUuid);
    return result.loadByView();
  }

  @Override
  public void cancelChanges(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    masterDataManagementApi.dropGlobal(context.definition.getName());
    refreshActions(context);
  }

  @Override
  public void finalizeChanges(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    masterDataManagementApi.mergeGlobal(context.definition.getName());
    refreshActions(context);
  }

  @Override
  public void sendForApproval(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    if (mdmApprovalApi == null) {
      throw new IllegalStateException("Az admin jóváhagyó nem elérhető!");
    }
    String definition = context.definition.getName();
    List<URI> approvers = mdmApprovalApi.getApprovers(definition);
    if (approvers == null || approvers.size() != 1) {
      throw new IllegalStateException("Az admin jóváhagyó nincs beállítva!");
    }
    masterDataManagementApi.sendForApprovalGlobal(
        context.definition.getName(),
        approvers.get(0));
    refreshActions(context);
  }

  @Override
  public void adminApproveOk(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    masterDataManagementApi.approvalAcceptedGlobal(context.definition.getName());
    refreshActions(context);
  }

  @Override
  public void adminApproveNotOk(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    // TODO send and store reason
    masterDataManagementApi.approvalRejectedGlobal(context.definition.getName());
    refreshActions(context);
  }

}
