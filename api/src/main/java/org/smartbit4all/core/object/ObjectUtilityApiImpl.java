package org.smartbit4all.core.object;

import static org.smartbit4all.core.utility.StringConstant.EMPTY;
import static org.springframework.util.ObjectUtils.isEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ObjectUtilityApiImpl implements ObjectUtilityApi {

  @Autowired
  private ObjectApi objectApi;

  @Override
  public Object value(Object value) {
    return value;
  }

  @Override
  public Integer addInt(Integer a, Integer b) {
    return a + b;
  }

  @Override
  public Boolean isGreater(Integer a, Integer b) {
    return a > b;
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
  public String concatStrings(String a, String b) {
    return a + b;
  }

  @Override
  public Integer getLengthOfList(List<?> list) {
    return list == null ? 0 : list.size();
  }

  @Override
  public <E> List<E> concatLists(List<E> firstList, List<E> secondList) {
    List<E> result = new ArrayList<>();
    result.addAll(firstList);
    result.addAll(secondList);
    return result;
  }

  @Override
  public <E> List<E> addItemToList(List<E> list, E item) {
    list.add(item);
    return list;
  }

  @Override
  public List<Object> objectToList(Object o) {
    List<Object> list = new ArrayList<>();
    list.add(o);
    return list;
  }

  @Override
  public <E> E listToObject(List<E> list) {
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
  public <E> E getItemFromList(List<E> list, Integer i) {
    return list.get(i.intValue());
  }


  @Override
  public <E> E getItemFromMap(Map<String, E> map, String key) {
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
  public List<Map<String, Object>> toMaps(List<Object> objects) {
    return objects.stream()
        .map(obj -> objectApi.toMapObject(obj)).toList();
  }

  @Override
  public String reduceListToString(List<? extends Object> objects, String concatString) {
    return objects.stream().map(o -> o.toString()).collect(Collectors.joining(concatString));
  }

  @Override
  public List<String> getKeySet(Map<String, ?> map) {
    return new ArrayList<>(map.keySet());
  }

  @Override
  public void throwException(String message) {
    throw new RuntimeException(message);
  }

  @Override
  public <E> List<E> asList(Class<E> clazz, List<?> value) {
    return objectApi.asList(clazz, value);
  }

  @Override
  public <E> List<E> setItemIntoList(Integer index, List<E> list, E item, String key) {
    Objects.requireNonNull(list);
    if (index == null) {
      list.add(item);
    }
    E current = list.get(index);
    if (current instanceof Map) {
      Map<Object, Object> map = (Map<Object, Object>) current;
      if (!(item instanceof Map)) {
        Objects.requireNonNull(key);
        map.put(key, item);
        return list;
      }
      map.putAll((Map<?, ?>) item);
      return list;
    }

    list.set(index, item);
    return list;
  }

}
