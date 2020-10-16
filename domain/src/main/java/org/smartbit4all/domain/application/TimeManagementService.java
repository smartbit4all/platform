package org.smartbit4all.domain.application;

import java.time.Clock;
import java.time.LocalDateTime;
import org.smartbit4all.core.SB4Service;

/**
 * The time management is responsible for managing the timing of the application and can access the
 * database time with the {@link #getSystemTime()} function.
 * 
 * @author Peter Boros
 */
public interface TimeManagementService extends SB4Service {

  /**
   * Returns the system time synchronized with the database.
   * 
   * @return
   */
  LocalDateTime getSystemTime();

  /**
   * @return The synchronized Clock in the application.
   */
  Clock getSynchronizedClock();

  /**
   * Do the synchronization of the system clock.
   */
  void synchnronizeClock();

}
