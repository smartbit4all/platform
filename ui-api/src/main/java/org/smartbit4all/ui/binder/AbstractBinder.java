package org.smartbit4all.ui.binder;

import io.reactivex.rxjava3.disposables.Disposable;

public class AbstractBinder {

  protected Disposable disposable;

  public void unbind() {
    if (disposable != null) {
      if (!disposable.isDisposed()) {
        disposable.dispose();
      }
      disposable = null;
    }
  }

}
