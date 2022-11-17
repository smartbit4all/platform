package org.smartbit4all.api.object;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;
import java.util.ListIterator;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Objects;

public class CompareContributionApiStorageImpl extends ContributionApiImpl
    implements CompareContributionApi {

  public CompareContributionApiStorageImpl() {
    super(CompareContributionApiStorageImpl.class.getName());
  }

  @Autowired
  private StorageApi storageApi;

  @Override
  public boolean deepEquals(URI uri, URI prevUri) {
    return deepEqualsRec(storageApi.load(uri), storageApi.load(prevUri));
  }

  CompareApi compareApi() {
    return (CompareApi) primaryApi();
  }

  @SuppressWarnings("unchecked")
  public boolean deepEqualsRec(StorageObject<?> storageObject,
      StorageObject<?> prevStorageObject) {

    // Compare fields except references and the uri itself.
    boolean result = equalsObjects(storageObject, prevStorageObject);

    if (result) {
      return false;
    }

    // Follow the outgoing references and copy them.
    for (ReferenceDefinition ref : storageObject.definition()
        .getOutgoingReferences().values()) {
      if (ref.getAggregation() == AggregationKind.COMPOSITE) {
        Object target = ref.getSourceValue(storageObject.getObject());
        Object targetPrev = ref.getSourceValue(prevStorageObject.getObject());
        switch (ref.getSourceKind()) {
          case VALUE:
            // This property that must contains the uri of the target object.
            if (!compareApi()
                .deepEquals((URI) target, (URI) targetPrev)) {
              return false;
            }
            break;
          case COLLECTION:
            // This property that must contains the list of uris point to target objects.
            List<URI> targetUris = (List<URI>) target;
            List<URI> targetPrevUris = (List<URI>) targetPrev;

            if ((targetUris == null && targetPrevUris != null)
                || (targetPrevUris == null && targetUris != null)
                || ((targetUris != null) && targetUris.size() != targetPrevUris.size())) {
              return false;
            }

            if (targetUris != null) {
              ListIterator<URI> iterTargetPrevUris = targetPrevUris.listIterator();

              for (URI targetUri : targetUris) {
                URI targetPrevUri = iterTargetPrevUris.next();
                if (!compareApi()
                    .deepEquals(targetUri, targetPrevUri)) {
                  return false;
                }
              }
            }
            break;
          case MAP:
            break;

          default:
            break;
        }
      }
    }

    return true;
  }

  private boolean equalsObjects(StorageObject<?> storageObject,
      StorageObject<?> prevStorageObject) {
    return storageObject.definition().meta().getProperties().values().stream()
        .filter(p -> !storageObject.definition()
            .getOutgoingReferences().containsKey(p.getName()) && !p.getName().equals("uri"))
        .anyMatch(p -> {
          Object value = null;
          Object prevValue = null;
          try {
            value = p.getGetter().invoke(storageObject.getObject());
            prevValue = p.getGetter().invoke(prevStorageObject.getObject());
          } catch (IllegalAccessException | IllegalArgumentException
              | InvocationTargetException e) {
            return false;
          }
          return !Objects.equal(value, prevValue);
        });
  }

}
