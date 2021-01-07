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

import java.util.List;
import java.util.stream.Collectors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;

@CssImport("./styles/components/application/main-menu.css")
public class MainMenu extends Div {

  private String CLASS_NAME = "main-menu";

  public MainMenu() {
    setClassName(CLASS_NAME);
  }

  protected void addMenuItem(MenuItem item) {
    add(item);
  }

  protected void addMenuItem(MenuItem parent, MenuItem item) {
    parent.addSubMenuItem(item);
    addMenuItem(item);
  }

  public MenuItem addMenuItem(String text,
      Class<? extends Component> screen) {
    MenuItem item = new MenuItem(text, screen);
    addMenuItem(item);
    return item;
  }

  public MenuItem addMenuItem(VaadinIcon icon, String text,
      Class<? extends Component> screen) {
    MenuItem item = new MenuItem(icon, text, screen);
    addMenuItem(item);
    return item;
  }

  public MenuItem addMenuItem(Image image, String text,
      Class<? extends Component> screen) {
    MenuItem item = new MenuItem(image, text, screen);
    addMenuItem(item);
    return item;
  }

  public MenuItem addMenuItem(MenuItem parent, String text,
      Class<? extends Component> screen) {
    MenuItem item = new MenuItem(text, screen);
    addMenuItem(parent, item);
    return item;
  }

  public List<MenuItem> getMenuItems() {
    List<MenuItem> items = (List) getChildren()
        .collect(Collectors.toList());
    return items;
  }

}
