package org.smartbit4all.core.object;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.smartbit4all.api.binarydata.BinaryData;

/**
 * This definition must exists for every api objects managed by the given module. It contains the
 * class itself, the URI accessor / mutator and the serializing methods.
 * 
 * @author Peter Boros
 */
public final class ObjectDefinition<T> {

  /**
   * The qualified name is the globally unique name of the given object. The namespace and the name
   * looks like a Java class name. If we need to change the format to use as a path or similar then
   * we can have more then one accessor.
   */
  private String qualifiedName;

  /**
   * The java class of the object.
   */
  private Class<T> clazz;

  /**
   * The URI property of the object. The URI is the unique identifier and storage locator of the
   * given object. It's mandatory if we would like to use it as storage root object.
   */
  private BiConsumer<T, URI> uriSetter;

  private Function<T, URI> uriGetter;

  /**
   * The ID property of the object. The ID is a unique identifier that technically unique but
   * doesn't contain the location info. If an object has URI also then it can be used as root object
   * in a Storage.
   */
  private BiConsumer<T, String> idSetter;

  private Function<T, String> idGetter;

  /**
   * The lower case version of the class name or a configured alias.
   */
  private String alias;

  /**
   * The default {@link ObjectSerializer} belongs to the given object. If there is no instruction in
   * the given situation that this will be used. The Storage will save the name of this serializer
   * to enable loading the given object later on.
   */
  private ObjectSerializer defaultSerializer;

  /**
   * In the configuration we can set the preferred {@link ObjectSerializer} by it's name. If it's
   * available in the context then it will be the {@link #defaultSerializer}.
   */
  private String preferredSerializerName;

  /**
   * The object summaries by name. There is always at least one default summary that contains the
   * uri and all the string properties of the given class.
   */
  private Map<String, ObjectSummarySupplier<T>> summariesByName = null;

  /**
   * Not too often but we might have a objects where we can use the URI generated by the business
   * logic. It means that we can set the uri property and use it in the Storage. It must be set
   * carefully to avoid invalid URI creation. It is mainly used by the platform when the object are
   * identified by natural naming. One example is the invocation of apis.
   */
  private boolean explicitUri = false;

  /**
   * The outgoing references of the given object mapped by the name of the referrer property.
   */
  private final Map<String, ReferenceDefinition> outgoingReferences = new HashMap<>();

  /**
   * The incoming references of the given object mapped by the name of the referrer object and
   * inside there is a map based on the back reference property.
   */
  private final Map<String, Map<String, ReferenceDefinition>> incomingReferences = new HashMap<>();

  /**
   * The Bean meta of the given object definition. The bean meta is related to the {@link #clazz}
   * but the object can be retrieved as other Class also.
   */
  private final BeanMeta meta;

  public ObjectDefinition(Class<T> clazz) {
    super();
    this.clazz = clazz;
    this.qualifiedName = clazz.getName();
    meta = BeanMetaUtil.meta(clazz);
  }

  public final Class<T> getClazz() {
    return clazz;
  }

  public final void setClazz(Class<T> clazz) {
    this.clazz = clazz;
  }

  public final String getAlias() {
    return alias;
  }

  public final void setAlias(String alias) {
    this.alias = alias;
  }

  public final ObjectSerializer getDefaultSerializer() {
    return defaultSerializer;
  }

  final void setDefaultSerializer(ObjectSerializer defaultSerializer) {
    this.defaultSerializer = defaultSerializer;
  }

  public final BiConsumer<T, URI> getUriSetter() {
    return uriSetter;
  }

  public final void setUriSetter(BiConsumer<T, URI> uriSetter) {
    this.uriSetter = uriSetter;
  }

  public final Function<T, URI> getUriGetter() {
    return uriGetter;
  }

  public final void setUriGetter(Function<T, URI> uriGetter) {
    this.uriGetter = uriGetter;
  }

  public URI getUri(T obj) {
    return obj != null ? uriGetter.apply(obj) : null;
  }

  public void setUri(T obj, URI uri) {
    if (obj != null) {
      uriSetter.accept(obj, uri);
    }
  }

  @SuppressWarnings("unchecked")
  public void setUriToObj(Object obj, URI uri) {
    if (obj != null) {
      uriSetter.accept((T) obj, uri);
    }
  }

