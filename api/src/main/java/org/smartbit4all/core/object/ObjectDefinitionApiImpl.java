package org.smartbit4all.core.object;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.object.bean.ReferenceDefinitionData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import static java.util.stream.Collectors.toMap;

public class ObjectDefinitionApiImpl implements ObjectDefinitionApi, InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(ObjectDefinitionApiImpl.class);

  private static final String URI = "URI";

  private static final String ID = "ID";

  public static final String SCHEMA = "objectDefinition";

  public static final String PROPERTIES = "propertyDefinitions";

  public static final String OBJECT_DEFINITIONS = "objectDefinitions";

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

  /**
   * The runtime autowires all the available {@link ObjectReferenceConfigs} in the context. The will
   * be summarized and saved into the storage to share with other modules. The reference meta cache
   * is populated from the storage.
   */
  @Autowired(required = false)
  List<ObjectReferenceConfigs> referenceConfigsList;

  @Autowired
  private ApplicationContext context;

  private StorageApi storageApi;

  @Autowired
  private ObjectDefinitionApi self;

  private ReadWriteLock lock = new ReentrantReadWriteLock();

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
    initSerializers();
    initSummarySuppliers();
    defaultSerializer = serializersByName.get(defaultSerializerName);
    initObjectDefinitions();
    initObjectReferences();
  }

  private void initObjectReferences() {
    if (referenceConfigsList != null) {
      for (ObjectReferenceConfigs referenceConfigs : referenceConfigsList) {
        for (ReferenceDefinitionData refData : referenceConfigs.getConfigs()) {
          ObjectDefinition<?> source = definition(refData.getSourceObjectName());
          ObjectDefinition<?> target = definition(refData.getTargetObjectName());
          ReferenceDefinition referenceDefinition = new ReferenceDefinition(refData);
          referenceDefinition.setSource(source);
          referenceDefinition.setTarget(target);
          source.getOutgoingReferences().put(refData.getPropertyPath(),
              referenceDefinition);
          Map<String, ReferenceDefinition> incomingFromThisSource = target.getIncomingReferences()
              .computeIfAbsent(refData.getSourceObjectName(),
                  s -> new HashMap<>());
          incomingFromThisSource.put(refData.getPropertyPath(),
              referenceDefinition);
        }
      }
    }
  }

  private final void initObjectDefinitions() {
    if (definitions != null) {
      for (ObjectDefinition<?> objectDefinition : definitions) {
        initObjectDefinition(objectDefinition);
        definitionsByAlias.put(objectDefinition.getAlias(), objectDefinition);
      }
    }
  }

  private void initObjectDefinition(ObjectDefinition<?> objectDefinition) {
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
  }

  private void initSummarySuppliers() {
    if (summarySuppliers != null) {
      for (ObjectSummarySupplier<?> summarySupplier : summarySuppliers) {
        Map<String, ObjectSummarySupplier<?>> suppliers = summarySuppliersByClass
            .computeIfAbsent(summarySupplier.getClazz(), c -> new HashMap<>());
        // If there is another supplier then we replace it and give a warning.
        ObjectSummarySupplier<?> prevSupplier =
            suppliers.put(summarySupplier.getName(), summarySupplier);
        if (prevSupplier != null) {
          log.warn("There are more than one summary supplier for the {} ({} is replace with {})",
              summarySupplier.getClazz(), prevSupplier.getClass(), summarySupplier.getClass());
        }
      }
    }
  }

  private final void initSerializers() {
    if (serializers != null) {
      for (ObjectSerializer objectSerializer : serializers) {
        serializersByName.put(objectSerializer.getName(), objectSerializer);
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> ObjectDefinition<T> definition(Class<T> clazz) {
    ObjectDefinition<?> definition = definitionByAlias(getDefaultAlias(clazz));
    return (ObjectDefinition<T>) definition;
  }

  @Override
  public ObjectDefinition<?> definition(URI objectUri) {
    if (objectUri == null || objectUri.getPath() == null) {
      return null;
    }
    String rootPath = PathUtility.getRootPath(objectUri.getPath());
    return definitionByAlias(rootPath);
  }

  @Override
  public ObjectDefinition<?> definition(String className) {
    return definitionByAlias(getAliasFromClassName(className));
  }

  private static final Class<?> getClassByName(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  private ObjectDefinition<?> definitionByAlias(String alias) {
    ObjectDefinition<?> objectDefinition = null;
    lock.readLock().lock();
    try {
      objectDefinition = definitionsByAlias.get(alias);
    } finally {
      lock.readLock().unlock();
    }
    if (objectDefinition == null) {
      lock.writeLock().lock();
      try {
        objectDefinition = definitionsByAlias.get(alias);
        if (objectDefinition == null) {
          String className = getClassNameFromAlias(alias);
          Class<?> clazz = getClassByName(className);
          if (clazz == null) {
            // The class doesn't exist in the current runtime. In this case we use the Object as a
            // common ancestor of all the classes.
            clazz = Object.class;
          }
          objectDefinition = constructDefinition(clazz);
          objectDefinition.setAlias(alias);
          objectDefinition.setQualifiedName(className);
          objectDefinition.setObjectDefinitionApi(self);
          // If there is a saved object definition data then we override the newly created object
          // definition.
          objectDefinition.initDefinitionData();
          definitionsByAlias.put(objectDefinition.getAlias(), objectDefinition);
        }
      } finally {
        lock.writeLock().unlock();
      }
    }
    return objectDefinition;
  }

  @Override
  public final void reloadDefinitionData(ObjectDefinition<?> definition) {
    Storage storage = getStorageApi().get(SCHEMA);
    if (storage.exists(definition.getDefinitionData().getUri())) {
      // If we have a definition data saved in the storage then we load this and merge with the
      // currently existing properties.
      definition.builder()
          .addAll(storage.read(definition.getDefinitionData().getUri(), ObjectDefinitionData.class)
              .getProperties())
          .commit();
    }
  }

  @Override
  public void saveDefinitionData(ObjectDefinition<?> definition) {
    Storage storage = getStorageApi().get(SCHEMA);
    ObjectDefinitionData definitionData = definition.getDefinitionData();
    if (storage.exists(definitionData.getUri())) {
      storage.update(definitionData.getUri(), ObjectDefinitionData.class,
          odd -> definition.getDefinitionData());
    } else {
      storage.saveAsNew(definitionData);
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

  /**
   * Some properties may define outgoing reference since they define the referred types and referred
   * properties. And on the other hand the outgoing references should be applied onto the properties
   * to contain this information.
   * 
   * @param definitionData
   * @return
   */
  public static ObjectDefinitionData synchronizeOutgoingReferences(
      ObjectDefinitionData definitionData) {
    Map<@NotNull String, ReferenceDefinitionData> existingReferences =
        definitionData.getOutgoingReferences().stream()
            .collect(toMap(ReferenceDefinitionData::getPropertyPath, r -> r));
    // Find the new references defined by the properties. If a property has referred type and the
    // existing references doesn't contain this property then we found a new outgoing reference.
    Map<@NotNull String, ReferenceDefinitionData> newOutgoingReferences =
        definitionData.getProperties().stream()
            .filter(
                p -> ((p.getReferredType() != null)
                    && !existingReferences.containsKey(p.getName())))
            .collect(toMap(PropertyDefinitionData::getName,
                pdd -> new ReferenceDefinitionData().propertyKind(getPropertyKind(pdd))
                    .propertyPath(pdd.getName())
                    .aggregation(AggregationKind.NONE)
                    .sourceObjectName(definitionData.getQualifiedName())
                    .targetObjectName(pdd.getReferredType()).targetValueSet(pdd.getValueSet())));
    // Now setup the properties of the existing reference
    Map<@NotNull String, PropertyDefinitionData> propertiesByName = definitionData.getProperties()
        .stream().collect(toMap(PropertyDefinitionData::getName, pdd -> pdd));
    for (ReferenceDefinitionData referenceDefinitionData : existingReferences.values()) {
      PropertyDefinitionData referenceProperty =
          propertiesByName.get(referenceDefinitionData.getPropertyPath());
      if (referenceProperty != null) {
        referenceProperty.referredType(referenceDefinitionData.getTargetObjectName())
            .valueSet(referenceDefinitionData.getTargetValueSet());
      }
    }


    // At the end add all the new references to the outgoing references list.
    definitionData.getOutgoingReferences().addAll(newOutgoingReferences.values());

    return definitionData;
  }

  private static final ReferencePropertyKind getPropertyKind(
      PropertyDefinitionData propertyDefinitionData) {
    if (List.class.getName().equals(propertyDefinitionData.getTypeClass())) {
      return ReferencePropertyKind.LIST;
    }
    if (Map.class.getName().equals(propertyDefinitionData.getTypeClass())) {
      return ReferencePropertyKind.MAP;
    }
    return ReferencePropertyKind.REFERENCE;
  }

  public static String getDefaultAlias(Class<?> clazz) {
    return getAliasFromClassName(clazz.getName());
  }

  public static String getAliasFromClassName(String clazzName) {
    return clazzName.replace('.', '_');
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

  private final StorageApi getStorageApi() {
    if (storageApi == null) {
      storageApi = context.getBean(StorageApi.class);
    }
    return storageApi;
  }

}
