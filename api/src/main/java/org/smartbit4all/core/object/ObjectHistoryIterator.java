package org.smartbit4all.core.object;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;

public class ObjectHistoryIterator implements Iterator<ObjectNode> {

  private long i = -1;

  private boolean reverese = false;

  private long latestVersionNr;

  private long firstVersion;

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
    if (versionNr < -1 || versionNr > latestVersionNr + 1) {
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

  public ObjectHistoryIterator firstIndex(long index) {
    checkRange(index);
    firstVersion = index;
    i = index;
    return this;
  }

  /**
   * Set the reverse value and initiate the index to the proper end of the stream.
   * 
   * @param rev
   * @return
   */
  public ObjectHistoryIterator reverse(boolean rev) {
    this.reverese = rev;
    if (reverese) {
      index(lastVersion + 1);
    } else {
      index(firstVersion - 1);
    }
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
    i = reverese ? i - 1 : i + 1;
    if (reverese ? i < firstVersion : i > lastVersion) {
      throw new NoSuchElementException(
          "There is no object with version greater than " + lastVersion);
    }
    URI currentObjectUri = ObjectStorageImpl.getUriWithVersion(uriWithoutVersion, i);
    return objectApi().load(currentObjectUri, branchUri);
  }

  @Override
  public boolean hasNext() {
    return reverese ? i > firstVersion : i < lastVersion;
  }

  private final ObjectApi objectApi() {
    return objectApiRef.get();
  }

  public final long getIndex() {
    return i;
  }

  public final long getLatestVersionNr() {
    return latestVersionNr;
  }

  public final long getLastVersion() {
    return lastVersion;
  }

  public final URI getUriWithoutVersion() {
    return uriWithoutVersion;
  }

  public final URI getBranchUri() {
    return branchUri;
  }

  public final boolean isUseCache() {
    return useCache;
  }



}
