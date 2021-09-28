package org.smartbit4all.core.object;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ObjectApiImpl implements ObjectApi, InitializingBean {

  private static final String URI = "URI";

  /**
   * We need at least one serializer to be able to start the module.
   */
  @Autowired
  private List<ObjectSerializer> serializers;

  /**
   * The available object serializers in a Context keyed by their name. The name is typically the
   * name of the underlying object serializer class.
   */
  private Map<String, ObjectSerializer> serializersByName = new HashMap<>();

  /**
   * The existing {@link ObjectDefinition}s available in the current context.
   */
  @Autowired(required = false)
  private List<ObjectDefinition<?>> definitions;

  /**
   * The active {@link ObjectDefinition}s keyed by the Class.
   */
  private Map<Class<?>, ObjectDefinition<?>> definitionsByClass = new ConcurrentHashMap<>();

  /**
   * By default we use the Jackson object mapper because it's referenced by the openApiGenerate.
   */
  @Value("${objects.defaultSerializer:com.fasterxml.jackson.databind.ObjectMapper}")
  private String defaultSerializerName;

  private ObjectSerializer defaultSerializer;

  /**
   * The cache of the object meta.
   */
  private static final Cache<Class<?>, BeanMeta> beanMetaCache = CacheBuilder.newBuilder().build();

  @Override
  public final BeanMeta meta(Class<?> apiClass) {
    if (apiClass == null) {
      return null;
    }
    try {
      return beanMetaCache.get(apiClass, new Callable<BeanMeta>() {

        @Override
        public BeanMeta call() throws Exception {
          return BeanMetaUtil.meta(apiClass);
        }

      });
    } catch (ExecutionException e) {
      throw new IllegalArgumentException("Unable to analyze the " + apiClass + " bean.", e);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (definitions == null) {
      return;
    }
    for (ObjectSerializer objectSerializer : serializers) {
      serializersByName.put(objectSerializer.getName(), objectSerializer);
    }
    defaultSerializer = serializersByName.get(defaultSerializerName);
    for (ObjectDefinition<?> objectDefinition : definitions) {
      if (objectDefinition.getDefaultSerializer() == null) {
        if (objectDefinition.getPreferredSerializerName() != null) {
          // Try to retrieve the preferred serializer.
          ObjectSerializer objectSerializer =
              serializersByName.get(objectDefinition.getPreferredSerializerName());
          objectDefinition.setDefaultSerializer(objectSerializer);
        }
        // If it's still empty then use the global default
        if (objectDefinition.getDefaultSerializer() == null) {
          objectDefinition.setDefaultSerializer(defaultSerializer);
        }
      }
      if (objectDefinition.getUriGetter() == null || objectDefinition.getUriSetter() == null) {
        setupUri(objectDefinition);
      }
      if (objectDefinition.getAlias() == null) {
        objectDefinition.setAlias(getDefaultAlias(objectDefinition.getClazz()));
      }
      definitionsByClass.put(objectDefinition.getClazz(), objectDefinition);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> ObjectDefinition<T> definition(Class<T> clazz) {
    return (ObjectDefinition<T>) definitionsByClass.computeIfAbsent(clazz,
        this::constructDefinition);
  }

  @SuppressWarnings("unchecked")
  private <T> ObjectDefinition<T> constructDefinition(Class<T> clazz) {
    ObjectDefinition<T> result = null;
    result = (ObjectDefinition<T>) definitionsByClass.get(clazz);
    if (result == null) {
      result = new ObjectDefinition<T>(clazz);
      setupUri(result);
      result.setAlias(getDefaultAlias(clazz));
      result.setDefaultSerializer(defaultSerializer);
    }
    return result;
  }

  private String getDefaultAlias(Class<?> clazz) {
    return clazz.getName().replace('.', '-');
  }

  private <T> void setupUri(ObjectDefinition<T> result) {
    BeanMeta beanMeta = meta(result.getClazz());
    // The definition was not found in the context. We need to analyze the bean by reflection.
    PropertyMeta uriMeta = beanMeta.getProperties().get(URI);
    if (uriMeta == null) {
      throw new IllegalArgumentException(
          "Unable to use the " + result.getClazz()
              + " as domain object because the lack of URI property!");
    }
    result.setUriGetter((o) -> {
      try {
        return (URI) uriMeta.getGetter().invoke(o);
      } catch (Exception e) {
        throw new IllegalArgumentException(
            "Unable to get the URI property of the " + o + " (" + result.getClazz()
                + ") domain object.");
      }
    });
    result.setUriSetter((o, u) -> {
      try {
        uriMeta.getSetter().invoke(o, u);
      } catch (Exception e) {
        throw new IllegalArgumentException(
            "Unable to set the URI property of the " + o + " (" + result.getClazz()
                + ") domain object.");
      }
    });
  }

}
