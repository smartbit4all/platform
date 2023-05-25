package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.object.bean.BranchOperation.OperationTypeEnum;
import org.smartbit4all.api.storage.bean.ObjectVersion;

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

}
