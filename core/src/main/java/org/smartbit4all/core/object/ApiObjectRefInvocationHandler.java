package org.smartbit4all.core.object;

import java.lang.reflect.Method;
import org.springframework.cglib.proxy.InvocationHandler;

public class ApiObjectRefInvocationHandler implements InvocationHandler {

  private final ApiObjectRef ref;

  public ApiObjectRefInvocationHandler(ApiObjectRef ref) {
    this.ref = ref;
  }

  @Override
  public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
    PropertyEntry propertyEntry = ref.propertiesByMethod.get(arg1);
    if (propertyEntry != null) {
      if (arg1.equals(propertyEntry.getMeta().getGetter())) {
        Object valueInner = ref.getValueInner(propertyEntry);
        if (valueInner instanceof ApiObjectRef) {
          return ((ApiObjectRef) valueInner).getWrapper(propertyEntry.getMeta().getType());
        } else if (valueInner instanceof ApiObjectCollection) {
          return ((ApiObjectCollection) valueInner).getProxy();
        }
        return valueInner;
      } else if (arg1.equals(propertyEntry.getMeta().getSetter())) {
        ref.setValueInner(arg2[0], propertyEntry);
        return null;
      }
    }
    return arg1.invoke(ref.getObject(), arg2);
  }

  public ApiObjectRef getRef() {
    return ref;
  }

}
