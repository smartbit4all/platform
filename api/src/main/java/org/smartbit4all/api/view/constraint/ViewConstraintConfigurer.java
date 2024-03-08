package org.smartbit4all.api.view.constraint;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.core.object.ObjectNode;
import com.google.common.base.Strings;

public final class ViewConstraintConfigurer {

  public static ViewConstraintConfigurer newInstance() {
    return new ViewConstraintConfigurer(null, null, null);
  }

  public static ViewConstraintConfigurer of(final View view, final ObjectNode domainobject,
      final URI userUri) {
    return new ViewConstraintConfigurer(view, domainobject, userUri);
  }

  private View view;
  private ObjectNode domainObject;
  private URI userUri;
  private final List<ConstraintConfigurationInstruction> instructions;

  private ViewConstraintConfigurer(View view, ObjectNode domainObject, URI userUri) {
    this.view = view;
    this.domainObject = domainObject;
    this.userUri = userUri;
    this.instructions = new ArrayList<>();
  }

  public ConstraintConfigurer set(String key, String... keys) {
    if (Strings.isNullOrEmpty(key)) {
      throw new IllegalArgumentException();
    }

    final List<String> dataNames = new ArrayList<>();
    dataNames.add(key);
    if (keys != null && keys.length > 0) {
      Arrays.stream(keys).filter(Objects::nonNull).forEach(dataNames::add);
    }

    return new ConstraintConfigurer(dataNames);
  }


  public ConstraintConfigurer any() {
    return set("**");
  }

  public ViewConstraint configure() {
    final List<ComponentConstraint> constraints = new ArrayList<>();

    for (final ConstraintConfigurationInstruction instruction : instructions) {

      final List<ComponentConstraint> componentConstraints = instruction.componentConstraints;
      final ConstraintMarker marker = instruction.marker;
      final List<Predicate<View>> viewPredicates = instruction.viewPredicates;
      final List<Predicate<ObjectNode>> domainObjectPredicates = instruction.domainObjectPredicates;
      final List<Predicate<URI>> userUriPredicates = instruction.userUriPredicates;

      if (matchesPredicateList(viewPredicates, view)
          && matchesPredicateList(domainObjectPredicates, domainObject)
          && matchesPredicateList(userUriPredicates, userUri)) {

        acceptConstraints(constraints, componentConstraints, marker);

      } else if (instruction.fallbackConfiguration != null) {

        final ConstraintConfigurer fallbackConfigurer =
            new ConstraintConfigurer(componentConstraints.stream()
                .toArray(ComponentConstraint[]::new));
        final ConstraintMarker fallbackMarker = instruction.fallbackConfiguration
            .apply(fallbackConfigurer).instruction.marker;
        acceptConstraints(constraints, componentConstraints, fallbackMarker);

      }
    }

    return new ViewConstraint().componentConstraints(constraints);
  }

  public ViewConstraint configure(final View view, final ObjectNode domainobject,
      final URI userUri) {
    this.view = view;
    this.domainObject = domainobject;
    this.userUri = userUri;
    return configure();
  }

  private void acceptConstraints(final List<ComponentConstraint> resultList,
      final List<ComponentConstraint> constraintsToAdd, final ConstraintMarker marker) {
    constraintsToAdd.forEach(c -> resultList.add(marker.apply(c)));
  }

  private <T> boolean matchesPredicateList(List<Predicate<T>> predicates, T subject) {
    return predicates.isEmpty() || predicates.stream().allMatch(p -> p.test(subject));
  }

  public final class ConstraintConfigurer {

    private final List<ComponentConstraint> componentConstraints;

    private ConstraintConfigurer(final List<String> keys) {
      componentConstraints = keys.stream()
          .map(it -> new ComponentConstraint().dataName(it))
          .collect(Collectors.toList());
    }

    private ConstraintConfigurer(final ComponentConstraint... componentConstraints) {
      this.componentConstraints = Arrays.asList(componentConstraints);
    }

    public ConditionConfigurer as(final boolean visible, final boolean enabled,
        final boolean mandatory) {
      final ConstraintMarker marker = new ConstraintMarker(visible, enabled, mandatory);
      return new ConditionConfigurer(componentConstraints, marker);
    }

    public ConditionConfigurer visible() {
      return as(true, false, false);
    }

    public ConditionConfigurer enabled() {
      return as(true, true, false);
    }

    public ConditionConfigurer mandatory() {
      return as(true, true, true);
    }

    public ConditionConfigurer optional() {
      return enabled();
    }

    public ConditionConfigurer disabled() {
      return visible();
    }

    public ConditionConfigurer hidden() {
      return as(false, false, false);
    }

  }

  public final class ConditionConfigurer {
    private final ConstraintConfigurationInstruction instruction;

    private ConditionConfigurer(List<ComponentConstraint> componentConstraints,
        ConstraintMarker marker) {
      instruction = new ConstraintConfigurationInstruction(componentConstraints, marker);
    }

    public ConditionConfigurer when(final Predicate<ObjectNode> domainObjectPredicate) {
      instruction.domainObjectPredicates.add(Objects.requireNonNull(domainObjectPredicate,
          "Domain Object Predicate must not be null!"));
      return ConditionConfigurer.this;
    }

    public ConditionConfigurer whenUser(final Predicate<URI> userUriPredicate) {
      instruction.userUriPredicates.add(Objects.requireNonNull(userUriPredicate,
          "User URI Predicate must not be null!"));
      return ConditionConfigurer.this;
    }

    public ConditionConfigurer whenView(final Predicate<View> viewPredicate) {
      instruction.viewPredicates.add(Objects.requireNonNull(viewPredicate,
          "View Predicate must not be null!"));
      return ConditionConfigurer.this;
    }

    public ViewConstraintConfigurer and() {
      ViewConstraintConfigurer.this.instructions.add(instruction);
      return ViewConstraintConfigurer.this;
    }

    public ViewConstraintConfigurer always() {
      instruction.domainObjectPredicates.clear();
      instruction.userUriPredicates.clear();
      return and();
    }

    public ViewConstraintConfigurer orElse(
        Function<ConstraintConfigurer, ConditionConfigurer> fallbackConfiguration) {
      instruction.fallbackConfiguration = fallbackConfiguration;
      return and();
    }

  }

  private static final class ConstraintMarker {
    private final boolean visible;
    private final boolean enabled;
    private final boolean mandatory;

    private ConstraintMarker(boolean visible, boolean enabled, boolean mandatory) {
      this.visible = visible;
      this.enabled = enabled;
      this.mandatory = mandatory;
    }

    private ComponentConstraint apply(final ComponentConstraint componentConstraint) {
      return new ComponentConstraint()
          .dataName(componentConstraint.getDataName())
          .valueSet(componentConstraint.getValueSet())
          .visible(visible)
          .enabled(enabled)
          .mandatory(mandatory);
    }

  }

  private static final class ConstraintConfigurationInstruction {
    private final List<ComponentConstraint> componentConstraints;
    private final ConstraintMarker marker;
    private final List<Predicate<View>> viewPredicates;
    private final List<Predicate<ObjectNode>> domainObjectPredicates;
    private final List<Predicate<URI>> userUriPredicates;
    private Function<ConstraintConfigurer, ConditionConfigurer> fallbackConfiguration;

    private ConstraintConfigurationInstruction(List<ComponentConstraint> componentConstraints,
        ConstraintMarker marker) {
      this.componentConstraints = componentConstraints;
      this.marker = marker;
      this.viewPredicates = new ArrayList<>();
      this.domainObjectPredicates = new ArrayList<>();
      this.userUriPredicates = new ArrayList<>();
    }
  }

}
