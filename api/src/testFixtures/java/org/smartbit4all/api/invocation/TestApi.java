package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.TestDataBean;

public interface TestApi {

  void doMethod(String p1);

  String echoMethod(String p1);

  TestDataBean modifyData(TestDataBean databean);

  static InvocationRequest echoMethodTemplate(String value) {
    return Invocations.invoke(TestApi.class)
        .methodName("echoMethod")
        .interfaceClass(TestApi.class.getName())
        .name(TestApiImpl.NAME).addParametersItem(
            new InvocationParameter()
                .name("p1")
                .typeClass(String.class.getName())
                .value(value));
  }
}
