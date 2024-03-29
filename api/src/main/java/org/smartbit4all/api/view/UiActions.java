package org.smartbit4all.api.view;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.View;

public final class UiActions {

  private static final int JUMBO_OPERATION_THRESHOLD = 64;

  public static final String INPUT = "input";

  public static final String INPUT2 = "input2";

  public static final String MODEL = "model";

  public static final String ITEM = "item";

  public static final String URI = "uri";

  public static final String TITLE = UiActionDescriptor.TITLE;

  public static final String FILES = "_files";

  public static final String SEPARATOR = "_actionSeparator_";

  public static class Color {
    private Color() {};

    public static final String PRIMARY = "primary";
    public static final String SECONDARY = "secondary";
    public static final String ACCENT = "accent";
    public static final String ERROR = "error";
    public static final String WARN = "warn";
  }

  private UiActions() {}

  public static UiActionBuilder builder() {
    return new UiActionBuilder();
  }

  public static UiActionBuilder builder(List<UiAction> actions) {
    return new UiActionBuilder(actions);
  }

  /**
   * Initiate a builder for the {@link UiAction#getSubActions()}.
   *
   * @param action The sub menu action.
   * @return
   */
  public static UiActionBuilder builder(UiAction action) {
    return new UiActionBuilder(action);
  }

  /**
   * Checks whether a {@code UiAction} with the specified code is present in the provided list of
   * actions.
   *
   * @param actions the {@code List} of actions to search through, not null
   * @param code {@code String} {@link UiAction#getCode()} to search for, not null
   * @return true if the list contains a matching action, false otherwise
   */
  public static boolean contains(Collection<? extends UiAction> actions, String code) {
    Objects.requireNonNull(actions, "actions cannot be null!");
    Objects.requireNonNull(code, "code cannot be null!");

    return actions.stream().anyMatch(actionCode(code));
  }

  /**
   * Checks whether a submenu with the specified code is present in the provided list of actions.
   *
   * @param actions the {@code List} of actions to search through, not null
   * @param code {@code String} {@link UiAction#getCode()} to search for, not null
   * @return true if the list contains a matching action, false otherwise
   */
  public static boolean containsSubmenu(Collection<UiAction> actions, String code) {
    Objects.requireNonNull(actions, "actions cannot be null!");
    Objects.requireNonNull(code, "code cannot be null!");

    return actions.stream().filter(
        a -> a.getDescriptor() != null && a.getDescriptor().getType() == UiActionButtonType.SUBMENU)
        .anyMatch(actionCode(code));
  }

  public static final List<UiAction> flatSubMenuStructure(List<UiAction> actions) {
    return actions.stream().flatMap(a -> {
      if (a.getDescriptor() != null && a.getSubActions() != null
          && UiActionButtonType.SUBMENU.equals(a.getDescriptor().getType())) {
        return flatSubMenuStructure(a.getSubActions()).stream();
      } else {
        return Stream.of(a);
      }
    }).collect(toList());
  }

  private static Predicate<? super UiAction> actionCode(String code) {
    return a -> code.equals(a.getCode());
  }

  /**
   * Adds {@code UiAction}(s) to the specified {@link View} if they are not present already.
   *
   * <p>
   * The new {@code UiAction}s are added with nothing but their {@link UiAction#CODE} set.
   *
   * @param view the {@link View} to modify, not null
   * @param code the {@code String} {@link UiAction#CODE} to add, not null
   * @param codes additional {@String} action codes to add, optional
   */
  public static void add(View view, String code, String... codes) {
    add(view.getActions(), code, codes);
  }

  /**
   * Adds {@code UiAction}(s) to the specified {@link View} if they are not present already.
   *
   * <p>
   * The new {@code UiAction}s are added with nothing but their {@link UiAction#CODE} set.
   *
   * @param view the {@link View} to modify, not null
   * @param idx the idx of the new uiAction
   * @param code the {@code String} {@link UiAction#CODE} to add, not null
   */
  public static void add(View view, int idx, String code) {
    add(view.getActions(), idx, code);
  }

