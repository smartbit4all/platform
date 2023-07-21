package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.object.bean.BranchOperation.OperationTypeEnum;
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
   * Initiate a version from the URI. The operation checks if we already have a branch from the
   * given object. Until the duration of the check it locks the branch to avoid parallel operations.
   * If the branch already exists then we get back the first version of this.
   * 
   * @param branchUri The URI of the branch to be used as branch for the operation.
   * @param brachedObjects The version URI map of the branched objects mapped by the source version
   *        URIs.
   * @return The constructed or already existing branched object init {@link BranchOperation} mapped
   *         by the original version uri.
   */
  Map<URI, BranchOperation> initBranchedObjects(URI branchUri,
      Map<URI, Supplier<URI>> brachedObjects);

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
   * Register the given object to delete if it is not already involved in the branch. If it is a
   * branched object with {@link BranchingStateEnum#NEW} then it is equivalent to canceling the
   * branch object. If the state is {@link BranchingStateEnum#MODIFIED} then the branched object is
   * canceled first and then the original uri will be registered to delete. Remove the branched
   * object from the branch. It won't modify anything if the given object is deleted!
   * 
   * @param branchUri The branch uri.
   * @param objectUri The uri of the object to register as deleted.
   * @return If it is not found then we get back false. If it was found as new or modified then we
   *         get back a branched object entry with state {@link BranchingStateEnum#NEW} or
   *         {@link BranchingStateEnum#MODIFIED}.
   */
  boolean deleteObject(URI branchUri, URI objectUri);

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
   * @return
   */
  List<ObjectNode> merge(URI branchUri);

}
