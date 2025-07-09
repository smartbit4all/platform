package org.smartbit4all.api.view;

import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.bean.ViewContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewContextExtensionNullUserApi implements ViewContextExtensionApi {

  @Autowired
  private SessionApi sessionApi;

  @Override
  public boolean isSessionExpired(ViewContext viewContext) {
    return sessionApi.getUserUri() == null;
  }

}
