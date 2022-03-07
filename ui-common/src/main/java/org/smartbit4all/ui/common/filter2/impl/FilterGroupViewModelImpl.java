package org.smartbit4all.ui.common.filter2.impl;

import java.util.Objects;
import java.util.UUID;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.DomainObjectRef;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.api.filter.FilterGroupViewModel;
import org.smartbit4all.ui.api.filter.model.FilterFieldModel;
import org.smartbit4all.ui.api.filter.model.FilterGroupModel;
import org.smartbit4all.ui.api.viewmodel.ObjectEditingImpl;

public class FilterGroupViewModelImpl extends ObjectEditingImpl implements FilterGroupViewModel {

  private ObservableObjectImpl filterGroupObservable;

  private String currentActive;

  public FilterGroupViewModelImpl() {
    filterGroupObservable = new ObservableObjectImpl();
  }

  @Override
  public void setModel(FilterGroupModel filterGroupModel) {
    setRef(new ApiObjectRef(null, filterGroupModel, ViewModelHelper.getFilterDescriptors()));
  }

  void setRef(ApiObjectRef ref) {
    this.ref = ref;
    filterGroupObservable.setRef(this.ref);
  }

  @Override
  public ObservableObject filterGroup() {
    return filterGroupObservable;
  }

  @Override
  public void executeCommand(String commandPath, String command, Object... params) {
    executeCommandWithoutNotify(commandPath, command, params);
    filterGroupObservable.notifyListeners();

  }

  public void executeCommandWithoutNotify(String commandPath, String command, Object... params) {
    switch (command) {
      case "FILTER_DROPPED":
        checkParamNumber(command, 1, params);
        moveFilter(commandPath, (String) params[0]);
        break;
      case "CLOSE_FILTERGROUP":
        closeFilterGroup(commandPath);
        break;
      case "ADD_CHILDGROUP":
        addChildGroup(commandPath);
        break;
      case "CHANGE_GROUPTYPE":
        changeGroupType(commandPath);
        break;
      case "GROUP_CLICKED":
        changeActive(commandPath);
        break;
      case "CLOSE_FILTERFIELD":
        closeFilterField(commandPath);
        break;
      case "CHANGE_OPERATION":
        checkParamNumber(command, 1, params);
        changeFilterOperation(commandPath, (String) params[0]);
        break;
      default:
        super.executeCommand(commandPath, command, params);
        break;
    }
  }

  private void closeFilterField(String filterPath) {
    ref.removeValueByPath(filterPath);
  }

  private void moveFilter(String targetGroupPath, String filterPath) {
    String originalGroupPath = PathUtility.getParentPath(PathUtility.getParentPath(filterPath));
    if (Objects.equals(targetGroupPath, originalGroupPath)) {
      return;
    }
    DomainObjectRef filterRef = ref.getValueRefByPath(filterPath);
    FilterFieldModel filter = filterRef.getWrapper(FilterFieldModel.class);
    closeFilterField(filterPath);
    DomainObjectRef groupRef = ref.getValueRefByPath(targetGroupPath);
    FilterGroupModel group = groupRef.getWrapper(FilterGroupModel.class);
    group.getFilters().add(filter);
  }

  private void changeFilterOperation(String filterFieldPath, String operationPath) {
    DomainObjectRef filterRef = ref.getValueRefByPath(filterFieldPath);
    FilterFieldModel filter = filterRef.getWrapper(FilterFieldModel.class);
    DomainObjectRef operationRef = ref.getValueRefByPath(operationPath);
    FilterOperation operation = operationRef.getWrapper(FilterOperation.class);
    if (!filter.getSelectedOperation().getId().equals(operation.getId())) {
      filter.setSelectedOperation(operation);
      // TODO ???
      // filter.setValue1(null);
      // filter.setValue2(null);
      // filter.setValue3(null);
    }
  }

  private void changeGroupType(String filterGroupPath) {
    DomainObjectRef currentRef = ref.getValueRefByPath(filterGroupPath);
    if (currentRef != null) {
      FilterGroupModel currentFilterGroup = currentRef.getWrapper(FilterGroupModel.class);
      FilterGroupType current = currentFilterGroup.getGroupType();
      if (current == FilterGroupType.AND) {
        currentFilterGroup.setGroupType(FilterGroupType.OR);
      } else {
        currentFilterGroup.setGroupType(FilterGroupType.AND);
      }
    }
  }

  private void addChildGroup(String parentGroupPath) {
    DomainObjectRef parentRef = ref.getValueRefByPath(parentGroupPath);
    if (parentRef != null) {
      FilterGroupModel parentFilterGroup = parentRef.getWrapper(FilterGroupModel.class);
      FilterGroupModel childFilterGroup = new FilterGroupModel();
      childFilterGroup.setId(UUID.randomUUID().toString());
      childFilterGroup.setGroupType(FilterGroupType.AND);
      childFilterGroup.setRoot(Boolean.FALSE);
      childFilterGroup.setGroupTypeChangeEnabled(parentFilterGroup.getGroupTypeChangeEnabled());
      childFilterGroup.setChildGroupAllowed(parentFilterGroup.getChildGroupAllowed());
      childFilterGroup.setCloseable(Boolean.TRUE);
      DomainObjectRef childRef =
          ref.addValueByPath(PathUtility.concatPath(parentGroupPath, "groups"), childFilterGroup);
      changeActive(childRef.getPath());
    }
  }

  private void closeFilterGroup(String filterGroupPath) {
    int pathSize = PathUtility.getPathSize(filterGroupPath);
    if (pathSize < 2) {
      throw new IllegalArgumentException(
          "Invalid (too short) path when removing child filter group: " + filterGroupPath);
    }
    ref.removeValueByPath(filterGroupPath);
    // if current active group was deleted
    if (currentActive != null && filterGroupPath.startsWith(currentActive)) {
      if (pathSize == 2) {
        changeActive(null);
      } else {
        changeActive(PathUtility.subpath(filterGroupPath, 0, pathSize - 2));
      }
    }
  }

  protected void changeActive(String newActive) {
    if (Objects.equals(currentActive, newActive)) {
      return;
    }
    setActiveByPath(currentActive, Boolean.FALSE);
    currentActive = newActive;
    setActiveByPath(currentActive, Boolean.TRUE);
  }

  private void setActiveByPath(String groupPath, Boolean active) {
    DomainObjectRef currentRef = ref.getValueRefByPath(groupPath);
    if (currentRef != null) {
      if (currentRef.getObject() instanceof FilterGroupModel) {
        currentRef.getWrapper(FilterGroupModel.class).setActive(active);
      }
    }
  }

  String getCurrentActive() {
    return currentActive;
  }

}
