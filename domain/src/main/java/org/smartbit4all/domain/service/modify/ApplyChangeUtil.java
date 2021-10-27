package org.smartbit4all.domain.service.modify;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.utility.crud.Crud;

public class ApplyChangeUtil {

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static Map<Property<?>, Object> createPrimarayKeyIdProvider(String uid,
      Property<Long> idProp, Property<String> uidProp, Supplier<Long> idProvider) {
    Long id = null;
    if (idProp instanceof PropertyRef) {
      idProp = ((PropertyRef) idProp).getReferredOwnedProperty();
    }
    if (uidProp instanceof PropertyRef) {
      uidProp = ((PropertyRef) uidProp).getReferredOwnedProperty();
    }
    try {
      id = Crud.read(idProp.getEntityDef())
          .select(idProp)
          .where(uidProp.eq(uid))
          .onlyOneValue(idProp).orElse(null);
    } catch (Exception e) {
      throw new RuntimeException("An error occured during creating primary key for apply change!",
          e);
    }

    if (id == null) {
      id = idProvider.get();
    }
    return Collections.singletonMap(idProp, id);
  }

}
