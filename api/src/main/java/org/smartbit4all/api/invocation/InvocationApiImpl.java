package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.registration.ApiRegistrationListenerImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The implementation of the {@link InvocationApi}. It collects all the
 * {@link InvocationExecutionApi} we have. If we call the {@link #invoke(InvocationRequest)} then
 * there is routing to the appropriate execution api. We always have a local execution api
 * 
 * @author Peter Boros
 */
public final class InvocationApiImpl implements InvocationApi, InitializingBean {

  @Autowired(required = false)
  StorageApi storageApi;

  /**
   * The list of {@link InvocationExecutionApi} from the Spring context.
   */
  @Autowired(required = false)
  List<InvocationExecutionApi> apis;

  @Autowired(required = false)
  ApiRegister apiRegister;

  Map<String, InvocationExecutionApi> apiByName = new HashMap<>();

  /**
   * The {@link #register(Object)} function puts here the instances by a generated UUID.
   */
  // TODO WeakReference!
  Map<UUID, Object> instancesByUUID = new ConcurrentHashMap<>();

  @Override
  public InvocationParameter invoke(InvocationRequest request) throws Exception {
    InvocationExecutionApi api = getApi(request.getExecutionApi());
    if (api != null) {
      return api.invoke(request);
    } else {
      throw new IllegalArgumentException(
          request.getExecutionApi() + " execution api is not available for the " + request);
    }
  }

  public InvocationExecutionApi getApi(String name) {
    return apiByName.get(name);
  }

  private void addApi(String name, InvocationExecutionApi api) {
    apiByName.put(name, api);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // The local implementation is always set plus the apis from the SpringContext.
    if (apis != null) {
      for (InvocationExecutionApi executionApi : apis) {
        apiByName.put(executionApi.getName(), executionApi);
        if (executionApi instanceof InvocationExecutionApiImpl) {
          ((InvocationExecutionApiImpl) executionApi).setInvocationApi(this);
        }
      }
    }
    if (apiRegister != null) {
      apiRegister.addRegistrationListener(
          new ApiRegistrationListenerImpl<InvocationExecutionApi>(InvocationExecutionApi.class,
              (executionApi, apiInfo) -> {
                addApi(executionApi.getName(), executionApi);
              }));
    }
  }

  @Override
  public URI save(InvocationRequestTemplate requestTemplate) {
    Storage<InvocationRequestTemplate> storage =
        storageApi == null ? null : storageApi.get(InvocationRequestTemplate.class);
    if (storage == null) {
      throw new UnsupportedOperationException(
          "Unable to save the invocation request templet without Storage<InvocationRequestTemple> setup.");
    }
    URI result = null;
    try {
      result = storage.save(requestTemplate);
    } catch (Exception e) {
      new RuntimeException("Unable to save " + requestTemplate, e);
    }
    return result;
  }

  @Override
  public InvocationRequestTemplate load(URI templateUri) {
    Storage<InvocationRequestTemplate> storage =
        storageApi == null ? null : storageApi.get(InvocationRequestTemplate.class);
    if (storage == null) {
      throw new UnsupportedOperationException(
          "Unable to load the invocation request templet without Storage<InvocationRequestTemple> setup.");
    }
    Optional<InvocationRequestTemplate> result = null;
    try {
      result = storage.load(templateUri);
    } catch (Exception e) {
      new RuntimeException("Unable to load " + templateUri + " request template.", e);
    }
    return result.get();
  }

  private void purge() {
    List<UUID> toRemove = instancesByUUID.entrySet().stream().filter(e -> {
      return e.getValue() == null;
    }).map(e -> e.getKey()).collect(Collectors.toList());
    if (toRemove != null) {
      toRemove.forEach(uuid -> instancesByUUID.remove(uuid));
    }
  }

  @Override
  public UUID register(Object apiInstance) {
    if (apiInstance == null) {
      return null;
    }
    UUID result = UUID.randomUUID();
    instancesByUUID.put(result, apiInstance);
    purge();
    return result;
  }

  @Override
  public Object find(UUID instanceId) {
    purge();
    Object ref = instancesByUUID.get(instanceId);
    return ref;
  }

}