  /**
   * Adds {@code UiAction}(s) to the specified {@code List} if they are not present already.
   *
   * <p>
   * The new {@code UiAction}s are added with nothing but their {@link UiAction#CODE} set.
   *
   * <p>
   * This method directly modifies to provided {@code List}, thus it must be ensured that invoking
   * the {@link List#add(Object)} method is supported (which is not the case for unmodifiable
   * collections).
   *
   * @param actions the {@code List} of {@code UiAction}s to modify, not null
   * @param idx the idx of the new uiAction
   * @param code the {@code String} {@link UiAction#CODE} to add, not null
   */
  public static void add(List<UiAction> actions, int idx, String code) {
    Objects.requireNonNull(actions, "actions cannot be null!");
    Objects.requireNonNull(code, "code cannot be null!");

    addSingle(actions, idx, code);
  }

  /**
   * Adds {@code UiAction}(s) to the specified {@code List} if they are not present already.
   *
   * <p>
   * The new {@code UiAction}s are added with nothing but their {@link UiAction#CODE} set.
   *
   * <p>
   * This method directly modifies to provided {@code List}, thus it must be ensured that invoking
   * the {@link List#add(Object)} method is supported (which is not the case for unmodifiable
   * collections).
   *
   * @param actions the {@code List} of {@code UiAction}s to modify, not null
   * @param code the {@code String} {@link UiAction#CODE} to add, not null
   * @param codes additional {@String} action codes to add, optional
   */
  public static void add(List<UiAction> actions, String code, String... codes) {
    Objects.requireNonNull(actions, "actions cannot be null!");
    Objects.requireNonNull(code, "code cannot be null!");

    if (isJumboOperation(actions, codes)) {
      addJumbo(actions, code, codes);
      return;
    }

    addSingle(actions, code);

    if (varargsPresent(codes)) {
      for (String c : codes) {
        addSingle(actions, c);
      }
    }
  }

  private static void addSingle(List<UiAction> actions, String code) {
    addSingle(actions, null, code);
  }

  private static void addSingle(List<UiAction> actions, Integer idx, String code) {
    if (!contains(actions, code)) {
      UiAction uiAction = new UiAction().code(code);
      if (idx == null) {
        actions.add(uiAction);
      } else {
        actions.add(idx, uiAction);
      }
    }
  }

  private static void addJumbo(Collection<UiAction> actions, String code, String... codes) {
    final Set<String> codesPresent = actions.stream()
        .map(UiAction::getCode)
        .collect(toSet());
    if (!codesPresent.contains(code)) {
      actions.add(new UiAction().code(code));
    }

    Arrays.stream(codes)
        .filter(c -> !codesPresent.contains(c))
        .forEach(c -> actions.add(new UiAction().code(c)));
  }

  public static void add(View view, Collection<UiAction> actions) {
    addActionsInternal(view.getActions(), actions);
  }

  private static void addActionsInternal(Collection<UiAction> present, Collection<UiAction> toAdd) {
    Objects.requireNonNull(present, "actions present cannot be null!");
    Objects.requireNonNull(toAdd, "actions toAdd cannot be null!");

    if (isJumboOperation(present, toAdd)) {
      final Set<String> codesPresent = present.stream()
          .map(UiAction::getCode)
          .collect(toSet());
      toAdd.stream()
          .filter(a -> !codesPresent.contains(a.getCode()))
          .forEach(present::add);
    } else {
      toAdd.forEach(a -> addSingle(present, a));
    }
  }

  /**
   * Adds {@code UiAction}(s) to the specified {@link View} if they are not present already.
   *
   * @param view the {@link View} to modify, not null
   * @param a the {@link UiAction} to add, not null
   * @param as additional {@code UiAction}s to add, optional
   */
  public static void add(View view, UiAction a, UiAction... as) {
    add(view.getActions(), a, as);
  }

  /**
   * Adds {@code UiAction}(s) to the specified {@code List} if they are not present already.
   *
   * <p>
   * This method directly modifies to provided {@code List}, thus it must be ensured that invoking
   * the {@link List#add(Object)} method is supported (which is not the case for unmodifiable
   * collections).
   *
   * @param actions the {@code List} of {@code UiAction}s to modify, not null
   * @param a the {@link UiAction} to add, not null
   * @param as additional {@code UiAction}s to add, optional
   */
  public static void add(Collection<UiAction> actions, UiAction a, UiAction... as) {
    Objects.requireNonNull(actions, "actions cannot be null!");
    Objects.requireNonNull(a, "action to add cannot be null!");

    if (isJumboOperation(actions, as)) {
      addJumbo(actions, a, as);
      return;
    }

    addSingle(actions, a);

    if (varargsPresent(as)) {
      for (UiAction e : as) {
        addSingle(actions, e);
      }
    }
  }

