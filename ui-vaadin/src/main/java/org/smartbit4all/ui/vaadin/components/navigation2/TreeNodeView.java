package org.smartbit4all.ui.vaadin.components.navigation2;

import java.util.List;
import java.util.Objects;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.ui.api.navigation.NavigationViewModel;
import org.smartbit4all.ui.api.tree.model.TreeNode;
import org.smartbit4all.ui.vaadin.localization.TranslationUtil;
import org.smartbit4all.ui.vaadin.util.Css;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import io.reactivex.rxjava3.disposables.Disposable;

public class TreeNodeView extends FlexLayout {

  private NavigationViewModel viewModel;
  private String path;
  private Label label;
  private Icon icon;
  private String iconKey;
  private Span toggle;
  private VaadinTreeNodeBinder treeNodeBinder;
  private String title;
  private List<String> styles;

  private FlexLayout childrenLayout;
  private Disposable captionSubs;
  private Disposable iconSubs;
  private Disposable stylesSubs;
  private Disposable expandedSubs;
  private Disposable selectedSubs;
  private Disposable levelSubs;

  private Boolean expanded = false;
  private FlexLayout itemLayout;
  private FlexLayout captionLayout;
  private Div spacerLayout;
  private Boolean hasChildren;

  public TreeNodeView(NavigationViewModel viewModel, String path) {
    this.viewModel = viewModel;
    this.path = path;

    createUI();
    ObservableObject model = this.viewModel.model();
    treeNodeBinder = new VaadinTreeNodeBinder(childrenLayout, model,
        this::createSubTreeNodeView, path, TreeNode.CHILDREN_NODES);
    captionSubs = model.onPropertyChange(this::onCaptionChanged, path, TreeNode.CAPTION);
    iconSubs = model.onPropertyChange(this::onIconChanged, path, TreeNode.ICON);
    stylesSubs = model.onPropertyChange(this::onStylesChanged, path, TreeNode.STYLES);
    stylesSubs = model.onPropertyChange(this::onHasChildrenChanged, path, TreeNode.HAS_CHILDREN);
    expandedSubs = model.onPropertyChange(this::onExpandedChanged, path, TreeNode.EXPANDED);
    selectedSubs = model.onPropertyChange(this::onSelectedChanged, path, TreeNode.SELECTED);
    levelSubs = model.onPropertyChange(this::onLevelChanged, path, TreeNode.LEVEL);
  }

  private void createUI() {
    addClassName("sb4-tree-node");
    title = "NodeLabel";
    label = new Label(title);
    icon = new Icon(VaadinIcon.CIRCLE_THIN);
    toggle = new Span();
    toggle.addClassName("sb4-tree-toggle");
    toggle.addClickListener(event -> {
      String command = expanded ? "collapse" : "expand";
      viewModel.executeCommand(path, command);
    });
    Css.stopClickEventPropagation(toggle);
    spacerLayout = new Div();
    spacerLayout.addClassName("sb4-tree-node-spacer");
    captionLayout = new FlexLayout(icon, label);
    captionLayout.addClassName("sb4-tree-node-caption");
    itemLayout = new FlexLayout(spacerLayout, toggle, captionLayout);
    itemLayout.addClassName("sb4-tree-node-item");
    itemLayout.addClickListener(event -> viewModel.executeCommand(path, "select"));
    childrenLayout = new FlexLayout();
    childrenLayout.addClassName("sb4-tree-node-children");

    add(itemLayout);
    add(childrenLayout);
  }

  private TreeNodeView createSubTreeNodeView(String path, TreeNode treeNode) {
    return new TreeNodeView(viewModel, path);
  }

  protected String getNodeTitle(String caption) {
    if (caption == null) {
      return "n/a";
    }
    String title = TranslationUtil.INSTANCE().getPossibleTranslation(caption);
    return title;
  }

  private void onCaptionChanged(PropertyChange change) {
    title = getNodeTitle((String) change.getNewValue());
    adjustTitle();
  }

  private void onIconChanged(PropertyChange change) {
    String newIconKey = (String) change.getNewValue();
    if (!Objects.equals(iconKey, newIconKey)) {
      captionLayout.remove(icon);
      iconKey = newIconKey;
      icon = new Icon(iconKey);
      captionLayout.addComponentAsFirst(icon);
    }
  }

  @SuppressWarnings("unchecked")
  private void onStylesChanged(PropertyChange change) {
    styles = (List<String>) change.getNewValue();
    adjustTitle();
  }

  private void onExpandedChanged(PropertyChange change) {
    expanded = (Boolean) change.getNewValue();
    if (expanded == null) {
      expanded = Boolean.FALSE;
    }
    refreshToggle();
  }

  private void onHasChildrenChanged(PropertyChange change) {
    hasChildren = (Boolean) change.getNewValue();
    if (hasChildren == null) {
      hasChildren = Boolean.FALSE;
    }
    refreshToggle();
  }

  private void refreshToggle() {
    if (hasChildren) {
      toggle.addClassName(expanded ? "sb4-tree-toggle-expanded" : "sb4-tree-toggle-collapsed");
      toggle.removeClassName(expanded ? "sb4-tree-toggle-collapsed" : "sb4-tree-toggle-expanded");
      childrenLayout.setVisible(expanded);
    } else {
      toggle.removeClassName("sb4-tree-toggle-collapsed");
      toggle.removeClassName("sb4-tree-toggle-expanded");
      childrenLayout.setVisible(false);
    }
  }

  private void onSelectedChanged(PropertyChange change) {
    Boolean selected = (Boolean) change.getNewValue();
    if (selected != null && selected) {
      itemLayout.addClassName("sb4-tree-node-item-selected");
    } else {
      itemLayout.removeClassName("sb4-tree-node-item-selected");
    }
  }

  private void onLevelChanged(PropertyChange change) {
    Integer level = (Integer) change.getNewValue();
    String width = "calc(" + (7 * level) + "em / 3)"; // + 3
    spacerLayout.getStyle().set("width", width);
  }

  private void adjustTitle() {
    if (styles != null && styles.contains("empty")) {
      label.addClassName("sb4-tree-node-empty");
      label.setText(title + " (0)");
    } else {
      label.removeClassName("sb4-tree-node-empty");
      label.setText(title);
    }
  }

  public void unbind() {
    if (treeNodeBinder != null) {
      treeNodeBinder.unbind();
      treeNodeBinder = null;
    }
    if (captionSubs != null) {
      captionSubs.dispose();
      captionSubs = null;
    }
    if (iconSubs != null) {
      iconSubs.dispose();
      iconSubs = null;
    }
    if (stylesSubs != null) {
      stylesSubs.dispose();
      stylesSubs = null;
    }
    if (expandedSubs != null) {
      expandedSubs.dispose();
      expandedSubs = null;
    }
    if (selectedSubs != null) {
      selectedSubs.dispose();
      selectedSubs = null;
    }
    if (levelSubs != null) {
      levelSubs.dispose();
      levelSubs = null;
    }
  }

}
