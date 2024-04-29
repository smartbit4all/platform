package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.object.bean.BranchOperation.OperationTypeEnum;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.core.object.ObjectNode;

/**
 * The branch api is responsible for creating branches for objects. The branch is represented by the
 * {@link BranchEntry} that collects all the operations - {@link BranchOperation} - made on this
 * branch. The operations can be the followings:
 * <ul>
 * <li>{@link OperationTypeEnum#INIT} - This operation initiate a new object from a version of the
 * source object. The branch is a totally new object but created with shallow copy so all the
 * references (uris) in this object still refer to the original object hierarchy. If there is
 * another change then the modified object is going to be branched on demand. The first version of
 * the new object is referring to the original object version as
 * {@link ObjectVersion#getRebasedFromUri()}</li>.
 * <li>{@link OperationTypeEnum#REBASE} - This operation merges the changes from the source object
 * versions if there where any. It results a new version in the branch and this version refers the
 * rebased version of the source object. Later on during the merge the last rebase version will be
 * the common ancestor of the rebase.</li>
 * <li>{@link OperationTypeEnum#MERGE} - This operation merges the changes from the branch to the
 * source object as a new version. The merge result created as new version of the source object and
 * refers to the branch version as {@link ObjectVersion#mergedWithUri(URI)} and the last known
 * rebase version from the source object as {@link ObjectVersion#commonAncestorUri(URI)}</li>
 * </ul>
 *
 * @author Peter Boros
 */
public interface BranchApi {

  /**
   * This operation constructs a new branch. The result can be used for branching to collect all the
   * operations made.
   *
   * @param caption The caption of the branch that could be useful to identify
   * @return The branch entry with the saved URI.
   */
  BranchEntry makeBranch(String caption);

  /**
   * This operation constructs a new branch. The result can be used for branching to collect all the
   * operations made.
   *
   * @param caption The caption of the branch that could be useful to identify
   * @return The branch entry that is not saved.
   */
  BranchEntry constructBranch(String caption);

  /**
   * The snapshot branch is a branch that is already contains the branched objects even if they have
   * not been modified yet. This is used to save a state of an object graph. Later on we can use
   * this branch to compare with a new version.
   *
   * @param branchUri The branch to add the loaded reference of the nodes.
   * @param nodes The nodes to add.
   */
  void addSnapshotBranch(URI branchUri, Collection<ObjectNode> nodes);

  void registerCollection(URI branchUri, URI storedListUri, URI storedListBranchedUri,
      StoredCollectionDescriptor descriptor);

  void addNewBranchedObjects(URI branchUri,
      Collection<URI> newObjects);

  /**
   * Remove the branched object from the branch. It won't modify anything if the given object is
   * deleted!
   *
   * @param branchUri The branch uri.
   * @param branchedUri The uri of the branched object.
   * @return If it is not found then we get back null. If it was found as new or modified then we
   *         get back a branched object entry with state {@link BranchingStateEnum#NEW} or
   *         {@link BranchingStateEnum#MODIFIED}.
   */
  BranchedObjectEntry removeBranchedObject(URI branchUri, URI branchedUri);

  /**
   * The merge constructs the object nodes for the objects of the branch necessary to update.
   *
   * We have to process all the branched objects let it be explicitly or implicitly branched.
   * Starting from one branched object we traverse all the accessible branched objects via the
   * references, lists and maps of the actual object. We construct the ObjectNode with the proper
   * ObjectReference list, map and so on. If we have an object with a list of uri to process (a
   * detail list) then all the objects in the list will be examined if it is branched or not. If
   * branched (modified / new / deleted) then we make the modification on the list in a proper way.
   *
   * @param branchUri The uri of the branch.
   * @return The modified {@link ObjectNode} list that can be saved in this order to apply the merge
   *         into the storage.
   */
  List<ObjectNode> merge(URI branchUri);

  /**
   * Compare the given list with the baseline. We need to know the branch and check if the given
   * object is a modified object on the given branch. If it is modified then we load the original
   * object by the uri of the {@link BranchedObject#getSourceObjectLatestUri()} and we compare the
   * content of the two list. In every other case we assume that all the list items are unmodified.
   * If the given object is brand new then the state of all the list items are new.
   *
   * @param branchUri The uri of the branch
   * @param objectUri The uri of the object.
   * @param path The path of the list in the given object.
   * @return The ordered list of the {@link BranchedObjectEntry}.
   */
  List<BranchedObjectEntry> compareListByUri(URI branchUri, URI objectUri, String... path);

  String toStringBranchedObjectEntry(BranchedObjectEntry bo, String... path);

  /**
   * Try to remove the new object from the branch.
   *
   * @param branchUri
   * @param objectUri
   * @return
   */
  BranchedObject removeNewBranchedObjects(URI branchUri, URI objectUri);

}
