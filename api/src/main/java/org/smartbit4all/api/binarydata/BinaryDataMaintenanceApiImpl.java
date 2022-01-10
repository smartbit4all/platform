package org.smartbit4all.api.binarydata;

import javax.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * The default implementation of the maintenance operations.
 * 
 * @author Peter Boros
 */
public class BinaryDataMaintenanceApiImpl implements BinaryDataMaintenanceApi {

  @Override
  @Scheduled(fixedDelayString = "${binarydata.purge.fixeddelay:5000}")
  public void purge() {
    BinaryData.purgeDataFiles();
  }

  @Override
  @PreDestroy
  public void purgeBeforeShutdown() {
    BinaryData.purgeDataFiles();
  }
}
