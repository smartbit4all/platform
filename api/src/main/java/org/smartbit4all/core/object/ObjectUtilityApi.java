package org.smartbit4all.core.object;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public interface ObjectUtilityApi {
  <E> List<E> asList(Class<E> clazz, List<?> value);

  List<?> concatLists(List<?> firstList, List<?> secondList);

  Object value(Object value);

  Integer getLengthOfList(List<? extends Object> list);

  Boolean isGreater(Integer a, Integer b);

  Integer increment(Integer i);

  Integer decrement(Integer i);

  List<Object> addItemToList(List<Object> list, Object item);

  List<Object> addItemsToList(List<Object> listA, List<Object> listB);

  String toString(Object obj);

  String getCurrentLocalDate(String formatPattern);

  OffsetDateTime getCurrentOffsetDateTime();

  List<Object> objectToList(Object o);

  Object listToObject(List<Object> list);

  Map<String, Object> objectToMap(String key, Object value);

  <T> T getItemFromList(List<T> list, Integer i);

  <T> T getItemFromMap(Map<String, T> map, String key);

  Map<String, Object> diminuteMap(Map<String, Object> map, List<String> remainingKeys);

  List<Map<String, Object>> toMaps(List<Object> objects);

  String writeValueAsString(Object obj);

  String concatString(List<String> strings, String concatString);

  String toStringAndConcat(List<Object> objects, String concatString);

  List<String> getKeySet(Map<String, ?> map);

  String concatStrings(String a, String b);

  List<?> stringToList(String s);

  String throwException(String message);

  Map<String, Object> mergeMapsWithLists(Map<String, Object> mapA,
      Map<String, Object> mapB);

}
