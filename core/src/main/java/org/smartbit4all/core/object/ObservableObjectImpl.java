package org.smartbit4all.core.object;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.event.EventPublisherImpl;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * Implementation of the {@link ObservableObject} interface that can be used in stateful API
 * implementations.
 * 
 * @author Peter Boros
 *
 */
public final class ObservableObjectImpl implements ObservableObject, EventPublisherImpl {

  private static final Logger log = LoggerFactory.getLogger(ObservableObjectImpl.class);

  ApiObjectRef ref;

  private PublishSubject<PropertyChange> propertyChangePublisher = PublishSubject.create();
  private PublishSubject<ReferenceChange> referenceChangePublisher = PublishSubject.create();
  private PublishSubject<ReferencedObjectChange> referencedObjectChangePublisher =
      PublishSubject.create();
  private PublishSubject<CollectionChange> collectionChangePublisher = PublishSubject.create();
  private PublishSubject<CollectionObjectChange> collectionObjectChangePublisher =
      PublishSubject.create();

  private void notify(ObjectChange objectChange) {
    notifyListeners(objectChange.getProperties(), propertyChangePublisher);
    notifyListeners(objectChange.getReferences(), referenceChangePublisher);
    notifyListeners(objectChange.getReferencedObjects(), referencedObjectChangePublisher);
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
  public Observable<ReferencedObjectChange> referencedObjects() {
    return referencedObjectChangePublisher;
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

  @Override
  public void onPropertyChange(String path, String property,
      @NonNull Consumer<? super PropertyChange> onPropertyChange) {
    properties()
        .filter(change -> ObservableObjectHelper.pathEquals(change, path, property))
        .subscribe(onPropertyChange);
    if (ref != null) {
      Object value = ref.getValueRefByPath(path).getValue(property);
      if (value instanceof ApiObjectRef || value instanceof ApiObjectCollection) {
        throw new IllegalArgumentException(
            "Expected value, found reference/collection at " + path + "." + property);
      }
      PropertyChange currentChange = new PropertyChange(path, property, null, value);
      try {
        onPropertyChange.accept(currentChange);
      } catch (Throwable e) {
        log.error("Unexpected error at onPropertyChange", e);
      }
    }
  }

  @Override
  public void onReferenceChange(String path, String reference,
      @NonNull Consumer<? super ReferenceChange> onReferenceChange) {
    references()
        .filter(change -> ObservableObjectHelper.pathEquals(change, path, reference))
        .subscribe(onReferenceChange);
  }

  @Override
  public void onReferencedObjectChange(String path, String reference,
      @NonNull Consumer<? super ReferencedObjectChange> onReferencedObjectChange) {
    referencedObjects()
        .filter(change -> ObservableObjectHelper.pathEquals(change, path, reference))
        .subscribe(onReferencedObjectChange);
    if (ref != null) {
      Object value = ref.getValueRefByPath(path).getValue(reference);
      if (value != null) {
        if (!(value instanceof ApiObjectRef)) {
          throw new IllegalArgumentException("Reference not found at " + path + "." + reference);
        }
        ReferencedObjectChange currentChange = new ReferencedObjectChange(path, reference,
            new ObjectChangeSimple(path, ChangeState.NEW, ((ApiObjectRef) value).getObject()));
        try {
          onReferencedObjectChange.accept(currentChange);
        } catch (Throwable e) {
          log.error("Unexpected error at onReferencedObjectChange", e);
        }
      }
    }
  }

  @Override
  public void onCollectionChange(String path, String collection,
      @NonNull Consumer<? super CollectionChange> onCollectionChange) {
    collections()
        .filter(change -> ObservableObjectHelper.pathEquals(change, path, collection))
        .subscribe(onCollectionChange);
  }

  @Override
  public void onCollectionObjectChange(String path, String collection,
      @NonNull Consumer<? super CollectionObjectChange> onCollectionObjectChange) {
    collectionObjects()
        .filter(change -> ObservableObjectHelper.pathEquals(change, path, collection))
        .subscribe(onCollectionObjectChange);
    if (ref != null) {
      Object value = ref.getValueRefByPath(path).getValue(collection);
      if (value != null) {
        if (!(value instanceof ApiObjectCollection)) {
          throw new IllegalArgumentException("Collection not found at " + path + "." + collection);
        }
        CollectionObjectChange currentChange = new CollectionObjectChange(path, collection);
        for (ApiObjectRef object : (ApiObjectCollection) value) {
          currentChange.getChanges()
              .add(new ObjectChangeSimple(object.getPath(), ChangeState.NEW, object.getObject()));
        }
        try {
          onCollectionObjectChange.accept(currentChange);
        } catch (Throwable e) {
          log.error("Unexpected error at onCollectionObjectChange", e);
        }
      }
    }
  }


}
