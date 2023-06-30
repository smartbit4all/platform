package org.smartbit4all.api.view;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.view.bean.ViewEventHandler;

public class ViewEventDescriptor {

  private List<ViewEventHandler> beforeEvents = new ArrayList<>();

  private ViewEventHandler insteadOf;

  private List<ViewEventHandler> afterEvents = new ArrayList<>();

  public ViewEventDescriptor() {
    super();
  }

  public ViewEventDescriptor(List<ViewEventHandler> beforeEvents, ViewEventHandler insteadOf,
      List<ViewEventHandler> afterEvents) {
    this();
    this.beforeEvents.addAll(beforeEvents);
    this.insteadOf = insteadOf;
    this.afterEvents.addAll(afterEvents);
  }

  public final List<ViewEventHandler> getBeforeEvents() {
    return beforeEvents;
  }

  public final void setBeforeEvents(List<ViewEventHandler> beforeEvents) {
    this.beforeEvents = beforeEvents;
  }

  public final ViewEventHandler getInsteadOf() {
    return insteadOf;
  }

  public final void setInsteadOf(ViewEventHandler insteadOf) {
    this.insteadOf = insteadOf;
  }

  public final List<ViewEventHandler> getAfterEvents() {
    return afterEvents;
  }

  public final void setAfterEvents(List<ViewEventHandler> afterEvents) {
    this.afterEvents = afterEvents;
  }

}
