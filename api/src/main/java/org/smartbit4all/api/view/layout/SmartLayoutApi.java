package org.smartbit4all.api.view.layout;

import java.util.List;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;

/**
 * The generic helper api for creating {@link SmartLayoutDefinition}s and composite form
 * definitions.
 * 
 * @author Peter Boros
 */
public interface SmartLayoutApi {

  SmartLayoutDefinition createLayout(ObjectDefinitionData objectDefinitionData,
      List<String> propertiesToInclude);

}
