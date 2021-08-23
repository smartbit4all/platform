package org.smartbit4all.core.object;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cglib.proxy.InvocationHandler;

public class ApiObjectRefInvocationHandler implements InvocationHandler {

  private final ApiObjectRef ref;

  public ApiObjectRefInvocationHandler(ApiObjectRef ref) {
    this.ref = ref;
  }

  @Override
  public Object invoke(Object object, Method method, Object[] args) throws Throwable {
    PropertyEntry propertyEntry = ref.propertiesByMethod.get(method);
    if (propertyEntry != null) {
      PropertyMeta meta = propertyEntry.getMeta();
      if (method.equals(meta.getGetter())) {
        Object valueInner = ref.getValueInner(propertyEntry);
        if (valueInner instanceof ApiObjectRef) {
          return ((ApiObjectRef) valueInner).getWrapper(meta.getType());
        } else if (valueInner instanceof ApiObjectCollection) {
          return ((ApiObjectCollection) valueInner).getProxy();
        }
        return valueInner;
      } else if (method.equals(meta.getSetter())) {
        ref.setValueInner(args[0], propertyEntry, true);
        return null;
      } else if (method.equals(meta.getFluidSetter())) {
        ref.setValueInner(args[0], propertyEntry, true);
        return object;
      } else if (method.equals(meta.getItemAdder())) {
        Object originalObject = ApiObjectRef.unwrapObject(object);
        List<?> list = (List<?>) meta.getValue(originalObject);
        if (list == null) {
          list = new ArrayList<>();
          ref.setValueInner(list, propertyEntry, true);
        }
        Object apiObjectCollection = ref.getValueInner(propertyEntry);
        if (apiObjectCollection instanceof ApiObjectCollection) {
          List<Object> proxy =
              (List<Object>) ((ApiObjectCollection) apiObjectCollection).getProxy();
          proxy.add(args[0]);
        } else {
          throw new RuntimeException("No collection found at " + meta.getName());
        }
        return object;
      }
    }
    return method.invoke(ref.getObject(), args);
  }

  public ApiObjectRef getRef() {
    return ref;
  }

}
