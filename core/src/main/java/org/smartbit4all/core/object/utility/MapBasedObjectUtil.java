package org.smartbit4all.core.object.utility;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.mapbasedobject.bean.BooleanValue;
import org.smartbit4all.api.mapbasedobject.bean.BooleanValueList;
import org.smartbit4all.api.mapbasedobject.bean.IntegerValue;
import org.smartbit4all.api.mapbasedobject.bean.IntegerValueList;
import org.smartbit4all.api.mapbasedobject.bean.LocalDateTimeValue;
import org.smartbit4all.api.mapbasedobject.bean.LocalDateTimeValueList;
import org.smartbit4all.api.mapbasedobject.bean.LongValue;
import org.smartbit4all.api.mapbasedobject.bean.LongValueList;
import org.smartbit4all.api.mapbasedobject.bean.MapBasedObjectData;
import org.smartbit4all.api.mapbasedobject.bean.ObjectValue;
import org.smartbit4all.api.mapbasedobject.bean.ObjectValueList;
import org.smartbit4all.api.mapbasedobject.bean.StringValue;
import org.smartbit4all.api.mapbasedobject.bean.StringValueList;
import org.smartbit4all.core.object.MapBasedObject;
import org.smartbit4all.core.utility.StringConstant;

/**
 * Utility class for {@link MapBasedObjectData} and {@link MapBasedObject} property value
 * conversions and class matching checks.
 * 
 * @author Andras Pallo
 *
 */
public class MapBasedObjectUtil {

  /**
   * Adds property with the given key and value got from a {@link MapBasedObject}.
   * 
   * @param data
   * @param key
   * @param value
   */
  public static void addObjectPropertyToData(MapBasedObjectData data, String key, Object value) {
    if (value instanceof StringValue) {
      data.putStringPropertyMapItem(key, (StringValue) value);

    } else if (value instanceof StringValueList) {
      data.putStringListMapItem(key, (StringValueList) value);

    } else if (value instanceof IntegerValue) {
      data.putIntegerPropertyMapItem(key, (IntegerValue) value);

    } else if (value instanceof IntegerValueList) {
      data.putIntegerListMapItem(key, (IntegerValueList) value);

    } else if (value instanceof LongValue) {
      data.putLongPropertyMapItem(key, (LongValue) value);

    } else if (value instanceof LongValueList) {
      data.putLongListMapItem(key, (LongValueList) value);

    } else if (value instanceof BooleanValue) {
      data.putBooleanPropertyMapItem(key, (BooleanValue) value);

    } else if (value instanceof BooleanValueList) {
      data.putBooleanListMapItem(key, (BooleanValueList) value);

    } else if (value instanceof LocalDateTimeValue) {
      data.putLocalDateTimePropertyMapItem(key, (LocalDateTimeValue) value);

    } else if (value instanceof LocalDateTimeValueList) {
      data.putLocalDateTimeListMapItem(key, (LocalDateTimeValueList) value);

    } else if (value instanceof MapBasedObject) {
      data.putObjectPropertyMapItem(key,
          new ObjectValue().name(key).value(MapBasedObject.toData((MapBasedObject) value)));

    } else if (value instanceof List<?>) {
      List<?> list = (List<?>) value;
      if (!list.isEmpty() && list.get(0) instanceof MapBasedObject) {
        List<MapBasedObjectData> objects = new ArrayList<>();
        for (Object object : list) {
          objects.add(MapBasedObject.toData((MapBasedObject) object));
        }
        data.putObjectListMapItem(key, new ObjectValueList().name(key).values(objects));
      }
    }
  }

