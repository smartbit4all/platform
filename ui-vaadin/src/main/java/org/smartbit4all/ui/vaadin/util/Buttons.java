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
package org.smartbit4all.ui.vaadin.util;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Utility for creating Vaadin buttons.
 * 
 * @author Attila Mate
 *
 */
public class Buttons {

  public static Button createPrimaryButton(String text) {
    return createButton(text, ButtonVariant.LUMO_PRIMARY);
  }

  public static Button createPrimaryButton(String text, VaadinIcon icon) {
    return createButton(text, icon, ButtonVariant.LUMO_PRIMARY);
  }

  public static Button createTertiaryButton(String text) {
    return createButton(text, ButtonVariant.LUMO_TERTIARY);
  }

  public static Button createTertiaryButton(VaadinIcon icon) {
    return createButton(icon, ButtonVariant.LUMO_TERTIARY);
  }

  public static Button createTertiaryInlineButton(VaadinIcon icon) {
    return createButton(icon, ButtonVariant.LUMO_TERTIARY_INLINE);
  }

  public static Button createTertiaryInlineButton(String text,
      VaadinIcon icon) {
    return createButton(text, icon, ButtonVariant.LUMO_TERTIARY_INLINE);
  }

  // Size

  public static Button createSmallButton(String text) {
    return createButton(text, ButtonVariant.LUMO_SMALL);
  }

  public static Button createSmallButton(VaadinIcon icon) {
    return createButton(icon, ButtonVariant.LUMO_SMALL);
  }

  public static Button createSmallButton(String text, VaadinIcon icon) {
    return createButton(text, icon, ButtonVariant.LUMO_SMALL);
  }

  public static Button createLargeButton(String text) {
    return createButton(text, ButtonVariant.LUMO_LARGE);
  }

  public static Button createLargeButton(VaadinIcon icon) {
    return createButton(icon, ButtonVariant.LUMO_LARGE);
  }

  public static Button createLargeButton(String text, VaadinIcon icon) {
    return createButton(text, icon, ButtonVariant.LUMO_LARGE);
  }

  // Text

  public static Button createButton(String text, ButtonVariant... variants) {
    Button button = new Button(text);
    button.addThemeVariants(variants);
    button.getElement().setAttribute("aria-label", text);
    return button;
  }

  // Icon

  public static Button createButton(VaadinIcon icon,
      ButtonVariant... variants) {
    Button button = new Button(new Icon(icon));
    button.addThemeVariants(variants);
    return button;
  }

  // Text and icon

  public static Button createButton(String text, VaadinIcon icon,
      ButtonVariant... variants) {
    Icon i = new Icon(icon);
    i.getElement().setAttribute("slot", "prefix");
    Button button = new Button(text, i);
    button.addThemeVariants(variants);
    return button;
  }
}
