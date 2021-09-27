package org.smartbit4all.api.invocation.restclient.apiregister;

import java.util.List;
import java.util.function.Function;
import org.smartbit4all.api.apiregister.bean.ApiInfo;

public interface RestGeneratedApiRegistry {

  <I, R extends I> void addRestStubFactory(Class<I> interfaceType,
      Function<ApiInfo, R> stubFactory);

  List<Class<?>> getRestGenImplementedInterfaces();

  boolean hasInterfaceGeneratedRestStub(Class<?> interfaceType);

  Function<ApiInfo, ?> getRestStubFactory(Class<?> interfaceType);

  Object getRestStubInstance(Class<?> interfaceType);
}
