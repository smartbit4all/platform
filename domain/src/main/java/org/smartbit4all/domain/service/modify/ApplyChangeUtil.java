package org.smartbit4all.domain.service.modify;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.utility.crud.Crud;

public class ApplyChangeUtil {

  private static final Logger log = LoggerFactory.getLogger(ApplyChangeUtil.class);

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

  @SuppressWarnings("unchecked")
  public static <T, U> Function<Object, Map<Property<?>, Object>> simpleIdPProcessor(
      Property<T> refferenceProperty, Property<U> uidProperty) {
    if (!(refferenceProperty instanceof PropertyRef)) {
      throw new IllegalArgumentException(
          "refferenceProperty must be a PropertyRef that can be used to get the source and the target properties!");
    }
    PropertyRef<T> propertyRef = (PropertyRef<T>) refferenceProperty;
    PropertyOwned<T> targetProp = propertyRef.getReferredOwnedProperty();
    Property<T> fkProp =
        (Property<T>) propertyRef.getJoinPath().first().joins().get(0).getSourceProperty();
    return simpleIdPProcessor(fkProp, targetProp, uidProperty);
  }

  public static <T, U> Function<Object, Map<Property<?>, Object>> simpleIdPProcessor(
      Property<T> fkProperty,
      Property<T> pkProperty, Property<U> uidProperty) {
    return (uri) -> {
      U uriValue = null;
      if (uidProperty.type().equals(String.class) && !(uri instanceof String)) {
        uriValue = (U) uri.toString();
      } else if (uidProperty.type().equals(URI.class) && !(uri instanceof URI)) {
        uriValue = (U) URI.create(uri.toString());
      } else {
        uriValue = (U) uri;
      }
      T idValue = queryPkId(uriValue, pkProperty, uidProperty);
      return Collections.singletonMap(fkProperty, idValue);
    };
  }

  public static <T, U> T queryPkId(U uid, Property<T> pkProperty, Property<U> uidProperty) {
    T ugyazon = null;
    try {
      ugyazon = Crud.read(pkProperty.getEntityDef())
          .select(pkProperty)
          .where(uidProperty.eq(uid))
          .onlyOneValue(pkProperty)
          .orElse(ugyazon);
    } catch (Exception e) {
      log.error("An error occured during querying primary key id for uid: [{}]", uid, e);
    }
    return ugyazon;
  }

}
