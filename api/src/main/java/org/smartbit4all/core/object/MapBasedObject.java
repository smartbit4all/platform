package org.smartbit4all.core.object;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.smartbit4all.api.mapbasedobject.bean.MapBasedObjectData;
import org.smartbit4all.api.mapbasedobject.bean.ObjectValue;
import org.smartbit4all.api.mapbasedobject.bean.ObjectValueList;
import org.smartbit4all.api.mapbasedobject.bean.StringValue;
import org.smartbit4all.api.mapbasedobject.bean.StringValueList;
import org.smartbit4all.core.object.utility.MapBasedObjectUtil;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.core.utility.StringConstant;
import com.google.common.base.Strings;

/**
 * Object used for merging all the maps from {@link MapBasedObjectData} to ensure the uniqueness of
 * the properties in it.<br>
 * 
 * After the merge, manages further operations with the property map according to given path
 * parameters.<br>
 * <br>
 * The path includes unique property names separated by slashes:
 * 
 * <ul>
 * <li>A path without slash refers to a property inside the current MapBasedObject (e.g
 * propertyName).</li>
 * <li>A path with slashes refers to an embedded property reached by the MapBasedObjects included in
 * this one (e.g embeddedObjectPropertyName/propertyName).</li>
 * <li>Embedded MapBasedObject can be reached from list too by writing the item index after the list
 * property name also separated by a slash (e.g
 * embeddedObjectListPropertyName/itemIndex/propertyName).</li>
 * </ul>
 * 
 * @author Andras Pallo
 *
 */
public class MapBasedObject implements DomainObjectRef {

  /**
   * Key is the property name. Value can be simple value (e.g {@link StringValue}), value list (e.g
   * {@link StringValueList}), {@link MapBasedObject} or list of {@link MapBasedObject}s.
   */
  private final Map<String, Object> propertyMap = new HashMap<>();

  /**
   * The stored state of the object immediately after the last renderAndCleanChanges() was done.
   */
  private MapBasedObject previousState;

  /**
   * In case of an embedded object the path contains the property name of the parent objects in
   * order separated by slashes.
   */
  private String path;

  /**
   * In case of embedded MapBasedObject lists, the removed items will be stored there to be able to
   * render the deleted collection changes effectively.
   */
  private final List<MapBasedObject> deletedObjectItems = new ArrayList<>();

  /**
   * Stores the URI of the {@link MapBasedObjectData} which this object was created from.
   */
  private URI dataUri;

  /**
   * Constructor for root object.
   */
  public MapBasedObject() {
    this.path = "";
  }

  /**
   * Constructor for embedded objects.
   * 
   * @param path The path includes in order the name of the parent objects containing this object.
   */
  private MapBasedObject(String path) {
    this.path = path.toUpperCase();
  }

  /**
   * @return The path of the object.
   */
  @Override
  public final String getPath() {
    return path;
  }

  /**
   * Merges all the maps from given data into a property map managed by the newly created
   * {@link MapBasedObject}.
   * 
   * @param data
   * @return The new {@link MapBasedObject} including the given data properties.
   */
  public static final MapBasedObject of(MapBasedObjectData data) {
    return of(data, "");
  }

  /**
   * Merges all the maps from given data into a property map managed by the newly created
   * {@link MapBasedObject}. Use the method only for creating embedded {@link MapBasedObject}s,
   * otherwise use the one parameter method!
   * 
   * @param data
   * @param path The property name of the parent {@link MapBasedObject}s in order separated by
   *        slashes.
   * @return The new {@link MapBasedObject} including the given data properties.
   */
  public static final MapBasedObject of(MapBasedObjectData data, String path) {
    MapBasedObject result = new MapBasedObject(path);

    setData(data, path, result);

    return result;
  }

  private static void setData(MapBasedObjectData data, String path, MapBasedObject result) {
    result.dataUri = data.getUri();

    setValuesByData(result, data);

    Map<String, ObjectValue> objectPropertyMap = data.getObjectPropertyMap();
    if (objectPropertyMap != null) {
      objectPropertyMap.forEach(
          (key, value) -> result.addProperty(key,
              of(value.getValue(), getObjectPath(path, key))));
    }

    Map<String, ObjectValueList> objectListMap = data.getObjectListMap();
    if (objectListMap != null) {
      objectListMap.forEach(
          (key, value) -> result.addProperty(key,
              listOf(value.getValues(), getObjectPath(path, key))));
    }
  }

