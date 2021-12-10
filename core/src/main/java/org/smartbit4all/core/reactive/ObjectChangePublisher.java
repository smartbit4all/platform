package org.smartbit4all.core.reactive;

import java.util.function.Consumer;
import io.reactivex.rxjava3.disposables.Disposable;

public interface ObjectChangePublisher<T> {

  // TODO create our own Disposable? use java9 reactive stream api?
  Disposable subscribeForChange(T object, Consumer<T> consumer);

  void onNext(T object);

}
