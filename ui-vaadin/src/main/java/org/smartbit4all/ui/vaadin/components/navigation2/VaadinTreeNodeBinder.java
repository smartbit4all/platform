package org.smartbit4all.ui.vaadin.components.navigation2;

import java.util.function.BiFunction;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.api.tree.model.TreeNode;
import org.smartbit4all.ui.vaadin.components.binder.VaadinWidgetCollectionBinder;
import com.vaadin.flow.component.HasComponents;

public class VaadinTreeNodeBinder extends VaadinWidgetCollectionBinder<TreeNode, TreeNodeView> {

  public VaadinTreeNodeBinder(HasComponents container, ObservableObject observableObject,
      String path, String collectionName, BiFunction<String, TreeNode, TreeNodeView> createWidget) {
    super(container, observableObject, path, collectionName, createWidget);
  }

  @Override
  protected void removeWidget(TreeNodeView widget) {
    super.removeWidget(widget);
    widget.unbind();
  }

}
