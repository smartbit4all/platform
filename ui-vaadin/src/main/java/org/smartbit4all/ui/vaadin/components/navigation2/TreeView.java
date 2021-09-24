package org.smartbit4all.ui.vaadin.components.navigation2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.api.navigation.NavigationViewModel;
import org.smartbit4all.ui.api.tree.model.TreeNode;
import org.smartbit4all.ui.vaadin.components.binder.VaadinCollectionBinder;
import org.smartbit4all.ui.vaadin.localization.TranslationUtil;
import org.smartbit4all.ui.vaadin.object.VaadinPublisherWrapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;

public class TreeView {

  private NavigationViewModel viewModel;

  private TreeGrid<TreeNode> tree;

  private TreeData treeData;

  private TreeDataProvider treeDataProvider;
  // private HierarchicalDataProvider<TreeNode, Void> treeDataProvider;

  private Map<String, TreeNodeBinder> nodeBindersByPath;

  private TreeNodeBinder rootNodeBinder;

  private Map<String, String> pathByNodeIds;

  private boolean expandRunning;

  private boolean collapseRunning;

  public TreeView(NavigationViewModel viewModel, TreeGrid<TreeNode> tree) {
    this.viewModel = viewModel;
    this.tree = tree;
    this.treeData = new TreeData();
    this.nodeBindersByPath = new HashMap<>();
    this.pathByNodeIds = new HashMap<>();

    this.tree.setUniqueKeyDataGenerator("identifier", node -> node.getIdentifier());
    this.tree.addComponentHierarchyColumn(this::getRowComponent).setAutoWidth(true);

    this.viewModel.model().setPublisherWrapper(VaadinPublisherWrapper.create());

    rootNodeBinder = new TreeNodeBinder(viewModel.model(), null, "rootNodes", null);

    treeDataProvider = new TreeDataProvider(treeData); // TODO idGetter?

    // treeDataProvider = new AbstractBackEndHierarchicalDataProvider<TreeNode, Void>() {
    //
    // @Override
    // public int getChildCount(HierarchicalQuery<TreeNode, Void> query) {
    // int size = treeData.getChildren(query.getParent()).size();
    // System.out.println(getCaption(query.getParent()) + " - getChildCount: " + size);
    // return size;
    // }
    //
    // @Override
    // public boolean hasChildren(TreeNode item) {
    // boolean has = treeData.getChildren(item).size() > 0;
    // System.out.println(getCaption(item) + " - hasChildren: " + has);
    // return has;
    // }
    //
    // @Override
    // protected Stream<TreeNode> fetchChildrenFromBackEnd(
    // HierarchicalQuery<TreeNode, Void> query) {
    // List<TreeNode> children = treeData.getChildren(query.getParent());
    // System.out.println(
    // getCaption(query.getParent()) + " - fetchChildrenFromBackEnd: " + children.size());
    // return children.stream();
    // }
    //
    // public String getCaption(TreeNode item) {
    // return item == null ? null : item.getCaption();
    // }
    //
    // @Override
    // public Object getId(TreeNode item) {
    // System.out.println(
    // getCaption(item) + " - getId: " + item.getIdentifier());
    // return item.getIdentifier();
    // }
    // };

    this.tree.setDataProvider(treeDataProvider);

    tree.addExpandListener(event -> nodesExpanded(event.getItems()));
    tree.addCollapseListener(event -> nodesCollapsed(event.getItems()));

    tree.addSelectionListener(event -> {
      Collection<TreeNode> items;
      if (event.getAllSelectedItems().isEmpty() &&
          event.getFirstSelectedItem().isPresent()) {
        items = new HashSet<TreeNode>();
        items.add(event.getFirstSelectedItem().get());
      } else {
        items = event.getAllSelectedItems();
      }
      nodesSelected(items);
    });
  }

  private void nodesExpanded(Collection<TreeNode> items) {
    if (!expandRunning) {
      Object[] itemPaths =
          items.stream().map(node -> pathByNodeIds.get(node.getIdentifier())).toArray();
      expandRunning = true;
      try {
        viewModel.executeCommand(null, "expand", itemPaths);
      } finally {
        expandRunning = false;
      }
    }
  }

  private void nodesCollapsed(Collection<TreeNode> items) {
    if (!collapseRunning) {
      Object[] itemPaths =
          items.stream().map(node -> pathByNodeIds.get(node.getIdentifier())).toArray();
      collapseRunning = true;
      try {
        viewModel.executeCommand(null, "collapse", itemPaths);
      } finally {
        collapseRunning = false;
      }
    }
  }

