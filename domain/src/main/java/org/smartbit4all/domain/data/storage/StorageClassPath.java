package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.storage.bean.ObjectReferenceList;

/**
 * Specific storage for accessing beans stored in the class path. These can be the settings bound
 * with deployment. This storage is not able to save anything because we cann't modify the
 * deployment itself.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public class StorageClassPath<T> extends ObjectStorageImpl<T> {

  public StorageClassPath(Function<T, URI> uriAccessor) {
    super(uriAccessor);
  }

  private static final Logger log = LoggerFactory.getLogger(StorageClassPath.class);

  @Override
  public URI save(T object, URI uri) throws Exception {
    throw new UnsupportedOperationException(
        "Unable to modify the URI = " + uri + " bean (" + object + ") on the class path.");
  }

  @Override
  public URI save(T object) throws Exception {
    throw new UnsupportedOperationException(
        "Unable to modify the bean (" + object + ") on the class path.");
  }

  @Override
  public Optional<T> load(URI uri) throws Exception {
    return null;
  }


  @Override
  public List<T> loadAll() throws Exception {
    throw new UnsupportedOperationException(
        "Unable to load all objects from the class path.");
  }

  @Override
  public List<T> load(List<URI> uris) throws Exception {
    if (uris == null || uris.isEmpty()) {
      return Collections.emptyList();
    }
    return uris.stream().map(u -> {
      try {
        return load(u);
      } catch (Exception e) {
        log.error("Unable to load the " + u + " from the class path.", e);
        return Optional.of((T) null);
      }
    }).filter(o -> o.isPresent()).map(o -> o.get())
        .collect(Collectors.toList());
  }

  @Override
  public boolean delete(URI uri) throws Exception {
    throw new UnsupportedOperationException(
        "Unable to delete the URI = " + uri + " bean from the class path.");
  }

  @Override
  public void saveReferences(ObjectReferenceRequest referenceRequest) {
    throw new UnsupportedOperationException(
        "Unable to save references the URI = " + referenceRequest.getObjectUri()
            + " bean from the class path.");
  }

  @Override
  public Optional<ObjectReferenceList> loadReferences(URI uri, String typeClassName) {
    throw new UnsupportedOperationException(
        "Unable to load references the URI = " + uri
            + " bean from the class path.");
  }

}
