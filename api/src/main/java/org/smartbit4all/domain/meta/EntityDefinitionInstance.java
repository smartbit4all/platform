package org.smartbit4all.domain.meta;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.smartbit4all.api.databasedefinition.bean.ColumnTypeDefinition;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.annotation.property.ReferenceMandatory;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterHelper;
import org.smartbit4all.domain.service.entity.EntityUris;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * The common meta data of an {@link EntityDefinition} instance.
 *
 * @author Peter Boros
 */
class EntityDefinitionInstance implements EntityDefinition {

  /**
   * The name of the entity definition.
   */
  protected String entityDefinitionName;

  /**
   * The {@link Property} instances mapped by their name.
   */
  protected final Map<String, Property<?>> propertiesByName = new HashMap<>();

  protected final Lock referredPropertiesByPathLock = new ReentrantLock();

  /**
   * The referred properties mapped by their full concatenated path.
   */
  protected final Map<String, Property<?>> referredPropertiesByPath = new HashMap<>();

  /**
   * The references of the entity mapped by their name.
   */
  protected final Map<String, Reference<?, ?>> referencesByName = new HashMap<>();

  /**
   * The referenced entities by the name of the reference. It's redundant because the
   * {@link #referencesByName} could be used indirectly. But it's faster.
   */
  protected final Map<String, EntityDefinition> referencedEntitiesByReferenceName = new HashMap<>();

  /**
   * The property set of the current entity definition. The {@link #countProperty} implicit property
   * is excluded.
   */
  protected final PropertySet allProperties = new PropertySet();

  /**
   * The count property.
   */
  protected Property<Long> countProperty;

  /**
   * The property set with the {@link Property}s that form the unique identifier of the given
   * entity.
   */
  protected final PropertySet primaryKeySet = new PropertySet();

  /**
   * The initialized flag prevents calling the function of the implementation. Depending on the
   * implementation how to use this information.
   */
  private boolean initialized = false;

  private PropertyFunctionMapper propertyFunctionMapper = new PropertyFunctionMapper();

  /**
   * The domain name of the given entity definition.
   */
  protected String domain;

  /**
   * If this entity is stored in a database, then it is stored in this table.
   */
  protected TableDefinition tableDefinition;

  /**
   * The data converter helper is the collection of converters between the Java and the database
   * JDBC bind. More or less static but can be extended at application level.
   */
  protected JDBCDataConverterHelper dataConverterHelper;

  @Override
  public final Property<?> getProperty(String propertyName) {
    Property<?> property = propertiesByName.get(propertyName);
    if (property == null) {
      property = getReferredPropertyByPath(propertyName);
    }
    if (property == null) {
      String[] propertyRefs = propertyName.split("\\.");
      if (propertyRefs.length > 1) {
        String referredPropertyName = propertyRefs[propertyRefs.length - 1];

        List<Reference<?, ?>> joinPath = new ArrayList<>();
        EntityDefinition currentEntity = this;
        for (int i = 0; i < propertyRefs.length - 1; i++) {
          String referenceName = propertyRefs[i];
          Reference<?, ?> reference = currentEntity.getReference(referenceName);
          joinPath.add(reference);
          currentEntity = reference.getTarget();
        }
        Property<?> referredProperty = currentEntity.getProperty(referredPropertyName);
        property = this.findOrCreateReferredProperty(joinPath, referredProperty);
      }
    }
    return property;
  }

  private Property<?> getReferredPropertyByPath(String propertyName) {
    referredPropertiesByPathLock.lock();
    try {
      return referredPropertiesByPath.get(propertyName);
    } finally {
      referredPropertiesByPathLock.unlock();
    }
  }

  private void putReferredPropertyByPath(String path, Property<?> property) {
    referredPropertiesByPathLock.lock();
    try {
      referredPropertiesByPath.put(path, property);
    } finally {
      referredPropertiesByPathLock.unlock();
    }
  }

  @Override
  public PropertyObject getPropertyObject(String propertyName) {
    return new PropertyObject(getProperty(propertyName));
  }

  @Override
  public final Reference<?, ?> getReference(String referenceName) {
    return referencesByName.get(referenceName);
  }

