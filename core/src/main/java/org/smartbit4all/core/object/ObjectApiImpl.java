package org.smartbit4all.core.object;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.utility.PathUtility;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ObjectApiImpl implements ObjectApi, InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(ObjectApiImpl.class);

  private static final String URI = "URI";

  private static final String ID = "ID";

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
   * The active {@link ObjectDefinition}s keyed by the Class.
   */
  private Map<String, ObjectDefinition<?>> definitionsByAlias = new ConcurrentHashMap<>();

  /**
   * By default we use the Jackson object mapper because it's referenced by the openApiGenerate.
   */
  @Value("${objects.defaultSerializer:com.fasterxml.jackson.databind.ObjectMapper}")
  private String defaultSerializerName;

  /**
   * The default serializer for the objects.
   */
  private ObjectSerializer defaultSerializer;

  /**
   * The cache of the object meta.
   */
  private static final Cache<Class<?>, BeanMeta> beanMetaCache = CacheBuilder.newBuilder().build();

  /**
   * The existing parameterized object summary producers. Pre processed while
   * {@link #afterPropertiesSet()} and stored in the map with class as key.
   */
  @Autowired(required = false)
  private List<ObjectSummarySupplier<?>> summarySuppliers;

  private Map<Class<?>, Map<String, ObjectSummarySupplier<?>>> summarySuppliersByClass =
      new HashMap<>();

  @Override
  public final BeanMeta meta(Class<?> apiClass) {
    return getMeta(apiClass);
  }

  private static BeanMeta getMeta(Class<?> apiClass) {
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
    // Pre process the serializers.
    if (serializers != null) {
      for (ObjectSerializer objectSerializer : serializers) {
        serializersByName.put(objectSerializer.getName(), objectSerializer);
      }
    }
    // Pre process the summary suppliers.
    if (summarySuppliers != null) {
      for (ObjectSummarySupplier<?> summarySupplier : summarySuppliers) {
        Map<String, ObjectSummarySupplier<?>> suppliers = summarySuppliersByClass
            .computeIfAbsent(summarySupplier.getClazz(), c -> new HashMap<>());
        // If there is another supplier then we replace it and give a warning.
        ObjectSummarySupplier<?> prevSupplier =
            suppliers.put(summarySupplier.getName(), summarySupplier);
        if (prevSupplier != null) {
          log.warn("There are more than one summary supplier for the " + summarySupplier.getClazz()
              + " (" + prevSupplier.getClass() + " is replace with " + summarySupplier.getClass()
              + ")");
        }
      }
    }

    defaultSerializer = serializersByName.get(defaultSerializerName);
    if (definitions != null) {
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
        if (objectDefinition.getIdGetter() == null || objectDefinition.getIdSetter() == null) {
          setupId(objectDefinition);
        }
        if (objectDefinition.getAlias() == null) {
          objectDefinition.setAlias(getDefaultAlias(objectDefinition.getClazz()));
        }
        objectDefinition
            .setupSummariesByName(summarySuppliersByClass.get(objectDefinition.getClazz()));

        definitionsByClass.put(objectDefinition.getClazz(), objectDefinition);
        definitionsByAlias.put(objectDefinition.getAlias(), objectDefinition);

        objectDefinition.setNewlyCreated(false);
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> ObjectDefinition<T> definition(Class<T> clazz) {
    ObjectDefinition<T> objectDefinition =
        (ObjectDefinition<T>) definitionsByClass.computeIfAbsent(clazz,
            this::constructDefinition);
    if (objectDefinition.isNewlyCreated()) {
      definitionsByAlias.put(objectDefinition.getAlias(), objectDefinition);
      objectDefinition.setNewlyCreated(false);
    }

    return objectDefinition;
  }

  @Override
  public ObjectDefinition<?> definition(URI objectUri) {
    if (objectUri == null || objectUri.getPath() == null) {
      return null;
    }
    String rootPath = PathUtility.getRootPath(objectUri.getPath());
    try {
      Class<?> clazz = Class.forName(getClassNameFromAlias(rootPath));
      return definition(clazz);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(
          "Unable to initiate " + rootPath + " class from " + objectUri + " URI.", e);
    }
  }

  private <T> ObjectDefinition<T> constructDefinition(Class<T> clazz) {
    ObjectDefinition<T> result = constructDefinitionBase(clazz);
    result.setDefaultSerializer(defaultSerializer);
    result.setupSummariesByName(summarySuppliersByClass.get(clazz));

    return result;
  }

  public static <T> ObjectDefinition<T> constructDefinitionBase(Class<T> clazz) {
    ObjectDefinition<T> result = null;
    result = new ObjectDefinition<>(clazz);
    setupUri(result);
    setupId(result);
    result.setAlias(getDefaultAlias(clazz));
    return result;
  }

  public static String getDefaultAlias(Class<?> clazz) {
    return clazz.getName().replace('.', '_');
  }

  private static String getClassNameFromAlias(String alias) {
    return alias.replace('_', '.');
  }

  @SuppressWarnings("unchecked")
  private static <T> void setupUri(ObjectDefinition<T> result) {
    setupObjectProperty(result, URI, ObjectDefinition::setUriGetter,
        ObjectDefinition::setUriSetter);
  }

  @SuppressWarnings("unchecked")
  private static <T> void setupId(ObjectDefinition<T> result) {
    boolean isIdSet = setupObjectProperty(result, ID, ObjectDefinition::setIdGetter,
        ObjectDefinition::setIdSetter);
    if (!isIdSet) {
      // try to set the uri when there is no id field
      setupObjectProperty(result, URI,
          (od, getter) -> od.setIdGetter(o -> getter.apply((T) o).toString()),
          (od, setter) -> od
              .setIdSetter((o, u) -> setter.accept((T) o, java.net.URI.create((String) u))));
    }
  }

  @SuppressWarnings("rawtypes")
  private static <T> boolean setupObjectProperty(ObjectDefinition<T> result, String propertyName,
      BiConsumer<ObjectDefinition, Function<T, ?>> getterSetter,
      BiConsumer<ObjectDefinition, BiConsumer<T, Object>> setterSetter) {
    return setupObjectProperty(result, propertyName, getterSetter, setterSetter, false);
  }

  @SuppressWarnings("rawtypes")
  private static <T> boolean setupObjectProperty(ObjectDefinition<T> result, String propertyName,
      BiConsumer<ObjectDefinition, Function<T, ?>> getterSetter,
      BiConsumer<ObjectDefinition, BiConsumer<T, Object>> setterSetter,
      boolean isMandatory) {
    BeanMeta beanMeta = getMeta(result.getClazz());
    // The definition was not found in the context. We need to analyze the bean by reflection.
    PropertyMeta propertyMeta = beanMeta.getProperties().get(propertyName);
    if (propertyMeta != null) {
      getterSetter.accept(result, (o) -> {
        try {
          return propertyMeta.getGetter().invoke(o);
        } catch (Exception e) {
          throw new IllegalArgumentException(
              "Unable to get the " + propertyName + " property of the " + o + " ("
                  + result.getClazz() + ") domain object.");
        }
      });
      setterSetter.accept(result, (o, u) -> {
        try {
          propertyMeta.getSetter().invoke(o, u);
        } catch (Exception e) {
          throw new IllegalArgumentException(
              "Unable to set the " + propertyName + " property of the " + o + " ("
                  + result.getClazz() + ") domain object.");
        }
      });
      return true;
    } else if (isMandatory) {
      throw new IllegalArgumentException(
          "Unable to use the " + result.getClazz()
              + " as domain object because the lack of URI property!");
    }
    return false;
  }

  /**
   * @return The default serializer for the objects.
   */
  @Override
  public final ObjectSerializer getDefaultSerializer() {
    return defaultSerializer;
  }

}
