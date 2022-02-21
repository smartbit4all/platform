package org.smartbit4all.core.object;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.event.ListenerAware;
import org.smartbit4all.core.object.ObservableObjectHelper.ObjectPropertyPath;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ObservableMapBasedObjectImpl implements ObservableObject, ListenerAware {

  private static final Logger log = LoggerFactory.getLogger(ObservableMapBasedObjectImpl.class);

  MapBasedObject object;

  private final ObservablePublisherWrapper publisherWrapper;

  private PublishSubject<PropertyChange> propertyChangePublisher = PublishSubject.create();
  private PublishSubject<ReferenceChange> referenceChangePublisher = PublishSubject.create();
  private PublishSubject<ReferencedObjectChange> referencedObjectChangePublisher =
      PublishSubject.create();
  private PublishSubject<CollectionChange> collectionChangePublisher = PublishSubject.create();
  private PublishSubject<CollectionObjectChange> collectionObjectChangePublisher =
      PublishSubject.create();

  public ObservableMapBasedObjectImpl() {
    this(null);
  }

  public ObservableMapBasedObjectImpl(ObservablePublisherWrapper publisherWrapper) {
    this.publisherWrapper = publisherWrapper;
  }

  private void notify(ObjectChange objectChange) {
    if (publisherWrapper != null) {
      try {
        publisherWrapper.accept(() -> notifyAllListeners(objectChange));
      } catch (Throwable e) {
        log.error("Unexpected error at event notification", e);
      }
    } else {
      notifyAllListeners(objectChange);
    }
  }

  protected void notifyAllListeners(ObjectChange objectChange) {
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
  public void notifyListeners() {
    Optional<ObjectChange> renderAndCleanChanges = object.renderAndCleanChanges();
    if (renderAndCleanChanges.isPresent()) {
      notify(renderAndCleanChanges.get());
    }
  }

  public final MapBasedObject getObject() {
    return object;
  }

  public final void setObject(MapBasedObject object) {
    this.object = object;
  }

  @Override
  public void setValue(String propertyPath, Object value) {
    object.setValueByPath(propertyPath, value);
    notifyListeners();
  }

  @Override
  public Disposable onPropertyChange(@NonNull Consumer<? super PropertyChange> onPropertyChange,
      String... propertyPath) {
    ObjectPropertyPath path = ObservableObjectHelper.processPathParameter(propertyPath);
    Disposable disposable = propertyChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onPropertyChange);
    if (object != null) {
      Object value = object.getValueByPath(path.property);
      PropertyChange currentChange = new PropertyChange(path.path, path.property, null, value);
      try {
        onPropertyChange.accept(currentChange);
      } catch (Throwable e) {
        log.error("Unexpected error at onPropertyChange", e);
      }
    }
    return disposable;
  }

  @Override
  public Disposable onReferenceChange(@NonNull Consumer<? super ReferenceChange> onReferenceChange,
      String... referencePath) {
    ObjectPropertyPath path = ObservableObjectHelper.processPathParameter(referencePath);
    return referenceChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onReferenceChange);
  }

  @Override
  public Disposable onReferencedObjectChange(
      @NonNull Consumer<? super ReferencedObjectChange> onReferencedObjectChange,
      String... referencePath) {
    ObjectPropertyPath path = ObservableObjectHelper.processPathParameter(referencePath);
    Disposable disposable = referencedObjectChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onReferencedObjectChange);
    if (object != null) {
      Object value = object.getValueByPath(path.property);
      if (value != null) {
        if (!(value instanceof MapBasedObject)) {
          throw new IllegalArgumentException("Reference not found at " + path.toString());
        }
        ReferencedObjectChange currentChange =
            new ReferencedObjectChange(path.path, path.property,
                new ObjectChangeSimple(path.path, ChangeState.NEW, value));
        try {
          onReferencedObjectChange.accept(currentChange);
        } catch (Throwable e) {
          log.error("Unexpected error at onReferencedObjectChange", e);
        }
      }
    }
    return disposable;
  }

  @Override
  public Disposable onCollectionChange(
      @NonNull Consumer<? super CollectionChange> onCollectionChange, String... collectionPath) {
    ObjectPropertyPath path = ObservableObjectHelper.processPathParameter(collectionPath);
    return collectionChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onCollectionChange);
  }

  @Override
  public Disposable onCollectionObjectChange(
      @NonNull Consumer<? super CollectionObjectChange> onCollectionObjectChange,
      String... collectionPath) {
    ObjectPropertyPath path = ObservableObjectHelper.processPathParameter(collectionPath);
    Disposable disposable = collectionObjectChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onCollectionObjectChange);
    if (object != null) {
      Object value = object.getValueByPath(path.property);
      if (value != null) {
        if (!(value instanceof List<?>)) {
          throw new IllegalArgumentException(
              "Collection not found at " + path.toString());
        }
        List<?> list = (List<?>) value;
        if (!list.isEmpty()) {
          if (!(list.get(0) instanceof MapBasedObject)) {
            throw new IllegalArgumentException(
                "Invalid collection items found at " + path.toString());
          }
          CollectionObjectChange currentChange =
              new CollectionObjectChange(path.path, path.property);
          for (MapBasedObject object : (List<MapBasedObject>) value) {
            currentChange.getChanges()
                .add(new ObjectChangeSimple(object.getPath(), ChangeState.NEW, object));
          }
          try {
            onCollectionObjectChange.accept(currentChange);
          } catch (Throwable e) {
            log.error("Unexpected error at onCollectionObjectChange", e);
          }
        }
      }
    }
    return disposable;
  }

}
