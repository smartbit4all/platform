package org.smartbit4all.api.collection;

import java.util.Map.Entry;
import org.smartbit4all.core.object.PropertyMeta;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.domain.utility.crud.CrudRead;

public class SearchIndexWithFilterBeanImpl<O, F> extends SearchIndexImpl<O>
    implements SearchIndexWithFilterBean<O, F> {

  public SearchIndexWithFilterBeanImpl(String logicalSchema, String name,
      Class<F> filterDefinitionClass, String indexedObjectSchema,
      Class<O> indexedObjectDefinitionClass) {
    this(logicalSchema, name, filterDefinitionClass, indexedObjectSchema,
        indexedObjectDefinitionClass, false);
  }

  public SearchIndexWithFilterBeanImpl(String logicalSchema, String name,
      Class<F> filterDefinitionClass, String indexedObjectSchema,
      Class<O> indexedObjectDefinitionClass, boolean useDatabase) {
    super(logicalSchema, name, indexedObjectSchema, indexedObjectDefinitionClass, useDatabase);
    objectMapping.filterClass = filterDefinitionClass;
  }

  @Override
  public TableData<?> executeSearch(F filterObject) {
    CrudRead<EntityDefinition> read = Crud.read(getDefinition().definition).selectAllProperties();
    Expression exp = null;
    for (Entry<String, PropertyMeta> entry : objectMapping.getTypeMeta().getProperties()
        .entrySet()) {
      // If the bean property is not null.
      PropertyMeta propertyMeta = entry.getValue();
      Object value = propertyMeta.getValue(filterObject);
      Expression currentExp = null;
      if (value != null) {
        Property<?> property = getDefinition().definition.getProperty(propertyMeta.getName());
        CustomExpressionMapping customExpressionMapping =
            expressionByPropertyName.get(propertyMeta.getName());
        if (customExpressionMapping != null) {
          if (customExpressionMapping.complexExpressionProcessor != null) {
            currentExp = customExpressionMapping.complexExpressionProcessor
                .apply(value, getDefinition().definition).BRACKET();
          } else if (customExpressionMapping.expressionProcessor != null && property != null) {
            currentExp = customExpressionMapping.expressionProcessor.apply(value, property);
          } else if (customExpressionMapping.detailExpressionProcessor != null) {
            currentExp =
                customExpressionMapping.detailExpressionProcessor.apply(value, objectMapping);
          }
        } else if (property != null) {
          Property<Object> propertyObject =
              getDefinition().definition.getPropertyObject(propertyMeta.getName());
          currentExp = propertyObject.eq(value);
        }
        if (exp == null) {
          exp = currentExp;
        } else if (currentExp != null) {
          exp = exp.AND(currentExp);
        }
      }
    }
    if (exp != null) {
      read.where(exp);
    }
    return executeSearch(read.getQuery());
  }

}
