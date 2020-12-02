package org.smartbit4all.ui.vaadin.components.filter;

import org.smartbit4all.ui.common.filter.FilterSelectorGroupUIState;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterSelectorGroupUI extends Details {

  private Label summaryText;

  public FilterSelectorGroupUI(FilterSelectorGroupUIState uiState) {
    addThemeVariants(DetailsVariant.FILLED);
    summaryText = new Label();
    FlexLayout summary = new FlexLayout(summaryText);

    setSummary(summary);
    setOpened(true);
    updateState(uiState);
  }

  public void updateState(FilterSelectorGroupUIState uiState) {
    summaryText.setText(getTranslation(uiState.getLabelCode()));

  }

}
