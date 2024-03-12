package org.smartbit4all.api.view.constraint;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.core.object.ObjectNode;
import com.google.common.base.Strings;

/**
 * 
 * @author Szabolcs Bazil Papp
 *
 */
public final class ViewConstraintConfigurer {

  public static ViewConstraintConfigurer newInstance() {
    return new ViewConstraintConfigurer(null, null, null, null);
  }

  public static ViewConstraintConfigurer of(final View view, final ObjectNode domainobject,
      final URI userUri, Object viewModel) {
    return new ViewConstraintConfigurer(view, domainobject, userUri, viewModel);
  }

  private final View view;
  private final ObjectNode domainObject;
  private final URI userUri;
  private final Object viewModel;
  private final List<ConstraintConfigurationInstructionBundle> instructionBundles;
  private final List<ComponentConstraint> results;

  private ViewConstraintConfigurer(View view, ObjectNode domainObject, URI userUri,
      Object viewModel) {
    this.view = view;
    this.domainObject = domainObject;
    this.userUri = userUri;
    this.viewModel = viewModel;
    this.instructionBundles = new ArrayList<>();
    this.results = new ArrayList<>();
  }

  public ViewConstraintConfigurer withViewModel(final Object newViewModel) {
    return of(view, domainObject, userUri, newViewModel);
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
    if (!results.isEmpty()) {
      return new ViewConstraint().componentConstraints(results);
    }


    for (final ConstraintConfigurationInstructionBundle bundle : instructionBundles) {
      processInstructionBundle(bundle);
    }

    return new ViewConstraint().componentConstraints(results);
  }

  private boolean processInstructionBundle(final ConstraintConfigurationInstructionBundle bundle) {
    for (final ConstraintConfigurationInstruction instruction : bundle.instructions) {

      final List<ComponentConstraint> componentConstraints = instruction.componentConstraints;
      final ConstraintMarker marker = instruction.marker;
      if (conditionMatched(instruction)) {
        acceptConstraints(componentConstraints, marker);
        return true;

      } else if (instruction.fallbackConfiguration != null) {
        final ConstraintConfigurer fallbackConfigurer =
            new ConstraintConfigurer(componentConstraints.stream()
                .toArray(ComponentConstraint[]::new));
        final ConditionConfigurer fallbackConditionConfigurer = instruction.fallbackConfiguration
            .apply(fallbackConfigurer);
        final boolean fallbackResult =
            processInstructionBundle(fallbackConditionConfigurer.instructionBundle);
        if (fallbackResult) {
          return true;
        }

      }

    }
    return false;
  }

  private boolean conditionMatched(ConstraintConfigurationInstruction instruction) {
    return matchesPredicateList(instruction.viewPredicates, view)
        && matchesPredicateList(instruction.domainObjectPredicates, domainObject)
        && matchesPredicateList(instruction.userUriPredicates, userUri)
        && matchesPredicateList(instruction.userDomainObjectBiPredicates, userUri, domainObject)
        && matchesTypeBasedPredicateLists(instruction.viewModelPredicates, viewModel);
  }

  private <T> boolean matchesPredicateList(List<Predicate<T>> predicates, T subject) {
    return predicates.isEmpty() // predicate list is empty -> auto match; else subject is not null
                                // and matches all predicates:
        || (subject != null && predicates.stream().allMatch(p -> p.test(subject)));
  }

