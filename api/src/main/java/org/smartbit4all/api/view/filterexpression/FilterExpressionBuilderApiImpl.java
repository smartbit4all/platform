package org.smartbit4all.api.view.filterexpression;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBoolOperator;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderApiConfig;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldEditor;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;

public class FilterExpressionBuilderApiImpl implements FilterExpressionBuilderApi {

  @Autowired(required = false)
  private ViewApi viewApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private FilterExpressionFieldUiConverter filterExpressionFieldUiConverter;

  @Override
  public FilterExpressionBuilderUiModel performWidgetActionRequest(UUID viewUuid,
      String filterIdentifier,
      UiActionRequest request) {

    FilterExpressionBuilderUiModel model = getModel(viewUuid, filterIdentifier);

    switch (request.getCode()) {
      case FilterExpressionBuilderApiActions.OPEN_FILTER_GROUPS:
        openFilterGroups(model);
        break;
      case FilterExpressionBuilderApiActions.CLOSE_FILTER_GROUPS:
        closeFilterGroups(model);
        break;
      case FilterExpressionBuilderApiActions.ADD_BRACKET:
        addBracket(viewUuid, filterIdentifier, request, model);
        break;
      case FilterExpressionBuilderApiActions.ADD_FILTER_EXPRESSION:
        addFilterExpression(model, request);
        break;
      case FilterExpressionBuilderApiActions.REMOVE_FILTER_EXPRESSION:
        removeFilterExpression(model, request);
        break;
      case FilterExpressionBuilderApiActions.REDO:
        resetFilterWorkspace(model);
        break;
      case FilterExpressionBuilderApiActions.SELECT_FIELD:
        selectField(viewUuid, request, model);
        break;
      case FilterExpressionBuilderApiActions.FILTER_GROUPS:
        break;
      case FilterExpressionBuilderApiActions.UPDATE_FILTER_EXPRESSION:
        updateExpressionData(model, request);
        break;
      case FilterExpressionBuilderApiActions.SAVE_EXPRESSION_FIELD_LIST:
        saveExpressionFiedlList(model, request);
        break;
      default:
        break;
    }

    viewApi.setWidgetModelInView(FilterExpressionBuilderUiModel.class, viewUuid,
        filterIdentifier,
        model);

    return model;
  }

  private void saveExpressionFiedlList(FilterExpressionBuilderUiModel model,
      UiActionRequest request) {
    FilterExpressionBuilderUiModel builderUiModel =
        actionRequestHelper(request).get(UiActions.MODEL, FilterExpressionBuilderUiModel.class);

    model.getModel().getWorkplaceList()
        .setFilters(builderUiModel.getModel().getWorkplaceList().getFilters());

  }

  private FilterExpressionBuilderUiModel getModel(UUID viewUuid, String filterIdentifier) {
    return viewApi
        .getWidgetModelFromView(FilterExpressionBuilderUiModel.class, viewUuid, filterIdentifier);
  }

  @Override
  public FilterExpressionBuilderUiModel createFilterBuilder(FilterExpressionBuilderModel model,
      FilterExpressionBuilderApiConfig config) {
    Objects.requireNonNull(model, "FilterExpressionBuilderModel cannot be null");

    FilterExpressionBuilderUiModel uiModel =
        new FilterExpressionBuilderUiModel().model(model);

    if (Objects.nonNull(uiModel.getModel().getWorkplaceList())) {
      setFilterFieldIds(uiModel.getModel().getWorkplaceList().getFilters());
    }

    if (Objects.isNull(config)) {
      return uiModel;
    }

    uiModel.setReadOnly(config.getReadOnly());
    uiModel.setExtarnalDatabase(config.getExtarnalDatabase());

    if (Boolean.FALSE.equals(config.getReadOnly())) {
      uiModel.possibleActions(config.getAvailableActions().stream()
          .map(code -> new UiAction().code(code)).collect(Collectors.toList()));
    }


    return uiModel;
  }

