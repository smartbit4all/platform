package org.smartbit4all.core.object;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class ObjectUtilityApiImpl implements ObjectUtilityApi {
  @Autowired
  private ObjectApi objectApi;


  @Override
  public Object value(Object value) {
    return value;
  }



  @Override
  public Integer getLengthOfList(List<? extends Object> list) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public Integer addInt(Integer a, Integer b) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public String toString(Object obj) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public String writeValueAsString(Object obj) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public String concatStrings(String a, String b) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public <E> String reduceListToString(List<E> objects, String concatString) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public List<String> getKeySet(Map<String, ?> map) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public <E> List<E> addItemToList(List<E> list, Object item) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public <E> List<E> addItemsToList(List<E> listA, List<E> listB) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public List<Object> objectToList(Object o) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public Object listToObject(List<Object> list) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public Map<String, Object> objectToMap(String key, Object value) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public <E> E getItemFromList(List<E> list, Integer i) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public <E> E getItemFromMap(Map<String, E> map, String key) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public Map<String, Object> diminuteMap(Map<String, Object> map, List<String> remainingKeys) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public List<Map<String, Object>> toMaps(List<Object> objects) {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public void throwException(String message) {
    // TODO Auto-generated method stub

  }



  @Override
  public <E> List<E> asList(Class<E> clazz, List<?> value) {
    return objectApi.asList(clazz, value);
  }


}
