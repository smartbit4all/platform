package org.smartbit4all.core.object;

import org.smartbit4all.core.event.ListenerAware;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

public class ObservableMapBasedObjectImpl implements ObservableObject, ListenerAware {

  MapBasedObject object;

  @Override
  public void setValue(String propertyPath, Object value) {
    // TODO Auto-generated method stub

  }

  @Override
  public Disposable onPropertyChange(@NonNull Consumer<? super PropertyChange> onPropertyChange,
      String... propertyPath) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Disposable onReferenceChange(@NonNull Consumer<? super ReferenceChange> onReferenceChange,
      String... referencePath) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Disposable onReferencedObjectChange(
      @NonNull Consumer<? super ReferencedObjectChange> onReferencedObjectChange,
      String... referencePath) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Disposable onCollectionChange(
      @NonNull Consumer<? super CollectionChange> onCollectionChange, String... collectionPath) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Disposable onCollectionObjectChange(
      @NonNull Consumer<? super CollectionObjectChange> onCollectionObjectChange,
      String... collectionPath) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void notifyListeners() {
    // TODO Auto-generated method stub

  }


}
