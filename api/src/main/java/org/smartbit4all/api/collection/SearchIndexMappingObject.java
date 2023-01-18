package org.smartbit4all.api.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.SearchEntityDefinition.DetailDefinition;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBoolOperator;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.core.object.BeanMeta;
import org.smartbit4all.core.object.BeanMetaUtil;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.PropertyMeta;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinitionBuilder;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.JoinPath;
import org.smartbit4all.domain.meta.PropertyObject;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.springframework.context.ApplicationContext;

public class SearchIndexMappingObject extends SearchIndexMapping {

  String logicalSchema;

  String name;

  /**
   * The path of the detail property that contains a list / map, referring to another object.
   */
  String[] path;

  /**
   * {@link LinkedHashMap} to preserve the parameterization order in the entity definition.
   */
  Map<String, SearchIndexMapping> mappingsByPropertyName = new LinkedHashMap<>();

  /**
   * The filter class is not necessary. If we have this then we can set the type of the given
   * property based on the property of this class.
   */
  Class<?> filterClass;

  /**
   * The meta of the filter class.
   */
  BeanMeta filterClassMeta;

  /**
   * The joined properties.
   */
  List<String[]> masterJoin = new ArrayList<>();

  /**
   * The name of the master reference.
   */
  String masterReferenceName;

  /**
   * The mapping is always points to an entity in the database side. This entity can be predefined
   * but if necessary then constructed dynamically even. We must be prepared to modify the entity
   * runtime if the setup of the search index has been changed.
   */
  SearchEntityDefinition entityDefinition;

  ApplicationContext ctx;

  EntityManager entityManager;

  SearchIndexMappingProperty property(String name) {
    return (SearchIndexMappingProperty) mappingsByPropertyName.get(name);
  }

  private final Class<?> getType(String propertyName) {
    BeanMeta meta = getTypeMeta();
    if (meta != null) {
      PropertyMeta propertyMeta = meta.getProperties().get(propertyName.toUpperCase());
      if (propertyMeta != null) {
        return propertyMeta.getType();
      }
    }
    return null;
  }

  final synchronized BeanMeta getTypeMeta() {
    if (filterClass != null) {
      if (filterClassMeta == null) {
        filterClassMeta = BeanMetaUtil.meta(filterClass);
      }
      return filterClassMeta;
    }
    return null;
  }

  public SearchIndexMappingObject map(String propertyName, String... pathes) {
    Objects.requireNonNull(pathes);
    if (pathes.length > 0) {

      mappingsByPropertyName.put(propertyName,
          new SearchIndexMappingProperty(propertyName, pathes, getType(propertyName), null, null));
    }
    return this;
  }

  public SearchIndexMappingObject map(String propertyName,
      UnaryOperator<Object> processor,
      String... pathes) {
    Objects.requireNonNull(pathes);
    if (pathes.length > 0) {
      mappingsByPropertyName.put(propertyName,
          new SearchIndexMappingProperty(propertyName, pathes, getType(propertyName), processor,
              null));
    }
    return this;
  }

  public SearchIndexMappingObject mapComplex(String propertyName,
      Function<ObjectNode, Object> complexProcessor) {
    Objects.requireNonNull(complexProcessor);
    mappingsByPropertyName.put(propertyName,
        new SearchIndexMappingProperty(propertyName, null, getType(propertyName), null,
            complexProcessor));
    return this;
  }

  public SearchIndexMappingObject detail(String propertyName, String uniqueIdName) {
    String masterReferenceQualified = propertyName + "parent";
    SearchIndexMappingObject detail =
        new SearchIndexMappingObject().master(masterReferenceQualified, masterReferenceQualified,
            uniqueIdName);
    mappingsByPropertyName.put(propertyName,
        detail);
    return detail;
  }

  public synchronized SearchEntityDefinition getDefinition() {
    if (entityDefinition == null) {
      entityDefinition = constructDefinition(ctx, null);
      allDefinitions(entityDefinition).forEach(e -> entityManager.registerEntityDef(e));
    }
    return entityDefinition;
  }

