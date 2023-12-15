package org.smartbit4all.domain.meta;

import static java.util.stream.Collectors.toList;
import java.util.Comparator;
import java.util.List;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.annotation.property.ReferenceMandatory;
import org.smartbit4all.domain.meta.EntityDefinition.TableDefinition;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterHelper;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.springframework.context.ApplicationContext;

/**
 * A builder api to setup a new {@link EntityDefinitionInstance} as {@link EntityDefinition}. This
 * instance can be registered into the {@link EntityManager} or can be used on the fly without any
 * registration.
 * 
 * @author Peter Boros
 */
public class EntityDefinitionBuilder {

  /**
   * The instance setup by the builder.
   */
  private EntityDefinitionInstance instance = new EntityDefinitionInstance();

  private EntityDefinitionBuilder() {
    super();
  }

  /**
   * Constructs a new builder for the entity definition programmatic setup.
   * 
   * @param ctx The {@link ApplicationContext} of the app.
   * @return
   */
  public static final EntityDefinitionBuilder of(ApplicationContext ctx) {
    EntityDefinitionBuilder builder = new EntityDefinitionBuilder();
    builder.instance
        .setDataConverterHelper(ctx.getBean(JDBCDataConverterHelper.class));
    builder.instance.setObjectDefinitionApi(ctx.getBean(ObjectDefinitionApi.class));
    return builder;
  }

  public EntityDefinitionBuilder name(String name) {
    instance.entityDefinitionName = name;
    return this;
  }

  public EntityDefinitionBuilder tableName(String name) {
    instance.tableDefinition = new TableDefinition(name);
    return this;
  }

  public EntityDefinitionBuilder domain(String domain) {
    instance.domain = domain;
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T> EntityDefinitionBuilder ownedProperty(String name, Class<T> typeClass) {
    addOwnedProperty(name, typeClass, -1, null, false);
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T> PropertyOwned<T> addOwnedProperty(String name, Class<T> typeClass, int length) {
    return addOwnedProperty(name, typeClass, length, null, false);
  }

  @SuppressWarnings("unchecked")
  public <T> PropertyOwned<T> addOwnedProperty(String name, Class<T> typeClass, int length,
      boolean primaryKey) {
    return addOwnedProperty(name, typeClass, length, null, primaryKey);
  }

  @SuppressWarnings("unchecked")
  public <T> PropertyOwned<T> addOwnedProperty(String name, Class<T> typeClass, int length,
      Comparator<? super T> comparator,
      boolean primaryKey) {
    PropertyOwned<T> result =
        PropertyOwned.create(name, typeClass, length, false, name, instance.dataConverterHelper);
    if (comparator != null) {
      result.setComparator(comparator);
    }
    PropertyOwned<T> resultProxy = instance.createPropertyProxy(result, PropertyOwned.class);
    instance.registerProperty(resultProxy);

    if (primaryKey) {
      instance.addPrimaryKey(resultProxy);
    }

    return resultProxy;
  }

  @SuppressWarnings("unchecked")
  public <T extends Comparable<T>> EntityDefinitionBuilder reference(String refName,
      EntityDefinitionBuilder target,
      List<String[]> joins) {
    instance.createReference(instance, target.instance, joins.stream()
        .map(j -> (Property<T>[]) new Property[] {(Property<T>) instance.getProperty(j[0]),
            (Property<T>) target.instance.getProperty(j[1])})
        .collect(toList()), refName,
        ReferenceMandatory.BYPROPERTY);

    return this;
  }

  public Reference<?, ?> getReference(String refName) {
    return instance.getReference(refName);
  }

  /**
   * The build function of the builder to get the assembled {@link EntityDefinition}.
   * 
   * @return The result of the build.
   */
  public EntityDefinition build() {
    instance.finishSetup();
    return instance;
  }

  public final EntityDefinition getInstance() {
    return instance;
  }

}
