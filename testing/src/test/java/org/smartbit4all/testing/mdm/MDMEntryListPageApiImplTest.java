package org.smartbit4all.testing.mdm;

import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.bff.api.mdm.MDMEntryListPageApiImpl;

@ViewApi(value = MDMApiTestConfig.MDM_LIST_PAGE, parent = MDMApiTestConfig.MDM_MAIN_PAGE,
    keepModelOnImplicitClose = true)
public class MDMEntryListPageApiImplTest extends MDMEntryListPageApiImpl {

}
