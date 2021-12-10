package org.smartbit4all.domain.data.storage;

import java.util.Objects;
import org.smartbit4all.api.storage.bean.ObjectVersion;

public class StorageUtil {

  public static final boolean equalsVersion(ObjectVersion v1, ObjectVersion v2) {
    if (v1 == null || v2 == null) {
      return false;
    }
    return Objects.equals(v1.getSerialNoData(), v2.getSerialNoData());
  }

}
