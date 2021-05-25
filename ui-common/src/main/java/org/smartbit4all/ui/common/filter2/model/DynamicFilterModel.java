package org.smartbit4all.ui.common.filter2.model;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterConfigMode;

public class DynamicFilterModel {

  private FilterConfigMode filterConfigMode;

  private List<FilterGroupSelectorModel> selectors = new ArrayList<>();

  private FilterGroupModel root;

  public FilterConfigMode getFilterConfigMode() {
    return filterConfigMode;
  }

  public void setFilterConfigMode(FilterConfigMode filterConfigMode) {
    this.filterConfigMode = filterConfigMode;
  }

  public List<FilterGroupSelectorModel> getSelectors() {
    return selectors;
  }

  public void setSelectors(List<FilterGroupSelectorModel> selectors) {
    this.selectors = selectors;
  }

  public FilterGroupModel getRoot() {
    return root;
  }

  public void setRoot(FilterGroupModel root) {
    this.root = root;
  }


}
