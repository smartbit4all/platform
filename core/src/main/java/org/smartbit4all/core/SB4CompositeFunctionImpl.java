package org.smartbit4all.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.SB4ExecutionNode.Mode;

/**
 * The default implementation of the composite service. By default it executes all the services in
 * the proper order and mode.
 * 
 * @author Peter Boros
 */
public class SB4CompositeFunctionImpl<I, O> extends SB4FunctionImpl<I, O>
    implements SB4CompositeFunction<I, O> {

  private static final Logger log = LoggerFactory.getLogger(SB4CompositeFunctionImpl.class);

  /**
   * By default we have a rootNode with {@link Mode#SEQUENTIAL} execution mode.
   */
  SB4ExecutionNode rootNode = new SB4ExecutionNode(Mode.SEQUENTIAL);

  /**
   * The function that are predecessor of the root.
   */
  SB4ExecutionNode preNode;

  /**
   * The function that are successor of the root.
   */
  SB4ExecutionNode postNode;

  @Override
  public SB4ExecutionNode sequenceOf() {
    // For the sake of changing mode we set this mode.
    rootNode.mode = Mode.SEQUENTIAL;
    return rootNode;
  }

  @Override
  public SB4ExecutionNode parallelOf() {
    // For the sake of changing mode we set this mode.
    rootNode.mode = Mode.PARELLEl;
    return rootNode;
  }

  @Override
  public SB4ExecutionNode call(SB4Function<?, ?> function) {
    return rootNode.call(function);
  }

  @Override
  public boolean isEmpty() {
    return rootNode.items.isEmpty();
  }

  @Override
  public void execute() throws Exception {
    executeNode(preNode);
    executeNode(rootNode);
    executeNode(postNode);
  }

  private void executeNode(SB4ExecutionNode node) {
    if (node == null) {
      return;
    }
    Iterator<SB4Function<?, ?>> it = functionsIter(node);
    while (it.hasNext()) {
      SB4Function<?, ?> exec = it.next();
      try {
        exec.execute();
      } catch (Exception e) {
        log.error("Error while executing " + exec + " function.", e);
      }
    }
  }

  private static Iterator<SB4Function<?, ?>> functionsIter(SB4ExecutionNode node) {
    return new Iterator<SB4Function<?, ?>>() {

      /**
       * The stack of the traverse.
       */
      Deque<Iterator<SB4Execution>> iterStack = new ArrayDeque<>();

      /**
       * The iterator of the actual node.
       */
      Iterator<SB4Execution> actualIter = node.items.iterator();

      @Override
      public boolean hasNext() {
        if (!actualIter.hasNext() && !iterStack.isEmpty()) {
          actualIter = iterStack.pop();
          return hasNext();
        }
        return actualIter.hasNext();
      }

      @Override
      public SB4Function<?, ?> next() {
        if (!actualIter.hasNext() && !iterStack.isEmpty()) {
          actualIter = iterStack.pop();
          return next();
        }
        SB4Execution item = actualIter.next();
        if (item instanceof SB4ExecutionNode) {
          // In this case we step into.
          iterStack.push(actualIter);
          actualIter = ((SB4ExecutionNode) item).items.iterator();
          return next();
        }
        return ((SB4ExecutionFunction) item).service;
      }

    };
  }


  @Override
  public Iterator<SB4Function<?, ?>> functionsIter() {
    return functionsIter(rootNode);
  }

  @Override
  public Iterable<SB4Function<?, ?>> functions() {
    return new Iterable<SB4Function<?, ?>>() {

      @Override
      public Iterator<SB4Function<?, ?>> iterator() {
        return functionsIter();
      }
    };
  }

  @Override
  public I input() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public O output() {
    // TODO Auto-generated method stub
    return null;
  }

}
