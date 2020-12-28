/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.components;

import org.smartbit4all.ui.vaadin.util.FontSize;
import org.smartbit4all.ui.vaadin.util.TextColor;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class Token extends FlexLayout {

  private final String CLASS_NAME = "token";

  public Token(String text) {
    setAlignItems(FlexComponent.Alignment.CENTER);
    getStyle().set("background-color", "var(--lumo-primary-color-10pct)");
    getStyle().set("border-radius", "var(--lumo-border-radius-m)");
    setClassName(CLASS_NAME);
    getStyle().set("display", "inline-flex");
    getStyle().set("padding-left", "var(--lumo-space-s)");
    getStyle().set("padding-right", "var(--lumo-space-xs)");
    addClassName("spacing-r-xs");

    Label label = UIUtils.createLabel(FontSize.S, TextColor.BODY, text);
    Button button = UIUtils.createButton(VaadinIcon.CLOSE_SMALL, ButtonVariant.LUMO_SMALL,
        ButtonVariant.LUMO_TERTIARY_INLINE);
    add(label, button);
  }

}
