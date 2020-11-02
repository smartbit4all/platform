package org.smartbit4all.ui.common.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.smartbit4all.ui.common.controller.UIController;
import org.springframework.scheduling.annotation.Async;

public class UIControllerInvocationHandler implements InvocationHandler {

  UIController uiController;

  public UIControllerInvocationHandler(UIController uiController) {
    super();
    this.uiController = uiController;
  }

  public static final ThreadLocal<Boolean> asyncExecution = new ThreadLocal<Boolean>();

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      if (method.isAnnotationPresent(Async.class)) {
        asyncExecution.set(true);
      }
      return method.invoke(uiController, args);
    } finally {
      asyncExecution.remove();
    }
  }

}
