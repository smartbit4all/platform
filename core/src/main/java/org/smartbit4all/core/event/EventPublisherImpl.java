package org.smartbit4all.core.event;

/**
 * The implementation interface for the publishers. Use this to enable automatic notification by
 * Proxys.
 * 
 * @author Peter Boros
 */
public interface EventPublisherImpl extends EventPublisher {

  /**
   * This function must be implemented in the publisher to be able to notify its listeners.
   */
  public abstract void notifyListeners();

}
