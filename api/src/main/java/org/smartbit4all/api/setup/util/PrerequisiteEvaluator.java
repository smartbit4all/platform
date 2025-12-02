package org.smartbit4all.api.setup.util;

import static java.util.stream.Collectors.toSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public final class PrerequisiteEvaluator<E> {

  public static <E> PrerequisiteEvaluator<E> of(Map<E, Set<E>> prerequisitesByItem) {
    if (prerequisitesByItem == null) {
      return new PrerequisiteEvaluator<>(new HashMap<>());
    }

    return new PrerequisiteEvaluator<>(new HashMap<>(prerequisitesByItem));
  }

  private final Map<E, Set<E>> prerequisitesByItem;

  private PrerequisiteEvaluator(Map<E, Set<E>> prerequisitesByItem) {
    this.prerequisitesByItem = prerequisitesByItem;
  }

  public List<E> evaluate(Predicate<E> validPrerequisite) {
    final List<E> result = new ArrayList<>();
    final Set<E> noPrereqs = prerequisitesByItem.entrySet().stream()
        .filter(e -> e.getValue().isEmpty())
        .map(Map.Entry::getKey)
        .collect(toSet());
    noPrereqs.forEach(it -> {
      prerequisitesByItem.remove(it);
      result.add(it);
    });

    // we are going to sieve until all items are exhausted. If any step results in no elements
    // added, we practically detected a cycle.
    while (!prerequisitesByItem.isEmpty()) {
      final Set<E> candidates = prerequisitesByItem.entrySet().stream()
          .filter(e -> result.containsAll(e.getValue()))
          .map(Map.Entry::getKey)
          .collect(toSet());
      if (candidates.isEmpty()) {
        throw new IllegalStateException("Cycle detected!");
      }

      candidates.forEach(it -> {
        prerequisitesByItem.remove(it);
        result.add(it);
      });
    }

    return result;
  }

}
