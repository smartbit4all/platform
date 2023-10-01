package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public class MDMAdminPageWithValuesApiImpl extends MDMAdminPageApiImpl
    implements MDMAdminPageWithValuesApi {

  @Override
  public Object initModel(View view) {
    Object model = super.initModel(view);
    view.getActions().add(0, new UiAction().code(ACTION_OPEN_VALUES));
    return model;
  }

  @Override
  protected boolean filterDescriptor(MDMEntryDescriptor entryDescriptor) {
    return !Boolean.TRUE.equals(entryDescriptor.getIsValueSet());
  }

  @Override
  public void openValues(UUID viewUuid, UiActionRequest request) {
    String mdmDefinitionName = getContextByViewUUID(viewUuid).definition.getName();
    viewApi.showView(new View().viewName(getAdminValueViewName())
        .putParametersItem(MDMAdminPageApi.PARAM_MDM_DEFINITION, mdmDefinitionName));
  }

  protected String getAdminValueViewName() {
    return MDM_ADMIN_VALUES;
  }

}
