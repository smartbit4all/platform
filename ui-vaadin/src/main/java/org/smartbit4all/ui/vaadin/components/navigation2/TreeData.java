package org.smartbit4all.ui.vaadin.components.navigation2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.ui.api.tree.model.TreeNode;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.function.ValueProvider;

public class TreeData implements Serializable {

  private static class HierarchyWrapper implements Serializable {
    private TreeNode parent;
    private List<TreeNode> children;

    public HierarchyWrapper(TreeNode parent) {
      this.parent = parent;
      children = new ArrayList<>();
    }

    public TreeNode getParent() {
      return parent;
    }

    public void setParent(TreeNode parent) {
      this.parent = parent;
    }

    public List<TreeNode> getChildren() {
      return children;
    }

    public void addChild(TreeNode child) {
      children.add(child);
    }

    public void removeChild(TreeNode child) {
      children.remove(child);
    }
  }

  private final Map<String, HierarchyWrapper> itemToWrapperMap;

  /**
   * Creates an initially empty hierarchical data representation to which items can be added or
   * removed.
   */
  public TreeData() {
    itemToWrapperMap = new LinkedHashMap<>();
    itemToWrapperMap.put(null, new HierarchyWrapper(null));
  }

  /**
   * Adds the items as root items to this structure.
   *
   * @param items the items to add
   * @return this
   *
   * @throws IllegalArgumentException if any of the given items have already been added to this
   *         structure
   * @throws NullPointerException if any of the items are {code null}
   */
  public TreeData addRootItems(TreeNode... items) {
    addItems(null, items);
    return this;
  }

  /**
   * Adds the items of the given collection as root items to this structure.
   *
   * @param items the collection of items to add
   * @return this
   *
   * @throws IllegalArgumentException if any of the given items have already been added to this
   *         structure
   * @throws NullPointerException if any of the items are {code null}
   */
  public TreeData addRootItems(Collection<TreeNode> items) {
    addItems(null, items);
    return this;
  }

  /**
   * Adds the items of the given stream as root items to this structure.
   *
   * @param items the stream of root items to add
   * @return this
   *
   * @throws IllegalArgumentException if any of the given items have already been added to this
   *         structure
   * @throws NullPointerException if any of the items are {code null}
   */
  public TreeData addRootItems(Stream<TreeNode> items) {
    addItems(null, items);
    return this;
  }

  /**
   * Adds a data item as a child of {@code parent}. Call with {@code null} as parent to add a root
   * level item. The given parent item must already exist in this structure, and an item can only be
   * added to this structure once.
   *
   * @param parent the parent item for which the items are added as children
   * @param item the item to add
   * @return this
   *
   * @throws IllegalArgumentException if parent is not null and not already added to this structure
   * @throws IllegalArgumentException if the item has already been added to this structure
   * @throws NullPointerException if item is null
   */
  public TreeData addItem(TreeNode parent, TreeNode item) {
    Objects.requireNonNull(item, "Item cannot be null");
    if (parent != null && !contains(parent)) {
      throw new IllegalArgumentException(
          "Parent needs to be added before children. "
              + "To add root items, call with parent as null");
    }
    if (contains(item)) {
      throw new IllegalArgumentException(
          "Cannot add the same item multiple times: " + item);
    }
    putItem(item, parent);
    return this;
  }

  /**
   * Adds a list of data items as children of {@code parent}. Call with {@code null} as parent to
   * add root level items. The given parent item must already exist in this structure, and an item
   * can only be added to this structure once.
   *
   * @param parent the parent item for which the items are added as children
   * @param items the list of items to add
   * @return this
   *
   * @throws IllegalArgumentException if parent is not null and not already added to this structure
   * @throws IllegalArgumentException if any of the given items have already been added to this
   *         structure
   * @throws NullPointerException if any of the items are null
   */
  public TreeData addItems(TreeNode parent,
      @SuppressWarnings("unchecked") TreeNode... items) {
    Arrays.stream(items).forEach(item -> addItem(parent, item));
    return this;
  }

