package org.smartbit4all.ui.vaadin.util;

import com.vaadin.flow.component.UI;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * If we are not on Vaadin UI thread open one and execute the event notifications on this thread.
 * 
 * @author Zoltan Suller
 *
 */
public class VaaadinPublisherWrapper implements Consumer<Runnable> {

  private UI ui;

  VaaadinPublisherWrapper(UI ui) {
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

  public static VaaadinPublisherWrapper create() {
    return new VaaadinPublisherWrapper(UI.getCurrent());
  }
}
