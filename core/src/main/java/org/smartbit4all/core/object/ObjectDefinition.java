package org.smartbit4all.core.object;

import java.net.URI;
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

  public ObjectDefinition(Class<T> clazz) {
    super();
    this.clazz = clazz;
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

  public final Optional<T> deserialize(BinaryData data) {
    return defaultSerializer.deserialize(data, clazz);
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

}
