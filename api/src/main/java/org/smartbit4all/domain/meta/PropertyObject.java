package org.smartbit4all.domain.meta;

public class PropertyObject extends Property<Object> {

  private Property<?> basic;

  public PropertyObject(Property<?> basic) {
    super(basic.getName(), Object.class, null);
    this.jdbcConverter = (JDBCDataConverter<Object, ?>) basic.jdbcConverter();
    this.basic = basic;
    createComparator(basic.type());
  }

  public final Property<?> getBasic() {
    return basic;
  }

  @Override
  public EntityDefinition getEntityDef() {
    EntityDefinition entityDef = super.getEntityDef();
    if (entityDef == null) {
      entityDef = basic.getEntityDef();
    }
    return entityDef;
  }

}