  private static void setValuesByData(MapBasedObject result, MapBasedObjectData data) {
    setValuesByDataMap(result, data.getStringPropertyMap(), String.class);
    setValuesByDataMap(result, data.getStringListMap(), String.class);
    setValuesByDataMap(result, data.getIntegerPropertyMap(), Integer.class);
    setValuesByDataMap(result, data.getIntegerListMap(), Integer.class);
    setValuesByDataMap(result, data.getLongPropertyMap(), Long.class);
    setValuesByDataMap(result, data.getLongListMap(), Long.class);
    setValuesByDataMap(result, data.getDoublePropertyMap(), Double.class);
    setValuesByDataMap(result, data.getDoubleListMap(), Double.class);
    setValuesByDataMap(result, data.getBooleanPropertyMap(), Boolean.class);
    setValuesByDataMap(result, data.getBooleanListMap(), Boolean.class);
    setValuesByDataMap(result, data.getUriPropertyMap(), URI.class);
    setValuesByDataMap(result, data.getUriListMap(), URI.class);
    setValuesByDataMap(result, data.getLocalDatePropertyMap(), LocalDate.class);
    setValuesByDataMap(result, data.getLocalDateListMap(), LocalDate.class);
    setValuesByDataMap(result, data.getLocalTimePropertyMap(), LocalTime.class);
    setValuesByDataMap(result, data.getLocalTimeListMap(), LocalTime.class);
    setValuesByDataMap(result, data.getLocalDateTimePropertyMap(), LocalDateTime.class);
    setValuesByDataMap(result, data.getLocalDateTimeListMap(), LocalDateTime.class);
  }

  private static void setValuesByDataMap(MapBasedObject object, Map<String, ?> dataMap,
      Class<?> clazz) {
    if (dataMap != null) {
      dataMap.forEach((key, value) -> {
        object.setValueByPath(key, MapBasedObjectUtil.getActualValue(value), clazz);
      });
    }
  }

  private static String getObjectPath(String parentPath, String key) {
    return parentPath.isEmpty() ? key : parentPath + "/" + key;
  }

  private void addProperty(String key, Object value) {
    propertyMap.put(key == null ? key : key.toUpperCase(), value);
  }

  private Object getProperty(String key) {
    return propertyMap.get(key);
  }

  /**
   * Merges all the maps from given data list into property maps managed by the newly created list
   * of {@link MapBasedObject}s.
   * 
   * @param datas
   * @return The new list of {@link MapBasedObject}s including the given data properties.
   */
  public static List<MapBasedObject> listOf(List<MapBasedObjectData> datas) {
    return listOf(datas, "");
  }

  /**
   * Merges all the maps from given data list into property maps managed by the newly created list
   * of {@link MapBasedObject}s. Use the method only for creating embedded {@link MapBasedObject}s,
   * otherwise use the one parameter method!
   * 
   * @param datas
   * @param path The property name of the parent {@link MapBasedObject}s in order separated by
   *        slashes.
   * @return The new list of {@link MapBasedObject}s including the given data properties.
   */
  public static List<MapBasedObject> listOf(List<MapBasedObjectData> datas, String path) {
    List<MapBasedObject> result = new ArrayList<>();
    int ind = 0;
    for (MapBasedObjectData data : datas) {
      result.add(of(data, path + "/" + ind++));
    }
    return result;
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
    result.setUri(object.dataUri);
    object.propertyMap.forEach((key, value) -> {
      MapBasedObjectUtil.addObjectPropertyToData(result, key, value);
    });
    return result;
  }

