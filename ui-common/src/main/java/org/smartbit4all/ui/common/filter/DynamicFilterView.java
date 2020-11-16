package org.smartbit4all.ui.common.filter;

import java.util.List;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfig;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;
import org.smartbit4all.ui.common.view.UIView;

public interface DynamicFilterView extends UIView {

  void renderFilterConfig(DynamicFilterConfig filterConfig);

  void renderFilter(String groupId, String filterId, DynamicFilter dynamicFilter,
      List<DynamicFilterOperation> filterOptions);

  void renderFilter(String filterId, DynamicFilter dynamicFilter);

  void renderGroup(String parentGroupId, String childGroupId, DynamicFilterGroup childGroup);

}
