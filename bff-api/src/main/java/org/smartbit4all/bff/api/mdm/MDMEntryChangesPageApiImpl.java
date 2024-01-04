package org.smartbit4all.bff.api.mdm;

import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.View;

public class MDMEntryChangesPageApiImpl extends PageApiImpl<Object>
    implements MDMEntryChangesPageApi {

  // @Autowired
  // private MasterDataManagementApi masterDataManagementApi;
  //
  // @Autowired
  // private SessionApi sessionApi;
  //
  // @Autowired(required = false)
  // private MDMApprovalApi mdmApprovalApi;
  //
  // /**
  // * The page context is a useful object to encapsulate all the parameters necessary to execute
  // the
  // * actions of the page.
  // */
  // protected class PageContext {
  //
  // View view;
  // public MDMDefinition definition;
  // public boolean branchActive;
  // public URI mdmApprover;
  //
  // PageContext loadByView() {
  // definition = masterDataManagementApi.getDefinition(getDefinition(view));
  //
  // ObjectNode definitionNode = objectApi.loadLatest(definition.getUri());
  // ObjectNode defitionState = definitionNode.ref(MDMDefinition.STATE).get();
  // MDMDefinitionState mdmDefinitionState = defitionState.getObject(MDMDefinitionState.class);
  // branchActive = mdmDefinitionState.getGlobalModification() != null;
  // if (branchActive) {
  // mdmApprover = mdmDefinitionState.getGlobalModification().getApprover();
  // }
  //
  // return this;
  // }
  //
  // private final String getDefinition(View view) {
  // return extractParam(String.class, PARAM_MDM_DEFINITION, view.getParameters());
  // }
  //
  // public boolean checkAdmin() {
  // return OrgUtils.securityPredicate(sessionApi, definition.getAdminGroupName());
  // }
  //
  // public View getView() {
  // return view;
  // }
  //
  // }

  public MDMEntryChangesPageApiImpl() {
    super(Object.class);
  }

  @Override
  public Object initModel(View view) {
    // PageContext pageContext = new PageContext();
    // pageContext.view = view;
    // pageContext.loadByView();
    // refreshActions(pageContext);

    return new Object();
  }

  // protected void refreshActions(PageContext ctx) {
  // if (ctx.definition.getBranchingStrategy() == MDMBranchingStrategy.GLOBAL) {
  // boolean isAdmin = ctx.checkAdmin();
  // boolean branchActive = ctx.branchActive;
  // // if API present, approving enabled
  // boolean approvingEnabled = mdmApprovalApi != null;
  // UiActionBuilder uiActions = UiActions.builder();
  // if (approvingEnabled) {
  // URI approver = ctx.mdmApprover;
  // boolean underApproval = approver != null;
  // boolean isApprover = approver != null && approver.equals(sessionApi.getUserUri());
  // boolean canEdit = canEdit(isAdmin, underApproval, isApprover);
  //
  // uiActions
  // .addIf(new UiAction().code(MDMActions.ACTION_START_EDITING).confirm(true),
  // isAdmin, !branchActive)
  // .addIf(new UiAction().code(MDMActions.ACTION_CANCEL_CHANGES).confirm(true),
  // canEdit, branchActive)
  // .addIf(new UiAction().code(MDMActions.ACTION_SEND_FOR_APPROVAL).confirm(true),
  // isAdmin, !underApproval, branchActive)
  // .addIf(new UiAction().code(MDMActions.ACTION_ADMIN_APPROVE_OK).confirm(true),
  // isApprover, underApproval, branchActive)
  // .addIf(
  // new UiAction().code(MDMActions.ACTION_ADMIN_APPROVE_NOT_OK).confirm(true)
  // .input2Type(UiActionInputType.TEXTAREA),
  // isApprover, underApproval, branchActive);
  // } else {
  // uiActions
  // .addIf(MDMActions.ACTION_START_EDITING, isAdmin, !branchActive)
  // .addIf(MDMActions.ACTION_FINALIZE_CHANGES, isAdmin, branchActive)
  // .addIf(MDMActions.ACTION_CANCEL_CHANGES, isAdmin, branchActive);
  // }
  //
  // ctx.view.actions(uiActions.build());
  // }
  // }
  //
  // protected boolean canEdit(boolean isAdmin, boolean underApproval, boolean isApprover) {
  // return (isAdmin && !underApproval) || (isApprover && underApproval);
  // }
  //
  // @Override
  // public void startEditing(UUID viewUuid, UiActionRequest request) {
  // PageContext context = getContextByViewUUID(viewUuid);
  // masterDataManagementApi.initiateGlobalBranch(context.definition.getName(),
  // String.valueOf(System.currentTimeMillis()));
  // refreshActions(context);
  // }
  //
  // protected PageContext getContextByViewUUID(UUID viewUuid) {
  // PageContext result = new PageContext();
  // result.view = viewApi.getView(viewUuid);
  // return result.loadByView();
  // }

}
