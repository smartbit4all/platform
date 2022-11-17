package org.smartbit4all.core.object;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ReferenceDefinitionData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;

/**
 * The object reference configuration is a bean to be used to define the references via
 * {@link ObjectReferenceConfigs} among the API domain objects. These configurations are collected
 * by the {@link ObjectApi} via autowired mechanism to summarize and save. Many algorithms can use
 * this meta data.
 * 
 * @author Peter Boros
 */
public class ObjectReferenceConfigs {

  /**
   * The reference configurations.
   */
  private List<ReferenceDefinitionData> configs = new ArrayList<>();

  /**
   * This function is used by the {@link ObjectApi} to summarize all the references in the
   * application.
   * 
   * @return Get all the references defined.
   */
  final List<ReferenceDefinitionData> getConfigs() {
    return configs;
  }

  public ObjectReferenceConfigs ref(Class<?> source, String propertyPath, Class<?> target,
      ReferencePropertyKind propertyKind) {
    return ref(source, propertyPath, target, propertyKind, AggregationKind.NONE);
  }

  /**
   * Adds a new reference to the configuration. To understand the configuration see
   * {@link ReferenceDefinitionData} and its belongings.
   * 
   * @param source Class of the source object.
   * @param propertyPath The path of the property.
   * @param target Class of the target object that is identified by its uri property by default.
   * @return this for fluent use.
   */
  public ObjectReferenceConfigs ref(Class<?> source, String propertyPath, Class<?> target,
      ReferencePropertyKind propertyKind, AggregationKind aggregation) {
    configs.add(new ReferenceDefinitionData()
        .sourceObjectName(source.getName())
        .propertyPath(propertyPath)
        .propertyKind(propertyKind)
        .targetObjectName(target.getName())
        .aggregation(aggregation));
    return this;
  }

}
