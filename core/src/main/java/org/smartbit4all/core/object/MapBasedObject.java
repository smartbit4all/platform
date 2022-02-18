package org.smartbit4all.core.object;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.smartbit4all.api.mapbasedobject.bean.MapBasedObjectData;
import org.smartbit4all.api.mapbasedobject.bean.ObjectValue;
import org.smartbit4all.api.mapbasedobject.bean.ObjectValueList;
import org.smartbit4all.api.mapbasedobject.bean.StringValue;
import org.smartbit4all.api.mapbasedobject.bean.StringValueList;
import org.smartbit4all.core.object.utility.MapBasedObjectUtil;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.core.utility.StringConstant;

/**
 * Object used for merging all the maps from {@link MapBasedObjectData} to ensure the uniqueness of
 * the properties in it.
 * 
 * After the merge, manages further operations with the property map according to given path
 * parameters.
 * 
 * The path includes unique property names separated by slashes. A path without slash refers to a
 * property inside the current MapBasedObject (e.g propertyName). A path with slashes refers to an
 * embedded property reached by the MapBasedObjects included in this one (e.g
 * embeddedObjectPropertyName/propertyName). Embedded MapBasedObject can be reached from list too by
 * writing the item index after the list property name also separated by a slash (e.g
 * embeddedObjectListPropertyName/itemIndex/propertyName).
 * 
 * @author Andras Pallo
 *
 */
public class MapBasedObject {

  /**
   * Key is the property name. Value can be simple value (e.g {@link StringValue}), value list (e.g
   * {@link StringValueList}), {@link MapBasedObject} or list of {@link MapBasedObject}s.
   */
  private final Map<String, Object> propertyMap = new HashMap<>();

  /**
   * The stored state of the object immediately after the last renderAndCleanChanges() was done.
   */
  private MapBasedObject previousState;

  private String name = null;

  public MapBasedObject() {}

  public MapBasedObject(String name) {
    this.name = name;
  }

  /**
   * Merges all the maps from given data into a property map managed by the newly created
   * {@link MapBasedObject}.
   * 
   * @param data
   * @return The new {@link MapBasedObject} including the given data properties.
   */
  public static final MapBasedObject of(MapBasedObjectData data) {
    MapBasedObject result = new MapBasedObject();

    setValuesByDataMap(result, data.getStringPropertyMap(), String.class);
    setValuesByDataMap(result, data.getStringListMap(), String.class);
    setValuesByDataMap(result, data.getIntegerPropertyMap(), Integer.class);
    setValuesByDataMap(result, data.getIntegerListMap(), Integer.class);
    setValuesByDataMap(result, data.getLongPropertyMap(), Long.class);
    setValuesByDataMap(result, data.getLongListMap(), Long.class);
    setValuesByDataMap(result, data.getBooleanPropertyMap(), Boolean.class);
    setValuesByDataMap(result, data.getBooleanListMap(), Boolean.class);
    setValuesByDataMap(result, data.getLocalDateTimePropertyMap(), LocalDateTime.class);
    setValuesByDataMap(result, data.getLocalDateTimeListMap(), LocalDateTime.class);

    Map<String, ObjectValue> objectPropertyMap = data.getObjectPropertyMap();
    if (objectPropertyMap != null) {
      objectPropertyMap
          .forEach((key, value) -> result.addProperty(key, of(value.getValue())));
    }

    Map<String, ObjectValueList> objectListMap = data.getObjectListMap();
    if (objectListMap != null) {
      objectListMap
          .forEach((key, value) -> result.addProperty(key, listOf(value.getValues())));
    }

    return result;
  }

  private static void setValuesByDataMap(MapBasedObject object, Map<String, ?> dataMap,
      Class<?> clazz) {
    if (dataMap != null) {
      dataMap.forEach((key, value) -> {
        object.setValueByPath(key, MapBasedObjectUtil.getActualValue(value), clazz);
      });
    }
  }

  /**
   * Merges all the maps from given data list into property maps managed by the newly created list
   * of {@link MapBasedObject}s.
   * 
   * @param datas
   * @return The new list of {@link MapBasedObject}s including the given data properties.
   */
  public static List<MapBasedObject> listOf(List<MapBasedObjectData> datas) {
    return datas.stream().map(data -> of(data)).collect(Collectors.toList());
  }

