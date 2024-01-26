package org.smartbit4all.core.object;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;

public class ObjectHistoryIterator implements Iterator<ObjectNode> {

  private long i = -1;

  private long latestVersionNr;

  private long lastVersion;

  private final URI uriWithoutVersion;

  private URI branchUri;

  private final WeakReference<ObjectApi> objectApiRef;

  private boolean useCache = false;

  public ObjectHistoryIterator(ObjectApi objectApi, long latestVersionNr,
      URI uriWithoutVersion) {
    super();
    this.lastVersion = latestVersionNr;
    this.latestVersionNr = latestVersionNr;
    this.uriWithoutVersion = uriWithoutVersion;
    this.objectApiRef = new WeakReference<>(objectApi);
  }

  private final void checkRange(long versionNr) {
    if (versionNr < 0 || versionNr >= latestVersionNr) {
      throw new ArrayIndexOutOfBoundsException(
          "The " + versionNr + " is not a valid version number for the " + uriWithoutVersion
              + " it must be greater or equal than 0 and less then " + lastVersion);
    }
  }

  public ObjectHistoryIterator lastVersion(long lastVersionNr) {
    checkRange(lastVersionNr);
    lastVersion = lastVersionNr;
    return this;
  }

  public ObjectHistoryIterator index(long index) {
    checkRange(index);
    i = index;
    return this;
  }

  public ObjectHistoryIterator useCache(boolean useCache) {
    this.useCache = useCache;
    return this;
  }

  public ObjectHistoryIterator branch(URI branchUri) {
    this.branchUri = branchUri;
    return this;
  }

  @Override
  public ObjectNode next() {
    i++;
    if (i > lastVersion) {
      throw new NoSuchElementException(
          "There is no object with version greater than " + lastVersion);
    }
    URI currentObjectUri = ObjectStorageImpl.getUriWithVersion(uriWithoutVersion, i);
    return objectApi().load(currentObjectUri, branchUri);
  }

  @Override
  public boolean hasNext() {
    return i < lastVersion;
  }

  private final ObjectApi objectApi() {
    return objectApiRef.get();
  }

}
