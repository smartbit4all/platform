package org.smartbit4all.ui.common.filter2.impl;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.filter.FilterApi;
import org.smartbit4all.api.filter.bean.FilterConfig;
import org.smartbit4all.api.filter.bean.FilterConfigMode;
import org.smartbit4all.api.filter.bean.FilterFieldMeta;
import org.smartbit4all.api.filter.bean.FilterGroupMeta;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.value.ValueApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.common.filter.FilterLabelPosition;
import org.smartbit4all.ui.common.filter2.api.DynamicFilterViewModel;
import org.smartbit4all.ui.common.filter2.model.DynamicFilterModel;
import org.smartbit4all.ui.common.filter2.model.FilterFieldLabel;
import org.smartbit4all.ui.common.filter2.model.FilterFieldModel;
import org.smartbit4all.ui.common.filter2.model.FilterFieldSelectorModel;
import org.smartbit4all.ui.common.filter2.model.FilterGroupLabel;
import org.smartbit4all.ui.common.filter2.model.FilterGroupModel;
import org.smartbit4all.ui.common.filter2.model.FilterGroupSelectorModel;
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
    root.setVisible(isRootVisible);
    root.setChildGroupAllowed(isChildGroupAllowed);
    root.setGroupTypeChangeEnabled(isGroupTypeChangeEnabled);

    dynamicFilterModel.setRoot(root);
    rootFilterGroupViewModel = new FilterGroupViewModelImpl();
    rootFilterGroupViewModel.setRef(ref.getValueRefByPath("root"));

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
      String rootlessPath = removeRootPrefix(commandPath);
      Object[] params2;
      if (params != null && params.length > 0) {
        params2 = new Object[params.length];
        removeRootFromParams(params2, params);
      } else {
        params2 = new Object[0];
      }
      rootFilterGroupViewModel.executeCommandWithoutNotify(rootlessPath, command, params2);
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

        default:
          break;
      }
      super.executeCommand(commandPath, command, params);
    }
    dynamicFilterModelObservable.notifyListeners();
  }

  private void removeRootFromParams(Object[] params2, Object... params) {
    for (int i = 0; i < params.length; i++) {
      Object param = params[i];
      if (param instanceof String) {
        params2[i] = removeRootPrefix((String) param);
      }
    }
  }

  private String removeRootPrefix(String path) {
    String result = null;
    if (!Strings.isNullOrEmpty(path) && path.toUpperCase().startsWith("ROOT")) {
      result = path.substring("root".length());
      if (result.startsWith("/")) {
        result = result.substring(1);
      }
      if ("".equals(result)) {
        result = null;
      }
    }
    return result;
  }

  private FilterFieldModel createFilterFieldFromSelectorPath(String selectorPath) {
    ApiObjectRef filterSelectorRef = ref.getValueRefByPath(selectorPath);
    FilterFieldSelectorModel filterSelector =
        (FilterFieldSelectorModel) filterSelectorRef.getObject();
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
      groupPath = "root";
      if (rootFilterGroupViewModel.getCurrentActive() != null) {
        groupPath += "/" + rootFilterGroupViewModel.getCurrentActive();
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
}
