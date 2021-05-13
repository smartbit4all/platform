package org.smartbit4all.core.stateful;

import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.core.object.proxy.ProxyStatefulApiConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class StatefulApiEditingTest {

  protected static AnnotationConfigApplicationContext ctx;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    ctx = new AnnotationConfigApplicationContext();
    ctx.register(ProxyStatefulApiConfiguration.class);
    ctx.register(StatefulEditingTestConfig.class);
    ctx.refresh();

  }

  @AfterAll
  static void tearDown() {
    ctx.close();
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void testSetValue() throws ExecutionException {
    StatefulBean bean = new StatefulBean();
    StatefulBeanEditing beanEditing = initBeanEditing(bean, "result");
    beanEditing.setValue("value");

    Assertions.assertNull(bean.getResult());
  }

  @Test
  void testSetValueAndNotifyAll() throws ExecutionException {
    StatefulBean bean = new StatefulBean();
    String expectedResult = "result";
    StatefulBeanEditing beanEditing = initBeanEditing(bean, expectedResult);
    beanEditing.setValueAndNotifyAll("value");

    Assertions.assertEquals(expectedResult, bean.getResult());
  }

  @Test
  void testSetValueAndNotify() throws ExecutionException {
    StatefulBean bean = new StatefulBean();
    String expectedResult = "result";
    StatefulBeanEditing beanEditing = initBeanEditing(bean, expectedResult);
    beanEditing.setValueAndNotify("value");

    Assertions.assertEquals(expectedResult, bean.getResult());
  }

  private StatefulBeanEditing initBeanEditing(StatefulBean bean, String expectedResult) {
    StatefulBeanEditing beanEditing = ctx.getBean(StatefulBeanEditing.class);
    beanEditing.setBean(bean);
    beanEditing.bean().onPropertyChange(null, "Value", event -> bean.setResult(expectedResult));
    return beanEditing;
  }

}
