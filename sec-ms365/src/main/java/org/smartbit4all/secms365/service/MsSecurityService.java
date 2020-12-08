package org.smartbit4all.secms365.service;

import org.smartbit4all.sec.service.SecurityService;

public interface MsSecurityService extends SecurityService {

  String getCurrentUserId();
  
}
