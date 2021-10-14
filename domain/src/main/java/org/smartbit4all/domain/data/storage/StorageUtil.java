package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.domain.data.storage.index.StorageIndex;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;

public class StorageUtil {

  public static <T> List<URI> getUris(EntityDefinition entityDef, Expression expression,
      List<StorageIndex<T>> indexes) throws Exception {
    List<URI> uris = new ArrayList<>();

    for (StorageIndex<T> index : indexes) {
      if (index.getEntityDef() == entityDef) {
        uris.addAll(index.listUris(expression));
      }
    }

    return uris;
  }

  public static final boolean equalsVersion(ObjectVersion v1, ObjectVersion v2) {
    if (v1 == null || v2 == null) {
      return false;
    }
    return Objects.equals(v1.getSerialNo(), v2.getSerialNo());
  }

}
