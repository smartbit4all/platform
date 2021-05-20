package org.smartbit4all.ui.common.filter2.impl;

import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.common.filter2.api.FilterGroupViewModel;
import org.smartbit4all.ui.common.filter2.model.FilterFieldModel;
import org.smartbit4all.ui.common.filter2.model.FilterGroupModel;
import com.google.common.base.Objects;

public class FilterGroupViewModelImpl extends ObjectEditingImpl implements FilterGroupViewModel {

  private ObservableObjectImpl filterGroupObservable;

  private String currentActive;

  public FilterGroupViewModelImpl() {
    filterGroupObservable = new ObservableObjectImpl();
  }

  @Override
  public void setModel(FilterGroupModel filterGroupModel) {
    ref = new ApiObjectRef(null, filterGroupModel, ViewModelHelper.getFilterDescriptors());
    filterGroupObservable.setRef(ref);
  }

  @Override
  public ObservableObject filterGroup() {
    return filterGroupObservable;
  }

  @Override
  public void executeCommand(String commandPath, String command, Object... params) {
    switch (command) {
      case "FILTER_DROPPED":

        break;
      case "SELECTOR_DROPPED":

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
        ref.removeValueByPath(commandPath);
        break;
      case "CHANGE_OPERATION":
        if (params == null || params.length != 1) {
          throw new IllegalArgumentException(
              "Missing or too many parameters at " + command + " command: ");
        }
        changeFilterOperation(commandPath, (String) params[0]);
        break;
      default:
        super.executeCommand(commandPath, command, params);
        break;
    }
    filterGroupObservable.notifyListeners();

  }

  private void changeFilterOperation(String filterFieldPath, String operationPath) {
    ApiObjectRef filterRef = ref.getValueRefByPath(filterFieldPath);
    FilterFieldModel filter = filterRef.getWrapper(FilterFieldModel.class);
    ApiObjectRef operationRef = ref.getValueRefByPath(operationPath);
    FilterOperation operation = (FilterOperation) operationRef.getObject();
    if (!filter.getSelectedOperation().getId().equals(operation.getId())) {
      filter.setSelectedOperation(operation);
    }
  }

  private void changeGroupType(String filterGroupPath) {
    ApiObjectRef currentRef = ref.getValueRefByPath(filterGroupPath);
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
    ApiObjectRef parentRef = ref.getValueRefByPath(parentGroupPath);
    if (parentRef != null) {
      FilterGroupModel parentFilterGroup = parentRef.getWrapper(FilterGroupModel.class);
      FilterGroupModel childFilterGroup = new FilterGroupModel();
      childFilterGroup.setRoot(Boolean.FALSE);
      childFilterGroup.setGroupTypeChangeEnabled(parentFilterGroup.getGroupTypeChangeEnabled());
      childFilterGroup.setChildGroupAllowed(parentFilterGroup.getChildGroupAllowed());
      childFilterGroup.setCloseable(Boolean.TRUE);
      ApiObjectRef childRef =
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
    if (Objects.equal(currentActive, newActive)) {
      return;
    }
    setActiveByPath(currentActive, Boolean.FALSE);
    currentActive = newActive;
    setActiveByPath(currentActive, Boolean.TRUE);
  }

  private void setActiveByPath(String groupPath, Boolean active) {
    ApiObjectRef currentRef = ref.getValueRefByPath(groupPath);
    if (currentRef != null) {
      currentRef.getWrapper(FilterGroupModel.class).setActive(active);
    }
  }
}
