package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.org.bean.ACLEntry;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.core.object.ObjectNode;

/**
 * The API is responsible for the evaluation of the access control lists. The {@link ACL} object
 * could be an aspect attached to the objects and in a business situation. The ACL objects are
 * consist of {@link Subject}s and operations and the api evaluating the available operations by
 * comparing the subjects of the current user with the {@link ACLEntry}s and {@link Subject}s of the
 * ACL object attached to the object. The api is internal because it deals with {@link ObjectNode}s.
 */
public interface AccessControlInternalApi {

  /**
   * The name of the ACL aspect in the {@link ObjectNode#aspects()} map.
   */
  static final String ACL_ASPECT = "ACL";

  /**
   * Extract the ACL object of the given {@link ObjectNode} from the {@link ObjectNode#aspects()}.
   * If it exists then evaluates if the user has the rights to the operations to check. To identify
   * the operations available it uses the {@link SubjectManagementApi} to identify the subjects the
   * user belongs to.
   * 
   * @param userUri The user uri (latest one is assumed).
   * @param objectNode The object node to evaluate.
   * @param operationsToCheck The operations we are interested in.
   * @param subjectModel The name of the subject model the subject definitions are coming from.
   * @return If the ACL doesn't exist then returns all the operations else it compares with the
   *         subjects of the user.
   */
  Set<String> getAvailableOperationsOn(URI userUri, ObjectNode objectNode,
      List<String> operationsToCheck, String subjectModel);

  /**
   * Extract the ACL object of the given {@link ObjectNode} from the {@link ObjectNode#aspects()}.
   * If it exists then evaluates if the user has the rights to the operations to check. To identify
   * the operations available it uses the {@link SubjectManagementApi} to identify the subjects the
   * current user belongs to.
   * 
   * @param objectNode
   * @param operationsToCheck The operations we are interested in.
   * @param subjectModel The name of the subject model the subject definitions are coming from.
   * @return
   */
  Set<String> getAvailableOperationsOn(ObjectNode objectNode, List<String> operationsToCheck,
      String subjectModel);

  List<String> getAvailableOperationsOfUser(String modelName, URI userUri, ACL acl,
      List<String> operations);

  List<String> getMyAvailableOperations(String modelName, ACL acl, List<String> operations);

  Map<String, List<URI>> getUsersByOperation(String modelName, ACL acl, List<String> operations);

  boolean getMatchingSubjects(String modelName, ACL acl, List<Subject> subjects,
      List<String> requiredOperations, List<String> forbiddenOperations);

  Map<String, List<Subject>> getSubjectsByOperations(String modelName, List<String> operations,
      ACL acl);

}