  /**
   * Creates a {@link MapBasedObjectData} including the properties of the given
   * {@link MapBasedObject} secluded into the proper maps of the data.
   * 
   * @param object
   * @return The new {@link MapBasedObjectData} including the object properties in the correct maps.
   */
  public static final MapBasedObjectData toData(MapBasedObject object) {
    MapBasedObjectData result = new MapBasedObjectData();
    object.propertyMap.forEach((key, value) -> {
      MapBasedObjectUtil.addObjectPropertyToData(result, key, value);
    });
    return result;
  }

  private void addProperty(String key, Object value) {
    propertyMap.put(key, value);
  }

  /**
   * Constructs the changes by the current modification state and clear the modification states.
   * 
   * @return
   */
  public final Optional<ObjectChange> renderAndCleanChanges() {
    ObjectChange result = null;

    ChangeState changeState;
    if (previousState == null) {
      changeState = ChangeState.NEW;
    } else {
      changeState = ChangeState.MODIFIED;
    }

    for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      Object actualValue = MapBasedObjectUtil.getActualValue(value);
      if (actualValue instanceof MapBasedObject) {
        Optional<ObjectChange> refChangeOpt =
            ((MapBasedObject) actualValue).renderAndCleanChanges();
        if (refChangeOpt.isPresent()) {
          if (result == null) {
            result = new ObjectChange(null, changeState);
          }
          result.getReferences().add(new ReferenceChange(null, key, refChangeOpt.get()));
        }

      } else if (actualValue instanceof List) {
        List<?> list = (List<?>) actualValue;
        if (list.isEmpty()) {
          Object prevValue = previousState.propertyMap.get(key);
          if (prevValue != null && prevValue instanceof List<?>) {
            List<?> prevList = (List<?>) prevValue;
            if (!prevList.isEmpty() && prevList.get(0) instanceof MapBasedObject) {
              CollectionChange collectionChange = new CollectionChange(null, key);
              int ind = 0;
              for (Object obj : prevList) {
                collectionChange.getChanges()
                    .add(new ObjectChange(key + "/" + ind++, ChangeState.DELETED));
              }
              if (result == null) {
                result = new ObjectChange(null, changeState);
              }
              result.getCollections().add(collectionChange);
            }
          }
        } else if (list.get(0) instanceof MapBasedObject) {
          CollectionChange collectionChange = new CollectionChange(null, key);
          list.forEach(o -> {
            Optional<ObjectChange> itemChangeOpt =
                ((MapBasedObject) o).renderAndCleanChanges();
            if (itemChangeOpt.isPresent()) {
              collectionChange.getChanges().add(itemChangeOpt.get());
            }
          });

          if (previousState != null) {
            Object prevValue = previousState.propertyMap.get(key);
            if (prevValue != null && prevValue instanceof List<?>) {
              List<?> prevList = (List<?>) prevValue;
              if (!prevList.isEmpty() && prevList.get(0) instanceof MapBasedObject) {
                int ind = 0;
                for (Object obj : prevList) {
                  // TODO how to compare MapBasedObjects
                  if (!list.contains(obj)) {
                    collectionChange.getChanges()
                        .add(new ObjectChange(key + "/" + ind, ChangeState.DELETED));
                    ind++;
                  }
                }
              }
            }
          }

          if (!collectionChange.getChanges().isEmpty()) {
            if (result == null) {
              result = new ObjectChange(null, changeState);
            }
            result.getCollections().add(collectionChange);
          }
        }

      } else if (actualValue == null) {
        Object prevValue = previousState.propertyMap.get(key);
        if (prevValue != null) {
          if (result == null) {
            result = new ObjectChange(null, changeState);
          }
          result.getReferences()
              .add(new ReferenceChange(null, key, new ObjectChange(key, ChangeState.DELETED)));
        }

      } else {
        if (previousState == null || !previousState.propertyMap.containsKey(key)) {
          if (actualValue != null) {
            if (result == null) {
              result = new ObjectChange(null, changeState);
            }
            result.getProperties().add(new PropertyChange(null, key, null, actualValue));
          }

        } else {
          Object prevValue =
              MapBasedObjectUtil.getActualValue(previousState.propertyMap.get(key));
          if (!prevValue.equals(actualValue)) {
            if (result == null) {
              result = new ObjectChange(null, changeState);
            }
            result.getProperties().add(new PropertyChange(null, key, prevValue, actualValue));
          }
        }
      }
    }

