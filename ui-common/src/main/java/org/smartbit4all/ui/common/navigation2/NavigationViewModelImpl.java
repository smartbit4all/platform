package org.smartbit4all.ui.common.navigation2;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationAssociation;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.api.navigation.NavigationViewModel;
import org.smartbit4all.ui.api.tree.model.TreeModel;
import org.smartbit4all.ui.api.tree.model.TreeNode;
import org.smartbit4all.ui.api.tree.model.TreeNodeKind;
import com.google.common.base.Strings;

public class NavigationViewModelImpl extends ObjectEditingImpl implements NavigationViewModel {

  private NavigationApi navigationApi;

  private TreeModel model;

  private ObservableObjectImpl modelObservable;

  private Navigation navigationState;

  public NavigationViewModelImpl(NavigationApi navigationApi) {
    this.navigationApi = navigationApi;
    ref = new ApiObjectRef(null, new TreeModel(),
        NavigationViewModelHelper.getNavigationDescriptors());
    modelObservable = new ObservableObjectImpl();
    modelObservable.setRef(ref);
    model = ref.getWrapper(TreeModel.class);
  }

  @Override
  public ObservableObject model() {
    return modelObservable;
  }

  public Navigation startNavigation(NavigationConfig config) {
    navigationState = new Navigation(config, navigationApi);
    return navigationState;
  }

  public void addRoot(URI entryMetaUri, URI rootObjectURI) {
    if (navigationState != null) {
      TreeNode node = treeNodeOf(navigationState.addRootNode(entryMetaUri, rootObjectURI));
      model.getRootNodes().add(node);
      // this way the new root node will be passed as a wrapper, and we need it.. won't load it
      // again, because of childNodesLoaded checking
      // model.getRootNodes().forEach(this::loadChildren);
      model.getRootNodes().forEach(this::expand);
      // loadChildren(node);
    }
  }

  @Override
  public void executeCommand(String commandPath, String command, Object... params) {
    switch (command) {
      case "expand":
        if (params != null) {
          for (int i = 0; i < params.length; i++) {
            String itemPath = (String) params[i];
            TreeNode node = getTreeNodeByPath(itemPath);
            expand(node);
          }
        }
        break;
      case "collapse":
        break;
      case "select":
        break;
      default:
        // super.executeCommand(commandPath, command, params);
        break;
    }
    notifyAllListeners();
  }

  private void expand(TreeNode node) {
    loadChildren(node);
    node.getChildrenNodes().forEach(this::loadChildren);
    node.setExpanded(Boolean.TRUE);
  }

