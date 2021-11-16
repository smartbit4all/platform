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
      String iconSize,
      ComponentEventListener<ClickEvent<Button>> listener) {

    Button newButton = new Button();

    icon.setSize(iconSize);

    newButton.getElement().appendChild(icon.getElement());
    newButton.getElement().appendChild(new Div(new Label(text)).getElement());

    if (listener != null) {
      newButton.addClickListener(listener);
    }

    return newButton;
  }

}
