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
import org.smartbit4all.ui.vaadin.util.FontWeight;
import org.smartbit4all.ui.vaadin.util.LumoStyles;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class Initials extends FlexLayout {

  private String CLASS_NAME = "initials";

  public Initials(String initials) {
    setAlignItems(FlexComponent.Alignment.CENTER);
    getStyle().set("background-color", "var(--lumo-contrast-10pct)");
    getStyle().set("border-radius", "var(--lumo-border-radius-l)");
    setClassName(CLASS_NAME);
    UIUtils.setFontSize(FontSize.S, this);
    UIUtils.setFontWeight(FontWeight._600, this);
    setHeight(LumoStyles.Size.M);
    setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    setWidth(LumoStyles.Size.M);

    add(initials);
  }

}
