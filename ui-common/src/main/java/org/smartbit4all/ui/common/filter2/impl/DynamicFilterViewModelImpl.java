package org.smartbit4all.ui.common.filter2.impl;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
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
import org.smartbit4all.api.filter.util.FilterConfigs;
import org.smartbit4all.api.value.ValueApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.DomainObjectRef;
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
import org.smartbit4all.ui.api.viewmodel.ObjectEditingImpl;
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

  public void setModel(DynamicFilterModel newModel) {
    Objects.requireNonNull(newModel);
    dynamicFilterModel.setFilterConfigMode(newModel.getFilterConfigMode());
    dynamicFilterModel.getSelectors().clear();
    dynamicFilterModel.getSelectors().addAll(newModel.getSelectors());
    dynamicFilterModel.setRoot(newModel.getRoot());
    dynamicFilterModelObservable.notifyListeners();
  }

  public DynamicFilterModel getRawModel() {
    return (DynamicFilterModel) ref.getObject();
  }

  public DynamicFilterModel getWrappedModel() {
    return dynamicFilterModel;
  }

  @Override
  public void initModel(String uri) {
    this.filterConfigUri = uri;
    filterConfigMode = dynamicFilterModel.getFilterConfigMode();
    boolean isRootVisible = filterConfigMode == FilterConfigMode.DYNAMIC;
    boolean isChildGroupAllowed = filterConfigMode == FilterConfigMode.DYNAMIC;
    boolean isGroupTypeChangeEnabled = filterConfigMode == FilterConfigMode.DYNAMIC;
    FilterGroupModel root = new FilterGroupModel();
    root.setId(UUID.randomUUID().toString());
    root.setRoot(Boolean.TRUE);
    root.setGroupType(FilterGroupType.AND);
    root.setVisible(isRootVisible);
    root.setChildGroupAllowed(isChildGroupAllowed);
    root.setGroupTypeChangeEnabled(isGroupTypeChangeEnabled);

    dynamicFilterModel.setRoot(root);
    rootFilterGroupViewModel = new FilterGroupViewModelImpl();
    rootFilterGroupViewModel.setRef((ApiObjectRef) ref);

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
          FilterFieldModel filter = createFilterFieldFromSelector(filterSelector);
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
    filterSelector.setId(UUID.randomUUID().toString());
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
    groupSelector
        .setGroupType(groupMeta.getType() == null ? FilterGroupType.AND : groupMeta.getType());
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
          String groupId = null;
          if (filterConfigMode == FilterConfigMode.SIMPLE_DYNAMIC) {
            groupId = ref.getValueRefByPath(commandPath).getWrapper(FilterGroupModel.class).getId();
          }
          rootFilterGroupViewModel.executeCommandWithoutNotify(commandPath, command, params);
          if (groupId != null) {
            removeFilterGroupFromSelectorGroups(groupId);
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
    String selectorId = filter.getSelectorId();
    if (!Strings.isNullOrEmpty(selectorId)) {
      dynamicFilterModel.getSelectors().stream()
          .flatMap(gm -> gm.getFilters().stream())
          .filter(s -> selectorId.equals(s.getId()))
          .findFirst()
          .ifPresent(s -> s.setEnabled(Boolean.TRUE));
    }
  }

  private void removeFilterGroupFromSelectorGroups(String groupId) {
    dynamicFilterModel.getSelectors().stream()
        .filter(s -> groupId.equals(s.getCurrentGroupId()))
        .findFirst()
        .ifPresent(s -> s.setCurrentGroupId(null));
  }

  private FilterFieldModel createFilterFieldFromSelectorPath(String selectorPath) {
    DomainObjectRef filterSelectorRef = ref.getValueRefByPath(selectorPath);
    FilterFieldSelectorModel filterSelector =
        filterSelectorRef.getWrapper(FilterFieldSelectorModel.class);
    return createFilterFieldFromSelector(filterSelector);
  }

  private FilterFieldModel createFilterFieldFromSelector(FilterFieldSelectorModel filterSelector) {
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
      filterField.setSelectorId(filterSelector.getId());
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
      groupPath = selectorGroup.getCurrentGroupId();
      if (Strings.isNullOrEmpty(groupPath)) {
        FilterGroupModel groupForSelectorGroup = createFilterGroupBySelectorGroup(selectorGroup);
        selectorGroup.setCurrentGroupId(groupForSelectorGroup.getId());
        groupPath = ref.addValueByPath("root/groups", groupForSelectorGroup).getPath();
      } else {
        return findFilterGroupById(dynamicFilterModel.getRoot(), groupPath);
      }
    } else {
      if (rootFilterGroupViewModel.getCurrentActive() != null) {
        groupPath = rootFilterGroupViewModel.getCurrentActive();
      } else {
        return dynamicFilterModel.getRoot();
      }
    }
    DomainObjectRef groupRef = ref.getValueRefByPath(groupPath);
    return groupRef.getWrapper(FilterGroupModel.class);
  }

  private FilterGroupModel findFilterGroupById(FilterGroupModel group, String id) {
    Objects.requireNonNull(group);
    Objects.requireNonNull(id);
    if (id.equals(group.getId())) {
      return group;
    }
    for (FilterGroupModel subgroup : group.getGroups()) {
      FilterGroupModel result = findFilterGroupById(subgroup, id);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  private FilterGroupModel createFilterGroupBySelectorGroup(
      FilterGroupSelectorModel selectorGroup) {
    FilterGroupModel groupForSelectorGroup = new FilterGroupModel();
    groupForSelectorGroup.setId(UUID.randomUUID().toString());
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
    DomainObjectRef selectorGroupRef = ref.getValueRefByPath(selectorGroupPath);
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
    group.setFilterGroups(model.getGroups().stream()
        .map(this::createFilterGroupByModel)
        .collect(Collectors.toList()));
    group.setFilterFields(model.getFilters().stream()
        .map(this::createFilterFieldByModel)
        .collect(Collectors.toList()));
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
    if (model.getSelectedValues().isEmpty() && model.getSelectedValue() != null) {
      List<URI> selectedUri = Collections.singletonList(model.getSelectedValue().getObjectUri());
      filter.setSelectedValues(selectedUri);
    } else {
      filter.setSelectedValues(extractSelectedValueURIs(model));
    }
    return filter;
  }

  private List<URI> extractSelectedValueURIs(FilterFieldModel model) {
    return model.getSelectedValues().stream()
        .map(v -> v.getObjectUri())
        .collect(Collectors.toList());
  }

  private FilterOperandValue createFilterOperandValueFromValue(String value) {
    if (Strings.isNullOrEmpty(value)) {
      return null;
    }
    FilterOperandValue result = checkType(FilterConfigs.PREFIX_STRING, value);
    if (result == null) {
      result = checkType(FilterConfigs.PREFIX_DATE, value);
    }
    if (result == null) {
      result = checkType(FilterConfigs.PREFIX_DATETIME, value);
    }
    if (result == null) {
      result = checkType(FilterConfigs.PREFIX_INTEGER, value);
    }
    if (result == null) {
      result = checkType(FilterConfigs.PREFIX_DOUBLE, value);
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

  public void setEnabled(boolean enabled) {
    dynamicFilterModel.getSelectors().stream()
        .flatMap(g -> g.getFilters().stream())
        .forEach(s -> s.setEnabled(enabled));
    setFilterGroupEnabled(dynamicFilterModel.getRoot(), enabled);
    dynamicFilterModelObservable.notifyListeners();

  }

  private void setFilterGroupEnabled(FilterGroupModel filterGroup, boolean enabled) {
    filterGroup.setEnabled(enabled);
    filterGroup.getFilters().stream().forEach(f -> f.setEnabled(enabled));
    filterGroup.getGroups().stream().forEach(g -> setFilterGroupEnabled(g, enabled));
  }

  @Override
  public void clearFilters() {
    dynamicFilterModel.getRoot().getGroups().clear();
    dynamicFilterModel.getRoot().getFilters().clear();
    // ---- TODO these should be handled by subscriptions to the previous two clears?!
    dynamicFilterModel.getSelectors().forEach(gm -> gm.setCurrentGroupId(null));
    dynamicFilterModel.getSelectors().stream()
        .flatMap(gm -> gm.getFilters().stream())
        .forEach(f -> f.setEnabled(true));
    // ----
    dynamicFilterModelObservable.notifyListeners();
  }
}
