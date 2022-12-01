package org.smartbit4all.api.object;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.storage.bean.BranchData;
import org.smartbit4all.api.storage.bean.BranchOperation;
import org.smartbit4all.api.storage.bean.BranchOperation.OperationTypeEnum;
import org.smartbit4all.api.storage.bean.ObjectBranchData;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.utility.concurrent.FutureValue;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The {@link StorageApi} based implementation of the {@link BranchApi} that stores the branch info
 * in {@link BranchData}.
 * 
 * @author Peter Boros
 */
public class BranchApiImpl extends PrimaryApiImpl<BranchContributionApi>
    implements BranchApi {

  protected static final String SCHEME = "branch";

  private static final Logger log = LoggerFactory.getLogger(BranchApiImpl.class);

  public BranchApiImpl() {
    super(BranchContributionApi.class);
  }

  @Autowired
  private BranchContributionApiStorageImpl contributionApiImplStorage;

  @Autowired
  ObjectApi objectApi;

  @Autowired
  StorageApi storageApi;

  /**
   * This is storage to use for the api.
   */
  protected Supplier<Storage> storage = new Supplier<Storage>() {

    private Storage storageInstance;

    @Override
    public Storage get() {
      if (storageInstance == null) {
        storageInstance = storageApi.get(SCHEME);
      }
      return storageInstance;
    }
  };


  private BranchContributionApi getContributionApi(URI uri) {
    ObjectDefinition<?> definition = objectApi.definition(uri);
    if (definition == null) {
      return contributionApiImplStorage;
    }
    BranchContributionApi api = getContributionApi(definition.getQualifiedName());
    return api == null ? contributionApiImplStorage : api;
  }

  @Override
  public URI makeBranch(String caption) {
    // TODO Use session api.
    OffsetDateTime now = OffsetDateTime.now();
    return storage.get().saveAsNew(new BranchData().caption(caption).createdAt(now));
  }

  @Override
  public URI branchObject(URI versionUri, URI branchUri) {
    OffsetDateTime now = OffsetDateTime.now();
    if (versionUri == null) {
      return null;
    }
    URI uri = objectApi.getLatestUri(versionUri);
    // TODO We might manage if we don't have the version uri.
    FutureValue<URI> newVersionUri = new FutureValue<>();
    storage.get().update(branchUri, BranchData.class, b -> {
      ObjectBranchData branchData = b.getObjects().get(uri.toString());
      // if we already have the branch for the object then we return the uri from this.
      if (branchData != null) {
        newVersionUri.setValue(branchData.getInit().getTargetUri());
        return null;
      }
      StorageObject<?> soVersion = storageApi.load(versionUri);
      if (soVersion == null || !soVersion.isPresent()) {
        return null;
      }
      // Create a copy from the given object.
      URI targetUri = getContributionApi(uri).saveAsNew(versionUri);
      newVersionUri.setValue(targetUri);
      BranchOperation initOperation = new BranchOperation().createdAt(now)
          .operationType(OperationTypeEnum.INIT).sourceUri(versionUri).targetUri(targetUri);
      b.getObjects().put(uri.toString(), new ObjectBranchData().objectUri(uri).init(initOperation));
      return b;
    });
    try {
      return newVersionUri.isDone() ? newVersionUri.get() : null;
    } catch (InterruptedException | ExecutionException e) {
      log.error("Unable to initiate the {} branch for the {} object", branchUri, versionUri);
      return null;
    }
  }

}
