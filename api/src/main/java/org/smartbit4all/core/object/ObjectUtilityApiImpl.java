package org.smartbit4all.core.object;

import static org.smartbit4all.core.utility.StringConstant.EMPTY;
import static org.springframework.util.ObjectUtils.isEmpty;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.domain.application.TimeManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;


public class ObjectUtilityApiImpl implements ObjectUtilityApi {

  @Autowired
  private TimeManagementService timeManagementService;
  @Autowired
  private ObjectApi objectApi;

  @Override
  public <E> List<E> asList(Class<E> clazz, List<?> value) {
    return objectApi.asList(clazz, value);
  }

  @Override
  public List<?> concatLists(List<?> firstList, List<?> secondList) {
    List<Object> result = new ArrayList<>();
    result.addAll(firstList);
    result.addAll(secondList);
    return result;
  }

  @Override
  public String concatStrings(String a, String b) {
    return a + b;
  }

  @Override
  public Object value(Object value) {
    return value;
  }

  @Override
  public Integer getLengthOfList(List<? extends Object> list) {
    return list == null ? 0 : list.size();
  }

  @Override
  public Boolean isGreater(Integer a, Integer b) {
    return a > b;
  }

  @Override
  public Integer increment(Integer i) {
    return ++i;
  }

  @Override
  public Integer decrement(Integer i) {
    return --i;
  }

  @Override
  public <T> T getItemFromList(List<T> list, Integer i) {
    return list.get(i.intValue());
  }

  @Override
  public List<Object> addItemToList(List<Object> list, Object item) {
    list.add(item);
    return list;
  }

  @Override
  public List<Object> addItemsToList(List<Object> listA, List<Object> listB) {
    if (listA == null) {
      listA = new ArrayList<>();
    }
    if (listB != null) {
      listA.addAll(listB);
    }
    return listA;
  }

  @Override
  public <T> T getItemFromMap(Map<String, T> map, String key) {
    return map.get(key);
  }

  @Override
  public Map<String, Object> diminuteMap(Map<String, Object> map, List<String> remainingKeys) {
    Map<String, Object> newMap = new HashMap<>();
    map.entrySet().forEach(e -> {
      if (remainingKeys.contains(e.getKey())) {
        newMap.put(e.getKey(), e.getValue());
      }
    });
    return newMap;
  }

  @Override
  public String toString(Object obj) {
    if (obj == null) {
      return null;
    }
    return obj.toString();
  }

  @Override
  public String writeValueAsString(Object obj) {
    ObjectWriter writer = ObjectSerializerByObjectMapper.getObjectMapper()
        .writerWithDefaultPrettyPrinter();
    try {
      return writer.writeValueAsString(obj);
    } catch (JsonProcessingException e) {

      return EMPTY;
    }
  }

  @Override
  public String concatString(List<String> strings, String concatString) {
    StringBuilder sb = new StringBuilder();
    strings
        .forEach(s -> sb.append(s).append(concatString));

    return sb.toString();
  }

  @Override
  public String toStringAndConcat(List<Object> objects, String concatString) {
    return concatString(objects.stream().map(this::toString).toList(), concatString);
  }

  @Override
  public List<String> getKeySet(Map<String, ?> map) {
    return new ArrayList<>(map.keySet());
  }

  @Override
  public String getCurrentLocalDate(String formatPattern) {
    return timeManagementService.getSystemTime().format(DateTimeFormatter.ofPattern(formatPattern));
  }

  @Override
  public OffsetDateTime getCurrentOffsetDateTime() {
    return OffsetDateTime.now();
  }

  @Override
  public List<Object> objectToList(Object o) {
    List<Object> list = new ArrayList<>();
    list.add(o);
    return list;
  }

  @Override
  public Object listToObject(List<Object> list) {
    if (isEmpty(list)) {
      return null;
    }
    return list.get(0);
  }

  @Override
  public Map<String, Object> objectToMap(String key, Object value) {
    if (isEmpty(key)) {
      return null;
    }
    return Map.of(key, value);
  }

  @Override
  public List<Map<String, Object>> toMaps(List<Object> objects) {
    return objects.stream()
        .map(obj -> objectApi.create(PlatformApiConfig.SCHEMA_TEMP, obj).getObjectAsMap()).toList();
  }

  @Override
  public List<?> stringToList(String s) {
    List<?> list = objectApi.fromString(s, List.class);
    return list;
  }

  @Override
  public String throwException(String message) {
    throw new RuntimeException(message);
  }

  @Override
  public Map<String, Object> mergeMapsWithLists(Map<String, Object> mapA,
      Map<String, Object> mapB) {
    mapB.entrySet().forEach(e -> {
      Object originalValue = mapA.get(e.getKey());
      if (originalValue == null) {
        originalValue = new ArrayList<>();
      }
      if (originalValue instanceof List) {
        List originalList = objectApi.asType(List.class, originalValue);
        Object mergeValue = e.getValue();
        if (mergeValue != null && mergeValue instanceof List) {
          List mergeList = objectApi.asType(List.class, mergeValue);
          originalList.addAll(mergeList);
          mapA.put(e.getKey(), originalList);
        }
      }
    });
    return mapA;
  }
}
