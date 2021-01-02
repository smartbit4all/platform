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

import org.smartbit4all.ui.vaadin.util.Css.TextColor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;

public class Notifications {

  public static void showNotification(String text) {
    Notification.show(text, 3000, Notification.Position.BOTTOM_CENTER);
  }

  public static void showErrorNotification(String message) {
    Label content = new Label(message);
    Css.setTextColor(TextColor.ERROR, content);
    Notification notification = new Notification(content);
    notification.setDuration(3000);
    notification.setPosition(Position.BOTTOM_CENTER);
    notification.open();
  }

}
