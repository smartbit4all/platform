package org.smartbit4all.api.view;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.View;

class UiActionTest {

  private static final Supplier<List<UiAction>> EXAMPLE_ACTIONS = () -> {
    final List<UiAction> actions = new ArrayList<>();

    actions.add(new UiAction().code("SAVE"));
    actions.add(new UiAction().code("BACK"));
    actions.add(new UiAction().code("NEXT"));

    return actions;
  };

  private static View initDefaultView() {
    return new View().actions(EXAMPLE_ACTIONS.get());
  }


  @Test
  @DisplayName("Adding an existing action does not modify view actions")
  void noActionDuplication() {
    final View view = initDefaultView();
    assertThat(view).isNotNull();
    assertThat(view.getActions()).hasSize(3);

    UiActions.add(view, "SAVE");
    assertThat(view.getActions()).hasSize(3);
  }

  @Test
  @DisplayName("New actions are appended to the end of the action list.")
  void actionAddedAsLast() {
    final View view = initDefaultView();
    assertThat(view).isNotNull();
    assertThat(view.getActions()).hasSize(3);

    UiActions.add(view, "CANCEL", "UPDATE", "TURBO");
    final List<UiAction> actions = view.getActions();
    assertThat(actions).hasSize(6);
    assertThat(actions.subList(3, actions.size()).stream().map(UiAction::getCode))
        .containsExactly("CANCEL", "UPDATE", "TURBO");
  }

  @Test
  @DisplayName("Removing unknown action does not alter the action list.")
  void noPhantomActionRemoval() {
    final View view = initDefaultView();
    assertThat(view).isNotNull();
    assertThat(view.getActions()).hasSize(3);

    UiActions.remove(view, "TURBO");
    assertThat(view.getActions()).hasSize(3);
  }

  @Test
  @DisplayName("Removing an action preserves original action ordering")
  void removeMiddleAction() {
    final View view = initDefaultView();
    assertThat(view).isNotNull();
    assertThat(view.getActions()).hasSize(3);

    UiActions.remove(view, "BACK");
    assertThat(view.getActions()).hasSize(2);
    assertThat(view.getActions().stream().map(UiAction::getCode)).containsExactly("SAVE", "NEXT");
  }

  @Test
  @DisplayName("Adding a new action and modifying it is performed in one operation.")
  void addOrModify_add() {
    final View view = initDefaultView();
    assertThat(view).isNotNull();
    assertThat(view.getActions())
        .hasSize(3)
        .noneMatch(a -> Boolean.TRUE.equals(a.getSubmit()));

    UiActions.addOrModify(view.getActions(), a -> a.submit(true), "TURBO");
    assertThat(view.getActions())
        .hasSize(4)
        .contains(new UiAction().code("TURBO").submit(true), Index.atIndex(3));
  }

  @Test
  @DisplayName("Modifying an action based on code works")
  void addOrModify_modify() {
    Predicate<? super UiAction> hasFileInputType = a -> UiActionInputType.FILE == a.getInputType();

    final View view = initDefaultView();
    assertThat(view).isNotNull();
    assertThat(view.getActions())
        .hasSize(3)
        .noneMatch(hasFileInputType);

    UiActions.addOrModify(view.getActions(),
        a -> a.inputType(UiActionInputType.FILE),
        "NEXT", "BACK", "SAVE");
    assertThat(view.getActions())
        .hasSize(3)
        .allMatch(hasFileInputType);

  }

}
