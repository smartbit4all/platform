package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.Comparator;

public class StorageUriComparator implements Comparator<URI> {

  @Override
  public int compare(URI o1, URI o2) {
    return compareTo(o1, o2);
  }

  public static int compareTo(URI o1, URI o2) {
    Long version1 = ObjectStorageImpl.getUriVersion(o1);
    Long version2 = ObjectStorageImpl.getUriVersion(o2);
    if (version1 == null && version2 == null) {
      return 0;
    }
    if (version1 != null && version2 == null) {
      // The second is infinite because it is a latest uri.
      return -1;
    }
    if (version1 == null && version2 != null) {
      // The second is infinite because it is a latest uri.
      return 1;
    }
    return version1.compareTo(version2);
  }

}
