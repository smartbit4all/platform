package org.smartbit4all.ui.api.navigation.restserver.impl;

import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.restserver.ViewModelApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class ViewModelApiDelegateImpl implements ViewModelApiDelegate {

  @Autowired
  private UINavigationApi uiNavigationApi;

  @Override
  public ResponseEntity<NavigationTarget> createViewModel(NavigationTarget navigationTarget)
      throws Exception {

    uiNavigationApi.createView(navigationTarget, null);
    return ResponseEntity.ok(navigationTarget);
  }

}
