package org.smartbit4all.api.view.filterexpression;

import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.Value;
import org.springframework.beans.factory.annotation.Autowired;

public class FilterExpressionFieldUiConverterImpl implements FilterExpressionFieldUiConverter {

  public static final String EXPRESSION_DATA_OPERAND2 =
      "expressionData.operand2.valueAsString";

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public SmartLayoutDefinition convertToSmartLayoutDefiniton(FilterExpressionField field) {
    SmartLayoutDefinition layoutDefinition = new SmartLayoutDefinition();

    if (field.getWidgetType().equals(FilterExpressionFieldWidgetType.RANGE)) {
      layoutDefinition.widgets(convertRangeFilter(field));
    } else if (field.getWidgetType().equals(FilterExpressionFieldWidgetType.SELECT)
        || field.getWidgetType().equals(FilterExpressionFieldWidgetType.SELECT_MULTIPLE)) {
      layoutDefinition.widgets(convertSelectFilter(field));
    } else {
      layoutDefinition.addWidgetsItem(
          new SmartWidgetDefinition()
              .key(EXPRESSION_DATA_OPERAND2)
              .type(getLayoutTypeFromField(field))
              .label(field.getLabel())
              .placeholder(field.getLabel()));
    }
    layoutDefinition.addWidgetsItem(getPossibleOperations(field));
    return layoutDefinition;
  }

  private SmartFormWidgetType getLayoutTypeFromField(FilterExpressionField field) {

    switch (field.getFilterFieldType()) {
      case NUMBER:
        return SmartFormWidgetType.TEXT_FIELD_NUMBER;
      case STRING:
        return SmartFormWidgetType.TEXT_FIELD;
      case DATE:
        return SmartFormWidgetType.DATE_PICKER;
      case DATE_TIME:
        return SmartFormWidgetType.DATE_TIME_PICKER;
      default:
        return SmartFormWidgetType.fromValue(field.getWidgetType().getValue());
    }


  }

  private SmartFormWidgetType getSelectLayoutTypeFromField(FilterExpressionField field) {
    if (field.getWidgetType() == null) {
      return getLayoutTypeFromField(field);
    }
    return SmartFormWidgetType.fromValue(field.getWidgetType().getValue());
  }

  private List<SmartWidgetDefinition> convertRangeFilter(FilterExpressionField field) {
    return new ArrayList<>(Arrays.asList(new SmartWidgetDefinition()
        .key(EXPRESSION_DATA_OPERAND2)
        .type(getLayoutTypeFromField(field))
        .label(field.getLabel())
        .placeholder(field.getLabel()),
        new SmartWidgetDefinition()
            .key("expressionData.operand3.valueAsString")
            .type(getLayoutTypeFromField(field))
            .label(field.getLabel())
            .placeholder(field.getLabel())));
  }

  private List<SmartWidgetDefinition> convertSelectFilter(FilterExpressionField field) {

    List<SmartWidgetDefinition> result = new ArrayList<>();
    result.add(new SmartWidgetDefinition()
        .key(EXPRESSION_DATA_OPERAND2)
        .type(getSelectLayoutTypeFromField(field))
        .label(field.getLabel())
        .placeholder(field.getLabel())
        .values(field.getPossibleValues()));
    return result;
  }

  private SmartWidgetDefinition getPossibleOperations(FilterExpressionField field) {
    return new SmartWidgetDefinition()
        .key("expressionData.currentOperation")
        .label("Művelet")
        .type(SmartFormWidgetType.SELECT)
        .values(field.getPossibleOperations().stream().map(
            op -> new Value().code(op.getValue()).displayValue(localeSettingApi.get(op.getValue())))
            .collect(toList()));
  }
}
