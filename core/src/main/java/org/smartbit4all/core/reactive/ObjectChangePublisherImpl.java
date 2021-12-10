package org.smartbit4all.core.reactive;

import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ObjectChangePublisherImpl<T> implements ObjectChangePublisher<T> {


  private static final Logger log = LoggerFactory.getLogger(ObjectChangePublisherImpl.class);

  private PublishSubject<T> publisher;

  public ObjectChangePublisherImpl() {
    publisher = PublishSubject.create();
  }

  @Override
  public Disposable subscribeForChange(T object, Consumer<T> observer) {
    if (observer != null) {
      try {
        if (object == null) {
          return publisher
              .subscribe(u -> observer.accept(u));
        }
        return publisher
            .filter(changedObject -> changedObject.equals(object))
            .subscribe(u -> publishTo(observer, u));
      } catch (Exception e) {
        log.error("Unable to subscribe for object (" + object + ")", e);
      }
    }
    return null;
  }

  @Override
  public void onNext(T object) {
    publisher.onNext(object);
  }

  protected void publishTo(Consumer<T> observer, T object) {
    observer.accept(object);
  }

}
