package org.smartbit4all.api.view;

import org.smartbit4all.api.view.bean.ViewContext;

public interface ViewContextExtensionApi {

  boolean isSessionExpired(ViewContext viewContext);
}
