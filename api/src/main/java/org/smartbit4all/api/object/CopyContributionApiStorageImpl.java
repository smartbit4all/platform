package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.core.utility.UriUtils;
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
    return copyRec(storageApi.load(rootObject).asMap()).getUri();
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
    newInstance.asMap().setObjectAsMap(storageObject.getObjectAsMap());

    // Follow the outgoing references and copy them.
    for (ReferenceDefinition ref : storageObject.definition()
        .getOutgoingReferences().values()) {
      if (ref.getAggregation() == AggregationKind.COMPOSITE) {
        Object target = ref.getSourceValue(storageObject.getObjectAsMap());
        switch (ref.getSourceKind()) {
          case REFERENCE:
            // This property that must contains the uri of the target object.
            if (target != null) {
              ref.setSourceValue(storageObject.getObjectAsMap(),
                  copyApi().deepCopyByContainment(UriUtils.asUri(target)));
            }
            break;
          case LIST:
            // This property that must contains the list of uris point to target objects.
            List<Object> targetUris = (List<Object>) target;
            List<URI> copiedTargetList =
                targetUris.stream()
                    .map(o -> copyApi().deepCopyByContainment(UriUtils.asUri(o)))
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
