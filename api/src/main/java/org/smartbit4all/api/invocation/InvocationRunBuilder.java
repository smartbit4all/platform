package org.smartbit4all.api.invocation;

import java.util.function.Consumer;
import org.smartbit4all.api.invocation.bean.InvocationRun;

public class InvocationRunBuilder {
  private final InvocationRun invocationRun = new InvocationRun();

  public InvocationRunBuilder addItem(InvocationRunItemBuilder itemBuilder) {
    invocationRun.addItemsItem(itemBuilder.build());
    return this;
  }

  public InvocationRunBuilder addItem(Consumer<InvocationRunItemBuilder> itemBuilder) {
    InvocationRunItemBuilder builder = InvocationRunItemBuilder.builder();
    itemBuilder.accept(builder);
    invocationRun.addItemsItem(builder.build());
    return this;
  }

  public InvocationRun build() {
    return invocationRun;
  }
}
