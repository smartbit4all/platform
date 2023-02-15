package org.smartbit4all.api.invocation;

import java.util.Objects;
import java.util.function.Consumer;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;

/**
 * The invocation api can initiate this builder. This can create a chain of call to be invoked.
 * 
 * @author Peter Boros
 */
public class AsyncInvocationBuilder {

  private static final String FIRST_WE_HAVE_TO_ADD_A_CALL_TO_CONTINUE =
      "First we have to add a call to continue.";

  private ObjectApi objectApi;

  private InvocationApi invocationApi;

  private ObjectNode nodeRoot;

  private ObjectNode currentNode;

  AsyncInvocationBuilder(ObjectApi objectApi, InvocationApi invocationApi) {
    super();
    this.objectApi = objectApi;
    this.invocationApi = invocationApi;
  }

  /**
   * Initiate the whole asynchronous invocation chain by calling the first API.
   * 
   * @param apiIf
   * @return
   */
  public <T> AsyncInvocationBuilder call(Class<T> apiIf, Consumer<T> apiCall, String channel) {
    nodeRoot = objectApi.create(Invocations.ASYNCINVOCATION_SCHEME,
        new AsyncInvocationRequest().request(invocationApi.builder(apiIf).build(apiCall))
            .channel(channel));
    currentNode = nodeRoot;
    return this;
  }

  public <T> AsyncInvocationBuilder evaluate(Class<T> apiIf, Consumer<T> apiCall) {
    Objects.requireNonNull(currentNode, FIRST_WE_HAVE_TO_ADD_A_CALL_TO_CONTINUE);
    currentNode.modify(AsyncInvocationRequest.class, air -> {
      return air.evaluate(invocationApi.builder(apiIf).build(apiCall));
    });
    return this;
  }

  public <T> AsyncInvocationBuilder andThen(Class<T> apiIf, Consumer<T> apiCall, String channel) {
    Objects.requireNonNull(currentNode, FIRST_WE_HAVE_TO_ADD_A_CALL_TO_CONTINUE);
    ObjectNode newNode = objectApi.create(Invocations.ASYNCINVOCATION_SCHEME,
        new AsyncInvocationRequest().request(invocationApi.builder(apiIf).build(apiCall))
            .channel(channel));
    currentNode.list(AsyncInvocationRequest.AND_THEN).add(newNode);
    return this;
  }

  public <T> AsyncInvocationBuilder andThenContinue(Class<T> apiIf, Consumer<T> apiCall,
      String channel) {
    Objects.requireNonNull(currentNode, FIRST_WE_HAVE_TO_ADD_A_CALL_TO_CONTINUE);
    ObjectNode newNode = objectApi.create(Invocations.ASYNCINVOCATION_SCHEME,
        new AsyncInvocationRequest().request(invocationApi.builder(apiIf).build(apiCall))
            .channel(channel));
    currentNode.list(AsyncInvocationRequest.AND_THEN).add(newNode);
    currentNode = newNode;
    return this;
  }

  public ObjectNode get() {
    return nodeRoot;
  }

}
