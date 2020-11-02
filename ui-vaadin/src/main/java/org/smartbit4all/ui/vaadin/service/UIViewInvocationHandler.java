package org.smartbit4all.ui.vaadin.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.smartbit4all.ui.common.service.UIControllerInvocationHandler;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.Command;

public class UIViewInvocationHandler implements InvocationHandler {

  UI vaadinUI;

  Object originalUI;

  public UIViewInvocationHandler(Object originalUI, UI vaadinUI) {
    super();
    this.vaadinUI = vaadinUI;
    this.originalUI = originalUI;
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
