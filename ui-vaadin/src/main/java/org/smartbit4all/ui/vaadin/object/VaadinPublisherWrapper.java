package org.smartbit4all.ui.vaadin.object;

import org.smartbit4all.core.object.ObservablePublisherWrapper;
import com.vaadin.flow.component.UI;

/**
 * If we are not on Vaadin UI thread open one and execute the event notifications on this thread.
 * 
 * @author Zoltan Suller
 *
 */
public class VaadinPublisherWrapper implements ObservablePublisherWrapper {

  private UI ui;

  public VaadinPublisherWrapper(UI ui) {
    super();
    this.ui = ui;
  }

  @Override
  public void accept(Runnable uiEventNotifications) throws Throwable {

    if (UI.getCurrent() == null) {
      ui.access(() -> uiEventNotifications.run());
    } else {
      uiEventNotifications.run();
    }
  }

}
