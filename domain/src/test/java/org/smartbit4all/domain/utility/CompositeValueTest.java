package org.smartbit4all.domain.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.utility.CompositeValue;

class CompositeValueTest {

  @Test
  void oneValue() throws Exception {
    CompositeValue cv1 = new CompositeValue("apple");
    CompositeValue cv2 = new CompositeValue("apple");
    assertEquals(0, cv1.compareTo(cv2));
    assertEquals(0, cv2.compareTo(cv1));
  }

  @Test
  void oneValueVsNull() throws Exception {
    CompositeValue cv1 = new CompositeValue("apple");
    List<Comparable> values = new ArrayList<>();
    values.add(null);
    CompositeValue cv2 = new CompositeValue(values);
    assertTrue(cv1.compareTo(cv2) < 0);
    assertTrue(cv2.compareTo(cv1) > 0);
  }

  @Test
  void oneNullVsNull() throws Exception {
    List<Comparable> values = new ArrayList<>();
    values.add(null);
    CompositeValue cv1 = new CompositeValue(values);
    CompositeValue cv2 = new CompositeValue(values);
    assertEquals(0, cv1.compareTo(cv2));
    assertEquals(0, cv2.compareTo(cv1));
  }

  @Test
  void moreValuesAllEquals() throws Exception {
    CompositeValue cv1 = new CompositeValue("apple", "pear", Long.valueOf(1));
    CompositeValue cv2 = new CompositeValue("apple", "pear", Long.valueOf(1));
    assertEquals(0, cv1.compareTo(cv2));
    assertEquals(0, cv2.compareTo(cv1));
  }

  @Test
  void moreValuesDifferences() throws Exception {
    CompositeValue cv1 = new CompositeValue("apple", "peach", Long.valueOf(1));
    CompositeValue cv2 = new CompositeValue("apple", "pear", Long.valueOf(1));
    assertTrue(cv1.compareTo(cv2) < 0);
    assertTrue(cv2.compareTo(cv1) > 0);
  }

  @Test
  void moreValuesDifferences2() throws Exception {
    CompositeValue cv1 = new CompositeValue("apple", "peach", Long.valueOf(1));
    CompositeValue cv2 = new CompositeValue("apple", "peach", Long.valueOf(2));
    assertTrue(cv1.compareTo(cv2) < 0);
    assertTrue(cv2.compareTo(cv1) > 0);
  }

  @Test
  void moreValuesDifferencesWithNull() throws Exception {
    CompositeValue cv1 = new CompositeValue("apple", "peach", Long.valueOf(1));
    CompositeValue cv2 = new CompositeValue("apple", null, Long.valueOf(1));
    assertTrue(cv1.compareTo(cv2) < 0);
    assertTrue(cv2.compareTo(cv1) > 0);
  }

  @Test
  void mapUsage() throws Exception {
    CompositeValue cv1v1 = new CompositeValue("apple", "peach", Long.valueOf(1));
    CompositeValue cv1v2 = new CompositeValue("apple", "peach", Long.valueOf(1));
    Map<CompositeValue, String> map = new HashMap<>();
    map.put(cv1v1, "value");
    assertEquals("value", map.get(cv1v2));
  }

}
