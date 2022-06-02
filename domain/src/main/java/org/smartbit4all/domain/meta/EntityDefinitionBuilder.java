package org.smartbit4all.domain.meta;

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
    return builder;
  }

  public EntityDefinitionBuilder name(String name) {
    instance.entityDefinitionName = name;
    return this;
  }

  public EntityDefinitionBuilder domain(String domain) {
    instance.domain = domain;
    return this;
  }

  public <T> EntityDefinitionBuilder ownedProperty(String name, Class<T> typeClass) {
    PropertyOwned<T> result =
        PropertyOwned.create(name, typeClass, false, name, instance.dataConverterHelper);
    PropertyOwned<T> resultProxy = instance.createPropertyProxy(result, PropertyOwned.class);
    instance.registerProperty(resultProxy);
    return this;
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

}
