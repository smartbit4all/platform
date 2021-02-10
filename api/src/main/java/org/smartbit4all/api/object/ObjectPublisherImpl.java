package org.smartbit4all.api.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.EventListener;

/**
 * The base implementation of the {@link ObjectPublisher} interface that can be used in the
 * Api implementations.
 * 
 * @author Peter Boros
 *
 */
public class ObjectPublisherImpl implements ObjectPublisher {

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
      String qualifiedName = change.fullyQualifiedName();
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ReferenceChangeEvent references() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CollectionChangeEvent collections() {
    // TODO Auto-generated method stub
    return null;
  }

}
