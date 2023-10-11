package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.util.Strings;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.org.bean.ACLEntry;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.Subject.CondEnum;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
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

  @Autowired
  private ObjectApi objectApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Override
  public Set<String> getAvailableOperationsOn(URI userUri, ObjectNode objectNode,
      List<String> operationsToCheck, String subjectModel) {
    ACL acl = objectNode.aspects().get(ACL_ASPECT, ACL.class);
    if (acl != null) {
      return new HashSet<>(
          getAvailableOperationsOfUser(subjectModel, userUri, acl, operationsToCheck));
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

  @Override
  public List<String> getAvailableOperationsOfUser(String modelName, URI userUri, ACL acl,
      List<String> operations) {
    if (operations == null) {
      return Collections.emptyList();
    }
    if (acl == null || acl.getEntries().isEmpty()) {
      return operations;
    }
    List<Subject> subjectsOfUser = subjectManagementApi.getSubjectsOfUser(modelName, userUri);
    return evaluateACL(acl, subjectsOfUser, operations);
  }

  @Override
  public List<String> getMyAvailableOperations(String modelName, ACL acl, List<String> operations) {
    if (sessionApi != null && !Strings.isEmpty(modelName)) {
      URI sessionUri = sessionApi.getSessionUri();
      URI userUri = sessionApi.getUserUri();
      if (sessionUri != null && userUri != null) {
        return getAvailableOperationsOfUser(modelName, userUri, acl, operations);
      }
    }
    return operations;
  }

  private final List<String> evaluateACL(ACL acl, List<Subject> subjects, List<String> operations) {
    // Should be cached.
    Map<URI, Subject> subjectMap =
        subjects.stream().collect(toMap(s -> objectApi.getLatestUri(s.getRef()), s -> s));
    Map<CondEnum, List<ACLEntry>> entriesByCond = acl.getEntries().stream()
        .filter(e -> subjectMap.containsKey(objectApi.getLatestUri(e.getSubject().getRef())))
        .collect(groupingBy(e -> e.getSubject().getCond()));
    List<String> result = new ArrayList<>();
    List<ACLEntry> inList = entriesByCond.get(CondEnum.IN);
    if (inList != null && !inList.isEmpty()) {
      result.addAll(inList.stream().flatMap(e -> e.getOperations().stream()).collect(toList()));
    } else {
      // By default all the operations are available.
      result.addAll(operations);
    }
    // Now we have the positive explicitly set operations. We have to remove the forbidden ones.
    List<ACLEntry> notInLIst = entriesByCond.get(CondEnum.NOTIN);
    if (notInLIst != null && !notInLIst.isEmpty()) {
      Set<String> forbiddenOperations =
          notInLIst.stream().flatMap(e -> e.getOperations().stream()).collect(toSet());
      result.removeIf(o -> forbiddenOperations.contains(o));
    }
    return result;
  }

}