  private final Stream<EntityDefinition> allDefinitions(
      SearchEntityDefinition searchEntityDefinition) {
    return Stream.concat(Stream.of(searchEntityDefinition.definition),
        searchEntityDefinition.detailsByName.values().stream()
            .flatMap(d -> allDefinitions(d.detail)));
  }

  SearchEntityDefinition constructDefinition(ApplicationContext ctx,
      EntityDefinitionBuilder masterBuilder) {
    EntityDefinitionBuilder builder = EntityDefinitionBuilder.of(ctx)
        .name(name)
        .domain(logicalSchema);

    if (entityDefinition != null) {
      return entityDefinition;
    }

    SearchEntityDefinition result = new SearchEntityDefinition();

    for (Entry<String, SearchIndexMapping> entry : mappingsByPropertyName.entrySet()) {
      if (entry.getValue() instanceof SearchIndexMappingProperty) {
        SearchIndexMappingProperty property = (SearchIndexMappingProperty) entry.getValue();
        builder.ownedProperty(property.name, property.type);
      }
    }

    // When all the owned properties exists.

    // We must refer to the master if any
    if (masterBuilder != null) {
      builder.reference(masterReferenceName, masterBuilder, masterJoin);
      result.masterRef = builder.getReference(masterReferenceName);
    }

    // We construct all the details.
    for (Entry<String, SearchIndexMapping> entry : mappingsByPropertyName.entrySet()) {
      if (entry.getValue() instanceof SearchIndexMappingObject) {
        // We must setup a detail referring to this entity as master. We won't create indirect
        // master join so we have only one reference in the join list.
        SearchEntityDefinition detailDefinition =
            ((SearchIndexMappingObject) entry.getValue()).constructDefinition(ctx, builder);
        result.detailsByName.put(entry.getKey(), new DetailDefinition(
            detailDefinition, new JoinPath(Arrays.asList(detailDefinition.masterRef))));
      }
    }

    return result.definition(builder.build());

  }

  public final SearchIndexMappingObject filterClass(Class<?> filterClass) {
    this.filterClass = filterClass;
    return this;
  }

  final SearchIndexMappingObject master(String masterReferenceName,
      String sourcePropertyName, String targetPropertyName) {
    this.masterReferenceName = masterReferenceName;
    this.masterJoin = new ArrayList<>();
    this.masterJoin.add(new String[] {sourcePropertyName, targetPropertyName});
    return this;
  }

  final Expression constructExpression(FilterExpressionList filterExpressions) {
    Expression currentExpression = null;
    FilterExpressionData prevFed = null;
    for (FilterExpressionData fed : filterExpressions.getExpressions()) {
      // Construct the Expression from the FilterExpressionData
      Expression exp = convertFilterExpression(fed);
      if (currentExpression != null && prevFed != null) {
        if (prevFed.getBoolOperator() == FilterExpressionBoolOperator.AND) {
          currentExpression = currentExpression.AND(exp);
        } else {
          currentExpression = currentExpression.OR(exp);
        }
      } else {
        currentExpression = exp;
      }
      prevFed = fed;
    }
    return currentExpression;
  }

  private final Expression convertFilterExpression(FilterExpressionData fed) {
    switch (fed.getCurrentOperation()) {
      case BETWEEN:
        break;
      case EQUAL:
        PropertyObject property =
            entityDefinition.definition.getPropertyObject(fed.getOperand1().getValueAsString());
        return property.eq(fed.getOperand2().getValueAsString());
      case EXISTS:
        break;
      case EXPRESSION:
        break;
      case GREATER:
        break;
      case GREATER_OR_EQUAL:
        break;
      case IS_EMPTY:
        break;
      case IS_NOT_EMPTY:
        break;
      case LESS:
        break;
      case LESS_OR_EQUAL:
        break;
      case LIKE:
        break;
      case NOT_BETWEEN:
        break;
      case NOT_EQUAL:
        break;
      case NOT_EXISTS:
        break;
      case NOT_LIKE:
        break;
      default:
        break;

    }
    return null;
  }

}