  /**
   * Adds a list of data items as children of {@code parent}. Call with {@code null} as parent to
   * add root level items. The given parent item must already exist in this structure, and an item
   * can only be added to this structure once.
   *
   * @param parent the parent item for which the items are added as children
   * @param items the collection of items to add
   * @return this
   *
   * @throws IllegalArgumentException if parent is not null and not already added to this structure
   * @throws IllegalArgumentException if any of the given items have already been added to this
   *         structure
   * @throws NullPointerException if any of the items are null
   */
  public TreeData addItems(TreeNode parent, Collection<TreeNode> items) {
    items.forEach(item -> addItem(parent, item));
    return this;
  }

  /**
   * Adds data items contained in a stream as children of {@code parent}. Call with {@code null} as
   * parent to add root level items. The given parent item must already exist in this structure, and
   * an item can only be added to this structure once.
   *
   * @param parent the parent item for which the items are added as children
   * @param items stream of items to add
   * @return this
   *
   * @throws IllegalArgumentException if parent is not null and not already added to this structure
   * @throws IllegalArgumentException if any of the given items have already been added to this
   *         structure
   * @throws NullPointerException if any of the items are null
   */
  public TreeData addItems(TreeNode parent, Stream<TreeNode> items) {
    items.forEach(item -> addItem(parent, item));
    return this;
  }

  /**
   * Adds the given items as root items and uses the given value provider to recursively populate
   * children of the root items.
   *
   * @param rootItems the root items to add
   * @param childItemProvider the value provider used to recursively populate this TreeData from the
   *        given root items
   * @return this
   */
  public TreeData addItems(Collection<TreeNode> rootItems,
      ValueProvider<TreeNode, Collection<TreeNode>> childItemProvider) {
    rootItems.forEach(item -> {
      addItem(null, item);
      Collection<TreeNode> childItems = childItemProvider.apply(item);
      addItems(item, childItems);
      addItemsRecursively(childItems, childItemProvider);
    });
    return this;
  }

  /**
   * Adds the given items as root items and uses the given value provider to recursively populate
   * children of the root items.
   *
   * @param rootItems the root items to add
   * @param childItemProvider the value provider used to recursively populate this TreeData from the
   *        given root items
   * @return this
   */
  public TreeData addItems(Stream<TreeNode> rootItems,
      ValueProvider<TreeNode, Stream<TreeNode>> childItemProvider) {
    // Must collect to lists since the algorithm iterates multiple times
    return addItems(rootItems.collect(Collectors.toList()),
        item -> childItemProvider.apply(item)
            .collect(Collectors.toList()));
  }

  /**
   * Remove a given item from this structure. Additionally, this will recursively remove any
   * descendants of the item.
   *
   * @param item the item to remove, or null to clear all data
   * @return this
   *
   * @throws IllegalArgumentException if the item does not exist in this structure
   */
  public TreeData removeItem(TreeNode item) {
    if (!contains(item)) {
      throw new IllegalArgumentException(
          "Item '" + item + "' not in the hierarchy");
    }
    new ArrayList<>(getChildren(item)).forEach(child -> removeItem(child));
    itemToWrapperMap.get(getIdentifier(itemToWrapperMap.get(getIdentifier(item)).getParent()))
        .removeChild(item);
    if (item != null) {
      // remove non root item from backing map
      itemToWrapperMap.remove(getIdentifier(item));
    }
    return this;
  }

  /**
   * Clear all items from this structure. Shorthand for calling {@link #removeItem(Object)} with
   * null.
   *
   * @return this
   */
  public TreeData clear() {
    removeItem(null);
    return this;
  }

  /**
   * Gets the root items of this structure.
   *
   * @return an unmodifiable list of root items of this structure
   */
  public List<TreeNode> getRootItems() {
    return getChildren(null);
  }

  /**
   * Get the immediate child items for the given item.
   *
   * @param item the item for which to retrieve child items for, null to retrieve all root items
   * @return an unmodifiable list of child items for the given item
   *
   * @throws IllegalArgumentException if the item does not exist in this structure
   */
  public List<TreeNode> getChildren(TreeNode item) {
    if (!contains(item)) {
      throw new IllegalArgumentException(
          "Item '" + item + "' not in the hierarchy");
    }
    return Collections
        .unmodifiableList(itemToWrapperMap.get(getIdentifier(item)).getChildren());
  }

