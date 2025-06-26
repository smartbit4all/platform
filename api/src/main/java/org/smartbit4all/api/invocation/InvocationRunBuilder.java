package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.InvocationRun;

public class InvocationRunBuilder {
  private final InvocationRun invocationRun = new InvocationRun();

  public InvocationRunBuilder addItem(InvocationRunItemBuilder itemBuilder) {
    invocationRun.addItemsItem(itemBuilder.build());
    return this;
  }

  public InvocationRun build() {
    return invocationRun;
  }
}
