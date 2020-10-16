package org.smartbit4all.core;

public class SB4FunctionVoid extends SB4FunctionImpl<Void, Void>
    implements SB4Function<Void, Void> {

  @Override
  public void execute() throws Exception {}

  @Override
  public Void input() {
    return null;
  }

  @Override
  public Void output() {
    return null;
  }

}