  /**
   * Get the parent item for the given item.
   *
   * @param item the item for which to retrieve the parent item for
   * @return parent item for the given item or {@code null} if the item is a root item.
   * @throws IllegalArgumentException if the item does not exist in this structure
   */
  public TreeNode getParent(TreeNode item) {
    if (!contains(item)) {
      throw new IllegalArgumentException(
          "Item '" + item + "' not in hierarchy");
    }
    return itemToWrapperMap.get(getIdentifier(item)).getParent();
  }

  /**
   * Moves an item to become a child of the given parent item. The new parent item must exist in the
   * hierarchy. Setting the parent to {@code null} makes the item a root item. After making changes
   * to the tree data, {@link TreeDataProvider#refreshAll()} should be called.
   *
   * @param item the item to be set as the child of {@code parent}
   * @param parent the item to be set as parent or {@code null} to set the item as root
   */
  public void setParent(TreeNode item, TreeNode parent) {
    if (!contains(item)) {
      throw new IllegalArgumentException(
          "Item '" + item + "' not in the hierarchy");
    }

    if (parent != null && !contains(parent)) {
      throw new IllegalArgumentException(
          "Parent needs to be added before children. "
              + "To set as root item, call with parent as null");
    }

    if (item.equals(parent)) {
      throw new IllegalArgumentException(
          "Item cannot be the parent of itself");
    }

    TreeNode oldParent = itemToWrapperMap.get(getIdentifier(item)).getParent();

    if (!Objects.equals(oldParent, parent)) {
      // Remove item from old parent's children
      itemToWrapperMap.get(getIdentifier(oldParent)).removeChild(item);

      // Add item to parent's children
      itemToWrapperMap.get(getIdentifier(parent)).addChild(item);

      // Set item's new parent
      itemToWrapperMap.get(getIdentifier(item)).setParent(parent);
    }
  }

  /**
   * Moves an item to the position immediately after a sibling item. The two items must have the
   * same parent. After making changes to the tree data, {@link TreeDataProvider#refreshAll()}
   * should be called.
   *
   * @param item the item to be moved
   * @param sibling the item after which the moved item will be located, or {@code
   *         null} to move item to first position
   */
  public void moveAfterSibling(TreeNode item, TreeNode sibling) {
    if (!contains(item)) {
      throw new IllegalArgumentException(
          "Item '" + item + "' not in the hierarchy");
    }

    if (sibling == null) {
      List<TreeNode> children = itemToWrapperMap.get(getIdentifier(getParent(item)))
          .getChildren();

      // Move item to first position
      children.remove(item);
      children.add(0, item);
    } else {
      if (!contains(sibling)) {
        throw new IllegalArgumentException(
            "Item '" + sibling + "' not in the hierarchy");
      }

      TreeNode parent = itemToWrapperMap.get(getIdentifier(item)).getParent();

      if (!Objects.equals(parent,
          itemToWrapperMap.get(getIdentifier(sibling)).getParent())) {
        throw new IllegalArgumentException("Items '" + item + "' and '"
            + sibling + "' don't have the same parent");
      }

      List<TreeNode> children = itemToWrapperMap.get(getIdentifier(parent)).getChildren();

      // Move item to the position after the sibling
      children.remove(item);
      children.add(children.indexOf(sibling) + 1, item);
    }
  }

  /**
   * Check whether the given item is in this hierarchy.
   *
   * @param item the item to check
   * @return {@code true} if the item is in this hierarchy, {@code false} if not
   */
  public boolean contains(TreeNode item) {
    return itemToWrapperMap.containsKey(getIdentifier(item));
  }

  private void putItem(TreeNode item, TreeNode parent) {
    HierarchyWrapper wrappedItem = new HierarchyWrapper(parent);
    if (itemToWrapperMap.containsKey(getIdentifier(parent))) {
      itemToWrapperMap.get(getIdentifier(parent)).addChild(item);
    }
    itemToWrapperMap.put(getIdentifier(item), wrappedItem);
  }

  private void addItemsRecursively(Collection<TreeNode> items,
      ValueProvider<TreeNode, Collection<TreeNode>> childItemProvider) {
    items.forEach(item -> {
      Collection<TreeNode> childItems = childItemProvider.apply(item);
      addItems(item, childItems);
      addItemsRecursively(childItems, childItemProvider);
    });
  }

  private String getIdentifier(TreeNode node) {
    return node == null ? null : node.getIdentifier();
  }
}
