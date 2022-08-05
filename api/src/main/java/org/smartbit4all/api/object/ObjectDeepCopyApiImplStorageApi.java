package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;

public class ObjectDeepCopyApiImplStorageApi implements ObjectDeepCopyApi {

  @Autowired
  StorageApi storageApi;

  @Override
  public URI copy(URI rootObject, List<ReferenceDefinition> includedReferences) {
    // Naive implementation without any care about the multiple occurrences in the directed graph.
    return copyRec(storageApi.load(rootObject), includedReferences).getUri();
  }

  public StorageObject<?> copyRec(StorageObject<?> storageObject,
      List<ReferenceDefinition> includedReferences) {
    Storage storage = storageObject.getStorage();
    Class<?> sourceClass = storageObject.getObject().getClass();
    StorageObject<?> newInstance =
        storage.instanceOf(sourceClass);
    newInstance.setObjectObj(storageObject.getObject());

    // Find the references to follow
    if (includedReferences != null) {
      List<ReferenceDefinition> references = includedReferences.stream()
          .filter(r -> r.getSource().getQualifiedName().equals(sourceClass.getName()))
          .collect(Collectors.toList());
      references.parallelStream().forEach(null);
    }


    storage.save(newInstance);
    return newInstance;
  }

}
