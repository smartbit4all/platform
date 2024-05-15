package org.smartbit4all.api.mdm;

import java.net.URI;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.mdm.bean.MDMModificationState;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;

/**
 * This modification api is responsible for one editing branch in an {@link MDMDefinition} and
 * manage all the operations available. This api can be initiated and accessed by the
 * {@link MasterDataManagementApi}. We can manage the lifecycle fo the {@link MDMModification} with
 * this.
 *
 * @author Peter Boros
 */
public interface MDMModificationApi {

  /**
   * Return the branch uri associated with the {@link MDMModification}.
   *
   * @return The uri of the {@link BranchEntry}.
   */
  MDMModification getModification();

  /**
   * The editing branch will be merged into be main branch. From that moment the getBranchingList()
   * return a {@link BranchedObjectEntry} list with {@link BranchingStateEnum#NOP} for all object.
   * The previous local editing branch is finished and removed from the {@link BranchEntry}. The
   * modification will be added to the archive.
   *
   */
  URI merge();

  /**
   * The current editing branch will be finished and the changes won't be merged into the main
   * branch.
   *
   */
  URI cancel();

  /**
   * The approval can be initiated if the {@link MDMDefinition#ADMIN_APPROVER_GROUP_NAME} is set.
   * The state of the modification is set to {@link MDMModificationState#APPROVING}.
   *
   * @param approver
   */
  void sendForApproval(URI approver);

  /**
   * The approval can be accepted
   */
  void approvalAccepted();

  /**
   * The approval can be rejected.
   *
   * @param reason At least one comment is necessary to reject the approval.
   */
  void approvalRejected(String reason);

  /**
   * A user can append comment to the modification.
   *
   * @param comment The comment itself.
   */
  void addComment(String comment);

  /**
   * The current user is added as editor to the modification. It is added to
   * {@link MDMModification#CURRENT_EDITORS}.
   */
  void startEditing();

  /**
   * The current user is removed from the {@link MDMModification#CURRENT_EDITORS}.
   */
  void stopEditing();

}
