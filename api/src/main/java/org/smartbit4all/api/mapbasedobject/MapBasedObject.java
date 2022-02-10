package org.smartbit4all.api.mapbasedobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.smartbit4all.core.utility.PathUtility;

public class MapBasedObject {

  private final Map<String, Object> propertyMap = new HashMap<>();

  private Map<String, Object> getPropertyMap() {
    return propertyMap;
  }

  public static final MapBasedObject of(MapBasedObjectData data) {
    MapBasedObject result = new MapBasedObject();

    putAll(result.propertyMap, data.getStringPropertyMap());
    putAll(result.propertyMap, data.getStringListMap());
    putAll(result.propertyMap, data.getIntegerPropertyMap());
    putAll(result.propertyMap, data.getIntegerListMap());
    putAll(result.propertyMap, data.getLongPropertyMap());
    putAll(result.propertyMap, data.getLongListMap());
    putAll(result.propertyMap, data.getLocalDateTimePropertyMap());
    putAll(result.propertyMap, data.getLocalDateTimeListMap());

    Map<String, ObjectValue> objectPropertyMap = data.getObjectPropertyMap();
    if (objectPropertyMap != null) {
      objectPropertyMap
          .forEach((key, value) -> result.propertyMap.put(key, of(value.getValue())));
    }

    Map<String, ObjectValueList> objectListMap = data.getObjectListMap();
    if (objectListMap != null) {
      objectListMap
          .forEach((key, value) -> result.propertyMap.put(key, listOf(value.getValue())));
    }

    return result;
  }

  private static void putAll(Map<String, Object> objectMap, Map<String, ?> propertyMap) {
    if (propertyMap != null) {
      objectMap.putAll(propertyMap);
    }
  }

  private static List<MapBasedObject> listOf(List<MapBasedObjectData> datas) {
    return datas.stream().map(data -> of(data)).collect(Collectors.toList());
  }

  public static final MapBasedObjectData toData(MapBasedObject object) {
    MapBasedObjectData result = new MapBasedObjectData();
    object.propertyMap.forEach((key, value) -> addToDataFromObject(result, key, value));
    return result;
  }

  private static void addToDataFromObject(MapBasedObjectData data, String key, Object value) {
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

    } else if (value instanceof LocalDateTimeValue) {
      data.putLocalDateTimePropertyMapItem(key, (LocalDateTimeValue) value);

    } else if (value instanceof LocalDateTimeValueList) {
      data.putLocalDateTimeListMapItem(key, (LocalDateTimeValueList) value);

    } else if (value instanceof MapBasedObject) {
      data.putObjectPropertyMapItem(key,
          new ObjectValue().name(key).value(toData((MapBasedObject) value)));

    } else if (value instanceof List) {
      List<?> list = (List<?>) value;
      if (!list.isEmpty() && list.get(0) instanceof MapBasedObject) {
        List<MapBasedObjectData> objects = new ArrayList<>();
        for (Object object : list) {
          objects.add(toData((MapBasedObject) object));
        }
        data.putObjectListMapItem(key, new ObjectValueList().name(key).value(objects));
      }
    }
  }

  public Object getValueByPath(String path) {
    String rootPath = PathUtility.getRootPath(path);

    Object value = propertyMap.get(rootPath);

    if (value instanceof MapBasedObject) {
      value = ((MapBasedObject) value).getValueByPath(PathUtility.nextFullPath(path));
    }

    return value;
  }

  // if-else value instanceof ...
  // null value is StringValue by default
  public void setValueByPath(String path, Object value) {

  }

  public MapBasedObject addValueByPath(String path, Object value) {
    return null;
  }

  public void removeValueByPath(String path) {

  }
}
