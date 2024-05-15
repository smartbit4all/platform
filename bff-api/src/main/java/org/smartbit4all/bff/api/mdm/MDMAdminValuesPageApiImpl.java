package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.MDMConstants;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewType;

public class MDMAdminValuesPageApiImpl extends MDMAdminPageApiImpl
    implements MDMAdminValuesPageApi {

  @Override
  protected boolean filterDescriptor(MDMEntryDescriptor entryDescriptor) {
    return Boolean.TRUE.equals(entryDescriptor.getIsValueSet());
  }

  @Override
  protected String getListViewName() {
    return MDM_LIST_VALUES;
  }

  @Override
  protected UiActionDescriptor getUiActionDescriptor(MDMEntryDescriptor e, String title) {
    return super.getUiActionDescriptor(e, title)
        .color(UiActions.Color.SECONDARY)
        .type(UiActionButtonType.RAISED);
  }

  @Override
  protected void styleAction(UiAction action, boolean isCurrentSelection) {
    if (!ACTION_ADD_NEW_ENTRY.equals(action.getCode())) {
      action.getDescriptor()
          .color(isCurrentSelection ? UiActions.Color.PRIMARY : UiActions.Color.SECONDARY);
    }
  }

  @Override
  protected void refreshUiActions(PageContext ctx) {
    super.refreshUiActions(ctx);
    // Add a new value action as last.
    if (ctx.checkAdmin() && ctx.getModificationApi() != null) {
      ctx.view.addActionsItem(new UiAction()
          .code(ACTION_ADD_NEW_ENTRY)
          .descriptor(new UiActionDescriptor()
              .title(localeSettingApi.get(MasterDataManagementApi.SCHEMA, ACTION_ADD_NEW_ENTRY))
              .color(UiActions.Color.ACCENT)
              .type(UiActionButtonType.RAISED)));
    }
    UiActions.remove(ctx.view, ACTION_OPEN_MDM_CHANGES, ACTION_OPEN_MDM_SESSIONS);
  }

  @Override
  public void performNewEntry(UUID viewUuid, UiActionRequest request) {
    InvocationRequest refreshActions =
        invocationApi.builder(MDMAdminValuesPageApi.class)
            .build(api -> api.refreshUiActions(viewUuid));
    PageContext context = getContextByViewUUID(viewUuid);
    MDMDefinition mdmDefinition = context.definition;
    viewApi.showView(
        new View().viewName(MDMConstants.MDM_ENTRY_DESCRIPTOR).type(ViewType.DIALOG)
            .putParametersItem(MDMEntryDescriptorPageApi.PARAM_MDM_DEFINITION,
                mdmDefinition.getName())
            .putCallbacksItem(MDMEntryDescriptorPageApi.CALLBACK_REFRESH_ACTIONS, refreshActions));
  }

  @Override
  public void refreshUiActions(UUID viewUuid) {
    PageContext refreshedContext = getContextByViewUUID(viewUuid);
    refreshUiActions(refreshedContext);
  }

}
