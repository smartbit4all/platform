package org.smartbit4all.domain.application;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.smartbit4all.api.invocation.bean.ApplicationRuntimeData;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;

public class TestApplicationRuntime {

  /**
   * The cluster management {@link Storage} instance.
   */
  private Storage storageCluster;

  private ApplicationRuntime myRuntime;

  private boolean maintaining = false;

  private URI runtimeUri;

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private Long maintainDelay = 5000l;

  private ScheduledFuture<?> scheduleAtFixedRate;

  public TestApplicationRuntime(StorageApi storageApi) {
    storageCluster = storageApi.get(ApplicationRuntimeApiStorageImpl.CLUSTER);
    storageCluster.setVersionPolicy(VersionPolicy.SINGLEVERSION);
  }

  public TestApplicationRuntime withMaintainDelay(Long maintainDelay) {
    this.maintainDelay = maintainDelay;
    return this;
  }

  public static TestApplicationRuntime create(StorageApi storageApi) {
    return new TestApplicationRuntime(storageApi);
  }

  public TestApplicationRuntime runtimeOf(ApplicationRuntimeData runtimeData) {
    myRuntime = new ApplicationRuntime(runtimeData);

    runtimeData.lastTouchTime(System.currentTimeMillis());
    runtimeUri = storageCluster.saveAsNew(runtimeData, "active");
    myRuntime.getData().setUri(runtimeUri);
    return this;

  }

  private void maintain() {
    if (maintaining) {
      storageCluster.update(runtimeUri, ApplicationRuntimeData.class, r -> {
        return r.lastTouchTime(System.currentTimeMillis());
      });
    }
  }

  public void start() {
    if (myRuntime == null) {
      throw new IllegalStateException("No ApplicationRuntimeData was provided!");
    }

    if (!maintaining && scheduleAtFixedRate == null || scheduleAtFixedRate.isCancelled()) {
      scheduleAtFixedRate =
          scheduler.scheduleAtFixedRate(this::maintain, maintainDelay, maintainDelay,
              TimeUnit.MILLISECONDS);
      maintaining = true;
    }
  }

  public void stop() {
    scheduleAtFixedRate.cancel(false);
    maintaining = false;
  }

}
