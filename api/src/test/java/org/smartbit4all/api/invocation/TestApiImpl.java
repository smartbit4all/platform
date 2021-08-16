package org.smartbit4all.api.invocation;

public class TestApiImpl implements TestApi {

  public static String lastDo;

  @Override
  public void doMethod(String p1) {
    System.out.println("do(" + p1 + ")");
    lastDo = p1;
  }

}
