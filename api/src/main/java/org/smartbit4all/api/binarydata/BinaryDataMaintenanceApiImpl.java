package org.smartbit4all.api.binarydata;

import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * The default implementation of the maintenance operations.
 *
 * @author Peter Boros
 */
public class BinaryDataMaintenanceApiImpl implements BinaryDataMaintenanceApi {

  private static final Logger log = LoggerFactory.getLogger(BinaryDataMaintenanceApiImpl.class);


  private static final Thread shutdownHook = new Thread(() -> {
    BinaryData.isJVMShutdownInProgress = true;
    log.info("JVM shutdown signal received");
  });

  @Override
  @EventListener(ApplicationReadyEvent.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public void registerJVMShutdownHook() {
    Runtime.getRuntime().addShutdownHook(shutdownHook);
    log.info("JVM shutdown hook registered");
  }

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
