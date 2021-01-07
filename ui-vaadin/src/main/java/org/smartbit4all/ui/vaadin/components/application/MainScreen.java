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

import java.util.stream.Stream;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

public interface MainScreen {

  public static MainScreen get() {
    Stream<Component> children = UI.getCurrent().getChildren();
    children.forEach(c -> {
      boolean assignableFrom = MainScreen.class.isAssignableFrom(c.getClass());
      System.out.println(assignableFrom);
    });

    return (MainScreen) UI.getCurrent().getChildren()
        .filter(
            component -> MainScreen.class.isAssignableFrom(component.getClass()))
        .findFirst()
        .orElse(null);
  }

  public MainToolbar getToolbar();

  public MenuHolder getMenuHolder();

  public <T extends Component> Class<T> getHomeView();

}