  private void setFilterFieldIds(List<FilterExpressionField> filterFields) {
    filterFields.forEach(field -> {
      if (Objects.nonNull(field.getSubFieldList())
          && !field.getSubFieldList().getFilters().isEmpty()) {
        setFilterFieldIds(field.getSubFieldList().getFilters());
      }
      field.setId(UUID.randomUUID().toString());
    });
  }

  @Override
  public void initFilterBuilderInView(UUID viewUuid, String filterId,
      FilterExpressionBuilderUiModel model) {
    Objects.requireNonNull(filterId, "FilterId cannot be null");
    Objects.requireNonNull(model, "FilterExpressionBuilderUiModel cannot be null");

    viewApi.setWidgetModelInView(FilterExpressionBuilderUiModel.class, viewUuid, filterId, model);
  }

  @Override
  public FilterExpressionFieldList getFilterExpressionFieldList(UUID viewUuid,
      String filterIdentifier) {
    FilterExpressionBuilderUiModel model = getModel(viewUuid, filterIdentifier);
    FilterExpressionFieldList workplaceList = model
        .getModel().getWorkplaceList();

    viewApi.setWidgetModelInView(FilterExpressionBuilderUiModel.class, viewUuid, filterIdentifier,
        model);

    return workplaceList;
  }

  @Override
  public FilterExpressionBuilderUiModel load(UUID viewUuid, String filterIdentifier) {
    Objects.requireNonNull(viewUuid, "UUID of the page cannot be null");
    Objects.requireNonNull(filterIdentifier, "FilterId cannot be null");
    return getModel(viewUuid, filterIdentifier);
  }

  private void openFilterGroups(FilterExpressionBuilderUiModel model) {
    setFilterGroupsVisibility(model, true);
  }

  private void closeFilterGroups(FilterExpressionBuilderUiModel model) {
    setFilterGroupsVisibility(model, false);
  }

  private void selectField(UUID viewUuid, UiActionRequest request,
      FilterExpressionBuilderUiModel model) {
    ObjectMapHelper params = actionRequestHelper(request);
    FilterExpressionField field = params.get(UiActions.MODEL, FilterExpressionField.class);

    if (Objects.isNull(field.getWidgetType())) {
      model.setSelectedField(null);
      model.setSelectedFieldEditor(null);
    } else {

      model.setSelectedField(field);

      if (field.getExpressionData().getCurrentOperation() != FilterExpressionOperation.EXPRESSION) {
        model.setSelectedFieldEditor(
            new FilterExpressionFieldEditor().layoutDef(createLayoutDef(field))
                .possibleActions(SELECTED_EXPRESSION_ACTIONS));
      } else {
        model.setSelectedFieldEditor(new FilterExpressionFieldEditor()
            .possibleActions(Arrays.asList(REMOVE_FILTER_EXPRESSION_ACTION)));
      }
    }

  }

  private void addBracket(UUID viewUuid, String filterIdentifier, UiActionRequest request,
      FilterExpressionBuilderUiModel model) {
    FilterExpressionField bracketExpression = new FilterExpressionField();
    bracketExpression.setSubFieldList(new FilterExpressionFieldList());
    bracketExpression.setExpressionData(
        new FilterExpressionData().currentOperation(FilterExpressionOperation.EXPRESSION)
            .boolOperator(FilterExpressionBoolOperator.AND));
    bracketExpression.setId(UUID.randomUUID().toString());

    model.getModel().getWorkplaceList().addFiltersItem(bracketExpression);
  }

  private void resetFilterWorkspace(FilterExpressionBuilderUiModel model) {
    model.getModel().getWorkplaceList().getFilters().clear();
  }

