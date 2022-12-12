package org.smartbit4all.api.view;

/**
 * A generic functional interface, similar to {@link Runnable} but can throw Exception.
 * 
 * @author matea
 *
 */
public interface ViewContextCommand {

  void execute() throws Exception;

}