  /**
   * Creates a deep copy from the given {@link MapBasedObject} property value.
   * 
   * @param propertyValue
   * @return
   */
  public static Object deepCopyPropertyValue(Object propertyValue) {
    if (propertyValue == null) {
      throw new NullPointerException("MapBasedObject's property value cannot be null.");

    } else if (propertyValue instanceof StringValue) {
      StringValue copiedValue = (StringValue) propertyValue;
      return new StringValue().name(copiedValue.getName()).value(copiedValue.getValue());

    } else if (propertyValue instanceof StringValueList) {
      StringValueList copiedValue = (StringValueList) propertyValue;
      return new StringValueList().name(copiedValue.getName())
          .values(new ArrayList<>(copiedValue.getValues()));

    } else if (propertyValue instanceof IntegerValue) {
      IntegerValue copiedValue = (IntegerValue) propertyValue;
      return new IntegerValue().name(copiedValue.getName()).value(copiedValue.getValue());

    } else if (propertyValue instanceof IntegerValueList) {
      IntegerValueList copiedValue = (IntegerValueList) propertyValue;
      return new IntegerValueList().name(copiedValue.getName())
          .values(new ArrayList<>(copiedValue.getValues()));

    } else if (propertyValue instanceof LongValue) {
      LongValue copiedValue = (LongValue) propertyValue;
      return new LongValue().name(copiedValue.getName()).value(copiedValue.getValue());

    } else if (propertyValue instanceof LongValueList) {
      LongValueList copiedValue = (LongValueList) propertyValue;
      return new LongValueList().name(copiedValue.getName())
          .values(new ArrayList<>(copiedValue.getValues()));

    } else if (propertyValue instanceof BooleanValue) {
      BooleanValue copiedValue = (BooleanValue) propertyValue;
      return new BooleanValue().name(copiedValue.getName()).value(copiedValue.getValue());

    } else if (propertyValue instanceof BooleanValueList) {
      BooleanValueList copiedValue = (BooleanValueList) propertyValue;
      return new BooleanValueList().name(copiedValue.getName())
          .values(new ArrayList<>(copiedValue.getValues()));

    } else if (propertyValue instanceof LocalDateTimeValue) {
      LocalDateTimeValue copiedValue = (LocalDateTimeValue) propertyValue;
      return new LocalDateTimeValue().name(copiedValue.getName()).value(copiedValue.getValue());

    } else if (propertyValue instanceof LocalDateTimeValueList) {
      LocalDateTimeValueList copiedValue = (LocalDateTimeValueList) propertyValue;
      return new LocalDateTimeValueList().name(copiedValue.getName())
          .values(new ArrayList<>(copiedValue.getValues()));

    } else if (propertyValue instanceof MapBasedObject) {
      return ((MapBasedObject) propertyValue).deepCopy();

    } else if (propertyValue instanceof List<?>) {
      List<?> list = (List<?>) propertyValue;
      if (!list.isEmpty() && list.get(0) instanceof MapBasedObject) {
        List<MapBasedObject> objects = new ArrayList<>();
        for (Object object : list) {
          objects.add(((MapBasedObject) object).deepCopy());
        }
        return objects;
      }
    }
    throw new IllegalArgumentException(
        "Invalid property value found, class: " + propertyValue.getClass().getName());
  }

  /**
   * Returns the actual value from the given {@link MapBasedObject} property value.
   * 
   * @param propertyValue
   * @return
   */
  public static Object getActualValue(Object propertyValue) {
    if (propertyValue == null) {
      return propertyValue;

    } else if (propertyValue instanceof StringValue) {
      return ((StringValue) propertyValue).getValue();

    } else if (propertyValue instanceof StringValueList) {
      return ((StringValueList) propertyValue).getValues();

    } else if (propertyValue instanceof IntegerValue) {
      return ((IntegerValue) propertyValue).getValue();

    } else if (propertyValue instanceof IntegerValueList) {
      return ((IntegerValueList) propertyValue).getValues();

    } else if (propertyValue instanceof LongValue) {
      return ((LongValue) propertyValue).getValue();

    } else if (propertyValue instanceof LongValueList) {
      return ((LongValueList) propertyValue).getValues();

    } else if (propertyValue instanceof BooleanValue) {
      return ((BooleanValue) propertyValue).getValue();

    } else if (propertyValue instanceof BooleanValueList) {
      return ((BooleanValueList) propertyValue).getValues();

    } else if (propertyValue instanceof LocalDateTimeValue) {
      return ((LocalDateTimeValue) propertyValue).getValue();

    } else if (propertyValue instanceof LocalDateTimeValueList) {
      return ((LocalDateTimeValueList) propertyValue).getValues();

    } else if (propertyValue instanceof MapBasedObject) {
      return propertyValue;

    } else if (propertyValue instanceof List<?>) {
      List<?> list = (List<?>) propertyValue;
      if (list.isEmpty() || list.get(0) instanceof MapBasedObject) {
        return propertyValue;
      }
    }
    throw new IllegalArgumentException(
        "Unknown value class: " + propertyValue.getClass().getName());
  }

