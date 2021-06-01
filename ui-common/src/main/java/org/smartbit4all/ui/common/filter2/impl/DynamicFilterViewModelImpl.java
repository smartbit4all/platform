package org.smartbit4all.ui.common.filter2.impl;

import static org.smartbit4all.api.filter.DateConverter.PREFIX_STRING;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.smartbit4all.api.filter.DateConverter;
import org.smartbit4all.api.filter.FilterApi;
import org.smartbit4all.api.filter.bean.FilterConfig;
import org.smartbit4all.api.filter.bean.FilterConfigMode;
import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterFieldMeta;
import org.smartbit4all.api.filter.bean.FilterGroup;
import org.smartbit4all.api.filter.bean.FilterGroupMeta;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.value.ValueApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.api.filter.DynamicFilterViewModel;
import org.smartbit4all.ui.api.filter.model.DynamicFilterModel;
import org.smartbit4all.ui.api.filter.model.FilterFieldLabel;
import org.smartbit4all.ui.api.filter.model.FilterFieldModel;
import org.smartbit4all.ui.api.filter.model.FilterFieldSelectorModel;
import org.smartbit4all.ui.api.filter.model.FilterGroupLabel;
import org.smartbit4all.ui.api.filter.model.FilterGroupModel;
import org.smartbit4all.ui.api.filter.model.FilterGroupSelectorModel;
import org.smartbit4all.ui.api.filter.model.FilterLabelPosition;
import com.google.common.base.Strings;

