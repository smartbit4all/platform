package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.TestDataBean;

public class TestApiImpl implements TestApi {

  public static final String NAME = TestApiImpl.class.getName();

  public static String lastDo;

  @Override
  public void doMethod(String p1) {
    System.out.println("do(" + p1 + ")");
    lastDo = p1;
  }

  @Override
  public String echoMethod(String p1) {
    return p1;
  }

  @Override
  public TestDataBean modifyData(TestDataBean databean) {
    return databean;
  }

}
