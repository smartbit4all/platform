package org.smartbit4all.core;

public abstract class SB4FunctionImpl<I, O> implements SB4Function<I, O> {

  /**
   * The input variable of the function.
   */
  protected I input;

  /**
   * The output variable of the function.
   */
  protected O output;

  /**
   * The pre composite function.
   */
  SB4CompositeFunction<I, O> pre = null;

  /**
   * The post composite function.
   */
  SB4CompositeFunction<I, O> post = null;

  @Override
  public SB4CompositeFunction<I, O> pre() {
    if (pre == null) {
      pre = new SB4CompositeFunctionImpl<I, O>();
    }
    return pre;
  }

  @Override
  public SB4CompositeFunction<I, O> post() {
    if (post == null) {
      post = new SB4CompositeFunctionImpl<I, O>();
    }
    return post;
  }

  @Override
  public I input() {
    return input;
  }

  @Override
  public O output() {
    return output;
  }

  @Override
  public void setInput(I input) {
    this.input = input;
  }

  @Override
  public void setOutput(O output) {
    this.output = output;
  }

  @Override
  public SB4CompositeFunction<I, O> getPreSection() {
    return pre;
  }

  @Override
  public SB4CompositeFunction<I, O> getPostSection() {
    return post;
  }

}