  @Override
  public Property<?> findOrCreateReferredProperty(List<Reference<?, ?>> joinPath,
      Property<?> referredProperty) {
    String refPath = PropertyRef.constructName(joinPath, referredProperty);
    Property<?> property = getReferredPropertyByPath(refPath);
    if (property == null) {
      property = createRefProperty(null, joinPath, referredProperty, refPath);
    }
    return property;
  }

  @Override
  public Property<?> findOrCreateReferredProperty(String[] joinPath, String referredPropertyName) {
    String refPath = PropertyRef.constructName(joinPath, referredPropertyName);
    Property<?> property = getReferredPropertyByPath(refPath);
    if (property == null) {
      property = createRefPropertyByPath(null, referredPropertyName, joinPath);
    }
    return property;
  }

  protected PropertyRef<?> createRefProperty(String propertyName, List<Reference<?, ?>> joinPath,
      Property<?> referredProperty) {
    String refPath = PropertyRef.constructName(joinPath, referredProperty);
    return createRefProperty(propertyName, joinPath, referredProperty, refPath);
  }

  private PropertyRef<?> createRefProperty(String propertyName, List<Reference<?, ?>> joinPath,
      Property<?> referredProperty,
      String refPath) {
    PropertyRef<?> property = new PropertyRef<>(propertyName, joinPath, referredProperty);

    PropertyRef<?> propertyProxy = createPropertyProxy(property, PropertyRef.class);

    putReferredPropertyByPath(refPath, propertyProxy);
    propertyProxy.setEntityDef(this);
    return propertyProxy;
  }

  protected PropertyRef<?> createRefPropertyByPath(String propertyName, String referredPropertyName,
      String[] path) {
    List<Reference<?, ?>> joinPath = new ArrayList<>();
    EntityDefinition currentEntity = this;
    for (int i = 0; i < path.length; i++) {
      String referenceName = path[i];
      Reference<?, ?> reference = currentEntity.getReference(referenceName);
      joinPath.add(reference);
      currentEntity = reference.getTarget();
    }
    Property<?> referredProperty = currentEntity.getProperty(referredPropertyName);
    PropertyRef<?> property = createRefProperty(propertyName, joinPath, referredProperty);
    return property;
  }

  public final <T extends Comparable<T>> Reference<?, ?> createReference(
      EntityDefinition sourceEntity, EntityDefinition targetEntity,
      List<Property<T>[]> joins,
      String referenceName, ReferenceMandatory referenceMandatory) {

    Reference<?, ?> reference = new Reference<>(sourceEntity, targetEntity, referenceName);
    for (Property<T>[] entry : joins) {
      reference.addJoin(entry[0], entry[1]);
    }
    if (referenceMandatory != ReferenceMandatory.BYPROPERTY) {
      reference.setMandatory(
          referenceMandatory == ReferenceMandatory.TRUE ? Boolean.TRUE : Boolean.FALSE);
    }
    referencesByName.put(referenceName, reference);
    return reference;
  }


  @SuppressWarnings("unchecked")
  <P extends Property<?>> P createPropertyProxy(P property, Class<P> propClazz) {
    Enhancer e = new Enhancer();
    e.setClassLoader(propClazz.getClassLoader());
    e.setSuperclass(propClazz);
    e.setCallback(new MethodInterceptor() {

      @Override
      public Object intercept(Object obj, Method method, Object[] args,
          MethodProxy proxy)
          throws Throwable {
        String methodName = method.getName();

        if ("function".equals(methodName)) {
          if (args.length != 1 || !(args[0] instanceof PropertyFunction)) {
            throw new RuntimeException(
                "function method can only be invoked with one PropertyFunction typed parameter!");
          }
          PropertyFunction functionParam = (PropertyFunction) args[0];
          return propertyFunctionMapper.getFunctionProperty(property, functionParam);
        }

        PropertyFunction basicFunction = PropertyFunction.basicFunctionsByName.get(methodName);
        if (basicFunction != null) {
          return propertyFunctionMapper.getFunctionProperty(property, basicFunction);
        }

        return method.invoke(property, args);
      }
    });
    if (property instanceof PropertyOwned) {
      // String name, Class<T> type, String defaultDbExpression, JDBCDataConverter<T, ?> typeHandler
      PropertyOwned<?> p = (PropertyOwned<?>) property;
      return (P) e.create(
          new Class<?>[] {String.class, Class.class, String.class, JDBCDataConverter.class,
              ColumnTypeDefinition.class, Boolean.class},
          new Object[] {p.getName(), p.type(), p.getDbExpression().get(null), p.jdbcConverter(),
              p.getColumnType(), p.isMandatory()});
    } else if (property instanceof PropertyRef) {
      // String name, List<Reference<?, ?>> joinPath, Property<T> referredProperty
      PropertyRef<?> p = (PropertyRef<?>) property;
      return (P) e.create(
          new Class<?>[] {String.class, List.class, Property.class},
          new Object[] {p.getName(), p.getJoinReferences(), p.getReferredProperty()});
    }
    throw new RuntimeException("Can not create proxy for property subtype: " + propClazz.getName());
  }

