package org.smartbit4all.bff.api.generic;

import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.object.bean.ObjectContainer;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ViewApi;

@ViewApi(value = PlatformViewNames.INVALID_SMARTLINK_PAGE_NAME)
public interface InvalidSmartLinkPageApi extends PageApi<ObjectContainer> {

}
