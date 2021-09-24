package org.smartbit4all.ui.vaadin.components.navigation2;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.smartbit4all.ui.api.tree.model.TreeNode;
import com.vaadin.flow.data.provider.InMemoryDataProvider;
import com.vaadin.flow.data.provider.hierarchy.AbstractHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.SerializablePredicate;


/**
 * An in-memory data provider for listing components that display hierarchical data. Uses an
 * instance of {@link TreeData} as its source of data.
 *
 * @author Vaadin Ltd
 * @since 1.2
 *
 * @param <T> data type
 */
public class TreeDataProvider
    extends AbstractHierarchicalDataProvider<TreeNode, SerializablePredicate<TreeNode>>
    implements InMemoryDataProvider<TreeNode> {

  private final TreeData treeData;

  private SerializablePredicate<TreeNode> filter = null;

  private SerializableComparator<TreeNode> sortOrder = null;

  /**
   * Constructs a new TreeDataProvider.
   * <p>
   * This data provider should be refreshed after making changes to the underlying {@link TreeData}
   * instance.
   *
   * @param treeData the backing {@link TreeData} for this provider, not {@code null}
   */
  public TreeDataProvider(TreeData treeData) {
    Objects.requireNonNull(treeData, "treeData cannot be null");
    this.treeData = treeData;
  }

  /**
   * Return the underlying hierarchical data of this provider.
   *
   * @return the underlying data of this provider
   */
  public TreeData getTreeData() {
    return treeData;
  }

  @Override
  public boolean hasChildren(TreeNode item) {
    if (!treeData.contains(item)) {
      // The item might be dropped from the tree already
      return false;
    }
    return !treeData.getChildren(item).isEmpty();
  }

  @Override
  public int getChildCount(
      HierarchicalQuery<TreeNode, SerializablePredicate<TreeNode>> query) {
    Stream<TreeNode> items;

    if (query.getParent() != null) {
      items = treeData.getChildren(query.getParent()).stream();
    } else {
      items = treeData.getRootItems().stream();
    }

    return (int) getFilteredStream(items,
        query.getFilter()).skip(query.getOffset()).limit(query.getLimit()).count();
  }

  @Override
  public Stream<TreeNode> fetchChildren(
      HierarchicalQuery<TreeNode, SerializablePredicate<TreeNode>> query) {
    if (!treeData.contains(query.getParent())) {
      throw new IllegalArgumentException("The queried item "
          + query.getParent()
          + " could not be found in the backing TreeData. "
          + "Did you forget to refresh this data provider after item removal?");
    }

    Stream<TreeNode> childStream = getFilteredStream(
        treeData.getChildren(query.getParent()).stream(),
        query.getFilter());

    Optional<Comparator<TreeNode>> comparing = Stream
        .of(query.getInMemorySorting(), sortOrder)
        .filter(Objects::nonNull)
        .reduce((c1, c2) -> c1.thenComparing(c2));

    if (comparing.isPresent()) {
      childStream = childStream.sorted(comparing.get());
    }

    return childStream.skip(query.getOffset()).limit(query.getLimit());
  }

  @Override
  public SerializablePredicate<TreeNode> getFilter() {
    return filter;
  }

  @Override
  public void setFilter(SerializablePredicate<TreeNode> filter) {
    this.filter = filter;
    refreshAll();
  }

  @Override
  public SerializableComparator<TreeNode> getSortComparator() {
    return sortOrder;
  }

  @Override
  public void setSortComparator(SerializableComparator<TreeNode> comparator) {
    sortOrder = comparator;
    refreshAll();
  }

  private Stream<TreeNode> getFilteredStream(Stream<TreeNode> stream,
      Optional<SerializablePredicate<TreeNode>> queryFilter) {
    final Optional<SerializablePredicate<TreeNode>> combinedFilter =
        filter != null ? Optional.of(queryFilter.map(filter::and).orElse(filter)) : queryFilter;
    return combinedFilter
        .map(f -> stream.filter(element -> flatten(element).anyMatch(f)))
        .orElse(stream);
  }

  private Stream<TreeNode> flatten(TreeNode element) {
    return Stream.concat(Stream.of(element),
        getTreeData().getChildren(element).stream().flatMap(this::flatten));
  }
}
