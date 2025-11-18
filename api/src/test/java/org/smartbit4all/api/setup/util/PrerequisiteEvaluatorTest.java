package org.smartbit4all.api.setup.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PrerequisiteEvaluatorTest {

  @Test
  void simpleBubblingPrerequisites() {
    Map<String, Set<String>> pMap = Map.of(
        "A", Collections.emptySet(),
        "B", Set.of("A"),
        "C", Set.of("B", "A"));
    List<String> result = PrerequisiteEvaluator.of(pMap).evaluate(it -> true);
    assertThat(result).containsExactly("A", "B", "C");
  }


  @Test
  void simpleBubblingOrderDoesNotMatter() {
    Map<String, Set<String>> pMap = Map.of(
        "A", Collections.emptySet(),
        "B", Set.of("C"),
        "C", Set.of("A"));
    List<String> result = PrerequisiteEvaluator.of(pMap).evaluate(it -> true);
    assertThat(result).containsExactly("A", "C", "B");
  }

  @Test
  void simpleBubblingWithSharedPrerequisites() {
    Map<String, Set<String>> pMap = Map.of(
        "A", Collections.emptySet(),
        "B", Set.of("A"),
        "C", Set.of("A"));
    List<String> result = PrerequisiteEvaluator.of(pMap).evaluate(it -> true);
    assertThat(result).containsExactlyInAnyOrder("A", "C", "B").first().isEqualTo("A");
  }

  @Test
  void twoMemberCycle_yieldsAnIllegalStateExceptionToBeThrown() {
    Map<String, Set<String>> pMap = Map.of(
        "A", Collections.emptySet(),
        "B", Set.of("C"),
        "C", Set.of("B"));
    assertThatThrownBy(() -> PrerequisiteEvaluator.of(pMap).evaluate(it -> true))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void threeMemberCycle_yieldsAnIllegalStateExceptionToBeThrown() {
    Map<String, Set<String>> pMap = Map.of(
        "A", Set.of("B"),
        "B", Set.of("C"),
        "C", Set.of("A"));
    assertThatThrownBy(() -> PrerequisiteEvaluator.of(pMap).evaluate(it -> true))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void twoIndependentChainsArePreservedInParallel() {
    Map<String, Set<String>> pMap = Map.of(
        "A", Set.of(),
        "B", Set.of("A"),
        "C", Set.of("B"),
        "X", Set.of(),
        "Y", Set.of("X"),
        "Z", Set.of("Y"));
    List<String> result = PrerequisiteEvaluator.of(pMap).evaluate(it -> true);

    List<String> first = new ArrayList<>();
    List<String> second = new ArrayList<>();
    for (final var e : result) {
      if (Set.of("A", "B", "C").contains(e)) {
        first.add(e);
      } else if (Set.of("X", "Y", "Z").contains(e)) {
        second.add(e);
      }
    }
    assertThat(first).containsExactly("A", "B", "C");
    assertThat(second).contains("X", "Y", "Z");
  }

  @Test
  void selfDependencyIsForbidden() {
    Map<String, Set<String>> pMap = Map.of(
        "A", Set.of(),
        "B", Set.of("A"),
        "C", Set.of("C"));
    assertThatThrownBy(() -> PrerequisiteEvaluator.of(pMap).evaluate(it -> true))
        .isInstanceOf(IllegalStateException.class);
  }

}
