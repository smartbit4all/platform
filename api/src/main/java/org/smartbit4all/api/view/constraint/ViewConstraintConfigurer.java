package org.smartbit4all.api.view.constraint;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.lang.Nullable;
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

  private boolean squash = false;

  private ViewConstraintConfigurer(View view, ObjectNode domainObject, URI userUri,
      Object viewModel) {
    this.view = view;
    this.domainObject = domainObject;
    this.userUri = userUri;
    this.viewModel = viewModel;
    this.instructionBundles = new ArrayList<>();
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

  public EnforcementConfigurer enforce() {
    return new EnforcementConfigurer();
  }

  /**
   *
   * @return this instance
   */
  public ViewConstraintConfigurer squash() {
    this.squash = true;
    return this;
  }

  private List<ComponentConstraint> doSquash(List<ComponentConstraint> constraints) {
    final List<ComponentConstraint> ret = new ArrayList<>(constraints.size());
    final Set<String> dataNames = new HashSet<>();
    for (int i = constraints.size() - 1; i >= 0; i--) {
      final ComponentConstraint constraint = constraints.get(i);
      final String dataName = constraint.getDataName();
      if (dataNames.contains(dataName)) {
        continue;
      }

      dataNames.add(dataName);
      ret.add(0, constraint);
    }
    return ret;
  }

  public ViewConstraint configure() {
    List<ComponentConstraint> constraints = new ArrayList<>();
    EnforcementInstruction enforcementInstruction = null;
    for (final ConstraintConfigurationInstructionBundle bundle : instructionBundles) {
      if (enforcementInstruction == null && bundle.enforcementBundle) {
        enforcementInstruction = processEnforcementBundle(bundle);
      } else if (!bundle.enforcementBundle) {
        processInstructionBundle(bundle, constraints);
      }
    }

    if (enforcementInstruction != null) {
      constraints.stream()
          .filter(enforcementInstruction.target::test)
          .forEach(enforcementInstruction.marker::modify);
    }

    if (squash) {
      constraints = doSquash(constraints);
    }
    instructionBundles.clear();

    return new ViewConstraint().componentConstraints(constraints);
  }

  private boolean processInstructionBundle(final ConstraintConfigurationInstructionBundle bundle,
      final List<ComponentConstraint> constraints) {
    for (final ConstraintConfigurationInstruction instruction : bundle.instructions) {

      final List<ComponentConstraint> componentConstraints = instruction.componentConstraints;
      final ConstraintMarker marker = instruction.marker;
      if (conditionMatched(instruction)) {
        acceptConstraints(componentConstraints, marker, constraints);
        return true;

      } else if (instruction.fallbackConfiguration != null) {
        final ConstraintConfigurer fallbackConfigurer =
            new ConstraintConfigurer(componentConstraints.stream()
                .toArray(ComponentConstraint[]::new));
        final ConditionConfigurer fallbackConditionConfigurer = instruction.fallbackConfiguration
            .apply(fallbackConfigurer);
        final boolean fallbackResult =
            processInstructionBundle(fallbackConditionConfigurer.instructionBundle, constraints);
        if (fallbackResult) {
          return true;
        }

      }

    }
    return false;
  }

  private @Nullable EnforcementInstruction processEnforcementBundle(
      final ConstraintConfigurationInstructionBundle bundle) {
    for (final ConstraintConfigurationInstruction i : bundle.instructions) {

      final EnforcementInstruction instruction = (EnforcementInstruction) i;
      if (conditionMatched(instruction)) {
        return instruction;

      } else if (instruction.fallbackConfiguration != null) {
        final ConstraintConfigurer fallbackConfigurer =
            new ConstraintConfigurer(instruction.target);
        final ConditionConfigurer fallbackConditionConfigurer = instruction.fallbackConfiguration
            .apply(fallbackConfigurer);
        final EnforcementInstruction fallbackResult =
            processEnforcementBundle(fallbackConditionConfigurer.instructionBundle);
        if (fallbackResult != null) {
          return fallbackResult;
        }

      }

    }
    return null;
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
      final ConstraintMarker marker, final List<ComponentConstraint> constraints) {
    constraintsToAdd.forEach(c -> constraints.add(marker.apply(c)));
  }

  public final class EnforcementConfigurer {

    private EnforcementConfigurer() {}


    private ConstraintConfigurer setTarget(final ConstraintTarget target) {
      return new ConstraintConfigurer(target);
    }

    public ConstraintConfigurer setEverything() {
      return setTarget(ConstraintTarget.ANY);
    }

    public ConstraintConfigurer setAnythingHidden() {
      return setTarget(ConstraintTarget.HIDDEN);
    }

    public ConstraintConfigurer setAnythingVisible() {
      return setTarget(ConstraintTarget.VISIBLE);
    }

    public ConstraintConfigurer setAnythingEnabled() {
      return setTarget(ConstraintTarget.ENABLED);
    }

    public ConstraintConfigurer setAnythingMandatory() {
      return setTarget(ConstraintTarget.MANDATORY);
    }

  }

  public final class ConstraintConfigurer {

    private final List<ComponentConstraint> componentConstraints;
    private final ConstraintTarget constraintTarget;

    private ConstraintConfigurer(final List<String> keys) {
      componentConstraints = keys.stream()
          .map(it -> new ComponentConstraint()
              .visible(null)
              .enabled(null)
              .mandatory(null)
              .dataName(it))
          .collect(Collectors.toList());
      constraintTarget = null;
    }

    private ConstraintConfigurer(final ComponentConstraint... componentConstraints) {
      this.componentConstraints = Arrays.asList(componentConstraints);
      constraintTarget = null;
    }

    private ConstraintConfigurer(final ConstraintTarget constraintTarget) {
      this.constraintTarget = Objects.requireNonNull(
          constraintTarget,
          "constraintTarget cannot be null!");
      this.componentConstraints = Collections.emptyList();
    }

    public ConditionConfigurer as(final Boolean visible, final Boolean enabled,
        final Boolean mandatory) {
      final ConstraintMarker marker = new ConstraintMarker(visible, enabled, mandatory);
      if (constraintTarget == null) {
        return new ConditionConfigurer(componentConstraints, marker);
      } else {
        return new ConditionConfigurer(constraintTarget, marker);
      }
    }

    public ConditionConfigurer visible() {
      return as(true, null, null);
    }

    public ConditionConfigurer enabled() {
      return as(true, true, null);
    }

    public ConditionConfigurer mandatory() {
      return as(true, true, true);
    }

    // public ConditionConfigurer optional() {
    // return enabled();
    // }

    public ConditionConfigurer disabled() {
      return as(null, false, false);
    }

    public ConditionConfigurer disabledAndVisible() {
      return as(true, false, false);
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

    private ConditionConfigurer(ConstraintTarget constraintTarget, ConstraintMarker marker) {
      currentInstruction = new EnforcementInstruction(constraintTarget, marker);
      instructionBundle = new ConstraintConfigurationInstructionBundle(true);
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
      currentInstruction.clear();
      return next();
    }

    public ViewConstraintConfigurer orElse(
        Function<ConstraintConfigurer, ConditionConfigurer> fallbackConfiguration) {
      currentInstruction.fallbackConfiguration = fallbackConfiguration;
      return next();
    }

  }


  private enum ConstraintTarget {
    // @formatter:off
    ANY(cc -> true),
    HIDDEN(cc -> !Boolean.TRUE.equals(cc.getVisible())),
    VISIBLE(cc -> cc.getVisible() == null || cc.getVisible()),
    ENABLED(cc -> Boolean.TRUE.equals(cc.getEnabled())),
    MANDATORY(cc -> Boolean.TRUE.equals(cc.getMandatory()));
    // @formatter:on

    private final Predicate<ComponentConstraint> p;

    private ConstraintTarget(final Predicate<ComponentConstraint> p) {
      this.p = Objects.requireNonNull(p, "p cannot be null!");
    }

    public boolean test(final ComponentConstraint constraint) {
      return p.test(constraint);
    }
  }


  private static final class ConstraintMarker {
    private final Boolean visible;
    private final Boolean enabled;
    private final Boolean mandatory;

    private ConstraintMarker(Boolean visible, Boolean enabled, Boolean mandatory) {
      this.visible = visible;
      this.enabled = enabled;
      this.mandatory = mandatory;
    }

    private ComponentConstraint apply(final ComponentConstraint componentConstraint) {
      return new ComponentConstraint()
          .dataName(componentConstraint.getDataName())
          .valueSet(componentConstraint.getValueSet())
          .visible(visible != null ? visible : componentConstraint.getVisible())
          .enabled(enabled != null ? enabled : componentConstraint.getEnabled())
          .mandatory(mandatory != null ? mandatory : componentConstraint.getMandatory());
    }

    private void modify(final ComponentConstraint componentConstraint) {
      componentConstraint
          .visible(visible != null ? visible : componentConstraint.getVisible())
          .enabled(enabled != null ? enabled : componentConstraint.getEnabled())
          .mandatory(mandatory != null ? mandatory : componentConstraint.getMandatory());
    }

  }

  private static class ConstraintConfigurationInstruction {
    protected final List<ComponentConstraint> componentConstraints;
    protected final ConstraintMarker marker;
    protected final List<Predicate<View>> viewPredicates;
    protected final List<Predicate<ObjectNode>> domainObjectPredicates;
    protected final List<Predicate<URI>> userUriPredicates;
    protected final List<BiPredicate<URI, ObjectNode>> userDomainObjectBiPredicates;
    protected final Map<Class<?>, List<Predicate<?>>> viewModelPredicates;
    protected Function<ConstraintConfigurer, ConditionConfigurer> fallbackConfiguration;

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

    protected ConstraintConfigurationInstruction cleanCopy() {
      return new ConstraintConfigurationInstruction(componentConstraints, marker);
    }

    private void clear() {
      viewPredicates.clear();
      domainObjectPredicates.clear();
      userUriPredicates.clear();
      userDomainObjectBiPredicates.clear();
      viewModelPredicates.clear();
    }

  }

  private static final class EnforcementInstruction extends ConstraintConfigurationInstruction {
    private final ConstraintTarget target;

    private EnforcementInstruction(final ConstraintTarget target, ConstraintMarker marker) {
      super(Collections.emptyList(), marker);
      this.target = target;
    }

    @Override
    protected ConstraintConfigurationInstruction cleanCopy() {
      return new EnforcementInstruction(target, marker);
    }

  }

  private static final class ConstraintConfigurationInstructionBundle {
    private final boolean enforcementBundle;
    private final List<ConstraintConfigurationInstruction> instructions;

    private ConstraintConfigurationInstructionBundle() {
      this(false);
    }

    private ConstraintConfigurationInstructionBundle(boolean enforcementBundle) {
      this.instructions = new ArrayList<>();
      this.enforcementBundle = enforcementBundle;
    }
  }

}