  private void loadChildren(TreeNode parent) {
    if (!parent.getChildrenNodesLoaded()) {
      // Kind kind, String identifier, String caption, String shortDescription,
      // String icon, String[] styles
      if (parent.getKind() == TreeNodeKind.ASSOCIATION) {
        navigationState.getReferencedNodes(parent.getIdentifier())
            .stream()
            .map(NavigationViewModelImpl::treeNodeOf)
            .forEachOrdered(n -> parent.getChildrenNodes().add(n));
        // .map(node -> parent.getChildrenNodes().add(node));
        // List<NavigationNode> nodes = navigationState.getReferencedNodes(parent.getIdentifier());
        // for (NavigationNode node : nodes) {
        // TreeNode treeNode = NavigationViewModelImpl.treeNodeOf(node);
        // parent.getChildrenNodes().add(treeNode);
        // }
        List<String> collect = new ArrayList<>();
        navigationState.getReferencedNodes(parent.getIdentifier())
            .stream()
            .map(n -> n.getId())
            .map(s -> s + " blabla")
            .forEachOrdered(collect::add);
        // .map(s -> collect.add(s));
        // .collect(Collectors.toList());
        List<String> collect2 = navigationState.getReferencedNodes(parent.getIdentifier())
            .stream()
            .map(n -> n.getId())
            .collect(Collectors.toList());

        // .map(s -> System.out.println(s));
        System.out.println("collect");
        collect.forEach(s -> System.out.println(s));
        System.out.println("collect2");
        collect2.forEach(s -> System.out.println(s));
      } else {

        NavigationNode node = navigationState.getNode(parent.getIdentifier());
        if (node != null) {
          navigationState.expandAll(node);
        }

        List<NavigationAssociation> associations =
            node.getAssociations() == null ? Collections.emptyList() : node.getAssociations();

        // create map with nulls to define the order
        LinkedHashMap<String, List<TreeNode>> treeNodesByOrderedAssocIds =
            new LinkedHashMap<>();
        associations.forEach(a -> {
          treeNodesByOrderedAssocIds.put(a.getId(), null);
        });

        Map<String, List<NavigationNode>> nodesByAssocIds =
            navigationState.getChildrenNodes(parent.getIdentifier(), true);
        nodesByAssocIds.forEach((assocId, nodes) -> {
          List<TreeNode> treeNodes = nodes.stream()
              .map(n -> new TreeNode()
                  .kind(TreeNodeKind.ENTRY)
                  .identifier(n.getId())
                  .caption(n.getEntry().getName())
                  .icon(n.getEntry().getIcon())
                  .actions(n.getEntry().getActions()))
              .collect(Collectors.toList());
          treeNodesByOrderedAssocIds.put(assocId, treeNodes);
        });


        // TODO Correct name for the association
        associations.stream()
            .filter(a -> !a.getHidden())
            .forEach(navigationAssociation -> {
              List<String> styles = new ArrayList<>();
              if (navigationAssociation.getReferences() == null
                  || navigationAssociation.getReferences().isEmpty()) {
                styles.add("empty");
              }

              TreeNode treeNode = new TreeNode()
                  .kind(TreeNodeKind.ASSOCIATION)
                  .identifier(navigationAssociation.getId())
                  .caption(getAssociationNodeCaption(navigationAssociation))
                  .icon(navigationAssociation.getIcon())
                  .styles(styles);
              // TreeNode treeNode = new TreeNode(
              // Kind.ASSOCIATION,
              // navigationAssociation.getId(),
              // getAssociationNodeCaption(navigationAssociation),
              // null,
              // navigationAssociation.getIcon(),
              // styles,
              // null);

              treeNodesByOrderedAssocIds.put(navigationAssociation.getId(),
                  Collections.singletonList(treeNode));
            });


        treeNodesByOrderedAssocIds.forEach((assoc, nodes) -> {
          if (nodes != null) {
            parent.getChildrenNodes().addAll(nodes);
          }
        });
      }

      parent.setChildrenNodesLoaded(Boolean.TRUE);
    }

  }

  private static TreeNode treeNodeOf(NavigationNode node) {
    return new TreeNode()
        .kind(TreeNodeKind.ENTRY)
        .identifier(node.getId())
        .caption(node.getEntry().getName())
        .icon(node.getEntry().getIcon())
        .actions(node.getEntry().getActions());
  }

  private TreeNode getTreeNodeByPath(String path) {
    return ref.getValueRefByPath(path).getWrapper(TreeNode.class);
  }

  public void notifyAllListeners() {
    modelObservable.notifyListeners();
  }

  private String getAssociationNodeCaption(NavigationAssociation association) {
    String associationCaption = association.getCaption();
    if (Strings.isNullOrEmpty(associationCaption)) {
      return getAssociationNodeCaption(association.getMetaUri());
    } else {
      return association.getCaption();
    }
  }

  private String getAssociationNodeCaption(URI assocMetaUri) {
    String assocUriString = assocMetaUri.toString();
    if (assocUriString.contains("?")) {
      assocUriString = assocUriString.substring(0, assocUriString.indexOf("?"));
    }
    String caption = assocUriString
        .replace(":/", ".")
        .replace("/", ".")
        .replace("#", ".");
    return caption;
  }


}