  private void nodesSelected(Collection<TreeNode> items) {
    Object[] itemPaths =
        items.stream().map(node -> pathByNodeIds.get(node.getIdentifier())).toArray();
    viewModel.executeCommand(null, "select", itemPaths);
  }

  private class TreeNodeBinder {

    private TreeNode parent;

    private VaadinCollectionBinder<TreeNode> binder;

    protected TreeNodeBinder(ObservableObject observableObject, String path,
        String collectionName, TreeNode parent) {
      this.parent = parent;
      binder = new VaadinCollectionBinder<TreeNode>(observableObject, path, collectionName) {

        private List<TreeNode> changeNodes;

        @Override
        protected void onCollectionObjectChanged(CollectionObjectChange changes) {
          changeNodes = new ArrayList<>();
          super.onCollectionObjectChanged(changes);
        }

        @Override
        protected void addItem(String itemPath, TreeNode item) {
          super.addItem(itemPath, item);
          treeData.addItem(parent, item);
          TreeNodeBinder treeNodeBinder =
              new TreeNodeBinder(observableObject, itemPath, "childrenNodes", item);
          nodeBindersByPath.put(itemPath, treeNodeBinder);
          pathByNodeIds.put(item.getIdentifier(), itemPath);
          changeNodes.add(item);
        }

        @Override
        protected void modifyItem(String itemPath, TreeNode newValue, TreeNode oldValue) {
          super.modifyItem(itemPath, newValue, oldValue);
          changeNodes.add(newValue);
        }

        @Override
        protected TreeNode deleteItem(String itemPath) {
          TreeNode node = itemsByPath.get(itemPath);
          pathByNodeIds.remove(node.getIdentifier());
          treeData.removeItem(node);
          TreeNodeBinder treeNodeBinder = nodeBindersByPath.remove(itemPath);
          if (treeNodeBinder != null) {
            treeNodeBinder.unbind();
          }
          changeNodes.add(parent);
          return super.deleteItem(itemPath);
        }

        @Override
        protected void handleItemRefreshed() {
          // List<TreeNode> itemsToRemove = new ArrayList<>(treeData.getChildren(parent));
          // for (TreeNode item : items) {
          // if (treeData.contains(item)) {
          // itemsToRemove.remove(item);
          // } else {
          // treeData.addItem(parent, item);
          // }
          // }
          // for (TreeNode itemToRemove : itemsToRemove) {
          // if (treeData.contains(itemToRemove)) {
          // treeData.removeItem(itemToRemove);
          // }
          // }
          if (treeDataProvider != null) {
            changeNodes.forEach(treeDataProvider::refreshItem);
            changeNodes.clear();
            // treeDataProvider.refreshAll();
          }
        }

      };

    }

    private void unbind() {
      if (binder != null) {
        binder.unbind();
        binder = null;
      }
    }

  }

  protected Component getRowComponent(TreeNode node) {
    Objects.requireNonNull(node);
    String iconKey = node.getIcon();
    String title = getNodeTitle(node);
    Label label = new Label(title);
    adjustStylesToNode(node, title, label);
    if (iconKey != null) {
      Icon icon = new Icon(iconKey);
      Button button = new TreeNodeButton(node);
      return new HorizontalLayout(icon, label, button);
    } else {
      return label;
    }
  }

  private class TreeNodeButton extends Button {

    private Boolean expanded;

    public TreeNodeButton(TreeNode treeNode) {
      String path = pathByNodeIds.get(treeNode.getIdentifier());
      viewModel.model().onPropertyChange(path, TreeNode.EXPANDED, change -> {
        expanded = (Boolean) change.getNewValue();
        if (expanded) {
          setText("-");
          // tree.expand(treeNode);
        } else {
          setText("+");
          // tree.collapse(treeNode);
        }
      });

      addClickListener(event -> {
        String command = expanded ? "collapse" : "expand";
        viewModel.executeCommand(null, command, path);
      });
    }
  }

  protected String getNodeTitle(TreeNode node) {
    String caption = node.getCaption();
    if (caption == null) {
      return "n/a";
    }
    String title = TranslationUtil.INSTANCE().getPossibleTranslation(caption);
    return title;
  }

  protected void adjustStylesToNode(TreeNode node, String title, Label label) {
    List<String> styles = node.getStyles();
    if (styles != null && styles.contains("empty")) {
      label.getStyle().set("font-style", "italic");
      label.setText(title + " (0)");
    }
  }


}
