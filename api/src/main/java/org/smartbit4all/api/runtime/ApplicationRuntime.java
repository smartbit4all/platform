package org.smartbit4all.api.runtime;

import java.util.UUID;

/**
 * An application runtime is running on a machine with an ip address and serving the incoming
 * requests on a port.
 * 
 * @author Peter Boros
 */
public class ApplicationRuntime {

  /**
   * The unique identifier of the application instance.
   */
  private UUID uuid;

  /**
   * The ip address of the given application instance.
   */
  private String ip;

  /**
   * The port for the incoming service requests.
   */
  private int port;

  /**
   * To have a synchronized time management the implementation must identify the offset of the given
   * runtime from the official time. It can be used to create official time stamps. This is the
   * number that must be used as a correction on the Java runtime current time to have an official
   * time.
   */
  private long timeOffset;

  /**
   * The startup GMT time of the runtime.
   */
  private long startupTime;

  /**
   * The stop GMT time of the runtime.
   */
  private long stopTime;

  /**
   * The last touch time of the runtime moved to our System time by the offset.
   */
  private long lastTouchTime;

  /**
   * @return The unique identifier of the application instance.
   */
  public final UUID getUuid() {
    return uuid;
  }

  /**
   * @return The ip address of the given application instance.
   */
  public final String getIp() {
    return ip;
  }

  /**
   * @return The port for the incoming service requests.
   */
  public final int getPort() {
    return port;
  }

  /**
   * @return The offset for the java runtime current time to have the official. Official time =
   *         {@link System#currentTimeMillis()} + offset.
   */
  public final long getTimeOffset() {
    return timeOffset;
  }

  final void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  final void setIp(String ip) {
    this.ip = ip;
  }

  final void setPort(int port) {
    this.port = port;
  }

  final void setTimeOffset(long timeOffset) {
    this.timeOffset = timeOffset;
  }

  public final long getStartupTime() {
    return startupTime;
  }

  final void setStartupTime(long startupTime) {
    this.startupTime = startupTime;
  }

  public final long getStopTime() {
    return stopTime;
  }

  final void setStopTime(long stopTime) {
    this.stopTime = stopTime;
  }

}
