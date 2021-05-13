package org.smartbit4all.core.object;

import java.util.List;
import java.util.Optional;
import org.smartbit4all.core.event.EventPublisherImpl;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * Implementation of the {@link ObservableObject} interface that can be used in stateful API
 * implementations.
 * 
 * @author Peter Boros
 *
 */
public final class ObservableObjectImpl implements ObservableObject, EventPublisherImpl {

  ApiObjectRef ref;

  private PublishSubject<PropertyChange> propertyChangePublisher = PublishSubject.create();
  private PublishSubject<ReferenceChange> referenceChangePublisher = PublishSubject.create();
  private PublishSubject<CollectionChange> collectionChangePublisher = PublishSubject.create();
  private PublishSubject<CollectionObjectChange> collectionObjectChangePublisher =
      PublishSubject.create();

  private void notify(ObjectChange objectChange) {
    notifyListeners(objectChange.getProperties(), propertyChangePublisher);
    notifyListeners(objectChange.getReferences(), referenceChangePublisher);
    notifyListeners(objectChange.getCollections(), collectionChangePublisher);
    notifyListeners(objectChange.getCollectionObjects(), collectionObjectChangePublisher);
    // After manage the changes at the actual level. Let's start recursion on the references and on
    // the collection items. CollectionObjectChanges are not recursive.
    for (ReferenceChange referenceChange : objectChange.getReferences()) {
      notify(referenceChange.getChangedReference());
    }
    for (CollectionChange collectionChange : objectChange.getCollections()) {
      for (ObjectChange collectionItem : collectionChange.getChanges()) {
        if (collectionItem.getOperation() != ChangeState.DELETED) {
          notify(collectionItem);
        }
      }
    }
  }

  private final <T> void notifyListeners(List<T> changes, PublishSubject<T> publisher) {
    for (T change : changes) {
      publisher.onNext(change);
    }
  }

  @Override
  public Observable<PropertyChange> properties() {
    return propertyChangePublisher;
  }

  @Override
  public Observable<ReferenceChange> references() {
    return referenceChangePublisher;
  }

  @Override
  public Observable<CollectionChange> collections() {
    return collectionChangePublisher;
  }

  @Override
  public Observable<CollectionObjectChange> collectionObjects() {
    return collectionObjectChangePublisher;
  }

  @Override
  public void notifyListeners() {
    Optional<ObjectChange> renderAndCleanChanges = ref.renderAndCleanChanges();
    if (renderAndCleanChanges.isPresent()) {
      notify(renderAndCleanChanges.get());
    }
  }

  public final ApiObjectRef getRef() {
    return ref;
  }

  public final void setRef(ApiObjectRef ref) {
    this.ref = ref;
  }

  @Override
  public void setValue(String propertyPath, Object value) {
    ref.setValueByPath(propertyPath, value);
    notifyListeners();
  }

  @Override
  public void addValue(String collectionPath, Object value) {
    ref.addValueByPath(collectionPath, value);
    notifyListeners();
  }

  @Override
  public void removeValue(String collectionElementPath) {
    ref.removeValueByPath(collectionElementPath);
    notifyListeners();
  }

}
