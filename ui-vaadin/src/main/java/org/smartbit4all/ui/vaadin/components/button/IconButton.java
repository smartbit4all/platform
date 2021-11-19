package org.smartbit4all.ui.vaadin.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;

public class IconButton {

  public static Button createIconButton(
      String text,
      Icon icon,
      ComponentEventListener<ClickEvent<Button>> listener) {

    Button button = new Button();
    button.addClassName("sb4-iconbutton");

    button.getElement().appendChild(icon.getElement());
    Label label = new Label(text);
    label.addClassName("sb4-iconbutton-label");
    button.getElement().appendChild(new Div(label).getElement());

    if (listener != null) {
      button.addClickListener(listener);
    }

    return button;
  }

}
