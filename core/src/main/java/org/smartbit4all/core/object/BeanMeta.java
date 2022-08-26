package org.smartbit4all.core.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The meta is the result of discovering the clazz over the reflection API.
 * 
 * @author Peter Boros
 */
public final class BeanMeta {

  private final Class<?> clazz;

  /**
   * Be careful, the key is upper case!
   */
  private final Map<String, PropertyMeta> properties = new HashMap<>();

  BeanMeta(Class<?> clazz) {
    super();
    this.clazz = clazz;
  }

  public final Class<?> getClazz() {
    return clazz;
  }

  public final Map<String, PropertyMeta> getProperties() {
    return properties;
  }

  /**
   * Construct the description of the meta for logging and debugging purposes.
   * 
   * @return
   */
  public String getDescription() {
    StringBuilder sb = new StringBuilder();
    sb.append(clazz.getName());
    for (PropertyMeta propertyMeta : properties.values().stream()
        .sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).collect(Collectors.toList())) {
      sb.append(StringConstant.NEW_LINE);
      sb.append(StringConstant.TAB);
      propertyMeta.appendTo(sb);
    }
    return sb.toString();
  }

  /**
   * Copy the object by the current meta. The by value properties are not copied only the
   * collections and the referred domain objects.
   * 
   * @param o
   * @return
   */
  @SuppressWarnings("unchecked")
  public Object deepCopy(Object o) {
    try {
      Object newInstance = clazz.newInstance();
      for (PropertyMeta propertyMeta : properties.values()) {
        Object value = propertyMeta.getValue(o);
        if (value != null) {
          switch (propertyMeta.getKind()) {
            case VALUE:
              if (propertyMeta.getType().isAssignableFrom(List.class)) {
                // We constructs a new list but the values are the same.
                ArrayList<Object> newList =
                    new ArrayList<>((List<Object>) value);
                propertyMeta.setValue(newInstance, newList);
              } else if (propertyMeta.getType().isAssignableFrom(Map.class)) {
                // We constructs a new map but the values are the same.
                Map<Object, Object> newList =
                    new HashMap<>((Map<Object, Object>) value);
                propertyMeta.setValue(newInstance, newList);
              }
              break;

            case REFERENCE:
              propertyMeta.setValue(newInstance,
                  BeanMetaUtil.meta(propertyMeta.getType()).deepCopy(value));
              break;

            case COLLECTION:
              List<Object> list = (List<Object>) value;
              ArrayList<Object> newList = new ArrayList<>();
              BeanMeta metaList = BeanMetaUtil.meta(propertyMeta.getReferredType());
              for (Object object : list) {
                newList.add(metaList.deepCopy(object));
              }
              propertyMeta.setValue(newInstance, newList);
              break;

            case MAP:
              Map<Object, Object> map = (Map<Object, Object>) value;
              Map<Object, Object> newMap = new HashMap<>();
              BeanMeta metaMap = BeanMetaUtil.meta(propertyMeta.getReferredType());
              for (Entry<Object, Object> entry : map.entrySet()) {
                newMap.put(entry.getKey(), metaMap.deepCopy(entry.getValue()));
              }
              propertyMeta.setValue(newInstance, newMap);
              break;

            default:
              break;
          }
        }
      }
      return newInstance;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException("Unable to copy the " + clazz.getName() + "object", e);
    }
  }

  /**
   * Deep compare the two objects and returns true if the values in the two object hierarchy is the
   * same..
   * 
   * @param o1
   * @param o2
   * @return
   */
  @SuppressWarnings("unchecked")
  public boolean deepEquals(Object o1, Object o2) {
    for (PropertyMeta propertyMeta : properties.values()) {
      Object value1 = propertyMeta.getValue(o1);
      Object value2 = propertyMeta.getValue(o2);
      if (value1 != null && value2 != null) {
        switch (propertyMeta.getKind()) {
          case VALUE:
            if (propertyMeta.getType().isAssignableFrom(List.class)) {
              // We compare the two lists value by value.
              List<Object> list1 = (List<Object>) value1;
              List<Object> list2 = (List<Object>) value2;
              if (!list1.equals(list2)) {
                return false;
              }
            } else if (propertyMeta.getType().isAssignableFrom(Map.class)) {
              // We compare the two maps.
              Map<Object, Object> map1 = (Map<Object, Object>) value1;
              Map<Object, Object> map2 = (Map<Object, Object>) value2;
              if (!map1.equals(map2)) {
                return false;
              }
            }
            break;

          case REFERENCE:
            BeanMeta meta = BeanMetaUtil.meta(propertyMeta.getReferredType());
            if (!meta.deepEquals(value1, value2)) {
              return false;
            }
            break;

          case COLLECTION:
            List<Object> list1 = (List<Object>) value1;
            List<Object> list2 = (List<Object>) value2;
            if (list1.size() != list2.size()) {
              return false;
            }
            BeanMeta metaList = BeanMetaUtil.meta(propertyMeta.getReferredType());
            ListIterator<Object> iterator2 = list2.listIterator();
            for (Object object1 : list1) {
              if (!metaList.deepEquals(object1,
                  iterator2.next())) {
                return false;
              }
            }
            break;

          case MAP:
            Map<Object, Object> map1 = (Map<Object, Object>) value1;
            Map<Object, Object> map2 = (Map<Object, Object>) value2;
            if (map1.size() != map2.size()) {
              return false;
            }
            BeanMeta metaMap = BeanMetaUtil.meta(propertyMeta.getReferredType());
            for (Entry<Object, Object> entry1 : map1.entrySet()) {
              Object map2Value = map2.get(entry1.getKey());
              if (map2Value == null) {
                return false;
              }
              if (!metaMap.deepEquals(entry1.getValue(),
                  map2Value)) {
                return false;
              }
            }
            break;

          default:
            break;
        }
      }
    }
    return true;
  }

}
