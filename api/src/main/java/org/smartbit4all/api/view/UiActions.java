package org.smartbit4all.api.view;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionDescriptor;

public class UiActions {

  public static final String INPUT = "input";

  public static final String INPUT2 = "input2";

  public static final String MODEL = "model";

  public static final String ITEM = "item";

  public static final String URI = "uri";

  public static final String TITLE = UiActionDescriptor.TITLE;

  private UiActions() {}

  public static UiActionBuilder builder() {
    return new UiActionBuilder();
  }

  public static class UiActionBuilder {

    private final List<UiAction> actions;

    private UiActionBuilder() {
      this.actions = new ArrayList<>();
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

    public List<UiAction> build() {
      return this.actions;
    }
  }
}
