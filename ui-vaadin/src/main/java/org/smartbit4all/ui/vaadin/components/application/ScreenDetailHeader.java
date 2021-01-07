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
package org.smartbit4all.ui.vaadin.components.application;

import org.smartbit4all.ui.vaadin.util.Buttons;
import org.smartbit4all.ui.vaadin.util.Css;
import org.smartbit4all.ui.vaadin.util.Labels;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tabs;

public class ScreenDetailHeader extends FlexLayout {

  private String CLASS_NAME = "screen-detail-header";

  private Button close;
  private Label title;

  public ScreenDetailHeader(String title) {
    setClassName(CLASS_NAME);

    this.close = Buttons.createTertiaryInlineButton(VaadinIcon.CLOSE);
    Css.setLineHeight("1", this.close);

    this.title = Labels.createH4Label(title);

    FlexLayout wrapper = new FlexLayout(this.close, this.title);
    wrapper.setClassName(CLASS_NAME + "__wrapper");
    add(wrapper);
  }

  public ScreenDetailHeader(String title, Tabs tabs) {
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
