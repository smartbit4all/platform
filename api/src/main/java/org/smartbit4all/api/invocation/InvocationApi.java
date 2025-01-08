package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import javax.script.ScriptException;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.FutureAwait;
import org.smartbit4all.api.invocation.bean.InvocationBatchRequest;
import org.smartbit4all.api.invocation.bean.InvocationBatchResult;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContext;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectPropertyResolver;

/**
 * The {@link InvocationApi} is a generic api to call other api function.
 *
 * @author Peter Boros
 */
public interface InvocationApi {

  /**
   * This is the constant object to use when we don't want to override the parameter of an
   * INvocationRequest.
   */
  Object LEAVE = new Object();

  /**
   * The builder can be used to produce an {@link InvocationRequest} by calling the method in the
   * {@link InvocationBuilder#build(java.util.function.Consumer)} function.
   * <p>
   * <code>
   *  InvocationRequest request = invocationApi.builder(testApi).build(a -> a.echoMethod(value));<br>
   *  InvocationParameter result = invocationApi.invoke(request);
   * </code>
   * </p>
   *
   * @param <T>
   * @param apiInterface
   * @return
   */
  <T> InvocationBuilder<T> builder(Class<T> apiInterface);

  /**
   * The builder can be used to produce an {@link AsyncInvocationRequest} by creating multiple
   * invocation chain.
   * <p>
   * <code>
   *  InvocationRequest request = invocationApi.builder(testApi).build(a -> a.echoMethod(value));<br>
   *  InvocationParameter result = invocationApi.invoke(request);
   * </code>
   * </p>
   *
   * @return
   */
  AsyncInvocationBuilder asyncBuilder();

  /**
   * The generic api call executed synchronously. BE CAREFUL! If it is a script then it must be
   * prepared previously in this case the args won't be applied.
   *
   * @param request The invocation request ready to call.
   * @param args The arguments of the invocation in order. If we pass nothing then the originaly set
   *        parameters will be used from the request!
   * @return The {@link InvocationParameter} as the result of the call.
   * @throws ApiNotFoundException
   */
  InvocationParameter invoke(InvocationRequest request, Object... args) throws ApiNotFoundException;

  /**
   * The generic api call executed synchronously. BE CAREFUL! If it is a script then it must be
   * prepared previously in this case the args won't be applied.
   *
   * @param request The invocation request ready to call.
   * @param args The arguments of the invocation in order. If we pass nothing then the originaly set
   *        parameters will be used from the request!
   * @return The optional {@link InvocationParameter} result of the call. If it is missing then the
   *         invocation has been failed, there is no api found.
   */
  Optional<InvocationParameter> tryInvoke(InvocationRequest request, Object... args);

  /**
   * The generic api call executed synchronously.
   *
   * @param batch The invocation batch with a list of request to invoke.
   * @throws ApiNotFoundException
   */
  InvocationBatchResult invokeBatch(InvocationBatchRequest batch) throws ApiNotFoundException;

  void invokeAsyncRequest(ObjectNode asyncInvocationNode);

  /**
   * This call register the invocation for the for execute after the successful commit of the
   * current transaction. But on the other hand it will save the given {@link InvocationRequest}
   * into the channel. If the transaction succeeded but the execution is not finished then the
   * invocation api will try to execute it later on.
   *
   * In this case the execution will inherit the session of the current user. If the session is
   * expired before the invocation is started then the invocation will fail.
   *
   * To ensure that the invocation is going to be executed then we can assign a technical user must
   * be set in the request to this call. The invocation will create a session and login with the
   * given user.
   *
   * @param request The invocation request.
   * @param channel The channel that is configured for the execution. If we don't give any parameter
   *        then it will be created with default parameters.
   */
  void invokeAsync(InvocationRequest request, String channel);

  /**
   * This call register the invocation for the for execute after the successful commit of the
   * current transaction. But on the other hand it will save the given {@link InvocationRequest}
   * into the channel. If the transaction succeeded but the execution is not finished then the
   * invocation api will try to execute it later on.
   *
   * In this case the execution will inherit the session of the current user. If the session is
   * expired before the invocation is started then the invocation will fail.
   *
   * To ensure that the invocation is going to be executed then we can assign a technical user must
   * be set in the request to this call. The invocation will create a session and login with the
   * given user.
   *
   * @param batch The invocation request batch with a list of invocation call.
   * @param channel The channel that is configured for the execution. If we don't give any parameter
   *        then it will be created with default parameters.
   */
  void invokeAsyncBatch(InvocationBatchRequest batch, String channel);

  /**
   * This call register the invocation for the for execute after the successful commit of the
   * current transaction. But on the other hand it will save the given {@link InvocationRequest}
   * into the channel. If the transaction succeeded but the execution is not finished then the
   * invocation api will try to execute it later on.
   *
   * In this case the execution will inherit the session of the current user. If the session is
   * expired before the invocation is started then the invocation will fail.
   *
   * To ensure that the invocation is going to be executed then we can assign a technical user must
   * be set in the request to this call. The invocation will create a session and login with the
   * given user.
   *
   * @param request The invocation request.
   * @param channel The channel that is configured for the execution. If we don't give any parameter
   *        then it will be created with default parameters.
   * @param executeAt The exact time when the invocation should be executed at.
   */
  URI invokeAt(InvocationRequest request, String channel, OffsetDateTime executeAt);

