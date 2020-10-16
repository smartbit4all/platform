package org.smartbit4all.domain.meta.logic;

import org.smartbit4all.domain.meta.EventHandler;
import org.smartbit4all.domain.meta.PropertyComputed;

/**
 * The classical max operation that works at SQL level. The {@link PropertyComputed} property with
 * this logic will contain the largest value. This logic is aware of the group by, if we have group
 * by properties then it will be calculated for the groups formed by the similar values of these
 * properties.
 * 
 * @author Peter Boros
 */
public interface Max extends EventHandler {

  /**
   * The constant for the naming.
   */
  public static String PROPERTTYNAME = "max";

}