  /**
   * Creates a {@link MapBasedObject} property value with the given key and value. If value is null,
   * then creates it according to the given previous value. If the previous value is null too, then
   * creates it according to the given class.
   * 
   * @param key
   * @param value
   * @param prevValue
   * @param clazz
   * @return
   */
  public static Object createPropertyValue(String key, Object value, Object prevValue,
      Class<?> clazz) {
    if (value == null) {
      return createNullPropertyValue(key, prevValue, clazz);

    } else if (value instanceof String) {
      return new StringValue().name(key).value((String) value);

    } else if (value instanceof Integer) {
      return new IntegerValue().name(key).value((Integer) value);

    } else if (value instanceof Long) {
      return new LongValue().name(key).value((Long) value);

    } else if (value instanceof Boolean) {
      return new BooleanValue().name(key).value((Boolean) value);

    } else if (value instanceof LocalDateTime) {
      return new LocalDateTimeValue().name(key).value((LocalDateTime) value);

    } else if (value instanceof MapBasedObjectData) {
      return MapBasedObject.of((MapBasedObjectData) value);

    } else if (value instanceof MapBasedObject) {
      return value;

    } else if (value instanceof List<?>) {
      List<?> list = (List<?>) value;
      if (list.isEmpty()) {
        if (clazz == null) {
          throw new IllegalArgumentException(
              "Newly added property's value cannot be set to empty list, key: " + key);
        }
        return createEmptyListPropertyValue(key, prevValue, clazz);

      } else {
        Object item = list.get(0);
        if (item instanceof String) {
          List<String> values =
              list.stream().map(e -> (String) e).collect(Collectors.toList());
          return new StringValueList().name(key).values(values);

        } else if (item instanceof Integer) {
          List<Integer> values =
              list.stream().map(e -> (Integer) e).collect(Collectors.toList());
          return new IntegerValueList().name(key).values(values);

        } else if (item instanceof Long) {
          List<Long> values =
              list.stream().map(e -> (Long) e).collect(Collectors.toList());
          return new LongValueList().name(key).values(values);

        } else if (item instanceof Boolean) {
          List<Boolean> values =
              list.stream().map(e -> (Boolean) e).collect(Collectors.toList());
          return new BooleanValueList().name(key).values(values);

        } else if (item instanceof LocalDateTime) {
          List<LocalDateTime> values =
              list.stream().map(e -> (LocalDateTime) e).collect(Collectors.toList());
          return new LocalDateTimeValueList().name(key).values(values);

        } else if (item instanceof MapBasedObjectData) {
          List<MapBasedObject> valueList = list.stream()
              .map(e -> MapBasedObject.of((MapBasedObjectData) e)).collect(Collectors.toList());
          return valueList;

        } else if (item instanceof MapBasedObject) {
          return value;
        }
      }
    }
    throw new IllegalArgumentException("Unknown value class: " + value.getClass().getName());
  }

  private static Object createNullPropertyValue(String key, Object prevValue, Class<?> clazz) {
    if (prevValue == null) {
      return createNewPropertyValue(key, clazz);

    } else if (prevValue instanceof StringValue) {
      return new StringValue().name(key).value(null);

    } else if (prevValue instanceof StringValueList) {
      return new StringValueList().name(key).values(null);

    } else if (prevValue instanceof IntegerValue) {
      return new IntegerValue().name(key).value(null);

    } else if (prevValue instanceof IntegerValueList) {
      return new IntegerValueList().name(key).values(null);

    } else if (prevValue instanceof LongValue) {
      return new LongValue().name(key).value(null);

    } else if (prevValue instanceof LongValueList) {
      return new LongValueList().name(key).values(null);

    } else if (prevValue instanceof BooleanValue) {
      return new BooleanValue().name(key).value(null);

    } else if (prevValue instanceof BooleanValueList) {
      return new BooleanValueList().name(key).values(null);

    } else if (prevValue instanceof LocalDateTimeValue) {
      return new LocalDateTimeValue().name(key).value(null);

    } else if (prevValue instanceof LocalDateTimeValueList) {
      return new LocalDateTimeValueList().name(key).values(null);

    } else if (prevValue instanceof MapBasedObject) {
      return new MapBasedObject();

    } else if (prevValue instanceof List<?>) {
      List<?> list = (List<?>) prevValue;
      if (list.isEmpty() || list.get(0) instanceof MapBasedObject) {
        return new ArrayList<>();
      }
    }
    throw new IllegalArgumentException("Invalid previously set value found, class: " + clazz);
  }

