package org.smartbit4all.bff.api.mdm;

import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;

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

}