  public void removeFilterExpression(FilterExpressionBuilderUiModel model,
      UiActionRequest request) {
    FilterExpressionField field =
        actionRequestHelper(request).get(UiActions.MODEL, FilterExpressionField.class);

    FilterExpressionFieldList updatedList = deepRemove(model.getModel().getWorkplaceList(), field);

    model.setSelectedField(null);
    model.setSelectedFieldEditor(null);
    model.getModel().setWorkplaceList(updatedList);
  }

  public FilterExpressionBuilderUiModel addFilterExpression(FilterExpressionBuilderUiModel model,
      UiActionRequest request) {

    FilterExpressionBuilderField field =
        actionRequestHelper(request).get(UiActions.MODEL, FilterExpressionBuilderField.class);
    field.getFieldTemplate().setId(UUID.randomUUID().toString());

    if (model.getSelectedField() != null
        && model.getSelectedField().getExpressionData().getCurrentOperation()
            .equals(FilterExpressionOperation.EXPRESSION)) {

      Optional<FilterExpressionField> selectedFieldFromWorkplace =
          model.getModel().getWorkplaceList().getFilters().stream()
              .filter(f -> f.equals(model.getSelectedField())).findFirst();
      if (selectedFieldFromWorkplace.isPresent()) {
        selectedFieldFromWorkplace.get().getSubFieldList().addFiltersItem(field.getFieldTemplate());
      }
    } else {
      model.getModel().getWorkplaceList().addFiltersItem(field.getFieldTemplate());
    }
    model.setSelectedField(field.getFieldTemplate());

    return model;

  }

  public void updateExpressionData(FilterExpressionBuilderUiModel model, UiActionRequest request) {
    FilterExpressionField expressionField =
        actionRequestHelper(request).get(UiActions.MODEL, FilterExpressionField.class);
    model.setSelectedField(expressionField);
    model.setSelectedFieldEditor(
        new FilterExpressionFieldEditor().layoutDef(createLayoutDef(expressionField))
            .possibleActions(SELECTED_EXPRESSION_ACTIONS));

    deepSave(model.getModel().getWorkplaceList(), expressionField);
  }

  private FilterExpressionFieldList deepSave(FilterExpressionFieldList filters,
      FilterExpressionField field) {

    filters.getFilters().forEach(filter -> {
      if (filter.getId().equals(field.getId())) {
        int index = filters.getFilters().indexOf(filter);
        filters.getFilters().set(index, field);
        return;
      }

      if (filter.getSubFieldList() != null && !filter.getSubFieldList().getFilters().isEmpty()) {
        deepSave(filter.getSubFieldList(), field);
      }

    });
    return filters;
  }

  private SmartLayoutDefinition createLayoutDef(FilterExpressionField field) {
    return filterExpressionFieldUiConverter.convertToSmartLayoutDefiniton(field);
  }

  private ObjectMapHelper actionRequestHelper(UiActionRequest request) {
    return new ObjectMapHelper(request.getParams(), objectApi, request.getCode()
        + StringConstant.SPACE_HYPHEN_SPACE + request.getIdentifier()
        + StringConstant.SPACE_HYPHEN_SPACE + request.getPath() + " action parameters");
  }

  private void setFilterGroupsVisibility(FilterExpressionBuilderUiModel model, boolean open) {
    model.setShowGroups(open);

    if (open) {
      model.setPossibleActions(UI_ACTIONS_CLOSED);
    } else {
      model.setPossibleActions(UI_ACTIONS_OPENED);
    }
  }

  private FilterExpressionFieldList deepRemove(FilterExpressionFieldList filters,
      FilterExpressionField field) {

    for (int i = 0; i < filters.getFilters().size(); i++) {
      FilterExpressionField filter = filters.getFilters().get(i);
      if (filter.getId().equals(field.getId())) {
        int index = filters.getFilters().indexOf(filter);
        filters.getFilters().remove(index);
        break;
      }
      if (filter.getSubFieldList() != null && !filter.getSubFieldList().getFilters().isEmpty()) {
        deepRemove(filter.getSubFieldList(), field);
      }
    }
    return filters;
  }
}
