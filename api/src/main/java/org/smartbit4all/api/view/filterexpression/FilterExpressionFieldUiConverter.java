package org.smartbit4all.api.view.filterexpression;

import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;

public interface FilterExpressionFieldUiConverter {
  SmartLayoutDefinition convertToSmartLayoutDefiniton(FilterExpressionField field);
}
