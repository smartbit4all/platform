package org.smartbit4all.core.object;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.object.bean.ObjectReferenceDefinitionData;
import org.smartbit4all.api.object.bean.PropertyKindEnum;
import org.smartbit4all.api.object.bean.ReferenceDefinitionData;

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

  /**
   * Adds a new reference to the configuration. To understand the configuration see
   * {@link ReferenceDefinitionData} and its belongings.
   * 
   * @param sourceObjectName The name of the source object.
   * @param referPropertyPath The path of the property.
   * @param kind The kind of the property.
   * @param targetObjectName The target object that is identified by its uri property by default.
   * @param referredPropertyPath The path of the property in the target object. It is the URI of the
   *        object in general.
   * @return This because it is a builder api.
   */
  public ObjectReferenceConfigs ref(String sourceObjectName, String referPropertyPath,
      PropertyKindEnum kind, String targetObjectName, String referredPropertyPath) {
    configs.add(new ReferenceDefinitionData().source(new ObjectReferenceDefinitionData()
        .objectName(sourceObjectName).propertyPath(referPropertyPath).propertyKind(kind))
        .target(new ObjectReferenceDefinitionData().objectName(targetObjectName)
            .propertyKind(PropertyKindEnum.PROPERTY).propertyPath(referredPropertyPath)));
    return this;
  }

}
