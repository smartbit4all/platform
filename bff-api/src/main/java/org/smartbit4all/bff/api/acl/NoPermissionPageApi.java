package org.smartbit4all.bff.api.acl;

import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.object.bean.ObjectContainer;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ViewApi;

@ViewApi(value = PlatformViewNames.NO_PERRMISSION_PAGE_NAME)
public interface NoPermissionPageApi extends PageApi<ObjectContainer> {

}
