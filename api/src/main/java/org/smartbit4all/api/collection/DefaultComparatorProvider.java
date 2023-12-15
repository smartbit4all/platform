package org.smartbit4all.api.collection;

import java.util.Comparator;
import java.util.Map;

public interface DefaultComparatorProvider {

  /**
   * @return Map of comparators, keys should be class names
   */
  Map<String, Comparator<Object>> getComparators();
}
