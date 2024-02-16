package org.smartbit4all.api.object;

import static java.util.stream.Collectors.toList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import org.smartbit4all.api.object.bean.ObjectValidationItem;
import org.smartbit4all.api.object.bean.ObjectValidationResult;
import org.smartbit4all.api.object.bean.ObjectValidationSeverity;

public final class ObjectValidations {

  private ObjectValidations() {}

  /**
   * Provides a comparator which is able to arrange validation items according to their severity in
   * increasing gravity.
   * 
   * <p>
   * The comparator is null tolerant, where null values are ranked lowest severity among individual
   * items and their respective severity - although supplying null values, or values with unassigned
   * severity levels is discouraged. This behaviour ensures that comparison of an item with no
   * severity, and another with any non-null severity yields the latter to be returned as the "more
   * severe".
   * 
   * <p>
   * Example usages can be found below, such as finding the validation item with the gravest
   * severity level:
   * 
   * <pre>
   * <code>
   * final List&lt;ObjectValidationItem&gt; items = ... ;
   * final ObjectValidationItem gravest = items().stream()
   *     .max(bySeverity())
   *     .orElseThrow();
   * </code>
   * </pre>
   * 
   * <p>
   * Similarly, to find the least severe item:
   * 
   * <pre>
   * <code>
   * final List&lt;ObjectValidationItem&gt; items = ... ;
   * final ObjectValidationItem leastSevere = items().stream()
   *     .min(bySeverity())
   *     .orElseThrow();
   * </code>
   * </pre>
   * 
   * @return a {@link Comparator} over {@link ObjectValidationItem}s, ranking items according to
   *         their {@link ObjectValidationItem#getSeverity()} in increasing severity
   */
  public static Comparator<ObjectValidationItem> bySeverity() {
    return (a, b) -> {
      if (a == null && b == null) {
        return 0;
      }

      if (a == null) {
        return 1;
      }

      if (b == null) {
        return -1;
      }

      final ObjectValidationSeverity sA = a.getSeverity();
      final ObjectValidationSeverity sB = b.getSeverity();
      if (sA == null && sB == null) {
        return 0;
      }

      if (sA == null) {
        return 1;
      }

      if (sB == null) {
        return -1;
      }

      return sB.ordinal() - sA.ordinal();
    };
  }

  public static Comparator<ObjectValidationSeverity> bySeverityEnum() {
    return (a, b) -> {
      if (a == null && b == null) {
        return 0;
      }

      if (a == null) {
        return 1;
      }

      if (b == null) {
        return -1;
      }

      return b.ordinal() - a.ordinal();
    };
  }

  /**
   * Extracts the most severe label from a collection of validation items.
   * 
   * <p>
   * Useful for assigning an overall severity level of a validation result built from a list of
   * validation items, such as:
   * 
   * <pre>
   * <code>
   * final List&lt;ObjectValidationItem&gt; items = ... ;
   * final ObjectValidationResult validationResult = new ObjectValidationResult()
   *     .severity(ObjectValidations.getTopSeverity(items))
   *     .items(items);
   * </code>
   * </pre>
   * 
   * <p>
   * This method does not tolerate a {@code null} parameter. {@link ObjectValidationSeverity#OK} is
   * returned for empty lists.
   * 
   * @param items an {@link Collection} of {@link ObjectValidationItem}s, not null; but elements may
   *        be null ({@code null} elements, and non-null elements without any severity assigned to
   *        them are not considered in the evaluation of the top severity)
   * @return the gravest {@link ObjectValidationSeverity} contained in the supplied list, or
   *         {@link ObjectValidationSeverity#OK} if the list was empty
   */
  public static ObjectValidationSeverity getTopSeverity(Collection<ObjectValidationItem> items) {
    Objects.requireNonNull(items, "object validation items cannot be null!");

    return items.stream()
        .max(bySeverity())
        .map(ObjectValidationItem::getSeverity)
        .orElse(ObjectValidationSeverity.OK);
  }

  public static final boolean lessThan(ObjectValidationResult result,
      ObjectValidationSeverity severity) {
    return bySeverityEnum().compare(result.getSeverity(), severity) < 0;
  }

  public static final ObjectValidationResult of(Collection<ObjectValidationItem> items) {
    return new ObjectValidationResult().items(items.stream().sorted(bySeverity()).collect(toList()))
        .severity(getTopSeverity(items));
  }

  public static final ObjectValidationResult OK() {
    return new ObjectValidationResult().severity(ObjectValidationSeverity.OK);
  }

  /**
   * Merge the toMerge into the baseline.
   * 
   * @param baseline The baseline that will contain the content of toMerge also after the call.
   * @param toMerge
   */
  public static final void merge(ObjectValidationResult baseline, ObjectValidationResult toMerge) {
    if (bySeverityEnum().compare(baseline.getSeverity(), toMerge.getSeverity()) < 0) {
      baseline.severity(toMerge.getSeverity());
    }
    baseline.getItems().addAll(toMerge.getItems());
  }

  /**
   * Unchecked exception to be thrown when an object validation fails in the domain layer.
   * 
   * <p>
   * Applications should be able to deploy a {@code ControllerAdvice} which handles thrown instances
   * of this class and shows the appropriate error display.
   * 
   * @author Szabolcs Bazil Papp
   *
   */
  public static final class ObjectValidationException extends RuntimeException {

    private static final long serialVersionUID = 1_348_995_121_533_113_055L;

    private final transient ObjectValidationResult objectValidationResult;

    public ObjectValidationException(final ObjectValidationResult objectValidationResult) {
      this.objectValidationResult = objectValidationResult;
    }

    public ObjectValidationResult getValidationResult() {
      return objectValidationResult;
    }

  }

}
