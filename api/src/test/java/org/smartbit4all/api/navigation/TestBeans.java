package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public class TestBeans {

  public static TestBean1 bean1Of(String name, List<String> addresses, List<TestBean2> testBean2s) {
    TestBean1 testBean1 = new TestBean1();
    testBean1.setUri(URI.create("/" + UUID.randomUUID().toString() + ".tb1"));
    testBean1.setName(name);
    testBean1.setAddresses(addresses);
    testBean1.setBean2s(testBean2s);
    return testBean1;
  }

  public static TestBean2 bean2Of(String name, List<String> addresses, List<TestBean3> testBean3s) {
    TestBean2 testBean2 = new TestBean2();
    testBean2.setUri(URI.create("/" + UUID.randomUUID().toString() + ".tb2"));
    testBean2.setName(name);
    testBean2.setAddresses(addresses);
    testBean2.setBean3s(testBean3s);
    return testBean2;
  }

  public static TestBean3 bean3Of(String name, List<String> addresses, TestBean4 bean4) {
    TestBean3 testBean3 = new TestBean3();
    testBean3.setUri(URI.create("/" + UUID.randomUUID().toString() + ".tb3"));
    testBean3.setName(name);
    testBean3.setAddresses(addresses);
    testBean3.setBean4(bean4);
    return testBean3;
  }

  public static TestBean4 bean4Of(String name, List<String> addresses) {
    TestBean4 testBean4 = new TestBean4();
    testBean4.setUri(URI.create("/" + UUID.randomUUID().toString() + ".tb3"));
    testBean4.setName(name);
    testBean4.setAddresses(addresses);
    return testBean4;
  }

}