  public void finishSetup() {
    allProperties.addAll(propertiesByName.values());
    allProperties.addAll(referredPropertiesByPath.values());
    allProperties.remove(countProperty);
    initialized = true;
  }

  @Override
  public PropertySet PRIMARYKEYDEF() {
    return primaryKeySet;
  }

  /**
   * @return The value of the {@link #initialized} defining if we can start using actively the
   *         definition.
   */
  protected final boolean isInitialized() {
    return initialized;
  }

  @Override
  public Property<Long> count() {
    return countProperty;
  }

  @Override
  public String entityDefName() {
    return entityDefinitionName;
  }

  @Override
  public String getDomain() {
    return domain;
  }

  @Override
  public URI getUri() {
    return EntityUris.createEntityUri(getDomain(), entityDefName());
  }

  @Override
  public PropertySet allProperties() {
    return new PropertySet(allProperties);
  }

  @Override
  public List<Reference<?, ?>> allReferences() {
    return new ArrayList<>(referencesByName.values());
  }

  @Override
  public ExpressionExists exists(JoinPath masterJoin, Expression expression) {
    // In this case we have a reference for the root entity directly
    return new ExpressionExists(this, masterJoin.first().getSource(), expression,
        JoinPath.EMPTY, masterJoin);
  }

  @Override
  public Expression exists(Expression expression) {
    // Normally the expression is related to the current entity definition so it's the same
    // expression.
    return expression;
  }

  @Override
  public JoinPath join() {
    return JoinPath.EMPTY;
  }

  static class PropertyFunctionMapper {

    private Map<Property<?>, List<Property<?>>> functionPropertiesByBasePropery = new HashMap<>();

    public Property<?> getFunctionProperty(Property<?> baseProp, PropertyFunction propFunction) {
      String functionName = propFunction.getName();
      List<Property<?>> funcProps = functionPropertiesByBasePropery.get(baseProp);
      if (funcProps == null) {
        funcProps = new ArrayList<>();
        functionPropertiesByBasePropery.put(baseProp, funcProps);
      }
      Property<?> funcProp = funcProps.stream()
          .filter(fp -> functionName.equals(fp.getPropertyFunction().getName()))
          .findFirst().orElse(null);
      if (funcProp == null) {
        if (baseProp instanceof PropertyOwned) {
          funcProp =
              PropertyOwned.createFunctionProperty(((PropertyOwned<?>) baseProp), propFunction);
          funcProps.add(funcProp);
        } else if (baseProp instanceof PropertyRef) {
          funcProp =
              PropertyRef.createFunctionProperty(((PropertyRef<?>) baseProp), propFunction);
          funcProps.add(funcProp);
        } else {
          funcProps.add(baseProp);
        }

      }
      return funcProp;
    }

  }

  @Override
  public TableDefinition tableDefinition() {
    return tableDefinition;
  }

  void registerProperty(Property<?> property) {
    property.setEntityDef(this);
    propertiesByName.put(property.getName(), property);
  }

  void addPrimaryKey(Property<?> property) {
    primaryKeySet.add(property);
  }

  final void setDataConverterHelper(JDBCDataConverterHelper dataConverterHelper) {
    this.dataConverterHelper = dataConverterHelper;
  }

  @Override
  public String toString() {
    return "entity: " + getDomain() + StringConstant.DOT + entityDefName();
  }

}
