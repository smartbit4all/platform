package org.smartbit4all.ui.common.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupMeta;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupType;

public class FilterSelectorGroupUIState extends AbstractUIState {

  private String labelCode;
  private String iconCode;
  private String name;
  private List<FilterSelectorUIState> filterSelectors = new ArrayList<>();
  private boolean isCloseable;

  FilterGroupUIState currentGroupUIState;

  public FilterSelectorGroupUIState(DynamicFilterGroupMeta filterGroupMeta) {
    super();
    applyDataFrom(filterGroupMeta);
  }

  void applyDataFrom(DynamicFilterGroupMeta filterGroupMeta) {
    this.labelCode = filterGroupMeta.getName();
    this.iconCode = filterGroupMeta.getIcon();
    this.name = filterGroupMeta.getName();
    // TODO handle isCloseable
  }

  void addFilterSelector(FilterSelectorUIState filterSelector) {
    filterSelectors.add(filterSelector);
  }

  public String getLabelCode() {
    return labelCode;
  }

  public String getIconCode() {
    return iconCode;
  }

  public String getName() {
    return name;
  }

  public boolean isCloseable() {
    return isCloseable;
  }

  public DynamicFilterGroupType getType() {
    // TODO should we handle anything else on selector level?
    return DynamicFilterGroupType.AND;
  }

  public List<FilterSelectorUIState> filterSelectors() {
    return Collections.unmodifiableList(filterSelectors);
  }
}
