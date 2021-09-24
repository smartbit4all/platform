package org.smartbit4all.api.navigation;

import java.util.List;

public class TestBean1 extends TestBean {

  private List<TestBean2> bean2s;

  public List<TestBean2> getBean2s() {
    return bean2s;
  }

  public void setBean2s(List<TestBean2> bean2s) {
    this.bean2s = bean2s;
  }

}
