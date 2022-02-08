package org.smartbit4all.api.mapbasedobject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
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

class MapBasedObjectTest {

  @Test
  void mapBasedObjectTest() {
    MapBasedObjectData subData = new MapBasedObjectData();
    addToData(subData, "subName", "testSubName");

    MapBasedObjectData subData2 = new MapBasedObjectData();
    addToData(subData2, "subName2", "testSubName2");

    MapBasedObjectData subData3 = new MapBasedObjectData();
    addToData(subData3, "subName3", "testSubName3");
    MapBasedObjectData data = new MapBasedObjectData();
    addToData(data, "name", "testName");
    addToData(data, "names", Arrays.asList("testName2", "testName3"));
    addToData(data, "age", 10);
    addToData(data, "ages", Arrays.asList(20, 30));
    addToData(data, "height", 160l);
    addToData(data, "heights", Arrays.asList(170l, 180l));
    addToData(data, "date", LocalDateTime.now());
    addToData(data, "dates",
        Arrays.asList(LocalDateTime.of(2022, 1, 1, 0, 0), LocalDateTime.of(2022, 2, 2, 0, 0)));
    addToData(data, "object", subData);
    addToData(data, "objects", Arrays.asList(subData2, subData3));

    MapBasedObject object = MapBasedObject.of(data);

    Object valueByPath = object.getValueByPath("object/subName");

    MapBasedObjectData newData = MapBasedObject.toData(object);
  }

  private void addToData(MapBasedObjectData data, String key, Object value) {
    if (value instanceof String) {
      data.putStringPropertyMapItem(key, new StringValue().name(key).value((String) value));

    } else if (value instanceof Integer) {
      data.putIntegerPropertyMapItem(key, new IntegerValue().name(key).value((Integer) value));

    } else if (value instanceof Long) {
      data.putLongPropertyMapItem(key, new LongValue().name(key).value((Long) value));

    } else if (value instanceof LocalDateTime) {
      data.putLocalDateTimePropertyMapItem(key,
          new LocalDateTimeValue().name(key).value((LocalDateTime) value));

    } else if (value instanceof MapBasedObjectData) {
      data.putObjectPropertyMapItem(key,
          new ObjectValue().name(key).value((MapBasedObjectData) value));

    } else if (value instanceof List) {
      List<?> list = (List<?>) value;
      if (!list.isEmpty()) {
        Object element = list.get(0);
        if (element instanceof String) {
          List<String> values =
              list.stream().map(e -> (String) e).collect(Collectors.toList());
          data.putStringListMapItem(key, new StringValueList().name(key).values(values));

        } else if (element instanceof Integer) {
          List<Integer> values =
              list.stream().map(e -> (Integer) e).collect(Collectors.toList());
          data.putIntegerListMapItem(key, new IntegerValueList().name(key).values(values));

        } else if (element instanceof Long) {
          List<Long> values =
              list.stream().map(e -> (Long) e).collect(Collectors.toList());
          data.putLongListMapItem(key, new LongValueList().name(key).values(values));

        } else if (element instanceof LocalDateTime) {
          List<LocalDateTime> values =
              list.stream().map(e -> (LocalDateTime) e).collect(Collectors.toList());
          data.putLocalDateTimeListMapItem(key,
              new LocalDateTimeValueList().name(key).values(values));

        } else if (element instanceof MapBasedObjectData) {
          List<MapBasedObjectData> valueList =
              list.stream().map(e -> (MapBasedObjectData) e).collect(Collectors.toList());
          data.putObjectListMapItem(key, new ObjectValueList().name(key).value(valueList));
        }
      }
    }
  }
}
