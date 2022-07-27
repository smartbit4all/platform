package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.core.object.ObjectApiImpl;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.application.ApplicationRuntime;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

public class InvocationRegisterApiIml implements InvocationRegisterApi {

  private static final Logger log = LoggerFactory.getLogger(InvocationRegisterApiIml.class);

  /**
   * The URI of the global registry.
   */
  public static final URI REGISTER_URI =
      URI.create(Invocations.APIREGISTRATION_SCHEME + StringConstant.COLON + StringConstant.SLASH
          + ObjectApiImpl.getDefaultAlias(ApiRegistryData.class));

  @Autowired(required = false)
  private StorageApi storageApi;

  @Autowired(required = false)
  private ApplicationRuntimeApi applicationRuntimeApi;

  /**
   * We need all the local {@link PrimaryApi}s to update them in case of {@link ContributionApi}
   * changes.
   */
  @Autowired(required = false)
  private List<PrimaryApi<?>> primaryApis;

  /**
   * We need to know which {@link ContributionApi} belongs to which{@link PrimaryApi}
   */
  private Map<String, List<PrimaryApi<?>>> primaryApisByContributionClass;

  private InvocationApi invocationApi;

  /**
   * The api register is the central repository of all the known apis available all over the tenant.
   * This contains the apis provided by all modules and applications. These are used to fill the
   * call info of the {@link ApiInvocationHandler}s in this module. There can be more than one api
   * for a given interface class. But the most often search pattern is the search with class name.
   * It's important that the {@link ApiDescriptor}s remains the same and updated only. Therefore the
   * {@link ApiInvocationHandler} instances can refer to the {@link ApiDescriptor} objects directly.
   */
  private final Map<String, Map<String, ApiDescriptor>> apiRegister = new HashMap<>();

  /**
   * All api instances that our application provides in case invocation request.
   */
  private final Map<URI, Object> apiInstanceByApiDataUri = new HashMap<>();

  /**
   * The runtime instances known in the cluster by Api uri. This map is always updated by the
   * maintenance.
   */
  private Map<URI, List<UUID>> runtimesByApis = new HashMap<>();

  /**
   * All {@link ApiData} uri that our application provides.
   */
  private Set<URI> apis = new HashSet<>();

  /**
   * All the api providers that are provided by the given application.
   */
  @Autowired(required = false)
  private List<ProviderApiInvocationHandler<?>> providedApis;

  /**
   * 
   */
  private boolean initialized = false;

  /**
   * This is the OrgApi scheme where we save the settings for the notify.
   */
  private Supplier<Storage> storage = new Supplier<Storage>() {

    private Storage storageInstance;

    @Override
    public Storage get() {
      if (storageInstance == null && storageApi != null
          && storageApi.getDefaultObjectStorage() != null) {
        storageInstance = storageApi.get(Invocations.APIREGISTRATION_SCHEME);
      }
      return storageInstance;
    }
  };

  @EventListener(ApplicationStartedEvent.class)
  public void maintainRegistry() throws Exception {
    if (storage.get() == null) {
      return;
    }
    if (!storage.get().exists(REGISTER_URI)) {
      try {
        storage.get().saveAsNew(new ApiRegistryData().uri(REGISTER_URI));
      } catch (Exception e) {
        log.debug("Unable to save the Api registry", e);
      }
    }
    // Update ApiRegistryData apis provided by our runtime
    storage.get().update(REGISTER_URI, ApiRegistryData.class, r -> {
      // We save all the provided apis into the invocation store.
      if (providedApis != null) {
        for (ProviderApiInvocationHandler<?> apiHandler : providedApis) {
          ApiData apiData = apiHandler.getData();
          if (!storage.get().exists(apiData.getUri())) {
            storage.get().saveAsNew(apiData);
            if (!r.getApiList().contains(apiData.getUri())) {
              r.addApiListItem(apiData.getUri());
            }
          }
          apis.add(apiData.getUri());
          addToApiRegister(apiData);
          apiInstanceByApiDataUri.put(apiData.getUri(), apiHandler.getApiInstance());
        }
      }
      return r;
    });

    // refresh primary api map
    fillPrimaryApiMap();
  }

  @EventListener(ApplicationReadyEvent.class)
  private void updateRuntimeApis() {
    // update runtime with our provided apis
    if (applicationRuntimeApi != null) {
      applicationRuntimeApi.setApis(new ArrayList<>(apis));
    }
    initialized = true;
  }

