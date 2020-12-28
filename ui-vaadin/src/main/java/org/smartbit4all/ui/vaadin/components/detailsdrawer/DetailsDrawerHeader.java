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

import org.smartbit4all.ui.vaadin.util.BoxShadowBorders;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tabs;

public class DetailsDrawerHeader extends FlexLayout {

  private Button close;
  private Label title;

  public DetailsDrawerHeader(String title) {
    addClassName(BoxShadowBorders.BOTTOM);
    setFlexDirection(FlexDirection.COLUMN);
    setWidthFull();

    this.close = UIUtils.createTertiaryInlineButton(VaadinIcon.CLOSE);
    UIUtils.setLineHeight("1", this.close);

    this.title = UIUtils.createH4Label(title);

    FlexLayout wrapper = new FlexLayout(this.close, this.title);
    wrapper.setAlignItems(FlexComponent.Alignment.CENTER);
    wrapper.getStyle().set("padding-left", "var(--lumo-space-r-l)");
    wrapper.getStyle().set("padding-right", "var(--lumo-space-r-l)");
    wrapper.getStyle().set("padding-top", "var(--lumo-space-m)");
    wrapper.getStyle().set("padding-bottom", "var(--lumo-space-m)");
    wrapper.addClassName("spacing-r-l");
    add(wrapper);
  }

  public DetailsDrawerHeader(String title, Tabs tabs) {
    this(title);
    add(tabs);
  }

  public void setTitle(String title) {
    this.title.setText(title);
  }

  public void addCloseListener(ComponentEventListener<ClickEvent<Button>> listener) {
    this.close.addClickListener(listener);
  }

}
