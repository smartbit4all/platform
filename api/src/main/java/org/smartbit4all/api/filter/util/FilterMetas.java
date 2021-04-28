package org.smartbit4all.api.filter.util;

import java.util.Arrays;
import java.util.Objects;
import org.smartbit4all.api.filter.bean.FilterFieldMeta;
import org.smartbit4all.api.filter.bean.FilterGroupMeta;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

public class FilterMetas {

  public static FilterGroupMeta createFilterGroupMeta(String labelCode, String iconCode) {
    FilterGroupMeta group = new FilterGroupMeta();
    group.setId(labelCode); // TODO other ID?
    group.setLabelCode(labelCode);
    group.setIconCode(iconCode);
    return group;
  }
  
  public static FilterFieldMeta createSimpleFieldMeta(Property<?> property, String labelCode,
      SimpleOperationFactory... operationFactories) {
    Objects.requireNonNull(property);
    Objects.requireNonNull(labelCode);
    Objects.requireNonNull(operationFactories);
    
    FilterFieldMeta filter = new FilterFieldMeta();
    filter.setLabelCode(labelCode);
    Arrays.stream(operationFactories).forEach(f -> filter.addOperationsItem(f.create(property)));
    return filter;
  }
  
  public static FilterFieldMeta createMultiFieldMeta(Property<?> property, String labelCode, String possibleValueCode,
      MultiOperationFactory... operationFactories) {
    Objects.requireNonNull(property);
    Objects.requireNonNull(labelCode);
    Objects.requireNonNull(possibleValueCode);
    Objects.requireNonNull(operationFactories);
    
    FilterFieldMeta filter = new FilterFieldMeta();
    filter.setLabelCode(labelCode);
    Arrays.stream(operationFactories).forEach(f -> filter.addOperationsItem(f.create(property, possibleValueCode)));
    return filter;
  }
  
  public static FilterFieldMeta createSimpleDetFieldMeta(Property<?> property, String labelCode, 
      EntityDefinition masterEntity, EntityDefinition fkEntity,
      SimpleDetailOperationFactory... operationFactories) {
    Objects.requireNonNull(property);
    Objects.requireNonNull(labelCode);
    Objects.requireNonNull(masterEntity);
    Objects.requireNonNull(fkEntity);
    Objects.requireNonNull(operationFactories);
    
    FilterFieldMeta filter = new FilterFieldMeta();
    filter.setLabelCode(labelCode);
    Arrays.stream(operationFactories).forEach(f -> filter
        .addOperationsItem(f.create(property, masterEntity, fkEntity)));
    return filter;
  }
  
  public static FilterFieldMeta createMultiDetFieldMeta(Property<?> property, String labelCode, 
      String possibleValueCode, EntityDefinition masterEntity, EntityDefinition fkEntity,
      MultiDetailOperationFactory... operationFactories) {
    Objects.requireNonNull(property);
    Objects.requireNonNull(labelCode);
    Objects.requireNonNull(masterEntity);
    Objects.requireNonNull(fkEntity);
    Objects.requireNonNull(possibleValueCode);
    Objects.requireNonNull(operationFactories);
    
    FilterFieldMeta filter = new FilterFieldMeta();
    filter.setLabelCode(labelCode);
    Arrays.stream(operationFactories).forEach(f -> filter
        .addOperationsItem(f.create(property, possibleValueCode, masterEntity, fkEntity)));
    return filter;
  }
  
  public static interface SimpleOperationFactory {
    FilterOperation create(Property<?> property);
  }
  
  public static interface MultiOperationFactory {
    FilterOperation create(Property<?> property, String possibleValueCode);
  }
  
  public static interface SimpleDetailOperationFactory {
    FilterOperation create(Property<?> property, EntityDefinition entityMaster,
        EntityDefinition entityFk);
  }
  
  public static interface MultiDetailOperationFactory {
    FilterOperation create(Property<?> property, String possibleValueCode,
        EntityDefinition entityMaster, EntityDefinition entityFk);
  }
  
}
