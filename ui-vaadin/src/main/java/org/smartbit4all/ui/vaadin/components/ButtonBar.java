/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
