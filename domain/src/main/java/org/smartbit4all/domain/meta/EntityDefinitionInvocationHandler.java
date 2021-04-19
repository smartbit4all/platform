/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.domain.annotation.property.ComputedProperty;
import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.Join;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.Ref;
import org.smartbit4all.domain.annotation.property.ReferenceEntity;
import org.smartbit4all.domain.annotation.property.ReferenceMandatory;
import org.smartbit4all.domain.annotation.property.ReferenceProperty;
import org.smartbit4all.domain.annotation.property.SqlExpression;
import org.smartbit4all.domain.annotation.property.SqlProperty;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.annotation.property.ValueComparator;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterHelper;
import org.smartbit4all.domain.meta.logic.Count;
import org.smartbit4all.domain.service.entity.EntityUris;
import org.smartbit4all.domain.service.query.QueryApi;
import org.smartbit4all.domain.utility.SupportedDatabase;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jmx.access.InvalidInvocationException;

public class EntityDefinitionInvocationHandler<T extends EntityDefinition>
    implements InvocationHandler, EntityDefinition, EntitySetup, ApplicationContextAware {

  private static final Logger log =
      LoggerFactory.getLogger(EntityDefinitionInvocationHandler.class);

  protected AnnotationConfigApplicationContext ctx;

  static Class<?>[] mutuallyExclusiveAnnotations = {OwnProperty.class, ReferenceProperty.class,
      ComputedProperty.class, SqlProperty.class, ReferenceEntity.class, Ref.class};

  private Map<String, Property<?>> propertiesByName;
  private Map<String, Property<?>> referredPropertiesByPath;
  private Map<String, Reference<?, ?>> referencesByName;
  private Map<String, EntityDefinition> referencedEntitiesByReferenceName;

  /**
   * The dynamic method handlers by the original method as key.
   */
  private Map<Method, EntityDefinitionMethod> methodMap = new HashMap<>();

  private Class<T> entityDefClazz;

  /**
   * The count property.
   */
  private Property<Long> countProperty;

  private final PropertySet primaryKeySet = new PropertySet();

  /**
   * If this entity is stored in a database, then it is stored in this table.
   */
  private TableDefinition tableDefinition;

  private EntityService<?> entityService;

  private String entityDefinitionName;

  private JDBCDataConverterHelper dataConverterHelper;

  private boolean isInitialized = false;

  private PropertySet allProperties;

  private EntityDefinitionInvocationHandler(Class<T> clazz) {
    this.entityDefClazz = clazz;
    if (!entityDefClazz.isAnnotationPresent(Entity.class)) {
      throw new IllegalArgumentException(
          "EntityDef must have @Entity annotation! (" + entityDefClazz.getName() + ")");
    }
    entityDefinitionName = entityDefClazz.getAnnotation(Entity.class).value();
    if (entityDefinitionName.isEmpty()) {
      entityDefinitionName = entityDefClazz.getName();
    }
    tableDefinition = createTableDefinition();

    this.ctx = new AnnotationConfigApplicationContext();
    propertiesByName = new HashMap<>();
    referredPropertiesByPath = new HashMap<>();
    referencesByName = new HashMap<>();
    referencedEntitiesByReferenceName = new HashMap<>();
    allProperties = new PropertySet();
  }

  static <T extends EntityDefinition> EntityDefinitionInvocationHandler<T> create(Class<T> def) {
    return new EntityDefinitionInvocationHandler<>(def);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    ctx.setParent(applicationContext);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("services".equals(method.getName())) {
      initEntityService(method);
      return services();
    }
    if (method.getDeclaringClass().isAssignableFrom(EntityDefinition.class)) {
      return method.invoke(this, args);
    }
    if (method.getDeclaringClass().isAssignableFrom(EntitySetup.class)) {
      if (!isInitialized) {
        return method.invoke(this, args);
      } else {
        log.warn("Entity setup method called when entity is already initialized! ("
            + entityDefClazz.getName() + "." + method.getName() + ")");
        return null;
      }
    }
    EntityDefinitionMethod entityDefinitionMethod = methodMap.get(method);
    if (entityDefinitionMethod != null) {
      return entityDefinitionMethod.invoke(proxy, method, args);
    }
    throw new InvalidInvocationException("Unhandled method invocation");
  }

  @Override
  public void initContext() {
    ctx.refresh();
  }

  @Override
  public void setupProperties() {
    dataConverterHelper = ctx.getBean(JDBCDataConverterHelper.class);

    countProperty = new PropertyComputed<>(Count.PROPERTTYNAME, Long.class,
        dataConverterHelper.from(Long.class), Count.class);
    propertiesByName.put(Count.PROPERTTYNAME, countProperty);

    for (Method method : entityDefClazz.getMethods()) {
      checkMultiplePropertyAnnotations(method);
      Property<?> property = null;
      if (method.isAnnotationPresent(OwnProperty.class)) {
        property = createOwnedProperty(method);
      }
      if (method.isAnnotationPresent(ComputedProperty.class)) {
        property = createComputedProperty(method);
      }
      if (method.isAnnotationPresent(SqlProperty.class)) {
        property = createSqlProperty(method);
      }

      registerProperty(method, property);
    }
    if (primaryKeySet.isEmpty()) {
      throw new IllegalArgumentException(
          "EntityDef without @Id property! (" + entityDefClazz.getName() + ")");
    }
  }

  @SuppressWarnings("unchecked")
  private <P> void addComparator(Property<P> property, Method method) {
    ValueComparator annot = method.getAnnotation(ValueComparator.class);
    Comparator<? super P> comparator = null;
    try {
      comparator = (Comparator<? super P>) annot.comparator().newInstance();
    } catch (ClassCastException | InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(
          "The Comparator class defined in the annotation of a proerty can not be initialized! Property name: "
              + property.getName() + "; Comparator class: " + annot.comparator().getName(),
          e);
    }
    property.setComparator(comparator);
  }

  private void registerProperty(Method method, Property<?> property) {
    if (property != null) {
      property.setEntityDef(this);
      propertiesByName.put(property.getName(), property);
      if (method.isAnnotationPresent(Id.class)) {
        primaryKeySet.add(property);
      }
      if (method.isAnnotationPresent(ValueComparator.class)) {
        addComparator(property, method);
      }
      // Register the method for the #invoke
      methodMap.put(method, new EDMProperty(property));
    }
  }

  @SuppressWarnings("unchecked")
  private void checkMultiplePropertyAnnotations(Method method) {
    int annotationsPresent = 0;
    for (Class<?> clazz : mutuallyExclusiveAnnotations) {
      annotationsPresent += method.isAnnotationPresent((Class<? extends Annotation>) clazz) ? 1 : 0;
    }
    if (annotationsPresent > 1) {
      throw new IllegalArgumentException("Mutually exclusive annotations on "
          + entityDefClazz.getName() + "." + method.getName() + "!");
    }
  }

  private Property<?> createOwnedProperty(Method method) {
    OwnProperty annot = method.getAnnotation(OwnProperty.class);
    String propertyName = getName(annot.name(), method);
    return PropertyOwned.create(propertyName, getPropertyType(method), annot.mandatory(),
        annot.columnName(), null,
        dataConverterHelper);
  }

  private Property<?> createComputedProperty(Method method) {
    ComputedProperty annot = method.getAnnotation(ComputedProperty.class);
    String propertyName = getName(annot.name(), method);
    // TODO check null implementation?
    return PropertyComputed.create(propertyName, getPropertyType(method), dataConverterHelper,
        annot.implementation());
  }

  private Property<?> createSqlProperty(Method method) {
    SqlProperty annot = method.getAnnotation(SqlProperty.class);
    String propertyName = getName(annot.name(), method);
    SqlExpression[] expressions = method.getAnnotationsByType(SqlExpression.class);
    if (expressions.length == 0) {
      return PropertySqlComputed.create(propertyName, getPropertyType(method), dataConverterHelper,
          annot.expression());
    } else {
      Map<SupportedDatabase, String> map = new HashMap<>();
      for (int i = 0; i < expressions.length; i++) {
        SqlExpression expression = expressions[i];
        map.put(expression.dialect(), expression.expression());
      }
      return PropertySqlComputed.create(propertyName, getPropertyType(method), dataConverterHelper,
          annot.expression(), map);
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Class<? extends Comparable> getPropertyType(Method method) {
    return (Class<? extends Comparable>) ((ParameterizedType) method.getGenericReturnType())
        .getActualTypeArguments()[0];
  }

  private String getName(String name, Method method) {
    if (name == null || name.isEmpty()) {
      return method.getName();
    }
    return name;
  }

  private TableDefinition createTableDefinition() {
    String tableName = null;
    String schema = null;
    if (entityDefClazz.isAnnotationPresent(Table.class)) {
      Table table = entityDefClazz.getAnnotation(Table.class);
      tableName = table.value();
      if (!table.schema().isEmpty()) {
        schema = table.schema();
      }
    } else {
      tableName = entityDefinitionName.toUpperCase();
      if (tableName.endsWith("DEF")) {
        tableName = tableName.substring(0, tableName.length() - 3);
      }
    }
    return new TableDefinition(schema, tableName);
  }

  @Override
  public void setupReferences() {
    // TODO lot's of error checking...

    // references
    // for (Method method : entityDefClazz.getMethods()) {
    // if (method.isAnnotationPresent(Ref.class)) {
    // String referenceName = getName(method.getAnnotation(Ref.class).value(), method);
    // Join[] joins = method.getAnnotationsByType(Join.class);
    // @SuppressWarnings("unchecked")
    // Class<? extends EntityDefinition> targetEntityClazz =
    // (Class<? extends EntityDefinition>) ((ParameterizedType) method.getGenericReturnType())
    // .getActualTypeArguments()[1];
    // processReference(joins, targetEntityClazz, referenceName);
    // @SuppressWarnings("unused")
    // Reference<?, ?> reference = referencesByName.get(referenceName);
    // EDMReference referenceMethod = new EDMReference(reference);
    // }
    // }
    // referred entities
    for (Method method : entityDefClazz.getMethods()) {
      if (method.isAnnotationPresent(ReferenceEntity.class)) {
        ReferenceEntity referenceEntityAnnot = method.getAnnotation(ReferenceEntity.class);
        String referenceName = getName(referenceEntityAnnot.value(), method);
        Reference<?, ?> reference = referencesByName.get(referenceName);
        if (reference == null) {
          Join[] joins = method.getAnnotationsByType(Join.class);
          @SuppressWarnings("unchecked")
          Class<? extends EntityDefinition> targetEntityClazz =
              (Class<? extends EntityDefinition>) method.getReturnType();
          processReference(joins, targetEntityClazz, referenceName,
              referenceEntityAnnot.mandatory());
          reference = referencesByName.get(referenceName);
        }
        EntityDefinition referencedEntity = referencedEntitiesByReferenceName.get(referenceName);
        EDMReferencedEntity referencedEntityMethod =
            new EDMReferencedEntity(referencedEntity, reference);
        methodMap.put(method, referencedEntityMethod);
      }
    }
    // references again
    for (Method method : entityDefClazz.getMethods()) {
      if (method.isAnnotationPresent(Ref.class)) {
        // We must use the previously created setup.
        String referenceName = getName(method.getAnnotation(Ref.class).value(), method);
        Reference<?, ?> reference = referencesByName.get(referenceName);
        if (reference == null) {
          throw new IllegalArgumentException(
              "Reference by name not found, probably no Join specification neither at @Ref nor @ReferencedEntity!");
        }
        EntityDefinition referencedEntity = referencedEntitiesByReferenceName.get(referenceName);
        methodMap.put(method, new EDMReference(referencedEntity, reference));
      }
    }
  }

  /**
   * If joins is empty, won't do anything. Joins can be specified at {@link Ref} or
   * {@link ReferenceEntity}, in any order. Joins existence check will be performed after creating
   * all references.
   * 
   * @param joins
   * @param targetEntityClazz
   * @param referenceName
   * @param referenceMethodName
   * @param entityMethodName
   */
  private void processReference(Join[] joins,
      Class<? extends EntityDefinition> targetEntityClazz,
      String referenceName, ReferenceMandatory referenceMandatory) {
    if (joins == null || joins.length == 0) {
      // joins can be specified at
      return;
    }
    EntityDefinition referencedEntity = createReferenceProxy(entityDefClazz, targetEntityClazz,
        joins, referenceName, referenceMandatory);
    referencedEntitiesByReferenceName.put(referenceName, referencedEntity);

  }

  @SuppressWarnings("unchecked")
  protected final <TARGET extends EntityDefinition, PROP extends Comparable<PROP>> TARGET createReferenceProxy(
      Class<T> sourceEntityClazz, Class<TARGET> targetEntityClazz, Join[] joins,
      String referenceName, ReferenceMandatory referenceMandatory) {

    T sourceEntity = ctx.getBean(sourceEntityClazz);
    TARGET targetEntity = ctx.getBean(targetEntityClazz);
    Reference<T, TARGET> reference = new Reference<>(sourceEntity, targetEntity, referenceName);
    for (int i = 0; i < joins.length; i++) {
      Join join = joins[i];
      Property<PROP> sourceProperty = (Property<PROP>) sourceEntity.getProperty(join.source());
      Property<PROP> targetProperty = (Property<PROP>) targetEntity.getProperty(join.target());
      reference.addJoin(sourceProperty, targetProperty);
    }
    if (referenceMandatory != ReferenceMandatory.BYPROPERTY) {
      reference.setMandatory(
          referenceMandatory == ReferenceMandatory.TRUE ? Boolean.TRUE : Boolean.FALSE);
    }
    referencesByName.put(referenceName, reference);
    return reference.createProxy(targetEntityClazz);
  }


  @Override
  public void setupReferredProperties() {
    for (Method method : entityDefClazz.getMethods()) {
      if (method.isAnnotationPresent(ReferenceProperty.class)) {
        ReferenceProperty annot = method.getAnnotation(ReferenceProperty.class);
        String[] path = annot.path();
        String propertyName = getName(annot.name(), method);
        String regerredPropertyName = annot.property();
        PropertyRef<?> property = createRefPropertyByPath(propertyName, regerredPropertyName, path);
        registerProperty(method, property);
      }
    }
  }

  private PropertyRef<?> createRefPropertyByPath(String propertyName, String referredPropertyName,
      String[] path) {
    List<Reference<?, ?>> joinPath = new ArrayList<>();
    // TODO getBean by name?
    EntityDefinition currentEntity = ctx.getBean(entityDefClazz);
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


  @Override
  public Property<?> findOrCreateReferredProperty(List<Reference<?, ?>> joinPath,
      Property<?> referredProperty) {
    String refPath = PropertyRef.constructName(joinPath, referredProperty);
    Property<?> property = referredPropertiesByPath.get(refPath);
    if (property == null) {
      property = createRefProperty(null, joinPath, referredProperty, refPath);
    }
    return property;
  }

  @Override
  public Property<?> findOrCreateReferredProperty(String[] joinPath, String referredPropertyName) {
    String refPath = PropertyRef.constructName(joinPath, referredPropertyName);
    Property<?> property = referredPropertiesByPath.get(refPath);
    if (property == null) {
      property = createRefPropertyByPath(referredPropertyName, referredPropertyName, joinPath);
    }
    return property;
  }

  private PropertyRef<?> createRefProperty(String propertyName, List<Reference<?, ?>> joinPath,
      Property<?> referredProperty) {
    String refPath = PropertyRef.constructName(joinPath, referredProperty);
    return createRefProperty(propertyName, joinPath, referredProperty, refPath);
  }

  private PropertyRef<?> createRefProperty(String propertyName, List<Reference<?, ?>> joinPath,
      Property<?> referredProperty,
      String refPath) {
    PropertyRef<?> property = new PropertyRef<>(propertyName, joinPath, referredProperty);
    property.setEntityDef(this);
    referredPropertiesByPath.put(refPath, property);
    return property;
  }

  @Override
  public void finishSetup() {
    allProperties.addAll(propertiesByName.values());
    allProperties.addAll(referredPropertiesByPath.values());
    allProperties.remove(countProperty);
    isInitialized = true;
  }

  @Override
  public final Property<?> getProperty(String propertyName) {
    return propertiesByName.get(propertyName);
  }

  @Override
  public Reference<?, ?> getReference(String referenceName) {
    return referencesByName.get(referenceName);
  }

  @Override
  public PropertySet PRIMARYKEYDEF() {
    return primaryKeySet;
  }

  @Override
  public TableDefinition tableDefinition() {
    return tableDefinition;
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
  public EntityService<?> services() {
    return entityService;
  }

  private void initEntityService(Method method) {
    if (entityService == null) {
      Class<?> serviceClass = method.getReturnType();
      if (serviceClass.equals(EntityService.class)) {
        // default implementation
        entityService = (EntityService<?>) ctx.getBean("defaultEntityService", this);
      } else {
        entityService = (EntityService<?>) ctx.getBean(serviceClass);
      }
    }
  }

  @Override
  public PropertySet allProperties() {
    return allProperties;
  }

  @Override
  public List<Reference<?, ?>> allReferences() {
    return new ArrayList<>(referencesByName.values());
  }

  @Override
  public String getDomain() {
    return entityDefClazz.getPackage().getName();
  }

  @Override
  public URI getUri() {
    return EntityUris.createEntityUri(getDomain(), entityDefName());
  }

  @Override
  public Expression exists(EntityDefinition fkEntity, Expression expressionOfTarget) {
    throw new UnsupportedOperationException();
    // return new ExpressionExists(this, fkEntity, expressionOfTarget);
  }

  @Override
  public Expression exists(Expression expressionOfTarget) {
    throw new UnsupportedOperationException();
    // return new ExpressionExists(this, null, expressionOfTarget);
  }

  @Override
  public QueryApi getQueryApi() {
    return ctx.getBean(QueryApi.class);
  }

}
