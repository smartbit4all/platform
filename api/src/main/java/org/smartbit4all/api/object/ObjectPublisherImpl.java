package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.smartbit4all.core.event.EventDefinitionBase;
import org.smartbit4all.core.event.EventListener;
import org.smartbit4all.core.event.EventPublisherImpl;
import org.smartbit4all.core.event.EventSubscription;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The base implementation of the {@link ObjectPublisher} interface that can be used in the Api
 * implementations.
 * 
 * @author Peter Boros
 *
 */
public class ObjectPublisherImpl implements ObjectPublisher, EventPublisherImpl {

  ApiObjectRef ref;

  /**
   * The listeners by the fully qualified name of the property.
   */
  Map<String, List<EventListener<PropertyChange>>> propertyListeners = new HashMap<>();

  /**
   * The listeners by the fully qualified name of the property.
   */
  Map<String, List<EventListener<ReferenceChange>>> referenceListeners = new HashMap<>();

  /**
   * The listeners by the fully qualified name of the property.
   */
  Map<String, List<EventListener<CollectionChange>>> collectionListeners = new HashMap<>();

  PropertyChangeEvent propertyChangeEvent = new PropertyChangeEventImpl();
  ReferenceChangeEvent referenceChangeEvent = new ReferenceChangeEventImpl();
  CollectionChangeEvent collectionChangeEvent = new CollectionChangeEventImpl();

  public void notify(ObjectChange change) {
    notifyRec(StringConstant.EMPTY, change);
  }

  private final void notifyRec(String path, ObjectChange objectChange) {
    notifyListeners(objectChange.getProperties(), propertyListeners);
    notifyListeners(objectChange.getReferences(), referenceListeners);
    notifyListeners(objectChange.getCollections(), collectionListeners);
    // After manage the changes at the actual level. Let's start recursion on the references and on
    // the collection items.
    for (ReferenceChange referenceChange : objectChange.getReferences()) {
      notifyRec(referenceChange.fullyQualifiedName(), referenceChange.getChangedReference());
    }
    for (CollectionChange collectionChange : objectChange.getCollections()) {
      for (ObjectChange collectionItem : collectionChange.getChanges()) {
        if (collectionItem.getOperation() != ChangeState.DELETED) {
          notifyRec(collectionChange.fullyQualifiedName(), collectionItem);
        }
      }
    }
  }

  private final <E extends ChangeItem> void notifyListeners(List<E> changes,
      Map<String, List<EventListener<E>>> listenerMap) {
    for (E change : changes) {
      // Check if we have subscription for the given property.
      String qualifiedName = change.fullyQualifiedName().toUpperCase();
      List<EventListener<E>> listeners = listenerMap.get(qualifiedName);
      if (listeners != null) {
        for (EventListener<E> listener : listeners) {
          listener.accept(change);
        }
      }
    }
  }

  @Override
  public PropertyChangeEvent properties() {
    return propertyChangeEvent;
  }

  @Override
  public ReferenceChangeEvent references() {
    return referenceChangeEvent;
  }

  @Override
  public CollectionChangeEvent collections() {
    return collectionChangeEvent;
  }

  private <E extends ChangeItem> void addToEventListeners(String name,
      Map<String, List<EventListener<E>>> listenerMap, EventListener<E> listener) {
    String key = name.toUpperCase();
    List<EventListener<E>> list = listenerMap.get(key);
    if (list == null) {
      list = new ArrayList<>();
      listenerMap.put(key, list);
    }
    list.add(listener);
  }

  private class PropertyChangeEventImpl extends EventDefinitionBase<PropertyChange>
      implements PropertyChangeEvent {

    protected PropertyChangeEventImpl() {
      super(URI.create("event:/objectediting/property"));
    }

    final List<EventSubscription<?>> subscriptions = new ArrayList<>();

    @Override
    public URI getUri() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public PropertyChangeSubscription subscribe() {
      PropertyChangeSubscription propertyChangeSubscription = new PropertyChangeSubscription() {
        @Override
        public EventSubscription<PropertyChange> add(EventListener<PropertyChange> listener) {
          EventSubscription<PropertyChange> eventSubscription = super.add(listener);
          addToEventListeners(fullyQualifiedName(), propertyListeners, listener);
          return eventSubscription;
        }
      };
      subscriptions.add(propertyChangeSubscription);
      return propertyChangeSubscription;
    }
  }

  private class CollectionChangeEventImpl extends EventDefinitionBase<CollectionChange>
      implements CollectionChangeEvent {

    protected CollectionChangeEventImpl() {
      super(URI.create("event:/objectediting/collection"));
    }

    final List<EventSubscription<?>> subscriptions = new ArrayList<EventSubscription<?>>();

    @Override
    public CollectionChangeSubscription subscribe() {
      CollectionChangeSubscription collectionChangeSubscription =
          new CollectionChangeSubscription() {
            @Override
            public EventSubscription<CollectionChange> add(
                EventListener<CollectionChange> listener) {
              EventSubscription<CollectionChange> eventSubscription = super.add(listener);
              addToEventListeners(fullyQualifiedName(), collectionListeners, listener);
              return eventSubscription;
            }
          };
      subscriptions.add(collectionChangeSubscription);
      return collectionChangeSubscription;
    }
  }

  private class ReferenceChangeEventImpl extends EventDefinitionBase<ReferenceChange>
      implements ReferenceChangeEvent {

    final List<EventSubscription<?>> subscriptions = new ArrayList<EventSubscription<?>>();

    protected ReferenceChangeEventImpl() {
      super(URI.create("event:/objectediting/reference"));
    }


    @Override
    public ReferenceChangeSubscription subscribe() {
      ReferenceChangeSubscription propertyChangeSubscription = new ReferenceChangeSubscription() {
        @Override
        public EventSubscription<ReferenceChange> add(EventListener<ReferenceChange> listener) {
          EventSubscription<ReferenceChange> eventSubscription = super.add(listener);
          addToEventListeners(fullyQualifiedName(), referenceListeners, listener);
          return eventSubscription;
        }
      };
      subscriptions.add(propertyChangeSubscription);
      return propertyChangeSubscription;
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
}
