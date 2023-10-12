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

  @Override
  public PropertyObject function(PropertyFunction function) {
    EntityDefinition entityDef = getEntityDef();
    if (entityDef instanceof EntityDefinitionInstance) {
      return (PropertyObject) ((EntityDefinitionInstance) entityDef)
          .getFunctionProperty(this,
              function);
    }
    return (PropertyObject) super.function(function);
  }

  public static PropertyObject createFunctionProperty(PropertyObject baseProperty,
      PropertyFunction function) {
    PropertyObject funcProp = new PropertyObject(baseProperty.getBasic());
    funcProp.setEntityDef(baseProperty.getEntityDef());
    funcProp.setPropertyFunction(function);
    return funcProp;
  }


}
