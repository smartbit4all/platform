package org.smartbit4all.domain.config;

import org.smartbit4all.core.SB4Module;
import org.smartbit4all.domain.application.TimeManagementService;
import org.smartbit4all.domain.service.transfer.TransferService;

/**
 * @author Peter Boros
 *
 */
public interface DomainAPI extends SB4Module {

  TimeManagementService timeManagementService();

  TransferService transferService();

  /**
   * To access the singleton instance configured by the Spring configurations.
   * 
   * @return
   */
  static DomainAPI get() {
    return DomainAPIImpl.instance;
  }

}