public class DynamicFilterViewModelImpl extends ObjectEditingImpl
    implements DynamicFilterViewModel {

  private ObservableObjectImpl dynamicFilterModelObservable;

  /**
   * Wrapper of model.
   */
  private DynamicFilterModel dynamicFilterModel;

  private FilterGroupViewModelImpl rootFilterGroupViewModel;

  private FilterApi filterApi;
  private String filterConfigUri;
  private ValueApi valueApi;

  private FilterConfigMode filterConfigMode;

  public DynamicFilterViewModelImpl(FilterApi api, FilterConfigMode filterConfigMode,
      ValueApi valueApi) {
    this.filterApi = api;
    this.valueApi = valueApi;
    dynamicFilterModelObservable = new ObservableObjectImpl();
    DynamicFilterModel dynamicFilterModel = new DynamicFilterModel();
    dynamicFilterModel.setFilterConfigMode(filterConfigMode);
    ref = new ApiObjectRef(null, dynamicFilterModel, ViewModelHelper.getFilterDescriptors());
    dynamicFilterModelObservable.setRef(ref);
    this.dynamicFilterModel = ref.getWrapper(DynamicFilterModel.class);
  }

  @Override
  public void initModel(String uri) {
    this.filterConfigUri = uri;
    filterConfigMode = dynamicFilterModel.getFilterConfigMode();
    boolean isRootVisible = filterConfigMode == FilterConfigMode.DYNAMIC;
    boolean isChildGroupAllowed = filterConfigMode == FilterConfigMode.DYNAMIC;
    boolean isGroupTypeChangeEnabled = filterConfigMode == FilterConfigMode.DYNAMIC;
    FilterGroupModel root = new FilterGroupModel();
    root.setRoot(Boolean.TRUE);
    root.setGroupType(FilterGroupType.AND);
    root.setVisible(isRootVisible);
    root.setChildGroupAllowed(isChildGroupAllowed);
    root.setGroupTypeChangeEnabled(isGroupTypeChangeEnabled);

    dynamicFilterModel.setRoot(root);
    rootFilterGroupViewModel = new FilterGroupViewModelImpl();
    rootFilterGroupViewModel.setRef(ref);

    FilterConfig filterConfig = filterApi.getFilterConfig(filterConfigUri);
    for (FilterGroupMeta groupMeta : filterConfig.getFilterGroupMetas()) {
      FilterGroupSelectorModel groupSelector = createFilterGroupSelectorByMeta(groupMeta);
      for (FilterFieldMeta filterMeta : groupMeta.getFilterFieldMetas()) {
        FilterFieldSelectorModel filterSelector = createFilterFieldSelectorByMeta(filterMeta);
        groupSelector.getFilters().add(filterSelector);
      }
      if (filterConfigMode == FilterConfigMode.STATIC) {
        FilterGroupModel group = createFilterGroupBySelectorGroup(groupSelector);
        for (FilterFieldSelectorModel filterSelector : groupSelector.getFilters()) {
          FilterFieldModel filter = createFilterFieldFromSelector(filterSelector, null);
          filterSelector.setEnabled(Boolean.FALSE);
          group.getFilters().add(filter);
        }
        dynamicFilterModel.getRoot().getGroups().add(group);
      } else {
        dynamicFilterModel.getSelectors().add(groupSelector);
      }
    }

    dynamicFilterModelObservable.notifyListeners();
  }

  private FilterFieldSelectorModel createFilterFieldSelectorByMeta(FilterFieldMeta filterMeta) {
    FilterFieldSelectorModel filterSelector = new FilterFieldSelectorModel();
    filterSelector.setEnabled(Boolean.TRUE);
    filterSelector.setLabelCode(filterMeta.getLabelCode());
    filterSelector.setIconCode(filterMeta.getIconCode());
    filterSelector.setStyle(filterMeta.getStyle());
    filterSelector.setOperations(filterMeta.getOperations());
    return filterSelector;
  }

  private FilterGroupSelectorModel createFilterGroupSelectorByMeta(FilterGroupMeta groupMeta) {
    FilterGroupSelectorModel groupSelector = new FilterGroupSelectorModel();
    groupSelector.setLabelCode(groupMeta.getLabelCode());
    groupSelector.setIconCode(groupMeta.getIconCode());
    groupSelector.setGroupType(groupMeta.getType());
    groupSelector.setVisible(Boolean.TRUE);
    return groupSelector;
  }

  @Override
  public ObservableObject dynamicFilterModel() {
    return dynamicFilterModelObservable;
  }

  @Override
  public void executeCommand(String commandPath, String command, Object... params) {
    if (commandPath.toUpperCase().startsWith("ROOT")) {
      // override filtergroup, filterfield actions
      switch (command) {
        case "CLOSE_FILTERGROUP":
          rootFilterGroupViewModel.executeCommandWithoutNotify(commandPath, command, params);
          if (filterConfigMode == FilterConfigMode.SIMPLE_DYNAMIC) {
            removeFilterGroupFromSelectorGroups(commandPath);
          }
          break;

        case "CLOSE_FILTERFIELD":
          if (filterConfigMode == FilterConfigMode.SIMPLE_DYNAMIC) {
            enableSelectorForFilter(commandPath);
          }
          rootFilterGroupViewModel.executeCommandWithoutNotify(commandPath, command, params);
          if (filterConfigMode == FilterConfigMode.SIMPLE_DYNAMIC) {
            removeGroupIfEmpty(commandPath);
          }
          break;

        default:
          rootFilterGroupViewModel.executeCommandWithoutNotify(commandPath, command, params);
          break;
      }
    } else {
      switch (command) {
        case "CREATE_FILTER":
          int pathSize = PathUtility.getPathSize(commandPath);
          if (pathSize != 4) {
            throw new IllegalArgumentException("CREATE_FILTER pathSize should be 4!");
          }
          FilterGroupModel group = createOrFindFilterGroupByPath(commandPath);
          FilterFieldModel filterField = createFilterFieldFromSelectorPath(commandPath);
          group.getFilters().add(filterField);
          break;

        case "SELECTOR_DROPPED":
          checkParamNumber(command, 1, params);
          FilterFieldModel filter = createFilterFieldFromSelectorPath((String) params[0]);
          FilterGroupModel groupDropped =
              ref.getValueRefByPath(commandPath).getWrapper(FilterGroupModel.class);
          groupDropped.getFilters().add(filter);

          break;
        default:
          super.executeCommand(commandPath, command, params);
          break;
      }
    }
    dynamicFilterModelObservable.notifyListeners();
  }

  private void removeGroupIfEmpty(String filterPath) {
    String groupPath = PathUtility.getParentPath(PathUtility.getParentPath(filterPath));
    FilterGroupModel group =
        ref.getValueRefByPath(groupPath).getWrapper(FilterGroupModel.class);
    if (group.getRoot() != Boolean.TRUE
        && (group.getFilters() == null || group.getFilters().isEmpty())) {
      executeCommand(groupPath, "CLOSE_FILTERGROUP");
    }
  }

  private void enableSelectorForFilter(String filterPath) {
    FilterFieldModel filter = ref.getValueRefByPath(filterPath).getWrapper(FilterFieldModel.class);
    String selectorPath = filter.getSelectorId();
    if (!Strings.isNullOrEmpty(selectorPath)) {
      FilterFieldSelectorModel selector =
          ref.getValueRefByPath(selectorPath).getWrapper(FilterFieldSelectorModel.class);
      selector.setEnabled(Boolean.TRUE);
    }
  }

  private void removeFilterGroupFromSelectorGroups(String commandPath) {
    if (dynamicFilterModel.getSelectors() != null) {
      for (FilterGroupSelectorModel selectorGroup : dynamicFilterModel.getSelectors()) {
        if (commandPath.equals(selectorGroup.getCurrentGroupPath())) {
          selectorGroup.setCurrentGroupPath(null);
          break;
        }
      }
    }

  }

  private FilterFieldModel createFilterFieldFromSelectorPath(String selectorPath) {
    ApiObjectRef filterSelectorRef = ref.getValueRefByPath(selectorPath);
    FilterFieldSelectorModel filterSelector =
        filterSelectorRef.getWrapper(FilterFieldSelectorModel.class);
    return createFilterFieldFromSelector(filterSelector, selectorPath);
  }

  private FilterFieldModel createFilterFieldFromSelector(FilterFieldSelectorModel filterSelector,
      String selectorId) {
    FilterFieldModel filterField = new FilterFieldModel();
    FilterFieldLabel label = new FilterFieldLabel();
    label.setCode(filterSelector.getLabelCode());
    FilterLabelPosition position = FilterLabelPosition.ON_TOP; // default
    if (filterConfigMode == FilterConfigMode.STATIC) {
      position = FilterLabelPosition.PLACEHOLDER;
    }
    label.setPosition(position);
    // TODO label.duplicateNum
    filterField.setLabel(label);
    filterField.setCloseable(filterConfigMode != FilterConfigMode.STATIC);
    List<FilterOperation> operations = filterSelector.getOperations();
    filterField.setOperations(operations);
    // TODO default operation handling
    if (operations != null && !operations.isEmpty()) {
      filterField.setSelectedOperation(operations.get(0));
    }
    if (filterConfigMode == FilterConfigMode.DYNAMIC) {
      filterField.setDraggable(true);
    }
    if (filterConfigMode == FilterConfigMode.SIMPLE_DYNAMIC) {
      filterField.setSelectorId(selectorId);
      filterSelector.setEnabled(Boolean.FALSE);
    }
    fillPossibleValues(filterField);
    return filterField;
  }

  private FilterGroupModel createOrFindFilterGroupByPath(String selectorPath) {
    String groupPath;
    if (filterConfigMode == FilterConfigMode.SIMPLE_DYNAMIC
        || filterConfigMode == FilterConfigMode.STATIC) {
      FilterGroupSelectorModel selectorGroup = getSelectorGroupBySelector(selectorPath);
      groupPath = selectorGroup.getCurrentGroupPath();
      if (Strings.isNullOrEmpty(groupPath)) {
        FilterGroupModel groupForSelectorGroup = createFilterGroupBySelectorGroup(selectorGroup);
        groupPath = ref.addValueByPath("root/groups", groupForSelectorGroup).getPath();
        selectorGroup.setCurrentGroupPath(groupPath);
      }
    } else {
      if (rootFilterGroupViewModel.getCurrentActive() != null) {
        groupPath = rootFilterGroupViewModel.getCurrentActive();
      } else {
        groupPath = "root";
      }
    }
    ApiObjectRef groupRef = ref.getValueRefByPath(groupPath);
    return groupRef.getWrapper(FilterGroupModel.class);
  }

  private FilterGroupModel createFilterGroupBySelectorGroup(
      FilterGroupSelectorModel selectorGroup) {
    FilterGroupModel groupForSelectorGroup = new FilterGroupModel();
    groupForSelectorGroup.setRoot(Boolean.FALSE);
    FilterGroupLabel groupLabel = new FilterGroupLabel();
    groupLabel.setLabelCode(selectorGroup.getLabelCode());
    groupLabel.setIconCode(selectorGroup.getIconCode());
    groupForSelectorGroup.setLabel(groupLabel);
    groupForSelectorGroup.setGroupType(selectorGroup.getGroupType());
    return groupForSelectorGroup;
  }

  private FilterGroupSelectorModel getSelectorGroupBySelector(String selectorPath) {
    String selectorGroupPath = PathUtility.subpath(selectorPath, 0, 2);
    ApiObjectRef selectorGroupRef = ref.getValueRefByPath(selectorGroupPath);
    FilterGroupSelectorModel selectorGroup =
        selectorGroupRef.getWrapper(FilterGroupSelectorModel.class);
    return selectorGroup;
  }

  private void fillPossibleValues(FilterFieldModel filter) {
    // TODO should be done at selected operation change!
    List<Value> possibleValues = null;
    if (filter.getSelectedOperation() != null) {
      URI uri = filter.getSelectedOperation().getPossibleValuesUri();
      if (uri != null) {
        try {
          possibleValues = valueApi.getPossibleValues(uri);
        } catch (Exception e) {
          // TODO handle this better
          throw new RuntimeException(e);
        }
      }
    }
    filter.setPossibleValues(possibleValues == null ? Collections.emptyList() : possibleValues);
  }

  @Override
  public void setSelectorGroupVisible(String labelCode, boolean visible) {
    Objects.requireNonNull(labelCode);
    Objects.requireNonNull(dynamicFilterModel.getSelectors());
    dynamicFilterModel.getSelectors().stream()
        .filter(g -> labelCode.equals(g.getLabelCode()))
        .forEach(g -> g.setVisible(visible));
    dynamicFilterModelObservable.notifyListeners();
  }

  @Override
  public FilterGroup getRootFilterGroup() {
    return createFilterGroupByModel(dynamicFilterModel.getRoot());
  }

  private FilterGroup createFilterGroupByModel(FilterGroupModel model) {
    FilterGroup group = new FilterGroup();
    group.setName(model.getLabel() == null ? null : model.getLabel().getLabelCode());
    group.setType(model.getGroupType());
    group.setIsNegated(model.getNegated());
    if (model.getGroups() != null) {
      group.setFilterGroups(model.getGroups().stream()
          .map(this::createFilterGroupByModel)
          .collect(Collectors.toList()));
    }
    if (model.getFilters() != null) {
      group.setFilterFields(model.getFilters().stream()
          .map(this::createFilterFieldByModel)
          .collect(Collectors.toList()));
    }
    return group;
  }

  private FilterField createFilterFieldByModel(FilterFieldModel model) {
    FilterField filter = new FilterField();
    FilterOperation operation = model.getSelectedOperation();
    if (operation == null) {
      // ???
      throw new IllegalStateException("FilterFieldModel doesn't specify operation");
    }
    filter.setOperationCode(operation.getOperationCode());
    filter.setPropertyUri1(operation.getPropertyUri1());
    filter.setPropertyUri2(operation.getPropertyUri2());
    filter.setPropertyUri3(operation.getPropertyUri3());
    filter.setValue1(createFilterOperandValueFromValue(model.getValue1()));
    filter.setValue2(createFilterOperandValueFromValue(model.getValue2()));
    filter.setValue3(createFilterOperandValueFromValue(model.getValue3()));
    filter.setSelectedValues(extractSelectedValueURIs(model));
    return filter;
  }

  private List<URI> extractSelectedValueURIs(FilterFieldModel model) {
    if (model.getSelectedValues() == null) {
      return Collections.emptyList();
    }
    return model.getSelectedValues().stream()
        .map(v -> v.getObjectUri())
        .collect(Collectors.toList());
  }

  private FilterOperandValue createFilterOperandValueFromValue(String value) {
    if (Strings.isNullOrEmpty(value)) {
      return null;
    }
    FilterOperandValue result = checkType(PREFIX_STRING, value);
    if (result == null) {
      checkType(DateConverter.PREFIX_DATE, value);
    }
    if (result == null) {
      checkType(DateConverter.PREFIX_DATETIME, value);
    }
    if (result == null) {
      result = new FilterOperandValue()
          .type(String.class.getName())
          .value(value);
    }
    return result;
  }

  private FilterOperandValue checkType(String type, String value) {
    if (value.startsWith(type)) {
      return new FilterOperandValue()
          .type(removeLastChar(type))
          .value(value.substring(type.length()));
    }
    return null;
  }

  private String removeLastChar(String s) {
    return s.substring(0, s.length() - 1);
  }


}
