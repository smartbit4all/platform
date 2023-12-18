package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public class MDMAdminPageWithValuesApiImpl extends MDMAdminPageApiImpl
    implements MDMAdminPageWithValuesApi {

  @Override
  protected void refreshUiActions(PageContext context) {
    super.refreshUiActions(context);
    context.view.getActions().add(
        0,
        new UiAction()
            .code(ACTION_OPEN_VALUES)
            .descriptor(getUiActionDescriptor(null,
                localeSettingApi.get(MasterDataManagementApi.SCHEMA, ACTION_OPEN_VALUES))));
  }

  @Override
  protected boolean filterDescriptor(MDMEntryDescriptor entryDescriptor) {
    return !Boolean.TRUE.equals(entryDescriptor.getIsValueSet());
  }

  @Override
  public void openValues(UUID viewUuid, UiActionRequest request) {
    PageContext ctx = getContextByViewUUID(viewUuid);
    String mdmDefinitionName = ctx.definition.getName();
    styleViewActions(ctx.view, ACTION_OPEN_VALUES);
    viewApi.showView(new View().viewName(getAdminValueViewName())
        .putParametersItem(MDMAdminPageApi.PARAM_MDM_DEFINITION, mdmDefinitionName));
  }

  protected String getAdminValueViewName() {
    return MDM_ADMIN_VALUES;
  }

}
