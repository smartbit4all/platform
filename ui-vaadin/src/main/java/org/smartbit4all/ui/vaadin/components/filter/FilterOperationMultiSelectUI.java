package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUp;
import org.smartbit4all.ui.vaadin.util.UIUtils;

public class FilterOperationMultiSelectUI extends FilterOperationUI {

  private MultiSelectPopUp<Value> popUp;

  public FilterOperationMultiSelectUI(List<Value> possibleValues) {
    addClassName("filter-multi");
    popUp = new MultiSelectPopUp<>();
    popUp.setItems(possibleValues);
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

}
