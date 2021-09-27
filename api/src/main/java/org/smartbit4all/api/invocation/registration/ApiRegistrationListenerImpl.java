package org.smartbit4all.api.invocation.registration;

import java.util.Objects;
import java.util.function.BiConsumer;
import org.smartbit4all.api.apiregister.bean.ApiInfo;

public class ApiRegistrationListenerImpl<T> implements ApiRegistrationListener {
  
  private Class<T> interfaceType;
  private String interfaceQName;
  private BiConsumer<T, ApiInfo> onRegistration;
  
  public ApiRegistrationListenerImpl(Class<T> interfaceType, BiConsumer<T, ApiInfo> onRegistration) {
    Objects.requireNonNull(interfaceType, "interfaceType can not be null!");
    Objects.requireNonNull(onRegistration, "onRegistration can not be null!");
    this.interfaceType = interfaceType;
    this.interfaceQName = interfaceType.getName();
    this.onRegistration = onRegistration;
  }
  
  @Override
  public String getInterfaceQName() {
   return interfaceQName; 
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onRegistration(Object apiInstance, ApiInfo apiInfo) throws Exception {
    if(interfaceType.isAssignableFrom(apiInstance.getClass())) {
      onRegistration.accept((T) apiInstance, apiInfo);
    } else {
      throw new Exception("The given local api instance's type is"
          + "not compatible with this listener! Given interface type: " + apiInstance.getClass() 
          + "; listener interface type: " + interfaceQName);
    }
  }
}
