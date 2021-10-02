package org.smartbit4all.ui.vaadin.components.navigation2;

import org.smartbit4all.ui.api.navigation.NavigationViewModel;
import org.smartbit4all.ui.api.tree.model.TreeModel;
import org.smartbit4all.ui.vaadin.object.VaadinPublisherWrapper;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

@CssImport("./styles/components/sb4-tree1.css")
public class TreeView extends FlexLayout {

  private NavigationViewModel viewModel;

  public TreeView(NavigationViewModel viewModel) {
    this.viewModel = viewModel;
    this.viewModel.model().setPublisherWrapper(VaadinPublisherWrapper.create());

    addClassName("sb4-tree");

    new VaadinTreeNodeBinder(this, viewModel.model(), null, TreeModel.ROOT_NODES,
        (itemPath, item) -> new TreeNodeView(viewModel, itemPath));
  }

}