  private <T, U> boolean matchesPredicateList(List<BiPredicate<T, U>> predicates, T subjectA,
      U subjectB) {
    return predicates.isEmpty() // predicate list is empty -> auto match; else subjects are not null
                                // and matches all predicates:
        || (subjectA != null && subjectB != null
            && predicates.stream().allMatch(p -> p.test(subjectA, subjectB)));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private boolean matchesTypeBasedPredicateLists(Map<Class<?>, List<Predicate<?>>> predicates,
      Object subject) {
    return predicates.isEmpty()
        || (subject != null && predicates.entrySet().stream()
            .filter(e -> e.getKey().isInstance(subject))
            .allMatch(e -> {
              final Class<?> clazz = e.getKey();
              final List<Predicate<?>> ps = e.getValue();
              return ps.stream().allMatch(p -> ((Predicate) p).test(clazz.cast(subject)));
            }));
  }

  private void acceptConstraints(final List<ComponentConstraint> constraintsToAdd,
      final ConstraintMarker marker) {
    constraintsToAdd.forEach(c -> results.add(marker.apply(c)));
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
    private final ConstraintConfigurationInstructionBundle instructionBundle;
    private ConstraintConfigurationInstruction currentInstruction;

    private ConditionConfigurer(List<ComponentConstraint> componentConstraints,
        ConstraintMarker marker) {
      currentInstruction = new ConstraintConfigurationInstruction(componentConstraints, marker);
      instructionBundle = new ConstraintConfigurationInstructionBundle();
      instructionBundle.instructions.add(currentInstruction);
    }

    public ConditionConfigurer whenDomainNode(final Predicate<ObjectNode> domainObjectPredicate) {
      currentInstruction.domainObjectPredicates.add(Objects.requireNonNull(domainObjectPredicate,
          "Domain Object Predicate must not be null!"));
      return ConditionConfigurer.this;
    }

    public ConditionConfigurer whenUser(final Predicate<URI> userUriPredicate) {
      currentInstruction.userUriPredicates.add(Objects.requireNonNull(userUriPredicate,
          "User URI Predicate must not be null!"));
      return ConditionConfigurer.this;
    }

    public ConditionConfigurer whenView(final Predicate<View> viewPredicate) {
      currentInstruction.viewPredicates.add(Objects.requireNonNull(viewPredicate,
          "View Predicate must not be null!"));
      return ConditionConfigurer.this;
    }

    public ConditionConfigurer whenUser(
        final BiPredicate<URI, ObjectNode> userDomainObjectBiPredicate) {
      currentInstruction.userDomainObjectBiPredicates.add(Objects.requireNonNull(
          userDomainObjectBiPredicate,
          "User-DomainObject joint predicate must not be null!"));
      return ConditionConfigurer.this;
    }

    public ConditionConfigurer whenViewModel(final Predicate<Object> viewModelPredicate) {
      currentInstruction.viewModelPredicates
          .computeIfAbsent(Object.class, k -> new ArrayList<>())
          .add(Objects.requireNonNull(
              viewModelPredicate,
              "ViewModel predicate must not be null!"));
      return ConditionConfigurer.this;
    }

    public <M> ConditionConfigurer whenViewModel(final Class<M> viewModelClass,
        Predicate<M> viewModelPredicate) {
      Objects.requireNonNull(viewModelClass, "viewModelClass cannot be null!");
      currentInstruction.viewModelPredicates
          .computeIfAbsent(viewModelClass, k -> new ArrayList<>())
          .add(Objects.requireNonNull(
              viewModelPredicate,
              "ViewModel predicate must not be null!"));
      return ConditionConfigurer.this;
    }

    public ConditionConfigurer or() {
      currentInstruction = currentInstruction.cleanCopy();
      instructionBundle.instructions.add(currentInstruction);
      return ConditionConfigurer.this;
    }

    public ViewConstraintConfigurer next() {
      currentInstruction = null;
      ViewConstraintConfigurer.this.instructionBundles.add(instructionBundle);
      return ViewConstraintConfigurer.this;
    }

    public ViewConstraintConfigurer always() {
      currentInstruction.domainObjectPredicates.clear();
      currentInstruction.userUriPredicates.clear();
      return next();
    }

    public ViewConstraintConfigurer orElse(
        Function<ConstraintConfigurer, ConditionConfigurer> fallbackConfiguration) {
      currentInstruction.fallbackConfiguration = fallbackConfiguration;
      return next();
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
    private final List<BiPredicate<URI, ObjectNode>> userDomainObjectBiPredicates;
    private final Map<Class<?>, List<Predicate<?>>> viewModelPredicates;
    private Function<ConstraintConfigurer, ConditionConfigurer> fallbackConfiguration;

    private ConstraintConfigurationInstruction(List<ComponentConstraint> componentConstraints,
        ConstraintMarker marker) {
      this.componentConstraints = componentConstraints;
      this.marker = marker;
      this.viewPredicates = new ArrayList<>();
      this.domainObjectPredicates = new ArrayList<>();
      this.userUriPredicates = new ArrayList<>();
      this.userDomainObjectBiPredicates = new ArrayList<>();
      this.viewModelPredicates = new HashMap<>();
    }

    private ConstraintConfigurationInstruction cleanCopy() {
      return new ConstraintConfigurationInstruction(componentConstraints, marker);
    }

  }

  private static final class ConstraintConfigurationInstructionBundle {
    private final List<ConstraintConfigurationInstruction> instructions;

    public ConstraintConfigurationInstructionBundle() {
      this.instructions = new ArrayList<>();
    }
  }

}
