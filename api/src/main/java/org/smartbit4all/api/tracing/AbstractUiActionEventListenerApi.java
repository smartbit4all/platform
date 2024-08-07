package org.smartbit4all.api.tracing;

import org.smartbit4all.api.tracing.bean.UiActionExecutionEvent;

public abstract class AbstractUiActionEventListenerApi implements UiActionEventListenerApi {

  @Override
  public void onActionExecuted(UiActionExecutionEvent e) {
    if (shouldHandle(e)) {
      handle(e);
    }
  }

  protected abstract boolean shouldHandle(UiActionExecutionEvent e);

  protected abstract void handle(UiActionExecutionEvent e);

}
