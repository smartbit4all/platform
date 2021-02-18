package org.smartbit4all.api.object;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

public class ObjectEditingInvocationHandler implements InvocationHandler {

  protected ObjectEditingImpl editing;
  
  public ObjectEditingInvocationHandler(ObjectEditingImpl editing) {
    super();
    this.editing = editing;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object result = method.invoke(editing, args);
    if (method.isAnnotationPresent(NotifiyListeners.class)) {
      Optional<ObjectChange> renderAndCleanChanges = editing.ref.renderAndCleanChanges();
      if(renderAndCleanChanges.isPresent()) {
        editing.publisher.notify(renderAndCleanChanges.get());
      }
    }
    return result;
  }
}