  private static Object createNewPropertyValue(String key, Class<?> clazz) {
    if (clazz.equals(String.class)) {
      return new StringValue().name(key).value(null);

    } else if (clazz.equals(Integer.class)) {
      return new IntegerValue().name(key).value(null);

    } else if (clazz.equals(Long.class)) {
      return new LongValue().name(key).value(null);

    } else if (clazz.equals(Boolean.class)) {
      return new BooleanValue().name(key).value(null);

    } else if (clazz.equals(LocalDateTime.class)) {
      return new LocalDateTimeValue().name(key).value(null);

    } else if (clazz.equals(MapBasedObjectData.class) || clazz.equals(MapBasedObject.class)) {
      return new MapBasedObject();
    }
    throw new IllegalArgumentException("Unknown value class: " + clazz);
  }

  private static Object createEmptyListPropertyValue(String key, Object prevValue, Class<?> clazz) {
    if (clazz.equals(String.class)) {
      return new StringValueList().name(key).values(new ArrayList<>());

    } else if (clazz.equals(Integer.class)) {
      return new IntegerValueList().name(key).values(new ArrayList<>());

    } else if (clazz.equals(Long.class)) {
      return new LongValueList().name(key).values(new ArrayList<>());

    } else if (clazz.equals(Boolean.class)) {
      return new BooleanValueList().name(key).values(new ArrayList<>());

    } else if (clazz.equals(LocalDateTime.class)) {
      return new LocalDateTimeValueList().name(key).values(new ArrayList<>());

    } else if (clazz.equals(MapBasedObjectData.class) || clazz.equals(MapBasedObject.class)) {
      return new ArrayList<>();
    }
    throw new IllegalArgumentException("Unknown value class: " + clazz);
  }

  /**
   * Decides whether the given value can be added to the given property map value list or not.
   * 
   * @param mapValueList
   * @param value
   * @return
   */
  public static boolean canBeAdded(Object mapValueList, Object value) {
    if (mapValueList == null) {
      throw new NullPointerException("Property map value list is null.");
    }

    String expectedValueClass = null;
    if (mapValueList instanceof StringValueList) {
      if (value == null || value instanceof String) {
        return true;
      }
      expectedValueClass = String.class.getName();

    } else if (mapValueList instanceof IntegerValueList) {
      if (value == null || value instanceof Integer) {
        return true;
      }
      expectedValueClass = Integer.class.getName();

    } else if (mapValueList instanceof LongValueList) {
      if (value == null || value instanceof Long) {
        return true;
      }
      expectedValueClass = Long.class.getName();

    } else if (mapValueList instanceof BooleanValueList) {
      if (value == null || value instanceof Boolean) {
        return true;
      }
      expectedValueClass = Boolean.class.getName();

    } else if (mapValueList instanceof LocalDateTimeValueList) {
      if (value == null || value instanceof LocalDateTime) {
        return true;
      }
      expectedValueClass = LocalDateTime.class.getName();

    } else if (mapValueList instanceof List<?>) {
      if (value instanceof MapBasedObject || value instanceof MapBasedObjectData) {
        return true;
      }
      expectedValueClass = MapBasedObject.class.getName();
    }

    if (expectedValueClass == null) {
      throw new IllegalArgumentException(
          "Not Collection found, class: "
              + (mapValueList == null ? "null" : mapValueList.getClass().getName()));

    } else {
      throw new IllegalArgumentException(
          "Collection and added value classes are not matching:" + StringConstant.NEW_LINE +
              "Expected class: " + expectedValueClass + StringConstant.NEW_LINE +
              "Given class: " + value.getClass().getName());
    }
  }

