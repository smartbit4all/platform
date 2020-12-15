/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.smartbit4all.ui.common.service.UIControllerInvocationHandler;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.communication.PushMode;

public class UIViewInvocationHandler implements InvocationHandler {

  UI vaadinUI;

  Object originalUI;

  public UIViewInvocationHandler(Object originalUI, UI vaadinUI) {
    super();
    this.vaadinUI = vaadinUI;
    this.originalUI = originalUI;
    vaadinUI.getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Boolean async = UIControllerInvocationHandler.asyncExecution.get();
    if (async != null && async) {
      vaadinUI.access(new Command() {

        @Override
        public void execute() {
          try {
            method.invoke(originalUI, args);
          } catch (IllegalAccessException | IllegalArgumentException
              | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }

      });
    }
    return null;
  }

}
