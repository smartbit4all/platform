package org.smartbit4all.bff.api.mdm;

import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;

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
  protected UiActionDescriptor getUiActionDescriptor(MDMEntryDescriptor e) {
    return super.getUiActionDescriptor(e)
        .type(UiActionButtonType.RAISED);
  }

}
