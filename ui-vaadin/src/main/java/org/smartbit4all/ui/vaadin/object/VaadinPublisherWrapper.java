package org.smartbit4all.ui.vaadin.object;

import com.vaadin.flow.component.UI;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * If we are not on Vaadin UI thread open one and execute the event notifications on this thread.
 * 
 * @author Zoltan Suller
 *
 */
public class VaadinPublisherWrapper implements Consumer<Runnable> {

  private UI ui;

  VaadinPublisherWrapper(UI ui) {
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

  public static VaadinPublisherWrapper create() {
    return new VaadinPublisherWrapper(UI.getCurrent());
  }
}
