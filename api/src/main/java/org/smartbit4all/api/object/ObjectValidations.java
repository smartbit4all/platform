package org.smartbit4all.api.object;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.object.bean.ObjectValidationItem;
import org.smartbit4all.api.object.bean.ObjectValidationResult;
import org.smartbit4all.api.object.bean.ObjectValidationSeverity;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.ComponentType;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import com.google.common.base.Strings;

public final class ObjectValidations {

  public static final String LOCALE_MANDATORY = "mandatory";

  private static final Set<ObjectValidationSeverity> ERROR_SEVERITIES = EnumSet.of(
      ObjectValidationSeverity.BLOCKER,
      ObjectValidationSeverity.ERROR);

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

  public static boolean atLeast(ObjectValidationSeverity severity,
      ObjectValidationSeverity sentinel) {
    return 0 <= bySeverityEnum().compare(severity, sentinel);
  }

  public static final ObjectValidationResult of(Collection<ObjectValidationItem> items) {
    return new ObjectValidationResult()
        .items(items.stream()
            .sorted(bySeverity())
            .collect(toCollection(ArrayList::new)))
        .severity(getTopSeverity(items));
  }

  public static ObjectValidationResult of(ObjectValidationItem... items) {
    if (items == null || items.length == 0) {
      return OK();
    }

    return of(new ArrayList<>(Arrays.asList(items)));
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
   * Validates whether every property marked as <i>mandatory</i> by the {@link ViewConstraint} has a
   * value or not.
   * 
   * <p>
   * Example usage:
   * 
   * <pre>
   * <code>
   * public void performSave(UUID viewUuid, UiActionRequest request) {
   *   final View view = viewApi.getView(viewUuid);
   *   final ViewConstraint viewConstraint = view.getConstraint();
   *   
   *   final Model model = actionRequestHelper(view).require(UiActions.MODEL, Model.class);
   *   final ObjectNode modelNode = objectApi.create(null, model);
   *   
   *   final ObjectValidationResult validationResult = ObjectValidations.validateMandatoryFields(
   *       viewConstraint, 
   *       model);
   *   if (ObjectValidationSeverity.OK != validationResult.getSeverity()) {
   *     viewApi.showView(new View()
   *        .viewName(PlatformViewNames.VALIDATION_RESULT_PAGE)
   *        .type(ViewType.DIALOG)
   *        .putParametersItem(
   *            ValidationResultPageApi.VALIDATION_RESULT,
   *            validationResult)
   *        .putCallbacksItem(
   *            ValidationResultPageApi.VALIDATION_PAGE_INVOCATION_REQUEST,
   *            invocationApi
   *                .builder(MyPageApiInterface.class)
   *                .build(a -> a.performSaveWithoutValidation(viewUuid, request))));
   * 
   *   } else {
   *     performSaveWithoutValidation(viewUuid, request);
   *   }
   * }
   * 
   * public void performSaveWithoutValidation(UUID viewUuid, UiActionRequest request) {
   *   // do actual save logic here...
   * }
   * </code>
   * </pre>
   * 
   * <p>
   * The returned result is only of severity {@link ObjectValidationSeverity#WARNING}, enabling
   * forceful continuation of the user operation (if any).
   * 
   * @param viewConstraint the {@link ViewConstraint} applicable for the current view, nullable; if
   *        null, {@link #OK()} is returned
   * @param viewModel the {@link ObjectNode} representation of the view's model, may be a virtual
   *        node (created with {@code null} schema), or even null; if null every mandatory
   *        requirement is failed
   * @return the {@link ObjectValidationResult} containing every unfilled mandatory field with its
   *         {@link LangString#getDefaultValue()} prefixed with {@link #LOCALE_MANDATORY} (it is the
   *         client's responsibility to resolve the actual locale-specific strings)
   */
  public static ObjectValidationResult validateMandatoryFields(ViewConstraint viewConstraint,
      ObjectNode viewModel) {
    return validateMandatoryFields(viewConstraint, viewModel, null);
  }

  /**
   * Validates whether every property marked as <i>mandatory</i> by the {@link ViewConstraint} has a
   * value or not.
   * 
   * <p>
   * Widgets not present in the provided {@link SmartComponentLayoutDefinition} are not considered.
   * 
   * <p>
   * Example usage:
   * 
   * <pre>
   * <code>
   * public void performSave(UUID viewUuid, UiActionRequest request) {
   *   final View view = viewApi.getView(viewUuid);
   *   final ViewConstraint viewConstraint = view.getConstraint();
   *   
   *   final SmartComponentLayoutDefinition layout;
   *   final Map<String, SmartComponentLayoutDefinition> layouts = view.getComponentLayouts();
   *   if (layouts == null || layouts.isEmpty()) {
   *     layout = null;
   *   } else {
   *     layout = componentLayouts.values().stream().findFirst().orElseThrow();
   *   }
   *   
   *   final Model model = actionRequestHelper(view).require(UiActions.MODEL, Model.class);
   *   final ObjectNode modelNode = objectApi.create(null, model);
   *   
   *   final ObjectValidationResult validationResult = ObjectValidations.validateMandatoryFields(
   *       viewConstraint, 
   *       model,
   *       layout);
   *   if (ObjectValidationSeverity.OK != validationResult.getSeverity()) {
   *     viewApi.showView(new View()
   *        .viewName(PlatformViewNames.VALIDATION_RESULT_PAGE)
   *        .type(ViewType.DIALOG)
   *        .putParametersItem(
   *            ValidationResultPageApi.VALIDATION_RESULT,
   *            validationResult)
   *        .putCallbacksItem(
   *            ValidationResultPageApi.VALIDATION_PAGE_INVOCATION_REQUEST,
   *            invocationApi
   *                .builder(MyPageApiInterface.class)
   *                .build(a -> a.performSaveWithoutValidation(viewUuid, request))));
   * 
   *   } else {
   *     performSaveWithoutValidation(viewUuid, request);
   *   }
   * }
   * 
   * public void performSaveWithoutValidation(UUID viewUuid, UiActionRequest request) {
   *   // do actual save logic here...
   * }
   * </code>
   * </pre>
   * 
   * <p>
   * The returned result is only of severity {@link ObjectValidationSeverity#WARNING}, enabling
   * forceful continuation of the user operation (if any).
   * 
   * @param viewConstraint the {@link ViewConstraint} applicable for the current view, nullable; if
   *        null, {@link #OK()} is returned
   * @param viewModel the {@link ObjectNode} representation of the view's model, may be a virtual
   *        node (created with {@code null} schema), or even null; if null every mandatory
   *        requirement is failed
   * @param layout the {@link SmartComponentLayoutDefinition} applicable for the current view,
   *        nullable; if null, every mandatory field of the {@code ViewConstraint} is considered for
   *        the calculation
   * @return the {@link ObjectValidationResult} containing every unfilled mandatory field with its
   *         {@link LangString#getDefaultValue()} prefixed with {@link #LOCALE_MANDATORY} (it is the
   *         client's responsibility to resolve the actual locale-specific strings)
   */
  public static ObjectValidationResult validateMandatoryFields(ViewConstraint viewConstraint,
      ObjectNode viewModel, SmartComponentLayoutDefinition layout) {
    if (viewConstraint == null) {
      return OK();
    }

    final List<ComponentConstraint> constraints = viewConstraint.getComponentConstraints();
    if (constraints == null || constraints.isEmpty()) {
      return OK();
    }

    final Predicate<String> widgetKeyPredicate;
    if (layout != null) {
      widgetKeyPredicate = flattenLayout(layout)
          .flatMap(it -> formWidgets(it))
          // we are not checking toggles (no value on toggles means false):
          .filter(w -> SmartFormWidgetType.TOGGLE != w.getType())
          .map(SmartWidgetDefinition::getKey)
          .collect(collectingAndThen(toSet(), keys -> keys::contains));
    } else {
      widgetKeyPredicate = s -> true;
    }

    final List<String[]> mandatoryProperties = constraints.stream()
        .filter(it -> isTrue(it.getVisible())
            && isTrue(it.getEnabled())
            && isTrue(it.getMandatory()))
        .map(ComponentConstraint::getDataName)
        .filter(widgetKeyPredicate)
        .distinct()
        .map(it -> it.split("\\.")).distinct()
        .collect(toList());
    if (mandatoryProperties.isEmpty()) {
      return OK();
    }

    if (viewModel == null) {
      return mandatoryProperties.stream()
          .map(ObjectValidations::mandatoryItem)
          .collect(collectingAndThen(toList(), ObjectValidations::of));
    }

    return mandatoryProperties.stream()
        .filter(path -> !hasValue(viewModel, path))
        .map(ObjectValidations::mandatoryItem)
        .collect(collectingAndThen(toList(), ObjectValidations::of));
  }

  public static boolean hasEnabledField(ViewConstraint viewConstraint,
      SmartComponentLayoutDefinition layout) {
    if (viewConstraint == null) {
      return true;
    }

    final List<ComponentConstraint> constraints = viewConstraint.getComponentConstraints();
    if (constraints == null || constraints.isEmpty()) {
      return true;
    }

    final List<String> widgets;
    if (layout != null) {
      widgets = flattenLayout(layout)
          .flatMap(it -> formWidgets(it))
          .map(SmartWidgetDefinition::getKey)
          .collect(toList());
    } else {
      widgets = Collections.emptyList();
    }

    // get the last constraint for each field
    Map<String, Boolean> fieldsEnabled = constraints.stream()
        .collect(groupingBy(it -> it.getDataName()))
        .values().stream()
        .flatMap(v -> {
          Optional<ComponentConstraint> lastElement = v.stream()
              .reduce((first, second) -> second);
          return lastElement.isPresent() ? Stream.of(lastElement.get()) : Stream.empty();
        })
        .collect(Collectors.toMap(ComponentConstraint::getDataName, c -> isTrue(c.getEnabled())));

    Optional<String> enabledFields = widgets.stream()
        .filter(it -> !fieldsEnabled.containsKey(it) || fieldsEnabled.get(it))
        .findFirst();

    return enabledFields.isPresent();
  }

  private static Stream<SmartComponentLayoutDefinition> flattenLayout(
      SmartComponentLayoutDefinition layout) {
    List<SmartComponentLayoutDefinition> components = layout.getComponents();
    if (components == null || components.isEmpty()) {
      return Stream.of(layout);
    }

    return Stream.concat(Stream.of(layout), components.stream().flatMap(it -> flattenLayout(it)));
  }

  private static ObjectValidationItem mandatoryItem(String[] keys) {
    return new ObjectValidationItem()
        .severity(ObjectValidationSeverity.WARNING)
        .message(new LangString()
            .defaultValue(LOCALE_MANDATORY + StringConstant.DOT
                + Arrays.stream(keys).collect(Collectors.joining(StringConstant.DOT))));
  }

  private static Stream<SmartWidgetDefinition> formWidgets(SmartComponentLayoutDefinition layout) {
    if (layout == null || ComponentType.FORM != layout.getType() || layout.getForm() == null) {
      return Stream.empty();
    }

    return layout.getForm().stream().filter(Objects::nonNull);
  }

  private static boolean hasValue(ObjectNode node, String... path) {
    final Object value = node.getValue(path);
    if (value instanceof String) {
      return !Strings.isNullOrEmpty((String) value);
    } else {
      return value != null;
    }
  }

  private static boolean isTrue(final Boolean boxed) {
    return Boolean.TRUE == boxed;
  }

  private static final Set<ObjectValidationSeverity> OK_SEVERITIES = EnumSet.of(
      ObjectValidationSeverity.OK,
      ObjectValidationSeverity.INFO);

  /**
   * Raises the overall severity of a validation result, if applicable.
   * 
   * <p>
   * If the provided {@link ObjectValidationResult} contains at least one item with severity greater
   * than {@link ObjectValidationSeverity#INFO}, raises the overall severity of the validation
   * result to the provided value. The severity of every item with (with greater value than INFO} is
   * also raised to the supplied level.
   * 
   * <p>
   * The provided {@link ObjectValidationResult} argument is directly mutated. If any of the
   * parameters are null, no operation is performed.
   *
   * @param validation the {@link ObjectValidationResult} to examine and modify, nullable
   * @param severity the target {@link ObjectValidationSeverity}, nullable
   */
  public static void raiseSeverity(ObjectValidationResult validation,
      ObjectValidationSeverity severity) {
    if (validation == null || severity == null) {
      return;
    }

    final List<ObjectValidationItem> items = validation.getItems();
    if (items == null || items.isEmpty()) {
      return;
    }

    boolean changed = false;
    for (ObjectValidationItem item : items) {
      final ObjectValidationSeverity s = item.getSeverity();
      if (s == null || !OK_SEVERITIES.contains(s)) {
        item.setSeverity(severity);
        changed = true;
      }
    }

    if (changed) {
      validation.setSeverity(severity);
    }
  }

  public static ObjectValidationItem blocker(String localeKey) {
    return new ObjectValidationItem()
        .severity(ObjectValidationSeverity.BLOCKER)
        .message(new LangString().defaultValue(localeKey));
  }

  public static ObjectValidationItem error(String localeKey) {
    return new ObjectValidationItem()
        .severity(ObjectValidationSeverity.ERROR)
        .message(new LangString().defaultValue(localeKey));
  }

  public static ObjectValidationItem warning(String localeKey) {
    return new ObjectValidationItem()
        .severity(ObjectValidationSeverity.WARNING)
        .message(new LangString().defaultValue(localeKey));
  }

  public static ObjectValidationItem info(String localeKey) {
    return new ObjectValidationItem()
        .severity(ObjectValidationSeverity.INFO)
        .message(new LangString().defaultValue(localeKey));
  }

  public static boolean isError(ObjectValidationResult validationResult) {
    return validationResult != null && ERROR_SEVERITIES.contains(validationResult.getSeverity());
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
