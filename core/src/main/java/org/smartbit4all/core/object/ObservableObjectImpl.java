package org.smartbit4all.core.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.event.ListenerAware;
import org.smartbit4all.core.utility.PathUtility;
import com.google.common.base.Strings;
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
public final class ObservableObjectImpl implements ObservableObject, ListenerAware {

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

  public ObservableObjectImpl() {
    this(null);
  }

  public ObservableObjectImpl(ObservablePublisherWrapper publisherWrapper) {
    this.publisherWrapper = publisherWrapper;
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
  public Disposable onPropertyChange(@NonNull Consumer<? super PropertyChange> onPropertyChange,
      String... propertyPath) {
    ObjectPropertyPath path = processPathParameter(propertyPath);
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
    ObjectPropertyPath path = processPathParameter(referencePath);
    return referenceChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onReferenceChange);
  }

  @Override
  public Disposable onReferencedObjectChange(
      @NonNull Consumer<? super ReferencedObjectChange> onReferencedObjectChange,
      String... referencePath) {
    ObjectPropertyPath path = processPathParameter(referencePath);
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
    ObjectPropertyPath path = processPathParameter(collectionPath);
    return collectionChangePublisher
        .filter(change -> ObservableObjectHelper.pathEquals(change, path))
        .subscribe(onCollectionChange);
  }

  @Override
  public Disposable onCollectionObjectChange(
      @NonNull Consumer<? super CollectionObjectChange> onCollectionObjectChange,
      String... collectionPath) {
    ObjectPropertyPath path = processPathParameter(collectionPath);
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

  static class ObjectPropertyPath {
    /**
     * Path within the object, separated by "/" characters.
     */
    String path;

    /**
     * Name of the property / reference / collection.
     */
    String property;

    @Override
    public String toString() {
      return path + "." + property;
    }
  }

  private ObjectPropertyPath processPathParameter(String... propertyPath) {
    // should be non-empty
    if (propertyPath == null || propertyPath.length == 0) {
      throw new IllegalArgumentException(
          "No path was given when subscribing to observable object changes!");
    }
    // first item can be null or empty string - skip it
    if (Strings.isNullOrEmpty(propertyPath[0])) {
      if (propertyPath.length == 1) {
        throw new IllegalArgumentException(
            "Empty path was given when subscribing to observable object changes!");
      }
    }
    // process all path parts: first split by "/"
    List<String[]> processedPathsList = new ArrayList<>();
    for (int i = 0; i < propertyPath.length; i++) {
      processedPathsList.add(PathUtility.decomposePath(propertyPath[i]));
    }
    // then collect all parts, ignore empty ones
    String[] processedPaths = processedPathsList.stream()
        .flatMap(Arrays::stream)
        .filter(part -> !Strings.isNullOrEmpty(part))
        .toArray(String[]::new);

    ObjectPropertyPath path = new ObjectPropertyPath();
    path.path = PathUtility.concatPath(false, processedPaths);
    path.property = processedPaths[processedPaths.length - 1];
    return path;
  }
}
