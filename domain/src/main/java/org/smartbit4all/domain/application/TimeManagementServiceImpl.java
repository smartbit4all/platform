package org.smartbit4all.domain.application;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * @author Peter Boros
 */
public class TimeManagementServiceImpl implements TimeManagementService {

  /**
   * The synchronized clock that holds the offset between the local JVM and the database time.
   */
  protected Clock synchronizedClock = Clock.systemDefaultZone();

  @Override
  public LocalDateTime getSystemTime() {
    return LocalDateTime.now(synchronizedClock);
  }

  @Override
  public Clock getSynchronizedClock() {
    return synchronizedClock;
  }

  @Override
  public void synchnronizeClock() {
    // The default implementation is empty, we use the JVM time.
  }

}
