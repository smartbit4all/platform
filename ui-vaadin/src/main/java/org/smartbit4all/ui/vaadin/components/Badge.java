package org.smartbit4all.ui.vaadin.components;

import static org.smartbit4all.ui.vaadin.util.css.lumo.BadgeShape.PILL;
import java.util.StringJoiner;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeColor;
import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeShape;
import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeSize;
import com.vaadin.flow.component.html.Span;

public class Badge extends Span {

  public Badge(String text) {
    this(text, BadgeColor.NORMAL);
  }

  public Badge(String text, BadgeColor color) {
    super(text);
    UIUtils.setTheme(color.getThemeName(), this);
  }

  public Badge(String text, BadgeColor color, BadgeSize size, BadgeShape shape) {
    super(text);
    StringJoiner joiner = new StringJoiner(" ");
    joiner.add(color.getThemeName());
    if (shape.equals(PILL)) {
      joiner.add(shape.getThemeName());
    }
    if (size.equals(BadgeSize.S)) {
      joiner.add(size.getThemeName());
    }
    UIUtils.setTheme(joiner.toString(), this);
  }

}
