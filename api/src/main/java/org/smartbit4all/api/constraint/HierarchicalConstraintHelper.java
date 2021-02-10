package org.smartbit4all.api.constraint;

import org.smartbit4all.core.utility.StringConstant;

/**
 * This can store constraints with given merge rules for a hierarchy. At a given level the
 * constraint can be defined or not. The directly defined constraint is stronger than the generic
 * ones.
 * 
 * root: (true, *) --> All node is true under the root. (Like everything is editable)
 * 
 * root/referred: (false, *) --> The referred path is an exception under the root. This sub tree is
 * false.
 * 
 * root/referred/myProperty (true, -) --> The myProperty of the referred object is editable in spite
 * of the whole referred object is not editable.
 * 
 * The root is the empty string.
 * 
 * @author Peter Boros
 */
public class HierarchicalConstraintHelper<C> {

  /**
   * The root entry that holds the default value for the constraint.
   */
  private final ConstraintEntry<C> root;

  /**
   * Initialize the helper with a default value valid for the whole tree.
   * 
   * @param defaultValue
   */
  public HierarchicalConstraintHelper(C defaultValue) {
    super();
    this.root =
        new ConstraintEntry<>(StringConstant.EMPTY, ConstraintEntryScope.SUBTREE, defaultValue);
  }

  /**
   * 
   * @param path
   * @param scope
   * @param value
   */
  public void set(String path, ConstraintEntryScope scope, C value) {
    // We save the changes in order and at the end of the transaction we produce the modification of
    // the tree and the changes at the same time. The subscriptions will be necessary to produce as
    // many changes as we need to avoid unnecessary change event.

    ConstraintEntry<C> constraintEntry = findOrCreate(path);
    if (constraintEntry != null) {
      constraintEntry.setScope(scope);
      constraintEntry.setValue(value);
    }
  }

  /**
   * This utility method traverse the hierarchy to find the {@link ConstraintEntry} on the given
   * path. We follow the path and if it's missing then we add all the items.
   * 
   * @param path
   * @return
   */
  private ConstraintEntry<C> findOrCreate(String path) {
    if (!StringConstant.EMPTY.contentEquals(path)) {
      String[] pathArray = path.split(StringConstant.SLASH);
      return findOrCreateRec(pathArray, 0, root, false);
    } else {
      return root;
    }
  }

  /**
   * The utility that is responsible for managing the constraint entry hierarchy. Work on a path and
   * constructs the missing entries to reach the end of te path.
   * 
   * @param pathArray
   * @param index
   * @param actualEntry
   * @param newEntry
   * @return
   */
  private ConstraintEntry<C> findOrCreateRec(String[] pathArray, int index,
      ConstraintEntry<C> actualEntry, boolean newEntry) {
    // If it's not a new entry then we try to find the child with the actual path fragment.
    ConstraintEntry<C> childEntry = null;
    String pathFragment = pathArray[index];
    boolean newChildCreated = false;
    if (!newEntry) {
      childEntry = actualEntry.children.get(pathFragment);
    }
    if (childEntry == null) {
      // In this case we construct the entry as new and continue the recursion
      childEntry =
          new ConstraintEntry<>(actualEntry, pathFragment, ConstraintEntryScope.ITEM, null);
      newChildCreated = true;
    }
    // If we reached the end of the path then we stop and return the new child as a result. Else we
    // continue the processing of the path.
    int nextIndex = index + 1;
    if (nextIndex == pathArray.length) {
      // We reached the end of the path.
      return childEntry;
    } else {
      return findOrCreateRec(pathArray, nextIndex, childEntry, newChildCreated);
    }
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    root.printContent(b, StringConstant.EMPTY);
    return b.toString();
  }

}
