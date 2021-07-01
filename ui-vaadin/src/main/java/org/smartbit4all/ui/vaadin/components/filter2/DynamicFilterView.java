package org.smartbit4all.ui.vaadin.components.filter2;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ReferencedObjectChange;
import org.smartbit4all.ui.api.filter.DynamicFilterViewModel;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@CssImport("./styles/components/dynamic-filter-view.css")
public class DynamicFilterView {

  private DynamicFilterViewModel viewModel;
  private HasComponents filterSelectorHolder;
  private HasComponents filterHolder;
  private VerticalLayout filterSelectorLayout;
  private ObservableObject dynamicFilterModel;

  private Map<String, FilterGroupSelectorView> groupSelectors = new HashMap<>();
  private FilterGroupView root;

  public DynamicFilterView(DynamicFilterViewModel viewModel, HasComponents filterSelectorHolder,
      HasComponents filterHolder) {
    this.viewModel = viewModel;
    this.dynamicFilterModel = viewModel.dynamicFilterModel();
    this.filterSelectorHolder = filterSelectorHolder;
    this.filterHolder = filterHolder;
    createUI();
  }

  private void createUI() {
    filterSelectorLayout = new VerticalLayout();
    filterSelectorLayout.addClassName("filterselector-layout");
    // TODO check at add/remove selector!
    if (filterSelectorHolder != null) {
      filterSelectorHolder.add(filterSelectorLayout);
    }
    dynamicFilterModel.onCollectionObjectChange(null, "selectors", this::onGroupSelectorChange);
    dynamicFilterModel.onReferencedObjectChange(null, "root", this::onRootChange);
  }

  private void onGroupSelectorChange(CollectionObjectChange changes) {
    for (ObjectChangeSimple change : changes.getChanges()) {
      String groupSelectorPath = change.getPath();
      if (change.getOperation() == ChangeState.NEW) {
        if (groupSelectors.get(groupSelectorPath) == null) {
          FilterGroupSelectorView groupSelector =
              new FilterGroupSelectorView(viewModel, dynamicFilterModel, groupSelectorPath);
          groupSelectors.put(groupSelectorPath, groupSelector);
          filterSelectorLayout
              .add(groupSelector);
        }
      } else if (change.getOperation() == ChangeState.DELETED) {
        FilterGroupSelectorView groupSelector = groupSelectors.get(groupSelectorPath);
        if (groupSelector != null) {
          filterSelectorLayout.remove(groupSelector);
          groupSelectors.remove(groupSelectorPath);
        }
      }
    }
  }

  private void onRootChange(ReferencedObjectChange changes) {
    ChangeState operation = changes.getChange().getOperation();
    if (operation == ChangeState.NEW) {
      removeRoot();
      createRoot();
    } else if (operation == ChangeState.DELETED) {
      removeRoot();
    }
  }

  private void createRoot() {
    root = new FilterGroupView(viewModel, dynamicFilterModel, "root");
    filterHolder.add(root);
  }

  private void removeRoot() {
    if (root != null) {
      filterHolder.remove(root);
      root = null;
    }
  }

}