  public static void addNewPropertyToData(MapBasedObjectData data, String key, Object value) {
    if (value instanceof String) {
      data.putStringPropertyMapItem(key, new StringValue().name(key).value((String) value));

    } else if (value instanceof Integer) {
      data.putIntegerPropertyMapItem(key, new IntegerValue().name(key).value((Integer) value));

    } else if (value instanceof Long) {
      data.putLongPropertyMapItem(key, new LongValue().name(key).value((Long) value));

    } else if (value instanceof Boolean) {
      data.putBooleanPropertyMapItem(key, new BooleanValue().name(key).value((Boolean) value));

    } else if (value instanceof LocalDateTime) {
      data.putLocalDateTimePropertyMapItem(key,
          new LocalDateTimeValue().name(key).value((LocalDateTime) value));

    } else if (value instanceof MapBasedObjectData) {
      data.putObjectPropertyMapItem(key,
          new ObjectValue().name(key).value((MapBasedObjectData) value));

    } else if (value instanceof List<?>) {
      List<?> list = (List<?>) value;
      if (!list.isEmpty()) {
        Object item = list.get(0);
        if (item instanceof String) {
          List<String> values =
              list.stream().map(e -> (String) e).collect(Collectors.toList());
          data.putStringListMapItem(key, new StringValueList().name(key).values(values));

        } else if (item instanceof Integer) {
          List<Integer> values =
              list.stream().map(e -> (Integer) e).collect(Collectors.toList());
          data.putIntegerListMapItem(key, new IntegerValueList().name(key).values(values));

        } else if (item instanceof Long) {
          List<Long> values =
              list.stream().map(e -> (Long) e).collect(Collectors.toList());
          data.putLongListMapItem(key, new LongValueList().name(key).values(values));

        } else if (item instanceof Boolean) {
          List<Boolean> values =
              list.stream().map(e -> (Boolean) e).collect(Collectors.toList());
          data.putBooleanListMapItem(key, new BooleanValueList().name(key).values(values));

        } else if (item instanceof LocalDateTime) {
          List<LocalDateTime> values =
              list.stream().map(e -> (LocalDateTime) e).collect(Collectors.toList());
          data.putLocalDateTimeListMapItem(key,
              new LocalDateTimeValueList().name(key).values(values));

        } else if (item instanceof MapBasedObjectData) {
          List<MapBasedObjectData> valueList =
              list.stream().map(e -> (MapBasedObjectData) e).collect(Collectors.toList());
          data.putObjectListMapItem(key, new ObjectValueList().name(key).values(valueList));
        }
      }
    }
  }

  public static void setPropertyValueListItem(Object valueList, int index, Object value) {
    if (canBeAdded(valueList, value)) {
      if (valueList instanceof StringValueList) {
        ((StringValueList) valueList).getValues().set(index, (String) value);

      } else if (valueList instanceof IntegerValueList) {
        ((IntegerValueList) valueList).getValues().set(index, (Integer) value);

      } else if (valueList instanceof LongValueList) {
        ((LongValueList) valueList).getValues().set(index, (Long) value);

      } else if (valueList instanceof BooleanValueList) {
        ((BooleanValueList) valueList).getValues().set(index, (Boolean) value);

      } else if (valueList instanceof LocalDateTimeValueList) {
        ((LocalDateTimeValueList) valueList).getValues().set(index, (LocalDateTime) value);

      } else if (valueList instanceof List<?>) {
        if (value instanceof MapBasedObjectData) {
          ((List<MapBasedObject>) valueList).set(index,
              MapBasedObject.of((MapBasedObjectData) value));

        } else if (value instanceof MapBasedObject) {
          ((List<MapBasedObject>) valueList).set(index, (MapBasedObject) value);
        }
      }
    }
  }

  public static boolean isValueList(Object value) {
    return value instanceof StringValueList ||
        value instanceof IntegerValueList ||
        value instanceof LongValueList ||
        value instanceof BooleanValueList ||
        value instanceof LocalDateTimeValueList;
  }
}
