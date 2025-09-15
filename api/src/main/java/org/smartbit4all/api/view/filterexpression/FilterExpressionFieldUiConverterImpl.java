package org.smartbit4all.api.view.filterexpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldWidgetType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.formdefinition.bean.TextFieldProperties;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class FilterExpressionFieldUiConverterImpl implements FilterExpressionFieldUiConverter {

  public static final String EXPRESSION_DATA_OPERAND2 =
      "expressionData.operand2.valueAsString";

  public static final String EXPRESSION_DATA_SELECTEDVALUES =
      "expressionData.operand2.selectedValues";

  public static final String EXPRESSION_DATA_SELECTEDOBJECTS =
      "expressionData.operand2.selectedObjects";

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public SmartLayoutDefinition convertToSmartLayoutDefiniton(FilterExpressionField field) {
    SmartLayoutDefinition layoutDefinition = new SmartLayoutDefinition();
    if (field.getWidgetType() != null) {

      if (field.getWidgetType().equals(FilterExpressionFieldWidgetType.RANGE) || (field
          .getExpressionData().getCurrentOperation() != null && field
              .getExpressionData().getCurrentOperation()
              .equals(FilterExpressionOperation.BETWEEN))) {
        layoutDefinition.widgets(convertRangeFilter(field));
      } else if (field.getWidgetType().equals(FilterExpressionFieldWidgetType.SELECT)
          || field.getWidgetType().equals(FilterExpressionFieldWidgetType.RADIO_BUTTON)) {
        layoutDefinition.widgets(convertSelectFilter(field));
      } else if (field.getWidgetType().equals(FilterExpressionFieldWidgetType.SELECT_MULTIPLE)) {
        layoutDefinition.widgets(convertSelectMultipleFilter(field));
      } else if (FilterExpressionFieldWidgetType.TEXT_FIELD_LOOKUP == field.getWidgetType()) {
        layoutDefinition.widgets(convertLookupFilter(field));
      } else if (FilterExpressionFieldWidgetType.TEXT_FIELD == field.getWidgetType()) {
        layoutDefinition.widgets(convertTextField(field));
      } else {
        layoutDefinition.addWidgetsItem(
            new SmartWidgetDefinition()
                .key(EXPRESSION_DATA_OPERAND2)
                .type(getLayoutTypeFromField(field))
                .label(field.getLabel()));
      }
      layoutDefinition.addWidgetsItem(getPossibleOperations(field));
    }
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
        .label(field.getLabel2() != null ? field.getLabel2() : field.getLabel()),
        new SmartWidgetDefinition()
            .key("expressionData.operand3.valueAsString")
            .type(getLayoutTypeFromField(field))
            .label(field.getLabel3() != null ? field.getLabel3() : field.getLabel())));
  }

  private List<SmartWidgetDefinition> convertSelectMultipleFilter(FilterExpressionField field) {

    List<SmartWidgetDefinition> result = new ArrayList<>();
    result.add(new SmartWidgetDefinition()
        .key(EXPRESSION_DATA_SELECTEDVALUES)
        .type(getSelectLayoutTypeFromField(field))
        .label(field.getLabel())
        .values(field.getPossibleValues()));
    return result;
  }

  private List<SmartWidgetDefinition> convertSelectFilter(FilterExpressionField field) {

    List<SmartWidgetDefinition> result = new ArrayList<>();
    result.add(new SmartWidgetDefinition()
        .key(EXPRESSION_DATA_OPERAND2)
        .type(getSelectLayoutTypeFromField(field))
        .label(field.getLabel())
        .values(field.getPossibleValues()));
    return result;
  }

  private List<SmartWidgetDefinition> convertTextField(final FilterExpressionField field) {
    List<SmartWidgetDefinition> result = new ArrayList<>();

    field.getExpressionData().getOperand2().getSelectedObjects();
    result.add(ObjectLayoutBuilder.textfield(
        EXPRESSION_DATA_OPERAND2,
        field.getLabel(), new TextFieldProperties().clearable(true)));
    return result;
  }

  private List<SmartWidgetDefinition> convertLookupFilter(final FilterExpressionField field) {
    List<SmartWidgetDefinition> result = new ArrayList<>();

    field.getExpressionData().getOperand2().getSelectedObjects();
    result.add(new SmartWidgetDefinition()
        .key(EXPRESSION_DATA_SELECTEDOBJECTS)
        .type(SmartFormWidgetType.TEXT_FIELD_LOOKUP)
        .label(field.getLabel())
        .toolbarId(field.getToolbarId()));
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
