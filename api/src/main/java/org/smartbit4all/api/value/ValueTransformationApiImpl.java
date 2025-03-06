package org.smartbit4all.api.value;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.api.value.bean.ValueTransformationConfig;
import org.smartbit4all.api.value.bean.ValueTransformationConfigData;
import org.smartbit4all.api.value.bean.ValueTransformationKind;
import org.smartbit4all.api.value.bean.ValueTransformationMappingItem;
import org.smartbit4all.api.value.bean.ValueTransformationResult;
import org.springframework.beans.factory.annotation.Autowired;

public class ValueTransformationApiImpl implements ValueTransformationApi {

  @Autowired
  private MasterDataManagementApi mdmApi;

  @Override
  public List<ValueTransformationResult> transform(String configName, List<Object> inputValues,
      String mdmType) {
    if (inputValues == null || inputValues.isEmpty()) {
      return Collections.emptyList();
    }
    MDMEntryApi api = mdmApi.getApi(mdmType,
        MDM_ENTRY_VALUETRANSFORMATIONCONFIG);
    ValueTransformationConfig config = api.lookup().findByUnique(
        new ObjectPropertyValue().addPathItem(ValueTransformationConfig.DATA)
            .addPathItem(ValueTransformationConfigData.NAME).value(configName),
        ValueTransformationConfig.class);
    if (config.getData().getKind() == ValueTransformationKind.MAPPING) {
      Map<Object, Object> mapping = config.getData().getMappings().stream()
          .collect(toMap(ValueTransformationMappingItem::getSourceValue,
              ValueTransformationMappingItem::getTargetValue));
      return inputValues.stream().map(o -> {
        Object transformedValue = mapping.get(o);
        return new ValueTransformationResult().sourceValue(o).transformedValue(transformedValue)
            .error(o == null ? "Not found in mapping" : null);
      }).collect(toList());
    }
    return inputValues.stream().map(o -> {
      return new ValueTransformationResult().sourceValue(o).transformedValue(null)
          .error("Tranformation is not defined");
    }).collect(toList());
  }

}