    // will it be necessary?
    renderDeletedFromPropertyMapChanges(result);

    previousState = deepCopy();

    return Optional.ofNullable(result);
  }

  private void renderDeletedFromPropertyMapChanges(ObjectChange result) {
    if (previousState != null) {
      for (Map.Entry<String, Object> entry : previousState.propertyMap.entrySet()) {
        String key = entry.getKey();

        if (!propertyMap.containsKey(key)) {
          Object actualValue = MapBasedObjectUtil.getActualValue(entry.getValue());

          if (actualValue instanceof MapBasedObject) {
            ObjectChange change = new ObjectChange(key, ChangeState.DELETED);
            if (result == null) {
              result = new ObjectChange(null, ChangeState.MODIFIED);
            }
            result.getReferences().add(new ReferenceChange(null, key, change));

          } else if (actualValue instanceof List) {
            List<?> list = (List<?>) actualValue;
            if (!list.isEmpty()) {
              CollectionChange collectionChange = new CollectionChange(null, key);
              list.forEach(o -> {
                ObjectChange change = new ObjectChange(key, ChangeState.DELETED);
                collectionChange.getChanges().add(change);
              });
              if (!collectionChange.getChanges().isEmpty()) {
                if (result == null) {
                  result = new ObjectChange(null, ChangeState.MODIFIED);
                }
                result.getCollections().add(collectionChange);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Creates a deep copy of this MapBasedObject (no references remain from the original object).
   * Previous state of the object is not copied.
   * 
   * @return
   */
  public MapBasedObject deepCopy() {
    MapBasedObject result = new MapBasedObject();
    propertyMap.forEach((key, value) -> {
      result.addProperty(key, MapBasedObjectUtil.deepCopyPropertyValue(value));
    });
    return result;
  }

  /**
   * @param path
   * @return The value found in the property map by the given path.
   */
  public Object getValueByPath(String path) {
    String rootPath = PathUtility.getRootPath(path);

    Object value = MapBasedObjectUtil.getActualValue(propertyMap.get(rootPath));

    if (value instanceof MapBasedObject) {
      if (PathUtility.getPathSize(path) > 1) {
        value = ((MapBasedObject) value).getValueByPath(PathUtility.nextFullPath(path));
      }

    } else if (value instanceof List<?> &&
        !((List<?>) value).isEmpty() &&
        ((List<?>) value).get(0) instanceof MapBasedObject) {

      if (PathUtility.getPathSize(path) > 1) {
        String nextPath = PathUtility.nextFullPath(path);
        try {
          int index = Integer.parseInt(PathUtility.getRootPath(nextPath));
          List<MapBasedObject> list = (List<MapBasedObject>) value;
          value = list.get(index).getValueByPath(PathUtility.nextFullPath(nextPath));
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              "Item index not found after MapBasedObject list property name, path: " + path);
        }
      }

    } else {
      if (PathUtility.getPathSize(path) > 1) {
        throw new IllegalArgumentException(
            "Not embedded MapBasedObject or list found in the middle of the path, class: " +
                (value == null ? "null" : value.getClass().getName()));
      }
    }

    return value;
  }

  /**
   * Sets the given value to the property found by the given path. Newly added property's value
   * cannot be set to null or empty list without value class given as third class parameter!
   * 
   * @param path
   * @param value
   */
  public void setValueByPath(String path, Object value) {
    setValueByPath(path, value, null);
  }

  /**
   * Sets the given value to the property found by the given path.
   * 
   * @param path
   * @param value
   * @param clazz
   */
  public void setValueByPath(String path, Object value, Class<?> clazz) {
    String lastPath = PathUtility.getLastPath(path);
    MapBasedObject parentObject = getParentObject(path);
    if (isItemIndex(lastPath)) {
      int pathSize = PathUtility.getPathSize(path);
      String key = PathUtility.subpath(path, pathSize - 2, pathSize - 1);
      Object currentValue = parentObject.propertyMap.get(key);

      MapBasedObjectUtil.setPropertyValueListItem(currentValue, Integer.valueOf(lastPath), value);

    } else {
      String key = lastPath;
      Object currentValue = parentObject.propertyMap.get(key);

      if (value == null && clazz == null && currentValue == null) {
        throw new IllegalArgumentException(
            "Newly added property's value cannot be set to null, path: " + path);
      }

      Object newValue = MapBasedObjectUtil.createPropertyValue(key, value, currentValue, clazz);
      if (currentValue == null || currentValue.getClass().equals(newValue.getClass())) {
        parentObject.addProperty(key, newValue);

      } else {
        throw new IllegalArgumentException(
            "Old and new value classes are not matching:" + StringConstant.NEW_LINE +
                "Expected class: " + currentValue.getClass().getName() + StringConstant.NEW_LINE +
                "Given class: " + value.getClass().getName());
      }
    }
  }

  /**
   * Adds the given value into the collection found by the given path.
   * 
   * @param path
   * @param value
   * @return The {@link MapBasedObject} including the collection that contains the new value.
   */
  public MapBasedObject addValueByPath(String path, Object value) {
    String key = PathUtility.getLastPath(path);

    if (isItemIndex(key)) {
      throw new IllegalArgumentException(
          "Value cannot be added into a list item, path: " + path);
    }

    MapBasedObject parentObject = getParentObject(path);
    Object mapValueList = parentObject.propertyMap.get(key);

    if (mapValueList == null) {
      throw new IllegalArgumentException("Collection is null, path: " + path);
    }

    if (MapBasedObjectUtil.canBeAdded(mapValueList, value)) {
      Object collectionValue = parentObject.getValueByPath(key);
      Object toAdd = value instanceof MapBasedObjectData ? of((MapBasedObjectData) value) : value;
      ((Collection) collectionValue).add(toAdd);
    }

    return parentObject;
  }

  private boolean isItemIndex(String path) {
    return path.matches("\\d+");
  }

  /**
   * Removes the element from the collection found by the given path.
   * 
   * @param path
   */
  public void removeValueByPath(String path) {
    if (PathUtility.getPathSize(path) < 2) {
      throw new IllegalArgumentException(
          "Path must contain at least one property name and element index, path: "
              + (path == null ? "null" : path));
    }

    Object foundValue = getValueByPath(PathUtility.getParentPath(path));

    if (foundValue instanceof Collection) {
      Collection collection = (Collection) foundValue;
      try {
        int index = Integer.parseInt(PathUtility.getLastPath(path));
        if (index >= collection.size()) {
          throw new IndexOutOfBoundsException(
              "Collection size: " + collection.size() + ", Given index: " + index);
        }
        int counter = 0;
        for (Object object : collection) {
          if (counter == index) {
            collection.remove(object);
            break;
          }
          counter++;
        }
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            "Collection item index not found at the end of the given path: " + path);
      }

    } else {
      throw new IllegalArgumentException(
          "Not Collection found, class: " + foundValue.getClass());
    }
  }

  // object/objects/2/name --- object/objects ---
  private MapBasedObject getParentObject(String path) {
    String parentPath = PathUtility.getParentPath(path);
    if (parentPath == null) {
      return this;
    }

    String rootPath = PathUtility.getRootPath(parentPath);
    Object value = propertyMap.get(rootPath);

    if (value == null) {
      value = new MapBasedObject();
      propertyMap.put(rootPath, value);
    }

    if (value instanceof MapBasedObject) {
      if (PathUtility.getPathSize(parentPath) == 1) {
        return (MapBasedObject) value;
      }
      value = ((MapBasedObject) value).getParentObject(PathUtility.nextFullPath(parentPath));

    } else if (value instanceof List<?> &&
        !((List<?>) value).isEmpty() &&
        ((List<?>) value).get(0) instanceof MapBasedObject) {

      if (PathUtility.getPathSize(parentPath) == 1) {
        return this;
      } else {
        String nextPath = PathUtility.nextFullPath(parentPath);
        try {
          int index = Integer.parseInt(PathUtility.getRootPath(nextPath));
          List<MapBasedObject> list = (List<MapBasedObject>) value;
          if (PathUtility.getPathSize(parentPath) == 2) {
            return list.get(index);
          } else {
            value = list.get(index).getParentObject(PathUtility.nextFullPath(nextPath));
          }
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              "Item index not found after MapBasedObject list property name, path: " + path);
        }
      }
    } else {

      // TODO handle proeprty value list with item index

      throw new IllegalArgumentException(
          "Not embedded single MapBasedObject or list found in the middle of the path, class: " +
              (value == null ? "null" : value.getClass().getName()));
    }
    return (MapBasedObject) value;
  }

}
