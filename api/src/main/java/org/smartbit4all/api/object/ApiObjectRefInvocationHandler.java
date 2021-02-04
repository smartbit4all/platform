package org.smartbit4all.api.object;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import org.springframework.cglib.proxy.Callback;

final class ApiObjectRefInvocationHandler implements Callback {

  private WeakReference<ApiObjectRef> objectRefRef;

  public ApiObjectRefInvocationHandler(ApiObjectRef objectRef) {
    super();
    this.objectRefRef = new WeakReference<>(objectRef);
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    ApiObjectRef objectRef = objectRefRef.get();
    if (objectRef == null) {
      throw new IllegalStateException(
          "The api object reference is missing, already garbage collected.");
    }
    PropertyEntry propertyEntry = objectRef.propertiesByMethod.get(method);
    if (propertyEntry != null) {
      if (method.equals(propertyEntry.getMeta().getGetter())) {
        // TODO Return Enhancer in case of reference or collection.
        return method.invoke(objectRef.getObject(), args);
      } else if (method.equals(propertyEntry.getMeta().getSetter())) {
        objectRef.setValueInner(args[0], propertyEntry);
        return null;
      }
    }
    return method.invoke(objectRef.getObject(), args);
  }

}
