package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor.BranchStrategyEnum;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
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
  StoredList getPublishedList();

  /**
   * @return Returns the version URI of the currently published objects.
   */
  StoredMap getPublishedMap();

  /**
   * Save the given object as published.
   *
   * @param object The object to save.
   * @return The URI of the currently saved draft version.
   */
  URI saveAsNewPublished(Object object);

  /**
   * Save the given object as published.
   * 
   * @param objectDefinition The object definition of the entry.
   * @param objectAsMap The object as map.
   * @return
   */
  URI saveAsNewPublished(ObjectDefinition<?> objectDefinition, Map<String, Object> objectAsMap);

  /**
   * Save a new object according to the current state of the branch entry. If a branch is initiated
   * then the save will appear on the branch. If no branch exists the the
   *
   * @param objectNode The object node to save as draft. We check if this object is a totally new
   *        one, a an object to modify and now we have to make the branched object also. Or the
   *        given object is already included in the branch.
   * @return The URI of the currently saved draft version.
   */
  URI save(ObjectNode objectNode);

  /**
   * The draft object is removed from the base list. If it was a totally new object then we simply
   * remove it from the branched list. If it is a modified object then we undo the modification of
   * the object and replace it with the source uri. The branched object won't be removed from the
   * branch. It will be skipped if every reference is removed from the branched objects.
   * 
   * @param draftUri If the given uri is a branched object let is be {@link BranchingStateEnum#NEW}
   *        or {@link BranchingStateEnum#MODIFIED} then it is going to be removed from the branch.
   *        In case of modification this operation restore the original object version. In case of
   *        new the given object disappear from the {@link #getAllEntries()} result. If we cancel an
   *        already deleted object then we remove it from the deleted object list. So it will be
   *        included in the {@link #getAllEntries()} list as {@link BranchingStateEnum#NOP} again.
   * @return If the canceling modified the state of the MDM entry at all.
   */
  boolean cancelDraft(URI draftUri);

  /**
   * If we already have a The delete will initiate a editing branch of it doesn't exist. Register
   * the given object to delete if it is a published object. If it is a branched object with
   * {@link BranchingStateEnum#NEW} then it is equivalent to canceling the draft object. If the
   * state is {@link BranchingStateEnum#MODIFIED} then the branched object is canceled first and
   * then the original uri will be registered to delete.
   * 
   * @param objectUri
   */
  boolean deleteObject(URI objectUri);

  /**
   * This will discard all the modified draft objects of the given given master data list.
   */
  void cancelCurrentModifications();

  /**
   * The current editing branch will be committed to be published. From that moment the
   * {@link #getAllEntries()} return a {@link BranchedObjectEntry} list with
   * {@link BranchingStateEnum#NOP} for all object. The previous local editing branch is finished
   * and removed from the {@link BranchEntry}.
   * 
   * @return The {@link BranchEntry} that has been finalized during the operation.
   */
  BranchEntry publishCurrentModifications();

  /**
   * Collect all the new and modified entries from the branch if there is any.
   * 
   * @return Returns BranchedObjectEntry for every new or branched object. The
   *         {@link BranchedObjectEntry#ORIGINAL_URI} points to the version uri of the original
   *         object if it is a {@link BranchingStateEnum#MODIFIED} object. The
   *         {@link BranchedObjectEntry#BRANCH_URI} points to the version uri of the branched object
   *         or to the new object.
   */
  List<BranchedObjectEntry> getDraftEntries();

  /**
   * Returns all the {@link BranchedObjectEntry} currently seen be an editor of the given MDM Entry.
   * It contains the published and untouched objects with {@link BranchingStateEnum#NOP} state.
   * 
   * @return Return all the published objects loaded.
   */
  List<BranchedObjectEntry> getAllEntries();

  /**
   * @return The branching strategy of the given entry that can be
   *         {@link BranchStrategyEnum#ENTRYLEVEL} if the draft management of the given entry
   *         defines a local private branch for the given entry. Or it can be
   *         {@link BranchStrategyEnum#GLOBAL} if modification of the given entry joins to a global
   *         branch.
   */
  BranchStrategyEnum getBranchStrategy();

}
