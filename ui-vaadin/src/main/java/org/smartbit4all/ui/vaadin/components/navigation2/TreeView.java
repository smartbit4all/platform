package org.smartbit4all.ui.vaadin.components.navigation2;

import org.smartbit4all.ui.api.navigation.NavigationViewModel;
import org.smartbit4all.ui.api.tree.model.TreeModel;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

@CssImport("./styles/components/sb4-tree1.css")
public class TreeView extends FlexLayout {

  private NavigationViewModel viewModel;

  public TreeView(NavigationViewModel viewModel) {
    this.viewModel = viewModel;

    addClassName("sb4-tree");

    new VaadinTreeNodeBinder(this, this.viewModel.model(),
        (itemPath, item) -> new TreeNodeView(viewModel, itemPath),
        TreeModel.ROOT_NODES);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    viewModel.onCloseWindow();
  }
}
