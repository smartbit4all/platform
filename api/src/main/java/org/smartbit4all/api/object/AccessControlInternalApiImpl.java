package org.smartbit4all.api.object;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.org.bean.ACLEntry;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * The implementation of the {@link AccessControlInternalApi}.
 * 
 * @author Peter Boros
 */
public final class AccessControlInternalApiImpl implements AccessControlInternalApi {

  @Autowired
  private SubjectManagementApi subjectManagementApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Override
  public Set<String> getAvailableOperationsOn(URI userUri, ObjectNode objectNode,
      List<String> operationsToCheck, String subjectModel) {
    ACL acl = objectNode.aspects().get(ACL_ASPECT, ACL.class);
    if (acl != null) {
      Map<URI, Subject> subjectsOfUser =
          subjectManagementApi.getSubjectsOfUser(subjectModel, userUri).stream()
              .collect(toMap(s -> s.getRef(), s -> s));
      Set<String> operations = new HashSet<>();
      for (ACLEntry aclEntry : acl.getEntries()) {
        Subject subject = subjectsOfUser.get(aclEntry.getSubject().getRef());
        if (subject != null) {
          operations.addAll(aclEntry.getOperations());
        }
      }
      return operationsToCheck.stream().filter(s -> operations.contains(s)).collect(toSet());
    }
    // If we have no ACL then we can access all the operations.
    return new HashSet<>(operationsToCheck);
  }

  @Override
  public Set<String> getAvailableOperationsOn(ObjectNode objectNode,
      List<String> operationsToCheck, String subjectModel) {
    if (sessionApi == null) {
      return new HashSet<>(operationsToCheck);
    }
    return getAvailableOperationsOn(sessionApi.getUserUri(), objectNode, operationsToCheck,
        subjectModel);
  }

}
