package org.smartbit4all.api.collection;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DefaultComparatorProviderImpl implements DefaultComparatorProvider {

  @Override
  public Map<String, Comparator<Object>> getComparators() {
    Map<String, Comparator<Object>> comparators = new HashMap<>();
    comparators.put(String.class.getName(), compareStringDefault());
    return comparators;
  }

  private Comparator<Object> compareStringDefault() {
    return (local, other) -> {
      Integer nullCompare = compareNulls(local, other);
      if (nullCompare != null) {
        return nullCompare;
      }
      return Collator.getInstance().compare(local, other);
    };
  }

  private Integer compareNulls(Object local, Object other) {
    if (local == null && other == null) {
      return 0;
    } else if (local != null && other == null) {
      return -1;
    } else if (local == null && other != null) {
      return 1;
    }
    return null;
  }
}
