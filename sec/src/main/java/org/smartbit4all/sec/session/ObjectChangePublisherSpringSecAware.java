package org.smartbit4all.sec.session;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.smartbit4all.core.reactive.ObjectChangePublisherImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import io.reactivex.rxjava3.disposables.Disposable;

public class ObjectChangePublisherSpringSecAware<T> extends ObjectChangePublisherImpl<T> {

  private Map<Consumer<T>, SecurityContext> contextByConsumer;

  public ObjectChangePublisherSpringSecAware() {
    contextByConsumer = new HashMap<>();
  }

  @Override
  public Disposable subscribeForChange(T object, Consumer<T> observer) {
    SecurityContext context = SecurityContextHolder.getContext();
    contextByConsumer.put(observer, context);
    Disposable disposable = super.subscribeForChange(object, observer);
    return new EmbeddedDisposable(disposable, observer);
  }

  @Override
  protected void publishTo(Consumer<T> observer, T object) {
    SecurityContext context = contextByConsumer.get(observer);
    SecurityContext oldContext = SecurityContextHolder.getContext();
    if (context == oldContext) {
      super.publishTo(observer, object);
    } else {
      SecurityContextHolder.setContext(context);
      try {
        super.publishTo(observer, object);
      } finally {
        SecurityContextHolder.setContext(oldContext);
      }
    }
  }

  private class EmbeddedDisposable implements Disposable {

    private Disposable disposable;
    private Consumer<T> observer;

    public EmbeddedDisposable(Disposable disposable, Consumer<T> observer) {
      this.disposable = disposable;
      this.observer = observer;
    }

    @Override
    public void dispose() {
      if (!isDisposed()) {
        contextByConsumer.remove(observer);
        disposable.dispose();
        observer = null;
        disposable = null;
      }
    }

    @Override
    public boolean isDisposed() {
      return disposable == null ? true : disposable.isDisposed();
    }

  }
}
