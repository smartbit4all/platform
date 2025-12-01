package org.smartbit4all.api.setup.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        .collect(Collectors.toSet());
    noPrereqs.forEach(it -> {
      prerequisitesByItem.remove(it);
      result.add(it);
    });

    // the lowest index for inserting any custom item (an item is 'custom' if it declares
    // prerequisites).
    final int customStart = result.size();
    final Map<E, Integer> boundHigh = new HashMap<>();
    for (final var e : prerequisitesByItem.entrySet()) {
      final E item = e.getKey();
      final var prerequisites = e.getValue();
      if (prerequisites.contains(item)) {
        throw new IllegalStateException("Item [ " + item + " ] is predicated on itself!");
      }

      // the prerequisites this item declared, but we haven't encountered yet:
      final Set<E> missingPrereqs = new HashSet<>();
      // the lowest bound (INCLUSIVE): this is the smallest index at which the item could be
      // inserted, because all items in the result with lower indices are declared as prerequisites
      // of this item. This is effectively the maximum index of the known prerequisites of this
      // item, plus one (this item shall be inserted at an index greater then the maximum of its
      // dependencies).
      //
      // We shall attempt to insert this item only after the already known items with no
      // prerequisites:
      int myBoundLow = customStart;
      for (final var prereq : prerequisites) {
        if (!validPrerequisite.test(prereq)) {
          continue;
        }
        int idx = result.indexOf(prereq);
        if (idx < 0) {
          // not present in the ordered list yet:
          missingPrereqs.add(prereq);
        } else {
          myBoundLow = Math.max(myBoundLow, idx + 1);
        }
      }

      // the highest bound (INCLUSIVE): this is the largest index at which the item could be
      // inserted, because there are items with larger index already present in the result which
      // declare this item as a prerequisite. This is effectively the minimum index of the already
      // encountered items who declare a prerequisite on this item. Inserting at this position will
      // shift the previous item at this index to the right.
      final Integer myBoundHigh = boundHigh.get(item);

      final int targetIdx;
      if (myBoundHigh == null) {
        // if there is no upper bound, we should append the element at the end of the list:
        targetIdx = result.size();
      } else if (myBoundLow > myBoundHigh) {
        // if the lowest possible insertion index is larger than the upper bound it is impossible to
        // insert this item anywhere: this is caused by a cycle of dependencies:
        throw new IllegalStateException("Cycle detected!");
      } else {
        // if there is an upper bound, we shall insert this item there (as high index as possible):
        targetIdx = myBoundHigh;
      }

      result.add(targetIdx, item);
      // the highest possible insertion index of this item's yet un-encountered prerequisites is the
      // target index (if a prerequisite were to be inserted here, this item would shift right in
      // the result):
      missingPrereqs.forEach(it -> boundHigh.compute(it, (k, v) -> (v == null)
          ? targetIdx
          : Math.min(v, targetIdx)));
      // because we inserted this item at the target index, everything with a higher index shifted
      // to the right by 2. We increment the upper bound for all, not yet encountered items which
      // are bounded higher than this target index:
      boundHigh.keySet().forEach(it -> boundHigh.computeIfPresent(it, (key, v) -> (targetIdx > v)
          ? v + 2
          : v));
    }

    return result;
  }

}
