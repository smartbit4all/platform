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

/**
 * This kind of service is the basement for the execution plans. These are initiated for each and
 * every execution by the logic of the given builder. Therefore the implementations can use field to
 * store information. They can depend on each other and this dependency graph defines if the
 * execution can be performed parallel or must be sequential. The dependencies are type parameters
 * (templates) of the given implementation to avoid invalid dependents. The dependencies are
 * discovered by reflection API (annotations) and the class names are used to identify the dependent
 * instance. If the current execution is not compatible with the new executable service then we get
 * runtime exception.
 * 
 * If we add an executable service to the builder there is an auto-wiring mechanism because it will
 * be plugged to the closest necessary dependent. For example if we add an aggregate function then
 * it will be linked to the closest data table provider executable.
 * 
 * @author Peter Boros
 * @param <I> The input type of the given function. If there is no input then it's practical to set
 *        this {@link Void}.
 * @param <O> The output of the function. Set this to {@link Void} if there is no output.
 */
public interface SB4Function<I, O> {

  /**
   * The execution of the given service.
   */
  void execute() throws Exception;

  /**
   * The input of the function. If null then the input is void.
   * 
   * @return
   */
  I input();

  /**
   * The output of the function. If there is no output then it's null.
   * 
   * @return
   */
  O output();

  /**
   * The predecessor of the given function. Optional. This call will create the pre() section and we
   * can add a new post function with call function.
   * 
   * @return
   */
  SB4CompositeFunction<I, O> pre();

  /**
   * The successor of the function. Optional. This call will create the post() section and we can
   * add a new post function with call function.
   * 
   * @return
   */
  SB4CompositeFunction<I, O> post();

  void setInput(I input);

  void setOutput(O output);

  /**
   * The predecessor of the given function. It's the accessor of the section.
   * 
   * @return Return null if we don't have any.
   */
  SB4CompositeFunction<I, O> getPreSection();

  /**
   * The successor of the function. It's the accessor of the section.
   * 
   * @return Return null if we don't have any.
   */
  SB4CompositeFunction<I, O> getPostSection();

}