  private static boolean isJumboOperation(Collection<?> c1, Collection<?> c2) {
    return (c1.size() + c2.size()) > JUMBO_OPERATION_THRESHOLD;
  }

  @SafeVarargs
  private static <T> boolean isJumboOperation(Collection<UiAction> actions, T... arr) {
    return (arr != null) && ((actions.size() + arr.length) > JUMBO_OPERATION_THRESHOLD);
  }

  @SafeVarargs
  private static <T> boolean varargsPresent(T... args) {
    return args != null && args.length > 0;
  }

  private static void addSingle(Collection<UiAction> actions, UiAction action) {
    if (!contains(actions, action.getCode())) {
      actions.add(action);
    }
  }

  private static void addJumbo(Collection<UiAction> actions, UiAction a, UiAction... as) {
    final Set<String> codesPresent = actions.stream()
        .map(UiAction::getCode)
        .collect(toSet());
    if (!codesPresent.contains(a.getCode())) {
      actions.add(a);
    }

    Arrays.stream(as)
        .filter(e -> !codesPresent.contains(e.getCode()))
        .forEach(actions::add);
  }



  /**
   * Removes the {@code UiAction}(s) denoted by the specified {@link UiAction#CODE}s from a given
   * view, if and only if they are present.
   *
   * @param view the {@link View} to modify, not null
   * @param code the {@link UiAction#CODE} to remove, not null
   * @param codes additional {@code UiAction#CODE}s to remove, optional
   */
  public static void remove(View view, String code, String... codes) {
    remove(view.getActions(), code, codes);
  }

  /**
   * Removes the specified {@code UiAction}(s) from a given view, if and only if they are present.
   *
   * <p>
   * Actions are matched by their {@link UiAction#getCode()}, thus the following code will remove
   * the action from the view:
   *
   * <pre>
   * <code>
   * final String myCode = "myCode";
   *
   * UiActions.add(view, new UiAction().code(myCode).submit(true));
   * UiActions.remove(view, new UiAction().code(myCode));
   * </code>
   * </pre>
   *
   * @param view the {@link View} to modify, not null
   * @param a the {@link UiAction} to remove, not null
   * @param as additional {@code UiAction}s to remove, optional
   */
  public static void remove(View view, UiAction a, UiAction... as) {
    remove(view.getActions(), a, as);
  }

  /**
   * Removes the specified {@code UiAction}(s) from a given {code List} of actions, if and only if
   * they are present.
   *
   * <p>
   * This method directly modifies to provided {@code List}, thus it must be ensured that invoking
   * the {@link List#removeIf(Predicate)} method is supported (which is not the case for
   * unmodifiable collections).
   *
   * <p>
   * Actions are matched by their {@link UiAction#getCode()}, thus the following code will remove
   * the action from the list:
   *
   * <pre>
   * <code>
   * final List<UiAction> actions = new ArrayList<>();
   * final String myCode = "myCode";
   *
   * UiActions.add(actions, new UiAction().code(myCode).submit(true));
   * UiActions.remove(actions, new UiAction().code(myCode));
   * </code>
   * </pre>
   *
   * @param actions the {@code List} of actions to modify, not null
   * @param a the {@link UiAction} to remove, not null
   * @param as additional {@code UiAction}s to remove, optional
   */
  public static void remove(Collection<UiAction> actions, UiAction a, UiAction... as) {
    if (!varargsPresent(as)) {
      remove(actions, a.getCode());
    } else {
      remove(actions, a.getCode(), Arrays.stream(as)
          .map(UiAction::getCode)
          .toArray(String[]::new));
    }
  }