  /**
   * Constructs the changes by the current modification state and clear the modification states.
   * 
   * @return
   */
  @Override
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
      if (value instanceof MapBasedObject) {
        result = renderReferenceChange(result, changeState, key, value, actualValue);

      } else if (value instanceof List && !MapBasedObjectUtil.isValueList(value)) {
        result = renderCollectionChange(result, changeState, key, value);

      } else if (value == null) {
        result = renderDeletedReferenceChange(result, changeState, key);

      } else {
        result = renderPropertyChange(result, changeState, key, actualValue);
      }
    }

    result = renderDeletedFromPropertyMapChanges(result);

    deletedObjectItems.clear();
    previousState = deepCopy();

    return Optional.ofNullable(result);
  }

  private ObjectChange renderReferenceChange(ObjectChange result, ChangeState changeState,
      String key, Object value, Object actualValue) {
    MapBasedObject embeddedObj = (MapBasedObject) value;
    Optional<ObjectChange> refChangeOpt = embeddedObj.renderAndCleanChanges();
    if (refChangeOpt.isPresent()) {
      if (result == null) {
        result = new ObjectChange(path, changeState);
      }
      ObjectChange refChange = refChangeOpt.get();
      result.getReferences().add(new ReferenceChange(path, key, refChange));
      result.getReferencedObjects().add(new ReferencedObjectChange(path, key,
          new ObjectChangeSimple(embeddedObj.path, refChange.getOperation(), actualValue)));
    }
    return result;
  }

  private ObjectChange renderCollectionChange(ObjectChange result, ChangeState changeState,
      String key, Object value) {
    List<?> list = (List<?>) value;
    if (list.isEmpty()) {
      result = renderDeletedCollectionChange(result, changeState, key);

    } else if (list.get(0) instanceof MapBasedObject) {
      result = renderCollectionChange(result, changeState, key, list);
    }
    return result;
  }

  private ObjectChange renderCollectionChange(ObjectChange result, ChangeState changeState,
      String key, List<?> list) {
    CollectionChange collectionChange = new CollectionChange(path, key);
    CollectionObjectChange collectionObjectChange = new CollectionObjectChange(path, key);

    int ind = 0;
    for (Object obj : list) {
      String itemPath = getCollectionChangePath(key, ind);
      Optional<ObjectChange> itemChangeOpt =
          ((MapBasedObject) obj).renderAndCleanChanges();
      if (itemChangeOpt.isPresent()) {
        ObjectChange itemChange = itemChangeOpt.get();
        collectionChange.getChanges().add(itemChange);
        collectionObjectChange.getChanges()
            .add(new ObjectChangeSimple(itemPath, itemChange.getOperation(), obj));
      }
      ind++;
    }

    for (MapBasedObject deletedObj : deletedObjectItems) {
      String deletedPath = deletedObj.path;
      String listKey = deletedPath.substring(0, deletedPath.indexOf(StringConstant.SLASH));
      if (listKey.equals(key)) {
        String deletedItemPath = getObjectPath(path, deletedPath);
        collectionChange.getChanges().add(new ObjectChange(deletedItemPath, ChangeState.DELETED));
        collectionObjectChange.getChanges()
            .add(new ObjectChangeSimple(deletedItemPath, ChangeState.DELETED, deletedObj));
      }
    }

    if (!collectionChange.getChanges().isEmpty()) {
      if (result == null) {
        result = new ObjectChange(path, changeState);
      }
      result.getCollections().add(collectionChange);
      result.getCollectionObjects().add(collectionObjectChange);
    }
    return result;
  }

  private ObjectChange renderDeletedCollectionChange(ObjectChange result, ChangeState changeState,
      String key) {
    if (previousState != null) {
      Object prevValue = previousState.getProperty(key);
      if (prevValue != null && prevValue instanceof List<?>) {
        List<?> prevList = (List<?>) prevValue;
        if (!prevList.isEmpty() && prevList.get(0) instanceof MapBasedObject) {
          CollectionChange collectionChange = new CollectionChange(path, key);
          CollectionObjectChange collectionObjectChange = new CollectionObjectChange(path, key);
          int ind = 0;
          for (Object obj : prevList) {
            String itemPath = getCollectionChangePath(key, ind);
            collectionChange.getChanges().add(new ObjectChange(itemPath, ChangeState.DELETED));
            collectionObjectChange.getChanges()
                .add(new ObjectChangeSimple(itemPath, ChangeState.DELETED, obj));
            ind++;
          }
          if (result == null) {
            result = new ObjectChange(path, changeState);
          }
          result.getCollections().add(collectionChange);
          result.getCollectionObjects().add(collectionObjectChange);
        }
      }
    }
    return result;
  }

  private ObjectChange renderDeletedReferenceChange(ObjectChange result, ChangeState changeState,
      String key) {
    if (previousState != null) {
      Object prevValue = previousState.getProperty(key);
      if (prevValue != null) {
        if (result == null) {
          result = new ObjectChange(path, changeState);
        }
        result.getReferences()
            .add(new ReferenceChange(path, key, new ObjectChange(key, ChangeState.DELETED)));
        result.getReferencedObjects().add(new ReferencedObjectChange(path, key,
            new ObjectChangeSimple(path + StringConstant.SLASH + key, ChangeState.DELETED, null)));
      }
    }
    return result;
  }

  private ObjectChange renderPropertyChange(ObjectChange result, ChangeState changeState,
      String key, Object actualValue) {
    if (previousState == null || !previousState.propertyMap.containsKey(key)) {
      if (actualValue != null) {
        if (result == null) {
          result = new ObjectChange(path, changeState);
        }
        result.getProperties().add(new PropertyChange(path, key, null, actualValue));
      }

    } else {
      Object prevValue =
          MapBasedObjectUtil.getActualValue(previousState.getProperty(key));
      if (prevValue == null && actualValue == null) {
        return result;
      }
      if (prevValue == null || actualValue == null || !prevValue.equals(actualValue)) {
        if (result == null) {
          result = new ObjectChange(path, changeState);
        }
        result.getProperties().add(new PropertyChange(path, key, prevValue, actualValue));
      }
    }
    return result;
  }

  private String getCollectionChangePath(String key, int index) {
    return getObjectPath(path, key) + StringConstant.SLASH + index;
  }

  private ObjectChange renderDeletedFromPropertyMapChanges(ObjectChange result) {
    if (previousState != null) {
      for (Map.Entry<String, Object> entry : previousState.propertyMap.entrySet()) {
        String key = entry.getKey();

        if (!propertyMap.containsKey(key)) {
          Object value = entry.getValue();
          Object actualValue = MapBasedObjectUtil.getActualValue(value);

          if (value instanceof MapBasedObject) {
            ObjectChange change = new ObjectChange(key, ChangeState.DELETED);
            if (result == null) {
              result = new ObjectChange(path, ChangeState.MODIFIED);
            }
            result.getReferences().add(new ReferenceChange(path, key, change));
            result.getReferencedObjects()
                .add(new ReferencedObjectChange(path, key, new ObjectChangeSimple(
                    path + StringConstant.SLASH + key, ChangeState.DELETED, actualValue)));

          } else if (value instanceof List) {
            List<?> list = (List<?>) actualValue;
            if (!list.isEmpty()) {
              CollectionChange collectionChange = new CollectionChange(path, key);
              CollectionObjectChange collectionObjectChange = new CollectionObjectChange(path, key);

              int ind = 0;
              for (Object obj : list) {
                collectionChange.getChanges()
                    .add(new ObjectChange(getCollectionChangePath(key, ind), ChangeState.DELETED));
                collectionObjectChange.getChanges()
                    .add(new ObjectChangeSimple(getCollectionChangePath(key, ind),
                        ChangeState.DELETED, obj));
                ind++;
              }

              if (!collectionChange.getChanges().isEmpty()) {
                if (result == null) {
                  result = new ObjectChange(path, ChangeState.MODIFIED);
                }
                result.getCollections().add(collectionChange);
                result.getCollectionObjects().add(collectionObjectChange);
              }
            }
          }
        }
      }
    }
    return result;
  }

  /**
   * Creates a deep copy of this MapBasedObject (no references remain from the original object).
   * Previous state of the object is not copied.
   * 
   * @return
   */
  public MapBasedObject deepCopy() {
    MapBasedObject result = new MapBasedObject(path);
    result.dataUri = dataUri;
    result.deletedObjectItems.addAll(deletedObjectItems);
    propertyMap.forEach((key, value) -> {
      result.addProperty(key, MapBasedObjectUtil.deepCopyPropertyValue(value));
    });
    return result;
  }

  /**
   * @param path
   * @return The actual value found in the property map by the given path.
   */
  @Override
  public Object getValueByPath(String path) {
    return getValueByPath(path, true);
  }

  /**
   * @param path
   * @param getData In case of embedded {@link MapBasedObject}s, decides whether in form of
   *        {@link MapBasedObject} or {@link MapBasedObjectData} the result will be returned.
   * @return The actual value found in the property map by the given path. (Exceptions:
   *         {@link MapBasedObject}s, if {@code getData} is true.)
   */
  public Object getValueByPath(String path, boolean getData) {
    if (Strings.isNullOrEmpty(path) || "/".equals(path)) {
      return getData ? toData(this) : this;
    }

    String upperPath = path.toUpperCase();
    String rootPath = PathUtility.getRootPath(upperPath);

    Object propertyValue = getProperty(rootPath);
    Object value = MapBasedObjectUtil.getActualValue(propertyValue, getData);

    if (propertyValue instanceof MapBasedObject) {
      if (PathUtility.getPathSize(upperPath) > 1) {
        value = ((MapBasedObject) propertyValue).getValueByPath(
            PathUtility.nextFullPath(upperPath),
            getData);
      }
    } else if (propertyValue instanceof List<?> &&
        !((List<?>) propertyValue).isEmpty() &&
        ((List<?>) propertyValue).get(0) instanceof MapBasedObject) {

      if (PathUtility.getPathSize(upperPath) > 1) {
        String nextPath = PathUtility.nextFullPath(upperPath);
        try {
          int index = Integer.parseInt(PathUtility.getRootPath(nextPath));
          List<MapBasedObject> list = (List<MapBasedObject>) propertyValue;
          value = PathUtility.getPathSize(nextPath) == 1
              ? list.get(index)
              : list.get(index).getValueByPath(PathUtility.nextFullPath(nextPath), getData);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              "Item index not found after MapBasedObject list property name, path: " + path);
        }
      }
    } else if (value instanceof List<?> &&
        !((List<?>) value).isEmpty() &&
        PathUtility.getPathSize(upperPath) == 2) {
      try {
        int index = Integer.parseInt(PathUtility.getLastPath(upperPath));
        List<?> list = (List<?>) value;
        value = list.get(index);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            "Item index not found after value list property name, path: " + path);
      }
    } else {
      if (PathUtility.getPathSize(upperPath) > 1) {
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
  @Override
  public void setValueByPath(String path, Object value) {
    setValueByPath(path, value, null);
  }

  /**
   * Sets the given value to the property found by the given path.
   * 
   * @param path
   * @param value The actual value that will be set to the property value.
   * @param clazz Defines the property value type, if the property is newly added and the value is
   *        null or empty list.
   */
  public void setValueByPath(String path, Object value, Class<?> clazz) {
    String upperPath = path.toUpperCase();
    String lastPath = PathUtility.getLastPath(upperPath);
    MapBasedObject parentObject = getParentObject(upperPath);

    if (isItemIndex(lastPath)) {
      int pathSize = PathUtility.getPathSize(upperPath);
      String key = PathUtility.subpath(upperPath, pathSize - 2, pathSize - 1);
      Object currentValue = parentObject.getProperty(key);
      setValueListItem(currentValue, Integer.valueOf(lastPath), value, parentObject.path);

    } else {
      String key = lastPath;
      Object currentValue = parentObject.getProperty(key);

      if (value == null && clazz == null && currentValue == null) {
        throw new IllegalArgumentException(
            "Newly added property's value cannot be set to null, path: " + path);
      }

      Object newValue = MapBasedObjectUtil.createPropertyValue(key, value, currentValue, clazz,
          parentObject.path);
      if (currentValue == null || currentValue.getClass().equals(newValue.getClass())) {
        if (currentValue instanceof MapBasedObject) {
          transferAttributes((MapBasedObject) currentValue, (MapBasedObject) newValue);

        } else if (currentValue instanceof List<?>) {
          storeInfoFromOldList((List<MapBasedObject>) currentValue,
              (List<MapBasedObject>) newValue);
        }
        parentObject.addProperty(key, newValue);

      } else {
        throw new IllegalArgumentException(
            "Old and new value classes are not matching:" + StringConstant.NEW_LINE +
                "Expected class: " + currentValue.getClass().getName() + StringConstant.NEW_LINE +
                "Given class: " + value.getClass().getName());
      }
    }
  }

  private void setValueListItem(Object valueList, int index, Object value, String parentPath) {
    if (value instanceof MapBasedObject || value instanceof MapBasedObjectData) {
      if (valueList instanceof List<?>) {
        List<MapBasedObject> list = (List<MapBasedObject>) valueList;
        int size = list.size();

        MapBasedObject newObj = value instanceof MapBasedObject
            ? (MapBasedObject) value
            : of((MapBasedObjectData) value, parentPath + "/" + index);

        if (size > index) {
          transferAttributes(list.get(index), newObj);
          list.set(index, newObj);

        } else if (size == index) {
          list.add(newObj);

        } else {
          throw new IndexOutOfBoundsException(
              "List size: " + size + ", Given index: " + index);
        }
      }

    } else {
      MapBasedObjectUtil.setPropertyValueListItem(valueList, index, value);
    }
  }

  /**
   * Store the information from the previous MapBasedObject list items in the new ones. Transfer the
   * MapBasedObject attributes other than the propertyMap from the old object to the new one. If the
   * size of the old list is bigger, store the deleted MapBasedObject items too.
   * 
   * @param oldList
   * @param newList
   */
  private void storeInfoFromOldList(List<MapBasedObject> oldList, List<MapBasedObject> newList) {
    int ind = 0;
    while (ind < oldList.size() && ind < newList.size()) {
      transferAttributes(oldList.get(ind), newList.get(ind));
      ind++;
    }
    while (ind < oldList.size()) {
      deletedObjectItems.add(oldList.get(ind));
      ind++;
    }
  }

  /**
   * Transfer the MapBasedObject attributes other than the propertyMap from the given object to the
   * other given one.
   * 
   * @param from
   * @param to
   */
  private void transferAttributes(MapBasedObject from, MapBasedObject to) {
    to.path = from.path;
    to.previousState = from.deepCopy();
    to.deletedObjectItems.addAll(from.deletedObjectItems);
  }

  /**
   * Adds the given value into the collection found by the given path.
   * 
   * @param path
   * @param value
   * @return The {@link MapBasedObject} including the collection that contains the new value.
   */
  @Override
  public MapBasedObject addValueByPath(String path, Object value) {
    String upperPath = path.toUpperCase();
    String key = PathUtility.getLastPath(upperPath);

    if (isItemIndex(key)) {
      throw new IllegalArgumentException(
          "Value cannot be added into a list item, path: " + path);
    }

    MapBasedObject parentObject = getParentObject(upperPath);
    Object mapValueList = parentObject.getProperty(key);

    if (mapValueList == null) {
      throw new IllegalArgumentException("Collection is null, path: " + path);
    }

    if (MapBasedObjectUtil.canBeAdded(mapValueList, value)) {
      Object collectionValue;
      Object toAdd = MapBasedObjectUtil.convertToValidValue(value, parentObject.path);
      if (toAdd instanceof MapBasedObject) {
        collectionValue = parentObject.getProperty(key);
      } else {
        collectionValue = parentObject.getValueByPath(key);
      }
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
  @Override
  public void removeValueByPath(String path) {
    String upperPath = path.toUpperCase();
    if (PathUtility.getPathSize(upperPath) < 2) {
      throw new IllegalArgumentException(
          "Path must contain at least one property name and element index, path: "
              + (path == null ? "null" : path));
    }

    Object foundValue = getValueByPath(PathUtility.getParentPath(upperPath));

    if (foundValue instanceof Collection) {
      Collection collection = (Collection) foundValue;
      try {
        int index = Integer.parseInt(PathUtility.getLastPath(upperPath));
        if (index >= collection.size()) {
          throw new IndexOutOfBoundsException(
              "Collection size: " + collection.size() + ", Given index: " + index);
        }
        int counter = 0;
        for (Object object : collection) {
          if (counter == index) {
            if (object instanceof MapBasedObject) {
              deletedObjectItems.add((MapBasedObject) object);
            }
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

  /**
   * @param path The parent object of the property found by this path will be searched. If a
   *        MapBasedObject is not found in the path, it will be created.
   * @return The MapBasedObject that contains directly the property found by the given path.
   */
  private MapBasedObject getParentObject(String path) {
    String parentPath = PathUtility.getParentPath(path);
    if (parentPath == null) {
      return this;
    }
    return getOrCreateObject(parentPath);
  }

  /**
   * @param path The MapBasedObject will be searched by this path. If a MapBasedObject is not found
   *        in the path, it will be created.
   * @return The MapBasedObject found (or created) by the given path.
   */
  private MapBasedObject getOrCreateObject(String path) {
    String rootPath = PathUtility.getRootPath(path);
    Object value = getProperty(rootPath);

    if (value == null) {
      value = new MapBasedObject();
      addProperty(rootPath, value);
    }

    if (value instanceof MapBasedObject) {
      if (PathUtility.getPathSize(path) == 1) {
        return (MapBasedObject) value;
      }
      value = ((MapBasedObject) value).getOrCreateObject(PathUtility.nextFullPath(path));

    } else if (value instanceof List<?> &&
        !((List<?>) value).isEmpty() &&
        ((List<?>) value).get(0) instanceof MapBasedObject) {

      if (PathUtility.getPathSize(path) == 1) {
        return this;
      } else {
        String nextPath = PathUtility.nextFullPath(path);
        try {
          int index = Integer.parseInt(PathUtility.getRootPath(nextPath));
          List<MapBasedObject> list = (List<MapBasedObject>) value;
          if (PathUtility.getPathSize(path) == 2) {
            return list.get(index);
          } else {
            value = list.get(index).getOrCreateObject(PathUtility.nextFullPath(nextPath));
          }
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              "Item index not found after MapBasedObject list property name, path: " + path);
        }
      }
    } else if (MapBasedObjectUtil.isValueList(value)) {
      return this;
    } else {
      throw new IllegalArgumentException(
          "Not embedded single MapBasedObject or list found in the middle of the path, class: " +
              (value == null ? "null" : value.getClass().getName()));
    }
    return (MapBasedObject) value;
  }

  @Override
  public DomainObjectRef getValueRefByPath(String path) {
    if (Strings.isNullOrEmpty(path) || "/".equals(path)) {
      return this;
    }

    DomainObjectRef foundRef = null;

    String upperPath = path.toUpperCase();
    String rootPath = PathUtility.getRootPath(upperPath);
    Object propertyValue = getProperty(rootPath);

    if (propertyValue instanceof MapBasedObject) {
      foundRef = (MapBasedObject) propertyValue;
      if (PathUtility.getPathSize(upperPath) > 1) {
        foundRef = foundRef.getValueRefByPath(PathUtility.nextFullPath(upperPath));
      }
    } else if (propertyValue instanceof List<?> &&
        !((List<?>) propertyValue).isEmpty() &&
        ((List<?>) propertyValue).get(0) instanceof MapBasedObject) {

      if (PathUtility.getPathSize(upperPath) > 1) {
        String nextPath = PathUtility.nextFullPath(upperPath);
        try {
          int index = Integer.parseInt(PathUtility.getRootPath(nextPath));
          List<MapBasedObject> list = (List<MapBasedObject>) propertyValue;
          foundRef = PathUtility.getPathSize(nextPath) == 1
              ? list.get(index)
              : list.get(index).getValueRefByPath(PathUtility.nextFullPath(nextPath));
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              "Item index not found after MapBasedObject list property name, path: " + path);
        }
      }
    } else {
      throw new IllegalArgumentException(
          "Not embedded MapBasedObject or list found in the middle of the path, class: " +
              (propertyValue == null ? "null" : propertyValue.getClass().getName()));
    }
    return foundRef;
  }

  @Override
  public void reevaluateChanges() {
    previousState = null;
  }

  @Override
  public void setObject(Object loadedObject) {
    MapBasedObjectData newObject = (MapBasedObjectData) loadedObject;
    propertyMap.clear();
    deletedObjectItems.clear();
    setData(newObject, getPath(), this);
  }

  @Override
  public Object getObject() {
    return toData(this);
  }

  @Override
  public <T> T getWrapper(Class<T> clazz) {
    if (clazz.equals(MapBasedObject.class)) {
      return (T) this;
    }
    throw new IllegalArgumentException("MapBasedObject's wrapper can only be MapBasedObject.");
  }

  /**
   * @param propertyName
   * @return The actual value or the original embedded {@link MapBasedObject}s found in the property
   *         map by the given path.
   */
  @Override
  public Object getValue(String propertyName) {
    return getValueByPath(propertyName, false);
  }

}