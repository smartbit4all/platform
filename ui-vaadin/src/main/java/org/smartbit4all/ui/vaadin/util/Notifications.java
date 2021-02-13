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
import org.springframework.stereotype.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;

@Component
public class Notifications {
  
  private static Notifications INSTANCE;
  
  private NotificationHandler notificationHandler;
  
  /**
   * <b>CAUTION!</b><br>
   * <b>Notifications can be initialized only by the Spring framework!</b><br>
   * Initializing in code causes {@link IllegalStateException}!
   */
  public Notifications(NotificationHandler notificationHandler) {
    if (INSTANCE != null) {
      throw new IllegalStateException(this.getClass()
          + " has been already initialized by the Spring framework! Use the static methods instead!");
    }
    this.notificationHandler = notificationHandler;
    INSTANCE = this;
  }

  public static void showNotification(String message) {
    if(INSTANCE != null) {
      INSTANCE.notificationHandler.showNotification(message);
    } else {
      ShowDefaultNotification(message);
    }
  }

  public static void showErrorNotification(String message) {
    if(INSTANCE != null) {
      INSTANCE.notificationHandler.showErrorNotification(message);
    } else {
      showDefaultErrorNotification(message);
    }
  }

  private static void ShowDefaultNotification(String message) {
    Notification.show(message, 3000, Notification.Position.BOTTOM_CENTER);
  }

  private static void showDefaultErrorNotification(String message) {
    Label content = new Label(message);
    Css.setTextColor(TextColor.ERROR, content);
    Notification notification = new Notification(content);
    notification.setDuration(3000);
    notification.setPosition(Position.BOTTOM_CENTER);
    notification.open();
  }

  public static interface NotificationHandler {
    
    void showNotification(String message);

    void showErrorNotification(String message);
  }
  
}
