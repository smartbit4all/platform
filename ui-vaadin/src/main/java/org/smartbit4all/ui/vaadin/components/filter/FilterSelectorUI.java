package org.smartbit4all.ui.vaadin.components.filter;

import org.smartbit4all.ui.common.filter.FilterSelectorUIState;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterSelectorUI extends FlexLayout {

  private Label label;
  private Button button;

  public FilterSelectorUI(FilterSelectorUIState uiState, Runnable select) {
    label = new Label();
    button = new Button(new Icon(VaadinIcon.PLUS));
    button.addClickListener(e -> select.run());
    add(label, button);
    addClassName("filtermeta-layout");
    updateState(uiState);
  }

  public void updateState(FilterSelectorUIState uiState) {
    label.setText(label.getTranslation(uiState.getLabelCode()));
    button.setEnabled(uiState.isEnabled());
    // setVisible(uiState.isEnabled());
  }
}