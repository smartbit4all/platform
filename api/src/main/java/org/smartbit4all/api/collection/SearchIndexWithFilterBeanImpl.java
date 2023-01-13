package org.smartbit4all.api.collection;

import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.PropertyMeta;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinitionBuilder;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.domain.utility.crud.CrudRead;

public class SearchIndexWithFilterBeanImpl<O, F> extends SearchIndexImpl<O>
    implements SearchIndexWithFilterBean<O, F> {

  private ObjectDefinition<F> filterDefinition;

  private Class<F> filterDefinitionClass;

  public SearchIndexWithFilterBeanImpl(String logicalSchema, String name,
      Class<F> filterDefinitionClass, String indexedObjectSchema,
      Class<O> indexedObjectDefinitionClass) {
    super(logicalSchema, name, indexedObjectSchema, indexedObjectDefinitionClass);
    this.filterDefinitionClass = filterDefinitionClass;
  }

  @Override
  public EntityDefinition constructDefinition() {
    EntityDefinitionBuilder builder = EntityDefinitionBuilder.of(ctx)
        .name(name())
        .domain(logicalSchema());

    Map<String, PropertyMeta> properties = filterDefinition().meta().getProperties();

    for (String propertyName : pathByPropertyName.keySet()) {
      PropertyMeta propertyMeta = properties.get(propertyName.toUpperCase());
      if (propertyMeta != null) {
        builder.ownedProperty(propertyMeta.getName(), propertyMeta.getType());
      }
    }

    return builder.build();
  }

  private ObjectDefinition<F> filterDefinition() {
    if (filterDefinition == null) {
      filterDefinition = objectApi.definition(filterDefinitionClass);
    }
    return filterDefinition;
  }

  @Override
  public TableData<?> executeSearch(F filterObject) {
    QueryInput input;
    CrudRead<EntityDefinition> read = Crud.read(getDefinition()).selectAllProperties();
    Expression exp = null;
    for (Entry<String, PropertyMeta> entry : filterDefinition().meta().getProperties().entrySet()) {
      // If the bean property is not null.
      PropertyMeta propertyMeta = entry.getValue();
      Object value = propertyMeta.getValue(filterObject);
      Expression currentExp = null;
      if (value != null) {
        Property<?> property = getDefinition().getProperty(propertyMeta.getName());
        CustomExpressionMapping customExpressionMapping =
            expressionByPropertyName.get(propertyMeta.getName());
        if (customExpressionMapping != null) {
          if (customExpressionMapping.complexExpressionProcessor != null) {
            currentExp = customExpressionMapping.complexExpressionProcessor
                .apply(value, getDefinition()).BRACKET();
          } else if (customExpressionMapping.expressionProcessor != null && property != null) {
            currentExp = customExpressionMapping.expressionProcessor.apply(value, property);
          }
        } else if (property != null) {
          currentExp = createDynamicExpression(propertyMeta.getType(), property, value);
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
