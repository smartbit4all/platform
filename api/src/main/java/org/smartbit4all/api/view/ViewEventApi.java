package org.smartbit4all.api.view;

import org.smartbit4all.api.view.bean.ViewEventHandler;

/**
 * 
 * The {@link ViewApi} provide this api object for a given view. We can use this to access the
 * currently registered {@link ViewEventHandler}s, modify them or fire events. This api object is
 * limited to use during a perform action.
 * 
 * @author Peter Boros
 */
public interface ViewEventApi {

  static final String WIDGET = "widget";

  static final String ACTION = "action";

  void add(ViewEventHandler eventHandler);

  ViewEventDescriptor get(String... path);

  boolean remove(ViewEventHandler eventHandler);

}
