package org.smartbit4all.domain.application;

import java.util.UUID;
import org.smartbit4all.api.invocation.bean.ApplicationRuntimeData;

/**
 * An application runtime is running on a machine with an ip address and serving the incoming
 * requests on a port.
 * 
 * @author Peter Boros
 */
public class ApplicationRuntime {

  private ApplicationRuntimeData data;

  /**
   * Constructs a runtime instance based on the data.
   * 
   * @param data
   */
  public ApplicationRuntime(ApplicationRuntimeData data) {
    super();
    this.data = data;
  }

  /**
   * To have a synchronized time management the implementation must identify the offset of the given
   * runtime from the official time. It can be used to create official time stamps. This is the
   * number that must be used as a correction on the Java runtime current time to have an official
   * time.
   */
  private long timeOffset;

  /**
   * @return The unique identifier of the application instance.
   */
  public final UUID getUuid() {
    return data.getUuid();
  }

  /**
   * @return The ip address of the given application instance.
   */
  public final String getIpAddress() {
    return data.getIpAddress();
  }

  /**
   * @return The port for the incoming service requests.
   */
  public final int getServerPort() {
    return data.getServerPort();
  }

  /**
   * @return The offset for the java runtime current time to have the official. Official time =
   *         {@link System#currentTimeMillis()} + offset.
   */
  public final long getTimeOffset() {
    return timeOffset;
  }

  final void setTimeOffset(long timeOffset) {
    this.timeOffset = timeOffset;
  }

  public final long getStartupTime() {
    return data.getStartupTime();
  }

  public final long getStopTime() {
    return data.getStopTime();
  }

  public final long getLastTouchTime() {
    return data.getLastTouchTime();
  }

  final ApplicationRuntimeData getData() {
    return data;
  }

}