  @Override
  @Scheduled(fixedDelayString = "${invocationregistry.refresh.fixeddelay:5000}")
  public void refreshRegistry() {
    if (storage.get() == null || !storage.get().exists(REGISTER_URI) || !initialized) {
      return;
    }
    if (applicationRuntimeApi == null) {
      // if there is ApplicationRuntimeApi, then we can't refresh the apis
      return;
    }
    // Get apis from all active runtimes
    List<ApplicationRuntime> activeRuntimes = applicationRuntimeApi.getActiveRuntimes();

    Set<URI> activeApis = new HashSet<>();
    Map<URI, List<UUID>> activeRuntimesByApisMap = new HashMap<>();
    for (ApplicationRuntime applicationRuntime : activeRuntimes) {
      List<URI> runtimeApis = applicationRuntimeApi.getApis(applicationRuntime.getUuid());

      if (!CollectionUtils.isEmpty(runtimeApis)) {
        for (URI api : runtimeApis) {
          List<UUID> runtimes =
              activeRuntimesByApisMap.computeIfAbsent(api, r -> new ArrayList<>());
          runtimes.add(applicationRuntime.getUuid());
        }

        activeApis.addAll(runtimeApis);
      }
    }

    runtimesByApis = activeRuntimesByApisMap;

    // apis that becoma active
    Set<URI> apisToAdd = new HashSet<>(activeApis);
    apisToAdd.removeAll(apis);

    // apis that are not active anymore
    Set<URI> apisToRemove = new HashSet<>(apis);
    apisToRemove.removeAll(activeApis);

    apis = activeApis;
    addApis(apisToAdd);
    removeApis(apisToRemove);
  }

  private void fillPrimaryApiMap() {
    primaryApisByContributionClass = new HashMap<>();
    if (primaryApis != null) {
      for (PrimaryApi<?> primaryApi : primaryApis) {
        Class<?> innerApiClass = primaryApi.getContributionApiClass();
        List<PrimaryApi<?>> primaryApisForClass = primaryApisByContributionClass
            .computeIfAbsent(innerApiClass.getName(), i -> new ArrayList<>());
        primaryApisForClass.add(primaryApi);
      }
    }
  }

  private void removeApis(Set<URI> apiUris) {
    List<StorageObject<ApiData>> apiDataSos =
        storage.get().load(new ArrayList<>(apiUris), ApiData.class);

    for (StorageObject<ApiData> apiDataSo : apiDataSos) {
      ApiData apiData = apiDataSo.getObject();
      removeFromApiRegister(apiData);
      unregisterFromPrimaryApi(apiData);
    }
  }

  private void removeFromApiRegister(ApiData apiData) {
    Map<String, ApiDescriptor> apisByName = apiRegister.get(apiData.getInterfaceName());
    apisByName.remove(apiData.getName());

    if (apisByName.isEmpty()) {
      apiRegister.remove(apiData.getInterfaceName());
    }
  }

  private void addApis(Set<URI> apiUris) {
    List<StorageObject<ApiData>> apiDataSos =
        storage.get().load(new ArrayList<>(apiUris), ApiData.class);

    for (StorageObject<ApiData> apiDataSo : apiDataSos) {
      ApiData apiData = apiDataSo.getObject();
      addToApiRegister(apiData);
      registerToPrimaryApi(apiData);
    }
  }

  private ApiDescriptor addToApiRegister(ApiData apiData) {
    Map<String, ApiDescriptor> apisByName =
        apiRegister.computeIfAbsent(apiData.getInterfaceName(), n -> new HashMap<>());
    ApiDescriptor apiDescriptor =
        apisByName.computeIfAbsent(apiData.getName(), n -> new ApiDescriptor(apiData));
    return apiDescriptor;
  }

  /**
   * If the api is a {@link ContributionApi}, then register to his {@link PrimaryApi}
   * 
   * @param apiData
   */
  private void registerToPrimaryApi(ApiData apiData) {

    List<PrimaryApi<?>> primaryApisForClass =
        primaryApisByContributionClass.get(apiData.getInterfaceName());
    if (primaryApisForClass != null) {
      for (PrimaryApi<?> primaryApi : primaryApisForClass) {
        registerAsRemoteApi(apiData, primaryApi);
      }
    }
  }

  private void unregisterFromPrimaryApi(ApiData apiData) {
    List<PrimaryApi<?>> primaryApisForClass =
        primaryApisByContributionClass.get(apiData.getInterfaceName());
    if (primaryApisForClass != null) {
      for (PrimaryApi<?> primaryApi : primaryApisForClass) {
        primaryApi.unregisterApi(apiData.getName());
      }
    }
  }

  protected <T extends ContributionApi> void registerAsRemoteApi(ApiData apiData,
      PrimaryApi<T> primaryApi) {
    T asRemote = ApiInvocationHandler.createProxy(primaryApi.getContributionApiClass(),
        apiData.getName(), invocationApi);
    primaryApi.registerApi(asRemote, apiData.getName());
  }

  @Override
  public void setInvocationApi(InvocationApi invocationApi) {
    this.invocationApi = invocationApi;
  }

  @Override
  public Object getApiInstance(URI apiDataUri) {
    return apiInstanceByApiDataUri.get(apiDataUri);
  }

  @Override
  public ApiDescriptor getApi(String interfaceClass, String name) {
    Map<String, ApiDescriptor> apisByName = apiRegister.get(interfaceClass);

    if (apisByName != null) {
      return apisByName.get(name);
    }

    return null;
  }

  @Override
  public List<UUID> getRuntimesForApi(URI apiDataUri) {
    return runtimesByApis.get(apiDataUri);
  }
}
