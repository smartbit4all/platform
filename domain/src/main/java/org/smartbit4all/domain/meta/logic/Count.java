package org.smartbit4all.domain.meta.logic;

import org.smartbit4all.domain.meta.EventHandler;
import org.smartbit4all.domain.meta.PropertyComputed;

/**
 * The count is the classic count function that works at SQL level but also at in memory solutions.
 * The {@link PropertyComputed} property with this logic will contain the number of rows. This logic
 * is aware of the group by, if we have group by properties then the count will be calculated for
 * the groups formed by the similar values of these properties.
 * 
 * @author Peter Boros
 */
public interface Count extends EventHandler {

  /**
   * The constant for the naming.
   */
  public static String PROPERTTYNAME = "countRows";

}
