/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
  protected SB4CompositeFunction<I, O> pre = null;

  /**
   * The post composite function.
   */
  protected SB4CompositeFunction<I, O> post = null;

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
