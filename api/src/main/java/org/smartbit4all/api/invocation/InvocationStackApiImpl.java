package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.invocation.bean.InvocationStackEntry;
import org.smartbit4all.api.invocation.bean.InvocationStackItem;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class InvocationStackApiImpl implements InvocationStackApi {

  private static final Logger log = LoggerFactory.getLogger(InvocationStackApiImpl.class);

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private CollectionApi collectionApi;

  private ThreadLocal<Deque<InvocationStack>> stack = new ThreadLocal<>();

  @Override
  public InvocationStack get() {
    Deque<InvocationStack> stackList = stack.get();
    return stackList != null ? stackList.getLast() : null;
  }

  private void set(InvocationStack processStack) {
    Deque<InvocationStack> stackList = stack.get();
    if (stackList == null) {
      stackList = new ArrayDeque<>();
      stack.set(stackList);
    }
    stackList.addLast(processStack);
  }

  public InvocationStack remove() {
    Deque<InvocationStack> stackList = stack.get();
    if (stackList != null) {
      InvocationStack result = null;
      if (!stackList.isEmpty()) {
        result = stackList.removeLast();
      }
      if (stackList.isEmpty()) {
        stack.remove();
      }
      return result;
    }
    return null;
  }

  @Override
  public URI initiateStack(String code, String name, Map<String, Object> inputParameters,
      Map<String, Object> defaultVariables) {
    return objectApi.saveAsNew(Invocations.INVOCATION_SCHEME,
        new InvocationStackEntry().root(new InvocationStackItem().code(code).name(name)
            .inputParameters(inputParameters != null ? inputParameters : new HashMap<>())
            .variables(defaultVariables != null ? defaultVariables : new HashMap<>())));
  }

  @Override
  public URI runOnNamedStack(String schema, String stackName, Consumer<InvocationStack> func) {
    // Lock the export stack.
    StoredReference<InvocationStackItem> stackRef = collectionApi.reference(
        schema, stackName,
        InvocationStackItem.class);
    Lock lock = objectApi.getLock(stackRef.getUri());
    try {
      lock.lock();
      if (!stackRef.exists()) {
        stackRef.update(s -> new InvocationStackItem());
      }
      try {
        InvocationStack invocationStack = new InvocationStack(objectApi, stackRef, stackRef.get());
        set(invocationStack);
        func.accept(invocationStack);
        commit();
      } catch (Exception e) {
        // Rollback the stack, do not save it.
        log.error("Failed to run function on stack. " + get().getRootItem(), e);
        rollback();
      }
    } finally {
      lock.unlock();
    }
    return stackRef.getUri();
  }

  @Override
  public InvocationStack loadStack(URI uri, List<String> path) {
    if (uri == null) {
      return null;
    }
    InvocationStack result = get();
    // Avoid loading the same process several times.
    if (result == null || (result != null && !Objects.equals(result.getUri(), uri))) {
      InvocationStackEntry stackEntry =
          objectApi.loadLatest(uri).getObject(InvocationStackEntry.class);
      result = new InvocationStack(objectApi, uri,
          stackEntry.getRoot());
      set(result);
    }
    return result;
  }

  @Override
  public void commit() {
    InvocationStack currentStack = remove();
    if (currentStack != null) {
      // Save it.
      Lock stackLock = objectApi.getLock(currentStack.getUri());
      stackLock.lock();
      try {
        if (currentStack.getStackRef() != null) {
          // Save into the stored reference.
          currentStack.getStackRef().update(s -> currentStack.getRootItem());
        } else {
          ObjectNode stackNode = objectApi.loadLatest(currentStack.getUri());
          stackNode.modify(InvocationStackEntry.class, s -> {
            s.setRoot(currentStack.getRootItem());
            return s;
          });
          objectApi.save(stackNode);
        }
      } finally {
        stackLock.unlock();
      }
    }
  }

  @Override
  public void rollback() {
    remove();
  }

}
