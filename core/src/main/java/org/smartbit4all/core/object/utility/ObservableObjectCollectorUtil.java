package org.smartbit4all.core.object.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObservableObject;

/**
 * 
 * Collector collections, which can store the changes of an ObservableObject collection events. Can
 * be used in tests or the extension collectors in UIs.
 * 
 * @author Zoltan Szegedi
 *
 */
public class ObservableObjectCollectorUtil {

  private static final Logger log = LoggerFactory.getLogger(ObservableObjectCollectorUtil.class);

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
        changes -> {
          onObjectChange(
              (path, newElement) -> collectorList.add(newElement),
              (path, deletedElement) -> collectorList.remove(deletedElement),
              (path, deletedElement) -> log
                  .warn("Cannot modify element with simple Collection collector!"),
              clazz,
              changes);
        },
        collectionName);

    return collectorList;
  }

  public static <T> Map<String, T> createCollectionChangeCollectorMap(
      ObservableObject observable,
      String collectionName,
      Class<T> clazz) {

    Map<String, T> collectorMap = new HashMap<>();

    observable.onCollectionObjectChange(
        changes -> {

          onObjectChange(
              (path, newElement) -> collectorMap.put(path, newElement),
              (path, deletedElement) -> collectorMap.remove(path),
              (path, modifiedElement) -> collectorMap.put(path, modifiedElement),
              clazz,
              changes);

        },
        collectionName);

    return collectorMap;
  }

  private static <T> void onObjectChange(
      BiConsumer<String, T> onNewElement,
      BiConsumer<String, T> onDeleteElement,
      BiConsumer<String, T> onModifyElement,
      Class<T> clazz,
      CollectionObjectChange changes) {

    for (ObjectChangeSimple collectionChange : changes.getChanges()) {
      ChangeState operation = collectionChange.getOperation();

      T bean = clazz.cast(collectionChange.getObject());

      if (operation == ChangeState.NEW) {

        onNewElement.accept(collectionChange.getPath(), bean);

      } else if (operation == ChangeState.DELETED) {

        onDeleteElement.accept(collectionChange.getPath(), bean);

      } else if (operation == ChangeState.MODIFIED) {

        onModifyElement.accept(collectionChange.getPath(), bean);

      } else {

        log.error("What is this operation? " + operation);

      }
    }
  }

}
