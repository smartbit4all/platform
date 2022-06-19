package org.smartbit4all.api.navigation.restserver.impl;

import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.restserver.NavigationApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;

public class NavigationApiDelegateImpl implements NavigationApiDelegate {

  @Autowired
  private NavigationApi navigationApi;


}
