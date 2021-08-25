package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.InvocationParameterTemplate;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;

public interface TestApi {

  void doMethod(String p1);

  String echoMethod(String p1);

  static InvocationRequestTemplate echoMethodTemplate =
      new InvocationRequestTemplate().apiClass(TestApi.class.getName()).methodName("echoMethod")
          .addParametersItem(
              new InvocationParameterTemplate().name("p1").typeClass(String.class.getName()));

}
