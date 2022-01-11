package org.smartbit4all.ui.vaadin.object;

import org.smartbit4all.core.object.ObservablePublisherWrapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;

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

    try {
      ui.access(() -> {
        uiEventNotifications.run();
      });
    } catch (UIDetachedException e) {
      // do nothing, no UI probably closed
    }
  }

}
