package org.smartbit4all.api.value;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.value.bean.ValueTransformationConfig;
import org.smartbit4all.api.value.bean.ValueTransformationResult;

/**
 * The value ransformation api is based on the {@link ValueTransformationConfig} that can be managed
 * in an MDM entry. In this way the transformation can be configured in the settings of the
 * application.
 * 
 * @author Peter Boros
 */
public interface ValueTransformationApi {

  String MDM_ENTRY_VALUETRANSFORMATIONCONFIG = "VALUETRANSFORMATIONCONFIG";

  /**
   * Execute the transformation based on the configuration that must be set before in the MDM entry
   * of the value transformations.
   * 
   * @param configName The name of the configuration. If it is missing then an
   *        {@link IllegalArgumentException} is thrown.
   * @param inputValues The input values where we get back the
   * @return The transformed values in very the same order then the input parameter list.
   */
  List<ValueTransformationResult> transform(String configName, List<Object> inputValues);

  /**
   * Execute the transformation based on the configuration that must be set before in the MDM entry
   * of the value transformations.
   * 
   * @param configName The name of the configuration. If it is missing then an
   *        {@link IllegalArgumentException} is thrown.
   * @param inputValue The input values where we get back the
   * @return The transformed value.
   */
  default ValueTransformationResult transform(String configName, Object inputValue) {
    List<Object> param = new ArrayList<>();
    param.add(inputValue);
    return transform(configName, param).get(0);
  }

}
