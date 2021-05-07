package org.smartbit4all.core.constraint;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.PropertyEntry;
import org.smartbit4all.core.object.PropertyMeta.PropertyKind;
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
public final class HierarchicalConstraintHelper<C> {

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
        new ConstraintEntry<>(defaultValue);
  }

  /**
   * This function updates the constraints on the specific path sets the scope and the constraint
   * value itself.
   * 
   * @param path The fully qualified path if the entry. Typically the path of the
   *        {@link ApiObjectRef} or its property.
   * @param scope The new scope.
   * @param value The new constraint value.
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
   * Return constraint for the given path. If it doesn't an existing entry then we can have the
   * nearest parent value that has default value for the subtree or its children.
   * 
   * @param path
   * @return
   */
  public final ConstraintEntry<C> findRelated(String path) {
    if (!StringConstant.EMPTY.contentEquals(path)) {
      String[] pathArray = path.split(StringConstant.SLASH);
      return findRelatedRec(pathArray, 0, root, root, null);
    } else {
      return root;
    }
  }

  /**
   * Construct the list of changes for the actual transaction. The rule is the following:
   * 
   * <ul>
   * <li>The given {@link ConstraintEntry} is changed, the scope is
   * {@link ConstraintEntryScope#ITEM} and unchanged: Include if we found the ApiObject item.</li>
   * <li>The given {@link ConstraintEntry} is changed, the scope is
   * {@link ConstraintEntryScope#ITEM} and something else: Include the existing ApiObject
   * items.</li>
   * </ul>
   * 
   * @param apiObjectRef The {@link ApiObjectRef} the constraints are stored for. It must be the
   *        changed object reference before calling the
   *        {@link ApiObjectRef#renderAndCleanChanges()}. This function will use the change state.
   * @return
   */
  public final List<ConstraintChange<C>> renderAndCleanChanges(ApiObjectRef apiObjectRef) {
    List<ConstraintChange<C>> changeList = new ArrayList<>();
    changeList = renderAndCleanChangesRec(changeList, apiObjectRef,
        apiObjectRef.getCurrentState() == ChangeState.NEW, root, root, root.getValue());
    root.commitChange();
    return changeList;
  }

  /**
   * This function collect the changes on the constraint hierarchy itself.
   * 
   * @return
   */
  public final List<ConstraintChange<C>> renderAndCleanChanges() {
    List<ConstraintChange<C>> result = new ArrayList<>();
    renderAndCleanChangesRec(root, result);
    return result;
  }

  private final void renderAndCleanChangesRec(ConstraintEntry<C> entry,
      List<ConstraintChange<C>> result) {
    entry.getValueChange();
  }

  /**
   * Pre order traversal of the changes. The given node is already processed when entering this
   * function.
   * 
   * @param changeList The change list to fill during the recursion.
   * @param actualApiObjectRef The {@link ApiObjectRef} that is currently managed. It's optional if
   *        we don't have api object for a given path of the constraints. In this can we add the
   *        changes only from the constraint hierarchy.
   * @param newObject A boolean that implies that this subtree of the api object is completely new.
   *        Therefore we construct the {@link ConstraintChange}s for every object without examining
   *        the changed state of the constraints.
   * @param actualEntry The actual entry that is responsible for the {@link ApiObjectRef} itself and
   *        can be used to identify the change. It's optional because if we don't have directly
   *        related constraint entry for the given path on the api object then
   * @param actualValue The nearest subtree scoped entry up in the tree defines the actualValue for
   *        the unspecified entries.
   * @return
   */
  private final List<ConstraintChange<C>> renderAndCleanChangesRec(
      List<ConstraintChange<C>> changeList, @NotNull ApiObjectRef actualApiObjectRef,
      boolean newObject,
      ConstraintEntry<C> actualEntry, @NotNull ConstraintEntry<C> nearestExistingEntry,
      C actualValue) {
    String path = actualApiObjectRef.getPath();
    ConstraintChange<C> apiObjectChange =
        evaluateEntry(newObject, actualEntry, nearestExistingEntry, actualValue, path);
    if (apiObjectChange != null) {
      changeList.add(apiObjectChange);
    }

    ConstraintEntry<C> nextNearestEntry = nearestExistingEntry;
    C nextActualValue = actualValue;
    if (actualEntry != null) {
      nextNearestEntry = actualEntry;
      if (actualEntry.getScope() == ConstraintEntryScope.SUBTREE) {
        nextActualValue = actualEntry.getValue();
      }
    }
    for (PropertyEntry propery : actualApiObjectRef.getProperties()) {
      String propertyName = propery.getMeta().getName();
      ConstraintEntry<C> childEntry =
          actualEntry == null ? null : actualEntry.children.get(propertyName);
      if (propery.getMeta().getKind() == PropertyKind.VALUE) {
        ConstraintChange<C> change =
            evaluateEntry(newObject, childEntry, nextNearestEntry,
                nextActualValue,
                path.isEmpty() ? propertyName : path + StringConstant.SLASH + propertyName);
        if (change != null) {
          changeList.add(change);
        }
      } else if (propery.getMeta().getKind() == PropertyKind.REFERENCE
          && propery.getReference() != null) {
        renderAndCleanChangesRec(changeList, propery.getReference(),
            newObject || propery.getReference().getCurrentState() == ChangeState.NEW, childEntry,
            nextNearestEntry, nextActualValue);
      } else if (propery.getMeta().getKind() == PropertyKind.COLLECTION) {
        for (ApiObjectRef collectionItem : propery.getCollection()) {
          if (collectionItem != null) {
            renderAndCleanChangesRec(changeList, collectionItem,
                newObject || collectionItem.getCurrentState() == ChangeState.NEW,
                childEntry,
                nextNearestEntry, nextActualValue);
          }
        }
      }
    }
    return changeList;
  }

  private ConstraintChange<C> evaluateEntry(boolean newObject, ConstraintEntry<C> actualEntry,
      ConstraintEntry<C> nearestExistingEntry, C actualValue, String path) {
    ConstraintChange<C> result = null;
    if (newObject) {
      if (actualEntry != null && actualEntry.getScope() != ConstraintEntryScope.NOTSET) {
        result =
            new ConstraintChange<>(path, actualEntry.getValue());
      } else {
        result = new ConstraintChange<>(path, actualValue);
      }
    }
    if (result == null) {
      result =
          evaluateEntryInner(actualEntry, nearestExistingEntry, path, actualValue);
    }
    return result;
  }

  /**
   * Calculates the effective change for an actual entry assuming the nearest existing entry if it's
   * null.
   * 
   * @param actualEntry The actual entry.
   * @param nearestExistingEntry The nearest existing entry.
   * @return If there is an effective change in the tree then it will be the result.
   */
  private final ConstraintChange<C> evaluateEntryInner(ConstraintEntry<C> actualEntry,
      @NotNull ConstraintEntry<C> nearestExistingEntry, @NotNull String path, C actualValue) {
    ConstraintChange<C> result = null;
    ConstraintEntry<C> effectiveEntry = actualEntry == null ? nearestExistingEntry : actualEntry;
    if (effectiveEntry.isScopeChanged()) {
      if (effectiveEntry.getScope() == ConstraintEntryScope.NOTSET) {
        // We use the actual value.
        return new ConstraintChange<>(path, actualValue);
      } else {
        // We use the value if the actual entry.
        return new ConstraintChange<>(path, effectiveEntry.getValue());
      }
    } else if (effectiveEntry.getScope() != ConstraintEntryScope.NOTSET
        && effectiveEntry.isValueChanged()) {
      return new ConstraintChange<>(path, effectiveEntry.getValue());
    } else if (effectiveEntry.getScope() == ConstraintEntryScope.NOTSET) {
      ConstraintEntry<C> changedParentEntry = effectiveEntry.getChangedParentEntry();
      if (changedParentEntry != null) {
        return new ConstraintChange<>(path, changedParentEntry.getValue());
      }
    }
    return result;
  }

  /**
   * Recursive function to find the related {@link ConstraintEntry} for the given path.
   * 
   * @param pathArray
   * @param index
   * @param actualEntry
   * @param nearestSubTree
   * @param nearestChildren
   * @return
   */
  private ConstraintEntry<C> findRelatedRec(String[] pathArray, int index,
      ConstraintEntry<C> actualEntry, ConstraintEntry<C> nearestSubTree,
      ConstraintEntry<C> nearestChildren) {
    int nextIndex = index + 1;
    String pathFragment = pathArray[index];
    ConstraintEntry<C> childEntry = actualEntry.children.get(pathFragment);
    if (childEntry == null) {
      // We reached the end of the existing path. We return by the current state.
      if (nearestChildren != null) {
        return nearestChildren;
      } else if (nearestSubTree != null) {
        return nearestSubTree;
      }
      return null;
    }
    // If it was the last fragment in the path then we arrived.
    if (nextIndex == pathArray.length) {
      return childEntry;
    }
    ConstraintEntry<C> nextNearestSubTree = nearestSubTree;
    ConstraintEntry<C> nextNearestChildren = null;
    switch (childEntry.getScope()) {
      case CHILDREN:
        nextNearestChildren = childEntry;
        break;
      case SUBTREE:
        nextNearestSubTree = childEntry;
        break;

      default:
        break;
    }


    return findRelatedRec(pathArray, nextIndex, childEntry, nextNearestSubTree,
        nextNearestChildren);
  }

  /**
   * This utility method traverse the hierarchy to find the {@link ConstraintEntry} on the given
   * path. We follow the path and if it's missing then we add all the items.
   * 
   * @param path
   * @return
   */
  private final ConstraintEntry<C> findOrCreate(String path) {
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
  private final ConstraintEntry<C> findOrCreateRec(String[] pathArray, int index,
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