  /**
   * Removes the {@code UiAction}(s) denoted by the specified {@link UiAction#CODE}s from a given
   * {@code List} of actions, if and only if they are present.
   *
   * <p>
   * This method directly modifies to provided {@code List}, thus it must be ensured that invoking
   * the {@link List#removeIf(Predicate)} method is supported (which is not the case for
   * unmodifiable collections).
   *
   * @param actions the {@code List} of actions to modify, not null
   * @param code the {@link UiAction#CODE} to remove, not null
   * @param codes additional {@code UiAction#CODE}s to remove, optional
   */
  public static void remove(Collection<UiAction> actions, String code, String... codes) {
    if (!varargsPresent(codes)) {
      actions.removeIf(actionCode(code));
    } else {
      final Set<String> allCodes = Stream
          .concat(Stream.of(code), Arrays.stream(codes))
          .collect(toSet());
      actions.removeIf(a -> allCodes.contains(a.getCode()));
    }
  }

  public static void addOrModify(List<UiAction> actions, UnaryOperator<UiAction> modifier,
      String code, String... codes) {
    Objects.requireNonNull(actions, "actions cannot be null!");
    Objects.requireNonNull(modifier, "modifier cannot be null!");
    Objects.requireNonNull(code, "code cannot be null!");

    final Set<String> allCodes = new HashSet<>();
    allCodes.add(code);
    if (varargsPresent(codes)) {
      allCodes.addAll(Arrays.asList(codes));
    }

    for (int i = 0; i < actions.size(); i++) {
      if (allCodes.isEmpty()) {
        break;
      }

      final UiAction a = actions.get(i);
      final String aCode = a.getCode();
      if (allCodes.contains(aCode)) {
        actions.set(i, modifier.apply(a));
        allCodes.remove(aCode);
      }
    }

    allCodes.forEach(c -> actions.add(modifier.apply(new UiAction().code(c))));
  }

  public static class UiActionBuilder {

    private final List<UiAction> actions;

    private UiActionBuilder() {
      this.actions = new ArrayList<>();
    }

    private UiActionBuilder(List<UiAction> actions) {
      this.actions = actions;
    }

    private UiActionBuilder(UiAction subMenu) {
      if (subMenu.getSubActions() == null) {
        subMenu.setSubActions(new ArrayList<>());
      }
      this.actions = subMenu.getSubActions();
    }

    public UiActionBuilder add(String action) {
      return add(new UiAction().code(action));
    }

    public UiActionBuilder add(UiAction action) {
      this.actions.add(action);
      return this;
    }

    public UiActionBuilder addIf(String action, boolean... conditions) {
      return addIf(new UiAction().code(action), conditions);
    }

    public UiActionBuilder addIf(UiAction action, boolean... conditions) {
      for (boolean c : conditions) {
        if (!c)
          return this;
      }
      return this.add(action);
    }

    public UiActionBuilder addSeparator(boolean... conditions) {
      for (boolean c : conditions) {
        if (!c)
          return this;
      }
      return this.add(separator());
    }

    /**
     * Return the builder for the sub menu!
     *
     * @param action
     * @param conditions
     * @return The sub menu builder if the action was added or null if net!!!
     */
    public UiActionBuilder addSubMenu(String action, boolean... conditions) {
      for (boolean c : conditions) {
        if (!c)
          return this;
      }
      Optional<UiAction> existingSubMenu =
          actions.stream().filter(a -> Objects.equals(action, a.getCode())).findFirst();
      return builder(existingSubMenu.orElseGet(() -> {
        UiAction sma = subMenu(action);
        add(sma);
        return sma;
      }));
    }

    public boolean removeIf(String action, boolean... conditions) {
      for (boolean c : conditions) {
        if (!c)
          return false;
      }

      return actions.removeIf(a -> Objects.equals(a.getCode(), action));
    }

    public boolean remove(String action) {
      return actions.removeIf(a -> Objects.equals(a.getCode(), action));
    }

    public boolean removeAll(String... actionCodes) {
      if (!Objects.isNull(actionCodes)) {
        for (int i = 0; i < actionCodes.length; i++) {
          String actionCode = actionCodes[i];
          actions.removeIf(a -> Objects.equals(a.getCode(), actionCode));
        }
        return true;
      }
      return false;
    }

    public List<UiAction> build() {
      return actions;
    }
  }

  public static UiAction separator() {
    return new UiAction()
        .code(SEPARATOR)
        .descriptor(new UiActionDescriptor()
            .type(UiActionButtonType.SEPARATOR));
  }

  public static UiAction subMenu(String action) {
    return new UiAction()
        .code(action)
        .descriptor(new UiActionDescriptor()
            .type(UiActionButtonType.SUBMENU));
  }

}
