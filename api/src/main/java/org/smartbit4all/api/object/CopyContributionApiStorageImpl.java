package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;

public class CopyContributionApiStorageImpl extends ContributionApiImpl
    implements CopyContributionApi {

  public CopyContributionApiStorageImpl() {
    super(CopyContributionApiStorageImpl.class.getName());
  }

  @Autowired
  private StorageApi storageApi;

  @Override
  public URI deepCopy(URI rootObject) {
    // Naive implementation without any care about the multiple occurrences in the directed graph.
    return copyRec(storageApi.load(rootObject)).getUri();
  }

  public CopyApi copyApi() {
    return (CopyApi) primaryApi();
  }

  @SuppressWarnings("unchecked")
  public StorageObject<?> copyRec(StorageObject<?> storageObject) {

    // Construct the new instance
    Storage storage = storageObject.getStorage();
    Class<?> sourceClass = storageObject.definition().getClazz();
    StorageObject<?> newInstance =
        storage.instanceOf(sourceClass);
    newInstance.setObjectObj(storageObject.getObject());

    // Follow the outgoing references and copy them.
    for (ReferenceDefinition ref : storageObject.definition()
        .getOutgoingReferences().values()) {
      if (ref.isContainment()) {
        Object target = ref.getSourceValue(storageObject.getObject());
        switch (ref.getSourceKind()) {
          case VALUE:
            // This property that must contains the uri of the target object.
            URI uri = (URI) target;
            ref.setSourceValue(storageObject.getObject(),
                copyApi().deepCopyByContainment(uri));
            break;
          case COLLECTION:
            // This property that must contains the list of uris point to target objects.
            List<URI> targetUris = (List<URI>) target;
            List<URI> copiedTargetList =
                targetUris.stream()
                    .map(u -> copyApi().deepCopyByContainment(u))
                    .collect(Collectors.toList());
            ref.setSourceValue(storageObject.getObject(), copiedTargetList);
            break;
          case MAP:

            break;

          default:
            break;
        }
      }
    }

    storage.save(newInstance);
    return newInstance;
  }

}
