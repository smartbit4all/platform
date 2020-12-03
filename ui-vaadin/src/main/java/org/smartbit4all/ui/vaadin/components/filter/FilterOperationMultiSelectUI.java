package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUp;
import org.smartbit4all.ui.vaadin.util.UIUtils;

public class FilterOperationMultiSelectUI extends FilterOperationUI {

  private MultiSelectPopUp<Value> popUp;
  private String filterName;

  public FilterOperationMultiSelectUI(String filterName) {
    this.filterName = filterName;
    addClassName("dynamic-filter-multi");
    popUp = new MultiSelectPopUp<>();
    popUp.setRequired(false);
    // popUp.setFilter(filter); TODO
    popUp.setItemDisplayValueProvider(v -> v.getValue());

    popUp.addValueChangeListener(e -> {
      UIUtils.showNotification("value changed");
    });

    add(popUp);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    popUp.setPlaceholder(placeHolderText);
  }

  @Override
  public String getFilterName() {
    return filterName;
  }

  public void setItems(List<Value> items) {
    popUp.setItems(items);
  }

  @Override
  public List<String> getPossibleOperations() {
    // TODO Auto-generated method stub
    return null;
  }
}
