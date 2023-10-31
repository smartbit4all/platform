package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;

/**
 * This is the interface of the Master Data Management entry APIs. The base implementation of the
 * master data management api. The basic feature is that if we configure an api like into the spring
 * context then it will be managed by the {@link MasterDataManagementApi}.
 *
 * It is based on a domain bean class and initiated by the {@link MDMEntryDescriptor}. It will have
 * a name that is coming from the simple name of the class or we can set a different name. In this
 * way we can have many entries from the same class with different names.
 *
 * The key property of the given object must be set also as a String[] path that can be used by the
 * {@link ObjectApi#getValueFromObjectMap(Map, String...)} or similar methods. It is used to extract
 * the key property from the object and identify the given instance uniquely. So one entry can have
 * exactly one object published in a moment with the same identifier.
 *
 * The entries can have
 *
 * @author Peter Boros
 */
public interface MDMEntryApi {

  /**
   * @return The name of the given entry
   */
  String getName();

  MDMEntryDescriptor getDescriptor();

  String getId(Object object);

  /**
   * @return Return the published {@link StoredList} if the published list name was set.
   */
  StoredList getList();

  /**
   * @return The inactive list if the {@link MDMEntryDescriptor#INACTIVE_MGMT} is set true.
   */
  StoredList getInactiveList();

  /**
   * Save a new object according to the current state of the branch entry. If a branch is initiated
   * then the save will appear on the branch. If no branch exists the the
   *
   * @param objectNode The object node to save as draft. We check if this object is a totally new
   *        one, a an object to modify and now we have to make the branched object also. Or the
   *        given object is already included in the branch.
   * @return The list of URI for the saved draft / final version. More than one if we have a
   *         hierarchical entry when the {@link MDMEntryDescriptor#SELF_CONTAINED_REF_LIST} is set.
   */
  List<URI> save(ObjectNode objectNode);

  /*
   * Save new objects from list according to the current state of the branch entry. If a branch is
   * initiated then the save will appear on the branch.
   *
   * @param List of objectNode. The object node to save as draft. We check if this object is a
   * totally new one, a an object to modify and now we have to make the branched object also. Or the
   * given object is already included in the branch.
   * 
   * @return The list of URI for the saved draft / final version. More than one if we have a
   * hierarchical entry when the {@link MDMEntryDescriptor#SELF_CONTAINED_REF_LIST} is set.
   */
  List<URI> save(List<ObjectNode> objectNode);

  /**
   * We try to cancel the editing of the object. If it was a totally new object then we simply
   * remove it from the branched list. If it is a modified object then we undo the modification of
   * the object and replace it with the source uri. The branched object won't be removed from the
   * branch. It will be skipped if every reference is removed from the branched objects.
   *
   * @param draftUri If the given uri is a branched object let is be {@link BranchingStateEnum#NEW}
   *        or {@link BranchingStateEnum#MODIFIED} then it is going to be removed from the branch.
   *        In case of modification this operation restore the original object version. In case of
   *        new the given object disappear from the {@link #getBranchingList()} result. If we cancel
   *        an already deleted object then we remove it from the deleted object list. So it will be
   *        included in the {@link #getBranchingList()} list as {@link BranchingStateEnum#NOP}
   *        again.
   * @return If the canceling modified the state of the MDM entry at all.
   */
  boolean cancel(URI draftUri);

  /**
   * This will restore the original list but do not remove the modified objects from the branch.
   */
  void cancelAll();

  /**
   * If we already have a branch then we remove the given object from the branch. Else we remove it
   * from the list of the main branch.
   *
   * @param objectUri
   */
  boolean remove(URI objectUri);

  /**
   * The given uri is restored from the inactive list if it exists over there.
   *
   * @param objectUri
   * @return true if we found the inactive item.
   */
  boolean restore(URI objectUri);

  /**
   * Returns all the {@link BranchedObjectEntry} currently seen be an editor of the given MDM Entry.
   * It contains the published and untouched objects with {@link BranchingStateEnum#NOP} state.
   *
   * @return Return all the published objects loaded.
   */
  List<BranchedObjectEntry> getBranchingList();

  /**
   * Returns if the given MDM Entry is in edit mode on a global or entry level Branch
   *
   * @return
   */
  boolean hasBranch();

  /**
   * Returns the approver of the given MDM Entry if it is in edit mode on a global or entry level
   * Branch. May return null.
   *
   * @return
   */
  URI getApprover();

  /**
   * Returns the bracnhUri of the given MDM Entry if it is in edit mode on a global or entry level
   * Branch. May return null.
   *
   * @return
   */
  URI getBranchUri();

  /**
   * Returns the displayable name / page title of entry list.
   *
   * @return
   */
  String getDisplayNameList();

  /**
   * Returns the displayable name / page title of entry form.
   *
   * @return
   */
  String getDisplayNameForm();

}
