package org.smartbit4all.api.invocation;

import java.util.concurrent.CountDownLatch;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.TestDataBean;
import org.smartbit4all.core.utility.concurrent.FutureValue;

public interface TestApi {

  public static FutureValue<String> futureValue = new FutureValue<>();

  public static int scheduleCounter = 10;

  public static CountDownLatch scheduledLatch = new CountDownLatch(scheduleCounter);

  void doMethod(String p1);

  String echoMethod(String p1);

  void setFutureValue(String p1);

  void setFutureValueAt(String p1);

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
