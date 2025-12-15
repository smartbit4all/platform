package org.smartbit4all.core.object;

import java.util.List;
import java.util.Map;

public interface ObjectUtilityApi {

  Object value(Object value);

  Integer addInt(Integer a, Integer b);

  Boolean isGreater(Integer a, Integer b);

  String toString(Object obj);

  String writeValueAsString(Object obj);

  String concatStrings(String a, String b);

  Integer getLengthOfList(List<?> list);

  <E> List<E> concatLists(List<E> listA, List<E> listB);

  <E> List<E> addItemToList(List<E> list, E item);

  <E> List<E> objectToList(E e);

  <E> E listToObject(List<E> list);

  Map<String, Object> objectToMap(String key, Object value);

  <T> T getItemFromList(List<T> list, Integer i);

  <T> T getItemFromMap(Map<String, T> map, String key);

  Map<String, Object> putItemToMap(Map<String, Object> map, String key, Object value);

  Map<String, Object> diminuteMap(Map<String, Object> map, List<String> remainingKeys);

  List<Map<String, Object>> toMaps(List<Object> objects);

  String reduceListToString(List<?> objects, String concatString);

  List<String> getKeySet(Map<String, ?> map);

  void throwException(String message);

  <E> List<E> asList(Class<E> clazz, List<?> value);

  <E> List<E> setItemIntoList(Integer index, List<E> list, E item, String key);

  Boolean equals(Object o, Object ob);

  <T> List<T> enusreListExists(List<T> list);

  <T> Map<String, T> ensureMapExists(Map<String, T> map);
}
