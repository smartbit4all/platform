package org.smartbit4all.ui.common.filter;

import org.smartbit4all.ui.common.controller.UIController;

public interface DynamicFilterController extends UIController {

  public static final String ROOT_FILTER_GROUP = "root";

  void setUi(DynamicFilterView dynamicFilterView);

  void loadData();

  void addFilterField(String filterSelectorId);

  void addFilterGroup(String parentGroupId);

  void removeFilterField(String groupId, String filterId);

  void removeFilterGroup(String groupId);

  void filterOptionChanged(String filterId, String filterOperation);

  void activeFilterGroupChanged(String filterGroupId);

  void changeGroup(String oldGroupId, String newGroupId, String filterId);

}
