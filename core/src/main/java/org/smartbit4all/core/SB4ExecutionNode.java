package org.smartbit4all.core;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the abstract super node for the service expression tree. This forms an expression tree
 * that consists of service instances to execute. That can refer to each other by reference because
 * all of them are private instances for the given execution.
 * 
 * This node can be constructed directly by calling the builder methods. But we also can use as an
 * on demand builder of the SB4Function call graph. In this case we can add the {@link SB4Function}s
 * and before the execution they will be analyzed and ordered by the dependencies.
 * 
 * @author Peter Boros
 */
public final class SB4ExecutionNode implements SB4Execution {

  /**
   * The execution mode.
   * 
   * @author Peter Boros
   */
  public static enum Mode {
    SEQUENTIAL, PARELLEl;
  }

  /**
   * The mode of the execution like sequential or parallel.
   */
  public Mode mode;

  /**
   * The items of the given node.
   */
  final List<SB4Execution> items = new ArrayList<>();

  /**
   * If this list is not empty then we must analyze it before the execution to setup the node and
   * its sub nodes if necessary.
   */
  List<SB4Function<?, ?>> functionsToAnalyze = null;

  /**
   * Constructs an execution node with a given mode for the {@link #items}.
   * 
   * @param mode
   */
  public SB4ExecutionNode(Mode mode) {
    super();
    this.mode = mode;
  }

  /**
   * Constructs an execution node with sequential content. It will be the next node in this
   * execution node.
   * 
   * @return The newly created node for fluid API.
   */
  public SB4ExecutionNode sequenceOf() {
    SB4ExecutionNode newNode = new SB4ExecutionNode(Mode.SEQUENTIAL);
    items.add(newNode);
    return newNode;
  }

  /**
   * Constructs a parallel execution context. All the
   * 
   * @return The newly created node for fluid API.
   */
  public SB4ExecutionNode parallelOf() {
    SB4ExecutionNode newNode = new SB4ExecutionNode(Mode.PARELLEl);
    items.add(newNode);
    return newNode;
  }

  /**
   * Add a function to the given node for execution.
   * 
   * @param function The new service to call by the {@link #mode} defined.
   * @return For the fluid API we return the actual node itself.
   */
  public SB4ExecutionNode call(SB4Function<?, ?> function) {
    items.add(new SB4ExecutionFunction(function));
    return this;
  }

  /**
   * Add a function to the given node for further analyze. Before the execution the analyze is
   * called by the {@link SB4CompositeFunction}. In this phase this node will setup an execution
   * graph by the dependencies among the SB4Function.
   * 
   * @param function
   * @return
   */
  public SB4ExecutionNode addToAnalyze(SB4Function<?, ?> function) {
    if (functionsToAnalyze == null) {
      functionsToAnalyze = new ArrayList<SB4Function<?, ?>>();
    }
    functionsToAnalyze.add(function);
    return this;
  }

  /**
   * This function must be called before executing the {@link SB4CompositeFunction} to setup the
   * {@link #items} and add sub nodes by the dependencies. Caution all these analyzed functions will
   * be added to the {@link #items} with the following logic:
   * <ul>
   * <li>If the items is empty then the mode will be set by the analyze and the functions will be
   * added by this logic.</li>
   * <li>If the items is not empty then a new node will be added with the necessary mode. All the
   * analyzed functions will be added to this node.</li>
   * </ul>
   * The function is recursive to analyze the whole execution tree.
   */
  void analyze() {
    if (functionsToAnalyze != null) {
      // Perform the last analysis to produce the execution graph. For this we must create a global
      // producing and consuming registry.

      // for (SB4Function<?, ?> logic : functionsToAnalyze) {
      // // Add the logics by the entity.
      // for (OutputValue<?> output : logic.parameter().outputs) {
      // // Check if we are the only one that produce the given property.
      // EventHandler existingLogic = producedBy.get(output.property());
      // if (existingLogic != null) {
      // log.error(
      // "The same {} property is produced be more than one logic. {} and {}. The framework skip the
      // second as a dependency!",
      // output.property().getName(), existingLogic.getClass().getName(),
      // logic.getClass().getName());
      // } else {
      // producedBy.put(output.property(), logic);
      // }
      // }
      // for (InputValue<?> input : logic.parameter().inputs) {
      // List<EventHandler> list = consumedBy.get(input.property());
      // if (list == null) {
      // list = new ArrayList<>(5);
      // consumedBy.put(input.property(), list);
      // }
      // list.add(logic);
      // }
      // }
      // // And then we can call the analyze.
      // for (EventHandler logic : logics) {
      // logic.parameter().analyzeDependencies(producedBy, consumedBy);
      // }
      // // Now we can draw the dependency graph. There is no trick because we already have the
      // graph
      // // in
      // // the dependencies.
      // // Just add all the nodes and edges from the node list.
      // for (EventHandler logic : logics) {
      // addNode(logic);
      // }
    }
    for (SB4Execution execution : items) {
      if (execution instanceof SB4ExecutionNode) {
        ((SB4ExecutionNode) execution).analyze();
      }
    }
  }

}
