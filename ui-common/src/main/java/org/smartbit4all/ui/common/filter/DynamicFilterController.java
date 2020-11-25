package org.smartbit4all.ui.common.filter;

import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupType;
import org.smartbit4all.ui.common.controller.UIController;

public interface DynamicFilterController extends UIController {

  public static final String ROOT_FILTER_GROUP = "root";

  void setUi(DynamicFilterView dynamicFilterView);

  void loadData();

  void addFilter(String filterSelectorId);

  void addFilter(String groupId, String filterMetaName, boolean isClosable);

  String addFilterGroup(String parentGroupId, String groupName, String groupIcon,
      DynamicFilterGroupType groupType, boolean isClosable);

  void filterOptionChanged(String filterId, int filterOptionIdx);

  DynamicFilterGroup getFilters();

  void removeFilter(String groupId, String filterId);

  void removeGroup(String groupId);

}
