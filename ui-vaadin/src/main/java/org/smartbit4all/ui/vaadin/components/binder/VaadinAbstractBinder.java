package org.smartbit4all.ui.vaadin.components.binder;

import io.reactivex.rxjava3.disposables.Disposable;

public class VaadinAbstractBinder {

  protected Disposable disposable;

  public void unbind() {
    if (disposable != null) {
      disposable.dispose();
    }
  }

}
