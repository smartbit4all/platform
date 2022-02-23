package org.smartbit4all.core.object;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.object.ObservableObjectHelper.ObjectPropertyPath;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * Implementation of the {@link ObservableObject} interface that can be used in stateful API
 * implementations.
 * 
 * @author Peter Boros
 *
 */
public final class ObservableObjectImpl implements ObservableObject {

  private static final Logger log = LoggerFactory.getLogger(ObservableObjectImpl.class);

  ApiObjectRef ref;

  private final ObservablePublisherWrapper publisherWrapper;

  private PublishSubject<PropertyChange> propertyChangePublisher = PublishSubject.create();
  private PublishSubject<ReferenceChange> referenceChangePublisher = PublishSubject.create();
  private PublishSubject<ReferencedObjectChange> referencedObjectChangePublisher =
      PublishSubject.create();
  private PublishSubject<CollectionChange> collectionChangePublisher = PublishSubject.create();
  private PublishSubject<CollectionObjectChange> collectionObjectChangePublisher =
      PublishSubject.create();

  private ObservableObject parent;
  private String parentPath;

  private boolean anyNonParentSubscription;

  public ObservableObjectImpl() {
    this(null);
  }

  public ObservableObjectImpl(ObservablePublisherWrapper publisherWrapper) {
    this.publisherWrapper = publisherWrapper;
    anyNonParentSubscription = false;
  }

  private void notify(ObjectChange objectChange) {
    if (publisherWrapper != null) {
      try {
        publisherWrapper.accept(() -> notifyAllListneners(objectChange));
      } catch (Throwable e) {
        log.error("Unexpected error at event notification", e);
      }
    } else {
      notifyAllListneners(objectChange);
    }
  }

  protected void notifyAllListneners(ObjectChange objectChange) {
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
    if (parent != null) {
      parent.notifyListeners();
    } else {
      Optional<ObjectChange> renderAndCleanChanges = ref.renderAndCleanChanges();
      if (renderAndCleanChanges.isPresent()) {
        notify(renderAndCleanChanges.get());
      }
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
    if (parent != null) {
      parent.setValue(parentPath + "/" + propertyPath, value);
    } else {
      ref.setValueByPath(propertyPath, value);
      notifyListeners();
    }
  }

  @Override
  public Disposable onPropertyChange(@NonNull Consumer<? super PropertyChange> onPropertyChange,
      String... propertyPath) {
    if (parent != null) {
      return parent.onPropertyChange(
          ch -> onPropertyChange
              .accept(PropertyChange.copyWithoutParent(ch, parentPath)),
          ObservableObjectHelper.concat(parentPath, propertyPath));
    }
    anyNonParentSubscription = true;
    ObjectPropertyPath path = ObservableObjectHelper.processPathParameter(propertyPath);
    Disposable disposable = propertyChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onPropertyChange);
    if (ref != null) {
      ApiObjectRef pathRef = ref.getValueRefByPath(path.path);
      if (pathRef != null) {
        Object value = pathRef.getValue(path.property);
        PropertyChange currentChange = new PropertyChange(path.path, path.property, null, value);
        try {
          onPropertyChange.accept(currentChange);
        } catch (Throwable e) {
          log.error("Unexpected error at onPropertyChange", e);
        }
      }
    }
    return disposable;
  }

  @Override
  public Disposable onReferenceChange(@NonNull Consumer<? super ReferenceChange> onReferenceChange,
      String... referencePath) {
    if (parent != null) {
      return parent.onReferenceChange(
          ch -> onReferenceChange
              .accept(ReferenceChange.copyWithoutParent(ch, parentPath)),
          ObservableObjectHelper.concat(parentPath, referencePath));
    }
    anyNonParentSubscription = true;
    ObjectPropertyPath path = ObservableObjectHelper.processPathParameter(referencePath);
    return referenceChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onReferenceChange);
  }

  @Override
  public Disposable onReferencedObjectChange(
      @NonNull Consumer<? super ReferencedObjectChange> onReferencedObjectChange,
      String... referencePath) {
    if (parent != null) {
      return parent.onReferencedObjectChange(
          ch -> onReferencedObjectChange
              .accept(ReferencedObjectChange.copyWithoutParent(ch, parentPath)),
          ObservableObjectHelper.concat(parentPath, referencePath));
    }
    anyNonParentSubscription = true;
    ObjectPropertyPath path = ObservableObjectHelper.processPathParameter(referencePath);
    Disposable disposable = referencedObjectChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onReferencedObjectChange);
    if (ref != null) {
      ApiObjectRef pathRef = ref.getValueRefByPath(path.path);
      if (pathRef != null) {
        Object value = pathRef.getValue(path.property);
        if (value != null) {
          if (!(value instanceof ApiObjectRef)) {
            throw new IllegalArgumentException("Reference not found at " + path.toString());
          }
          ReferencedObjectChange currentChange =
              new ReferencedObjectChange(path.path, path.property,
                  new ObjectChangeSimple(path.path, ChangeState.NEW,
                      ((ApiObjectRef) value).getObject()));
          try {
            onReferencedObjectChange.accept(currentChange);
          } catch (Throwable e) {
            log.error("Unexpected error at onReferencedObjectChange", e);
          }
        }
      }
    }
    return disposable;
  }

  @Override
  public Disposable onCollectionChange(
      @NonNull Consumer<? super CollectionChange> onCollectionChange, String... collectionPath) {
    if (parent != null) {
      return parent.onCollectionChange(
          ch -> onCollectionChange
              .accept(CollectionChange.copyWithoutParent(ch, parentPath)),
          ObservableObjectHelper.concat(parentPath, collectionPath));
    }
    anyNonParentSubscription = true;
    ObjectPropertyPath path = ObservableObjectHelper.processPathParameter(collectionPath);
    return collectionChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onCollectionChange);
  }

  @Override
  public Disposable onCollectionObjectChange(
      @NonNull Consumer<? super CollectionObjectChange> onCollectionObjectChange,
      String... collectionPath) {
    if (parent != null) {
      return parent.onCollectionObjectChange(
          ch -> onCollectionObjectChange
              .accept(CollectionObjectChange.copyWithoutParent(ch, parentPath)),
          ObservableObjectHelper.concat(parentPath, collectionPath));
    }
    anyNonParentSubscription = true;
    ObjectPropertyPath path = ObservableObjectHelper.processPathParameter(collectionPath);
    Disposable disposable = collectionObjectChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onCollectionObjectChange);
    if (ref != null) {
      ApiObjectRef pathRef = ref.getValueRefByPath(path.path);
      if (pathRef != null) {
        Object value = pathRef.getValue(path.property);
        if (value != null) {
          if (!(value instanceof ApiObjectCollection)) {
            throw new IllegalArgumentException(
                "Collection not found at " + path.toString());
          }
          CollectionObjectChange currentChange =
              new CollectionObjectChange(path.path, path.property);
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
    return disposable;
  }

  public void setParent(ObservableObject parent, String path) {
    if (anyNonParentSubscription) {
      throw new IllegalArgumentException(
          "Parent set to ObservableObject, but subscription already happened!");
    }
    this.parent = parent;
    this.parentPath = path;
  }

}
