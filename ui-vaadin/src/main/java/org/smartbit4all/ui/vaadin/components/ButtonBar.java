package org.smartbit4all.ui.vaadin.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;

public class ButtonBar extends FlexBoxLayout {

  public ButtonBar() {
    this.setClassName("details-drawer__buttonbar");
  }

  public void addButton(Button button) {
    this.add(button);
  }

  public void addButton(String buttonTitle, Icon buttonIcon,
      ComponentEventListener<ClickEvent<Button>> buttonFunction, ButtonVariant buttonVariants) {
    Button button = new Button(buttonTitle);
    button.setIcon(buttonIcon);
    button.addClickListener(buttonFunction);
    button.addThemeVariants(buttonVariants);
    this.add(button);
  }

  public void setDirection(FlexDirection flexDirection) {
    this.setFlexDirection(flexDirection);
  }

  public Button getButtonAt(int idx) {
    return (Button) getComponentAt(idx);
  }
}
