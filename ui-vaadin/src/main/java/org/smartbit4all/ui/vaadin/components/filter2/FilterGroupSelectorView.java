package org.smartbit4all.ui.vaadin.components.filter2;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterGroupSelectorView extends Details {

  private ObjectEditing viewModel;
  private ObservableObject groupSelector;
  private String path;

  private Map<String, FilterFieldSelectorView> filterSelectors = new HashMap<>();

  public FilterGroupSelectorView(ObjectEditing viewModel, ObservableObject groupSelector,
      String path) {
    this.viewModel = viewModel;
    this.groupSelector = groupSelector;
    this.path = path;
    createUI();
  }

  private void createUI() {
    addThemeVariants(DetailsVariant.FILLED);
    Label summaryText = new Label();
    FlexLayout summary = new FlexLayout(summaryText);

    setSummary(summary);
    setOpened(true);

    VaadinBinders.bind(summaryText, groupSelector,
        PathUtility.concatPath(path, "labelCode"), s -> getTranslation((String) s));

    groupSelector.onCollectionObjectChange(path, "filters", this::onFiltersChange);
  }

  private void onFiltersChange(CollectionObjectChange changes) {
    for (ObjectChangeSimple change : changes.getChanges()) {
      String filterPath = change.getPath();
      if (change.getOperation() == ChangeState.NEW) {
        if (filterSelectors.get(filterPath) == null) {
          FilterFieldSelectorView filterSelector =
              new FilterFieldSelectorView(viewModel, groupSelector, filterPath);
          filterSelectors.put(filterPath, filterSelector);
          addContent(filterSelector);
        }
      }
    }
  }
}
