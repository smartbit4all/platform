package org.smartbit4all.core.object.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObservableObject;

/**
 * 
 * Collector collections, which can store the changes of an ObservableObject collection events.
 * Can be used in tests or the extension collectors in UIs.
 * 
 * @author Zoltan Szegedi
 *
 */
public class ObservableObjectCollectorUtil {

  public static <T> Collection<T> createCollectionChangeCollector(
      ObservableObject observable,
      String collectionName,
      Class<T> clazz) {

    return createCollectionChangeCollector(
        observable,
        collectionName,
        new ArrayList<>(),
        clazz);
  }

  public static <T> Collection<T> createCollectionChangeCollector(
      ObservableObject observable,
      String collectionName,
      Collection<T> collectorList,
      Class<T> clazz) {

    collectorList.clear();

    observable.onCollectionObjectChange(
        null,
        collectionName,

        changes -> {
          onObjectChange(
              (path, newElement) -> collectorList.add(newElement),
              (path, deletedElement) -> collectorList.remove(deletedElement),
              clazz,
              changes);
        });

    return collectorList;
  }

  public static <T> Map<String, T> createCollectionChangeCollectorMap(
      ObservableObject observable,
      String collectionName,
      Class<T> clazz) {

    Map<String, T> collectorMap = new HashMap<>();
    
    observable.onCollectionObjectChange(
        null,
        collectionName,

        changes -> {
          
          onObjectChange(
              (path, newElement) -> collectorMap.put(path, newElement),
              (path, deletedElement) -> collectorMap.remove(path),
              clazz,
              changes);
          
        });

    return collectorMap;
  }
  
  private static <T> void onObjectChange(
      BiConsumer<String, T> onNewElement,
      BiConsumer<String, T> onDeleteElement,
      Class<T> clazz,
      CollectionObjectChange changes) {

    for (ObjectChangeSimple collectionChange : changes.getChanges()) {
      ChangeState operation = collectionChange.getOperation();

      T bean = clazz.cast(collectionChange.getObject());

      if (operation == ChangeState.NEW) {

        onNewElement.accept(collectionChange.getPath(), bean);

      } else if (operation == ChangeState.DELETED) {

        onDeleteElement.accept(collectionChange.getPath(), bean);

      } else {
        
        throw new IllegalArgumentException("What is this operation? " + operation);
        
      }
    }
  }
  
}
