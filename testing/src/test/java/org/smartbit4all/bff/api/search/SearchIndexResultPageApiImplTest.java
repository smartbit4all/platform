package org.smartbit4all.bff.api.search;

import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.testing.mdm.MDMApiTestConfig;

@ViewApi(value = MDMApiTestConfig.SEARCHINDEX_LIST_PAGE, parent = MDMApiTestConfig.MDM_MAIN_PAGE,
    keepModelOnImplicitClose = true)
public class SearchIndexResultPageApiImplTest extends SearchIndexResultPageApiImpl {

}
