package org.smartbit4all.domain.config;

import org.smartbit4all.domain.application.TimeManagementService;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class DomainAPIImpl implements DomainAPI, InitializingBean {

  static DomainAPI instance;

  @Autowired
  TimeManagementService timeManagementService;

  @Autowired
  TransferService transferService;

  @Autowired
  ApplicationContext ctx;

  @Override
  public TimeManagementService timeManagementService() {
    return timeManagementService;
  }

  @Override
  public TransferService transferService() {
    return transferService;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    instance = ctx.getBean(DomainAPI.class);
  }

}
