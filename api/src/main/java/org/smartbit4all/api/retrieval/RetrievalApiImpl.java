package org.smartbit4all.api.retrieval;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The abstract implementation of the retrieval. It will use contribution apis to access objects.
 * 
 * @author Peter Boros
 */
public class RetrievalApiImpl implements RetrievalApi {

  @Autowired
  private StorageApi storageApi;

  @Override
  public ObjectModel load(RetrievalRequest request) {
    ObjectModel result = new ObjectModel();

    result.objects
        .putAll(
            request.startWithObjects.parallelStream().map(o -> readAll(o, o.getUriList(), result))
                .flatMap(List::stream)
                .collect(Collectors.toMap(ObjectNode::getUri, n -> n)));

    return result;
  }

  private final List<ObjectNode> readAll(ObjectRetrievalRequest objRequest, List<URI> uris,
      ObjectModel model) {
    List<ObjectNode> result = null;
    for (URI uri : uris) {
      // TODO Load latest version from the object.
      // if(objRequest.isLoadLatestVersion()) {
      // }
      StorageObject<?> storageObject = storageApi.load(uri);
      ObjectNode objectNode =
          new ObjectNode(model, objRequest.getDefinition(), storageObject.getStorage().getScheme());
      objectNode.setObjectAsMap(storageObject.getObjectAsMap());
      if (result == null) {
        result = new ArrayList<>();
      }
      result.add(objectNode);

      // Recursive read of all referred objects.
      for (Entry<ReferenceDefinition, ObjectRetrievalRequest> entry : objRequest.getReferences()
          .entrySet()) {
        Object sourceValue = entry.getKey().getSourceValue(objectNode.getObjectAsMap());
        if (sourceValue != null) {
          if (sourceValue instanceof URI) {
            ListIterator<ObjectNode> valueObjectIterator =
                readAll(objRequest, Collections.singletonList((URI) sourceValue), model)
                    .listIterator();
            if (valueObjectIterator.hasNext()) {
              objectNode.getReferenceValues().put(entry.getKey(), valueObjectIterator.next());
            }
          } else if (sourceValue instanceof List) {
            @SuppressWarnings("unchecked")
            List<URI> refUriList = ((List<Object>) sourceValue).stream()
                .map(o -> (URI) (o instanceof URI ? o : URI.create((String) o)))
                .collect(Collectors.toList());
            List<ObjectNode> readAllRef = readAll(objRequest, refUriList, model);
            objectNode.getReferenceListValues().put(entry.getKey(), readAllRef);
          } else if (sourceValue instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, URI> refUriMap = ((Map<String, Object>) sourceValue).entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, e -> {
                  if (e.getValue() instanceof URI) {
                    return (URI) e.getValue();
                  } else {
                    return URI.create((String) e.getValue());
                  }
                }));
            List<String> keys = new ArrayList<>();
            List<URI> uriList = new ArrayList<>();
            for (Entry<String, URI> refEntry : refUriMap.entrySet()) {
              keys.add(refEntry.getKey());
              uriList.add(refEntry.getValue());
            }
            List<ObjectNode> readAllRef =
                readAll(objRequest, uriList, model);
            Map<String, ObjectNode> refObjectMap = new HashMap<>();
            ListIterator<String> iterKeys = keys.listIterator();
            for (ObjectNode refObjectNode : readAllRef) {
              refObjectMap.put(iterKeys.next(), refObjectNode);
            }
            objectNode.getReferenceMapValues().put(entry.getKey(), refObjectMap);
          }
        }
      }
    }

    return result == null ? Collections.emptyList() : result;
  }

  @Override
  public RetrievalRequest request() {
    return new RetrievalRequest(this);
  }

}