  /**
   * Constructs an event publisher that is responsible for recording an {@link InvocationRequest} by
   * calling a function on the interface. Later on this {@link InvocationRequest} is going to be
   * used as an invocation toward the subscribed api calls.
   *
   * @param <P>
   * @param <S>
   * @param publisherApiInterface The interface that defines the fire operations typically.
   * @param subscriberApiInterface The interface of the subscriber api.
   * @param event The name of the event.
   * @return The event publisher that can make a new published event as an {@link InvocationRequest}
   *         via the {@link EventPublisher#publish(java.util.function.Consumer)} function. It
   *         provides an instance of the publisher api interface and we can call the function in
   *         right syntax.
   */
  <P, S> EventPublisher<P, S> publisher(Class<P> publisherApiInterface,
      Class<S> subscriberApiInterface, String event);

  /**
   * The definition contains an prepared instance from the {@link InvocationRequest} and some
   * mapping between the context object properties and the parameters. With this call we can
   * initiate a new {@link InvocationRequest} ready to call by resolving the referred parameters
   * from the objects provided in the context with the {@link ObjectPropertyResolver}.
   *
   * @param definition The invocation definition.
   * @param context The object context for the resolution.
   * @return
   */
  InvocationRequest resolve(InvocationRequestDefinition definition,
      ObjectPropertyResolverContext context);

  /**
   * Constructs a new invocation request copy and set the parameters by position.
   *
   * @param request The original request
   * @param parameters At a given position we can use the {@link #LEAVE} constant. This is the
   *        constant object to use when we don't want to override the parameter of an
   *        INvocationRequest.
   * @return The result request that can be used for the
   *         {@link #invoke(InvocationRequest, Object...)} for example.
   */
  InvocationRequest prepareByPosition(InvocationRequest request, Object... parameters);

  /**
   * Constructs a new invocation request copy and set the parameters by position.
   *
   * @param request The original request
   * @param parameters The parameters in a map. The key is the name of the parameter.
   * @return The result request that can be used for the
   *         {@link #invoke(InvocationRequest, Object...)} for example.
   */
  InvocationRequest prepareByName(InvocationRequest request, Map<String, Object> parameters);

  /**
   * Executes a script via an available script engine from the current JVM.
   *
   * @param scriptEngine The name of the script engine.
   * @param script The text of the script itself.
   * @param contextObjects The context objects to use to set the input parameters of the script. The
   *        input variable identified by the name of the context objects. Their names are starting
   *        with the names of the context objects.
   * @param inputParams The input
   * @return The value of the evaluation.
   */
  Object executeScript(String scriptEngine, String script, Map<String, ObjectNode> contextObjects,
      Map<String, Object> inputParams) throws ScriptException;

  /**
   * Checks whether or not the given api is a callable.
   * 
   * @param api The api object.
   * @return If it is not a proxy then the result is true. If the api is null then the result is
   *         false. If the api is remote (it is a {@link Proxy} object with the
   *         {@link ApiInvocationHandler} as {@link InvocationHandler}) then we check if the given
   *         application runtime exists and up.
   */
  boolean checkCallable(Object api);

  /**
   * The await is persisting a {@link FutureAwait} object for tha later signal event expressed by
   * the {@link #signalFuture(String, String, Object...)}. This future is nothing else but a saved
   * {@link InvocationRequest} that will be invoked when the signal is coming. The parameters must
   * be started with a reserved parameter, the identifier object itself that is not necessarily the
   * id value. The additional parameters are filled with the parameters passed to the
   * {@link #signalFuture(String, String, Object...)}.
   * 
   * @param scheme The scheme of the future object.
   * @param id The unique identifier that must be uuid, or a numeric id in stringified form.
   * @param request The request to be called. Be careful to fill the first parameter with the
   *        necessary parameter to find the context for the incoming signal.
   * @return The URI of the saved {@link FutureAwait}
   */
  URI awaitFor(String scheme, String id, InvocationRequest request);

  /**
   * The await is persisting a {@link FutureAwait} object for tha later signal event expressed by
   * the {@link #signalFuture(String, String, Object...)}. This future is nothing else but a saved
   * {@link InvocationRequest} that will be invoked when the signal is coming. The parameters must
   * be started with a reserved parameter, the identifier object itself that is not necessarily the
   * id value. The additional parameters are filled with the parameters passed to the
   * {@link #signalFuture(String, String, Object...)}.
   * 
   * @param scheme The scheme of the future object.
   * @param id The unique identifier that must be uuid, or a numeric id in stringified form.
   * @param request The request to be called. Be careful to fill the first parameter with the
   *        necessary parameter to find the context for the incoming signal.
   * @param firstParamIndex The first parameter index that is awaiting as parameter of the signal
   *        call. By default it is 1 which meand that the first (0.) parameter is reserved as
   *        context parameter.
   * @return The URI of the saved {@link FutureAwait}
   */
  URI awaitFor(String scheme, String id, InvocationRequest request, int firstParamIndex);

  /**
   * If an incoming event is coming and we have to give a signal for a {@link FutureAwait} to call
   * the proper request to manage the result of the event.
   * 
   * @param scheme The scheme of the future object.
   * @param id The unique identifier that must be uuid, or a numeric id in stringified form.
   * @param parameters The parameters of the future that will be additional parameters of the
   *        {@link InvocationRequest} saved in the future.
   */
  void signalFuture(String scheme, String id, Object... parameters);

}
