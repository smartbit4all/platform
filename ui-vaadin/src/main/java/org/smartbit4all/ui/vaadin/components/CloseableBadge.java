package org.smartbit4all.ui.vaadin.components;

import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeColor;
import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeShape;
import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;

@Tag(value = "sb4-closeablebadge")
public class CloseableBadge extends Badge {

  private Button btnClose;

  public CloseableBadge(String text, BadgeColor color, BadgeSize size, BadgeShape shape) {
    super(text, color, size, shape);
    btnClose = new Button("x");
    this.add(btnClose);
  }

  public Button getCloseButton() {
    return btnClose;
  }

}
