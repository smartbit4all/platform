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
package org.smartbit4all.ui.vaadin.components.detailsdrawer;

import java.util.function.Supplier;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.shared.Registration;

public class DetailsDrawerFooter extends FlexLayout {

  private Button view;
  private Button delete;
  private Button save;
  private Button cancel;

  public DetailsDrawerFooter() {
    getStyle().set("background-color", "var(--lumo-contrast-5pct)");
    getStyle().set("padding-left", "var(--lumo-space-r-l)");
    getStyle().set("padding-right", "var(--lumo-space-r-l)");
    getStyle().set("padding-top", "var(--lumo-space-s)");
    getStyle().set("padding-bottom", "var(--lumo-space-s)");
    addClassName("spacing-r-l");
    setJustifyContentMode(JustifyContentMode.EVENLY);
    setWidthFull();
  }

  public Registration addViewListener(
      ComponentEventListener<ClickEvent<Button>> listener) {
    view = addButton(view, () -> UIUtils.createPrimaryButton(getTranslation("title.view")));
    return view.addClickListener(listener);
  }

  public Registration addDeleteListener(
      ComponentEventListener<ClickEvent<Button>> listener) {
    delete = addButton(delete, () -> UIUtils.createTertiaryButton(getTranslation("title.delete")));
    return delete.addClickListener(listener);
  }

  public Registration addSaveListener(
      ComponentEventListener<ClickEvent<Button>> listener) {
    save = addButton(save, () -> UIUtils.createPrimaryButton(getTranslation("title.save")));
    return save.addClickListener(listener);
  }

  public Registration addCancelListener(
      ComponentEventListener<ClickEvent<Button>> listener) {

    cancel = addButton(cancel, () -> UIUtils.createTertiaryButton(getTranslation("title.cancel")));
    return cancel.addClickListener(listener);
  }

  private Button addButton(Button buttonField, Supplier<Button> buttonFactory) {
    if (buttonField != null) {
      remove(buttonField);
    }
    buttonField = buttonFactory.get();
    add(buttonField);
    return buttonField;
  }

}