  public String getId(T obj) {
    return obj != null && idGetter != null ? idGetter.apply(obj) : null;
  }

  public void setId(T obj, String uri) {
    if (obj != null && idSetter != null) {
      idSetter.accept(obj, uri);
    }
  }

  public final String getPreferredSerializerName() {
    return preferredSerializerName;
  }

  public final void setPreferredSerializerName(String preferredSerializerName) {
    this.preferredSerializerName = preferredSerializerName;
  }

  public final BinaryData serialize(Object o) {
    return defaultSerializer.serialize(o, getClazz());
  }

  public final Optional<T> deserialize(BinaryData data) throws IOException {
    return defaultSerializer.deserialize(data, clazz);
  }

  @SuppressWarnings("unchecked")
  public final Map<String, Object> deserializeAsMap(BinaryData data) throws IOException {
    return defaultSerializer.deserialize(data, Map.class).orElseGet(() -> new HashMap<>());
  }

  public final Map<String, Object> toMap(Object o) {
    return defaultSerializer.toMap(o);
  }

  public final T fromMap(Map<String, Object> map) {
    return defaultSerializer.fromMap(map, clazz);
  }

  public final BiConsumer<T, String> getIdSetter() {
    return idSetter;
  }

  public final void setIdSetter(BiConsumer<T, String> idSetter) {
    this.idSetter = idSetter;
  }

  public final Function<T, String> getIdGetter() {
    return idGetter;
  }

  // TODO should be at least package-private! For setup time configuration a builder must be
  // provided
  public final void setIdGetter(Function<T, String> idGetter) {
    this.idGetter = idGetter;
  }

  public final boolean hasUri() {
    return uriGetter != null && uriSetter != null;
  }

  public final boolean hasId() {
    return idGetter != null && idSetter != null;
  }

  final Map<String, ObjectSummarySupplier<T>> getSummariesByName() {
    return summariesByName;
  }

  @SuppressWarnings("unchecked")
  final void setupSummariesByName(Map<String, ObjectSummarySupplier<?>> summariesByNameParam) {
    if (summariesByName == null) {
      summariesByName = new HashMap<>();
      if (summariesByNameParam != null) {
        for (Entry<String, ObjectSummarySupplier<?>> entry : summariesByNameParam.entrySet()) {
          summariesByName.put(entry.getKey(), (ObjectSummarySupplier<T>) entry.getValue());
        }
      } else {
        summariesByName.put(ObjectSummarySupplier.DEFAULT,
            new ObjectSummarySupplier<T>(clazz, ObjectSummarySupplier.DEFAULT) {

              @Override
              public String apply(T t) {
                return ObjectSummarySupplier.getDefaultSummary(t);
              }
            });
      }
    }
  }

  public final ObjectSummarySupplier<T> getSummarySupplier() {
    return getSummarySupplier(ObjectSummarySupplier.DEFAULT);
  }

  public final ObjectSummarySupplier<T> getSummarySupplier(String name) {
    ObjectSummarySupplier<T> supplier = summariesByName.get(name);
    return supplier == null ? summariesByName.get(ObjectSummarySupplier.DEFAULT) : supplier;
  }

  public final boolean isExplicitUri() {
    return explicitUri;
  }

  public final void setExplicitUri(boolean explicitUri) {
    this.explicitUri = explicitUri;
  }

  /**
   * The outgoing references of the given object mapped by the name of the referrer property.
   */
  public final Map<String, ReferenceDefinition> getOutgoingReferences() {
    return outgoingReferences;
  }

  /**
   * The outgoing reference of the given object by the name of the referrer property.
   */
  public final ReferenceDefinition getOutgoingReference(String referenceName) {
    return outgoingReferences.get(referenceName);
  }

  /**
   * The incoming references of the given object mapped by the name of the referrer object and
   * inside there is a map based on the back reference property.
   */
  public final Map<String, Map<String, ReferenceDefinition>> getIncomingReferences() {
    return incomingReferences;
  }

  /**
   * The qualified name is the globally unique name of the given object. The namespace and the name
   * looks like a Java class name. If we need to change the format to use as a path or similar then
   * we can have more then one accessor.
   * 
   * @return
   */
  public final String getQualifiedName() {
    return qualifiedName;
  }

  public final BeanMeta meta() {
    return meta;
  }

}