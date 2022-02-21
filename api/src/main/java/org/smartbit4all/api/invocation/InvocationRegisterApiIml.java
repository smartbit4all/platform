package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class InvocationRegisterApiIml implements InvocationRegisterApi, InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(InvocationRegisterApiIml.class);

  @Autowired(required = false)
  private StorageApi storageApi;

  /**
   * We need all the local {@link PrimaryApi}s to update them in case of {@link ContributionApi}
   * changes.
   */
  @Autowired(required = false)
  private List<PrimaryApi<?>> primaryApis;

  /**
   * All the apis that are provided by the given application.
   */
  @Autowired(required = false)
  private List<ProviderApiInvocationHandler<?>> providedApis;

  /**
   * The api register is the central repository of all the known apis available all over the tenant.
   * This contains the apis provided by all modules and applications. These are used to fill the
   * call info of the {@link RemoteApiInvocationHandler}s in this module. There can be more than one
   * api for a given interface class. But the most often search pattern is the search with class
   * name. It's important that the {@link ApiDescriptor}s remains the same and updated only.
   * Therefore the {@link RemoteApiInvocationHandler} instances can refer to the
   * {@link ApiDescriptor} objects directly.
   */
  private final Map<String, Map<String, ApiDescriptor>> apiRegister = new HashMap<>();

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

  /**
   * The URI of the global registry.
   */
  private URI registryUri =
      URI.create(Invocations.APIREGISTRATION_SCHEME + StringConstant.COLON + StringConstant.SLASH
          + ApiRegistryData.class.getName().replace(StringConstant.DOT, StringConstant.SLASH));

  @Override
  public void afterPropertiesSet() throws Exception {
    if (storage.get() == null) {
      return;
    }
    if (!storage.get().exists(registryUri)) {
      try {
        storage.get().saveAsNew(new ApiRegistryData().uri(registryUri));
      } catch (Exception e) {
        log.debug("Unable to save the Api registry", e);
      }
    }
    storage.get().update(registryUri, ApiRegistryData.class, r -> {
      // We save all the provided apis into the invocation store.
      if (providedApis != null) {
        for (ProviderApiInvocationHandler<?> apiHandler : providedApis) {
          ApiData apiData = apiHandler.getData();
          if (!storage.get().exists(apiData.getUri())) {
            storage.get().saveAsNew(apiData);
            r.putApisItem(apiData.getUri().toString(), apiData.getUri());
          }
        }
      }
      return r;
    });
  }

  @Override
  @Scheduled(fixedDelayString = "${invocationregistry.refresh.fixeddelay:5000}")
  public void refreshRegistry() {
    if (storage.get() == null || !storage.get().exists(registryUri)) {
      return;
    }
    ApiRegistryData registryData = storage.get().read(registryUri, ApiRegistryData.class);
    List<ApiData> apis =
        storage.get().read(new ArrayList<>(registryData.getApis().values()), ApiData.class);
    for (ApiData apiData : apis) {
      Map<String, ApiDescriptor> apisByName =
          apiRegister.computeIfAbsent(apiData.getInterfaceName(), i -> new HashMap<>());
      ApiDescriptor apiDescriptor =
          apisByName.computeIfAbsent(apiData.getName(), n -> new ApiDescriptor(apiData));
      // TODO actualize the api descriptors with the providing nodes.
    }
  }

}
