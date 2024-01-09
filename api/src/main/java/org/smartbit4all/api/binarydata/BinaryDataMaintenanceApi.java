package org.smartbit4all.api.binarydata;

/**
 * The scheduled maintenances of the {@link BinaryData} objects.
 *
 * @author Peter Boros
 */
public interface BinaryDataMaintenanceApi {

  /**
   * Register JVM shutdown hook to notify BinaryData that shutdown is in progress. This is to ensure
   * that we don't delete temp files during shutdown.
   */
  void registerJVMShutdownHook();

  /**
   * The scheduled purge operation.
   */
  public void purge();

  /**
   * The same as {@link #purge()} but at the moment before shutdown.
   */
  public void purgeBeforeShutdown();

}
