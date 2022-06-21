package org.smartbit4all.api.session.restserver.impl;

import org.smartbit4all.api.session.Session;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.api.session.bean.StartSessionResult;
import org.smartbit4all.api.session.restserver.SessionApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class SessionApiDelegateImpl implements SessionApiDelegate {

  @Autowired
  UserSessionApi userSessionApi;
  
  @Override
  public ResponseEntity<StartSessionResult> startSession() throws Exception {
    Session session = userSessionApi.startSession(null);
    
    return SessionApiDelegate.super.startSession();
  }
  
}
