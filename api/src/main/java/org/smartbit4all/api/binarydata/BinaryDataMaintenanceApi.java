package org.smartbit4all.api.binarydata;

/**
 * The scheduled maintenances of the {@link BinaryData} objects.
 * 
 * @author Peter Boros
 */
public interface BinaryDataMaintenanceApi {

  /**
   * The scheduled purge operation.
   */
  public void purge();

  /**
   * The same as {@link #purge()} but at the moment before shutdown.
   */
  public void purgeBeforeShutdown();

}
