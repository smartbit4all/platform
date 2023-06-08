package org.smartbit4all.api.view;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.view.bean.UiAction;

public class UiActions {

  public static final String INPUT = "input";

  public static final String INPUT2 = "input2";

  public static final String MODEL = "model";

  public static final String ITEM = "item";

  public static final String URI = "uri";

  private UiActions() {}

  public static UiActionsBuilder builder() {
    return new UiActionsBuilder();
  }

  public static class UiActionsBuilder {

    private final List<UiAction> actions;

    private UiActionsBuilder() {
      this.actions = new ArrayList<>();
    }

    public UiActionsBuilder add(String action) {
      return add(new UiAction().code(action));
    }

    public UiActionsBuilder add(UiAction action) {
      this.actions.add(action);
      return this;
    }

    public UiActionsBuilder addIf(String action, boolean... conditions) {
      return addIf(new UiAction().code(action), conditions);
    }

    public UiActionsBuilder addIf(UiAction action, boolean... conditions) {
      for (boolean c : conditions) {
        if (!c)
          return this;
      }
      return this.add(action);
    }

    public List<UiAction> build() {
      return this.actions;
    }
  }
}
