package org.smartbit4all.api.object;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.logging.log4j.util.Strings;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.org.bean.ACLEntry;
import org.smartbit4all.api.org.bean.ACLEntry.EntryKindEnum;
import org.smartbit4all.api.org.bean.ACLEntry.SetOperationEnum;
import org.smartbit4all.api.org.bean.ACLObject;
import org.smartbit4all.api.org.bean.ACLOperation;
import org.smartbit4all.api.org.bean.ACLOperationReference;
import org.smartbit4all.api.org.bean.ACLSubject;
import org.smartbit4all.api.org.bean.ACLSubjectOperationModification;
import org.smartbit4all.api.org.bean.ACLSubjectOperations;
import org.smartbit4all.api.org.bean.ACLSubjectSubscription;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.SubjectCondition;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

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

  @Autowired
  @Lazy
  private OrgApi orgApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Autowired
  private CollectionApi collectionApi;

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
    if (acl == null || acl.getRootEntry() == null || acl.getRootEntry().getEntries().isEmpty()) {
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
    Map<SubjectCondition, List<ACLEntry>> entriesByCond =
        acl.getRootEntry().getEntries().stream()
            .filter(e -> subjectMap.containsKey(objectApi.getLatestUri(e.getSubject().getRef())))
            .collect(groupingBy(e -> e.getSubjectCondition()));
    List<String> result = new ArrayList<>();
    List<ACLEntry> inList = entriesByCond.get(SubjectCondition.IN);
    if (inList != null && !inList.isEmpty()) {
      result.addAll(inList.stream().flatMap(e -> e.getOperations().stream()).collect(toList()));
    } else {
      // If the acl is empty then by default all the operations are available.
      // else nothing.
      if (acl.getRootEntry().getEntries().isEmpty()) {
        result.addAll(operations);
      }
    }
    // Now we have the positive explicitly set operations. We have to remove the forbidden ones.
    List<ACLEntry> notInLIst = entriesByCond.get(SubjectCondition.NOTIN);
    if (notInLIst != null && !notInLIst.isEmpty()) {
      Set<String> forbiddenOperations =
          notInLIst.stream().flatMap(e -> e.getOperations().stream()).collect(toSet());
      result.removeIf(forbiddenOperations::contains);
    }
    return result;
  }

  @Override
  public Map<String, List<URI>> getUsersByOperation(String modelName, ACL acl,
      List<String> operations) {
    Map<SubjectCondition, List<ACLEntry>> entriesByCond =
        acl.getRootEntry().getEntries().stream().collect(groupingBy(e -> e.getSubjectCondition()));
    Map<String, List<URI>> result;
    List<ACLEntry> inList = entriesByCond.get(SubjectCondition.IN);
    // if (inList != null && !inList.isEmpty()) {
    // List<User> allUsers = orgApi.getAllUsers();
    // List<URI> allUserUris = allUsers.stream().map(u -> u.getUri()).collect(toList());
    // result = operations.stream().collect(toMap(o -> o, o -> allUserUris));
    // } else {
    // // We construct the subjects for every operation
    // result = getUsersByOpartion(modelName, operations, inList);
    // }

    if (inList != null && !inList.isEmpty()) {
      // We construct the subjects for every operation
      result = getUsersByOperation(modelName, operations, inList);
    } else {
      List<User> allUsers = orgApi.getAllUsers();
      List<URI> allUserUris = allUsers.stream().map(u -> u.getUri()).collect(toList());
      result = operations.stream().collect(toMap(o -> o, o -> allUserUris));

    }
    // Now we have the positive explicitly set operations. We have to remove the forbidden ones.
    List<ACLEntry> notInLIst = entriesByCond.get(SubjectCondition.NOTIN);
    if (notInLIst != null && !notInLIst.isEmpty()) {
      Map<String, List<URI>> forbiddenUsersByOperation =
          getUsersByOperation(modelName, operations, notInLIst);
      for (Entry<String, List<URI>> entry : forbiddenUsersByOperation.entrySet()) {
        List<URI> positiveList = result.get(entry.getKey());
        positiveList.removeAll(entry.getValue());
      }
    }
    return result;
  }

  private final Map<String, List<URI>> getUsersByOperation(String modelName,
      List<String> operations,
      List<ACLEntry> inList) {
    Map<String, List<URI>> result;
    result = operations.stream()
        .collect(toMap(o -> o, o -> subjectManagementApi.getUsersOf(modelName, inList.stream()
            .filter(a -> a.getOperations().contains(o))
            .map(a -> {
              return a.getSubject();
            })
            .collect(toList()))));
    return result;
  }

  @Override
  public final Map<String, List<ACLSubject>> getSubjectsByOperations(List<String> operations,
      ACL acl) {
    if (acl == null || acl.getRootEntry() == null) {
      return Collections.emptyMap();
    }
    return acl.getRootEntry().getEntries().stream().flatMap(
        e -> e.getOperations().stream()
            .map(
                op -> {
                  if (operations.contains(op)) {
                    // Try to find the relevant ACLOperation or create a new one if it is not
                    // exists.
                    Optional<ACLOperation> firstMatchinOp = e.getOperationObjects().stream()
                        .filter(aclOp -> op.equals(aclOp.getName())).findFirst();
                    return new ACLSubject()
                        .subject(e.getSubject())
                        .operation(firstMatchinOp.orElseGet(() -> new ACLOperation().name(op)));
                  }
                  return null;
                })
            .filter(Objects::nonNull))
        .collect(groupingBy(aclSubject -> aclSubject.getOperation().getName()));
  }

  private final List<ACLEntry> getEntriesByOperation(String operation,
      ACL acl) {
    if (acl == null || acl.getRootEntry() == null) {
      return Collections.emptyList();
    }
    return acl.getRootEntry().getEntries().stream().filter(
        e -> e.getOperations().contains(operation))
        .collect(toList());
  }

  @Override
  public boolean getMatchingSubjects(String modelName, ACL acl, List<Subject> subjects,
      List<String> requiredOperations, List<String> forbiddenOperations) {
    // getUsersByOperation(modelName, acl, forbiddenOperations)
    // if(acl == null || acl.getRootEntry() == null || )
    return true;
  }

  @Override
  public List<ACLSubject> getSubjects(ACL acl, String operation) {
    Map<String, List<ACLSubject>> subjectsByOperations =
        getSubjectsByOperations(Arrays.asList(operation), acl);
    return subjectsByOperations.getOrDefault(operation, new ArrayList<>());
  }

  @Override
  public ACL applySubjects(ACL acl, List<ACLSubject> subjects, String operation) {
    return applySubjects(acl, subjects, operation, null, null);
  }


  private final String constructSubjectId(Subject subject) {
    return subject.getModel() + StringConstant.HYPHEN + subject.getType();
  }

  private final String constructSubjectIdWithRef(Subject subject) {
    return subject.getModel() + StringConstant.HYPHEN + subject.getType() + StringConstant.HYPHEN
        + objectApi.getLatestUri(subject.getRef());
  }

  @Override
  public ACL applySubjects(ACL acl, List<ACLSubject> subjects, String operation, URI contextEntity,
      String contextConfigCode) {
    // Find all the entries currently attached to the operation in the ACL.
    Map<String, ACLEntry> currentEntries = getEntriesByOperation(operation, acl).stream()
        .collect(toMap(e -> subjectManagementApi.toString(e.getSubject()), e -> e));
    List<ACLSubject> toAdd = new ArrayList<>();
    for (ACLSubject aclSubject : subjects) {
      ACLEntry currentEntry =
          currentEntries.remove(subjectManagementApi.toString(aclSubject.getSubject()));
      if (currentEntry == null) {
        toAdd.add(aclSubject);
      } else {
        if (!currentEntry.getOperations().contains(operation)) {
          currentEntry.addOperationsItem(operation);
        }
        currentEntry.getOperationObjects().removeIf(op -> operation.equals(op.getName()));
        if (aclSubject.getOperation() != null) {
          currentEntry.addOperationObjectsItem(aclSubject.getOperation());
        } else {
          currentEntry.addOperationObjectsItem(new ACLOperation().name(operation));
        }
      }
    }
    // Collect all the changes on the subjects.
    Map<String, ACLSubjectOperationModification> modifications = new HashMap<>();
    // Add the necessary entries and set
    for (ACLSubject aclSubject : toAdd) {
      final ACLEntry entry = new ACLEntry()
          .subject(aclSubject.getSubject())
          .addOperationsItem(operation);
      final ACLOperation op = aclSubject.getOperation();
      if (op != null) {
        entry.addOperationObjectsItem(op);
      } else {
        entry.addOperationObjectsItem(new ACLOperation().name(operation));
      }

      final SubjectCondition subjectCondition = aclSubject.getSubjectCondition();
      if (subjectCondition != null) {
        entry.subjectCondition(subjectCondition);
      }

      acl.getRootEntry().addEntriesItem(entry);
      if (contextEntity != null) {
        // Add the operation reference to the referenced entries.
        ACLSubjectOperationModification subjectModification =
            modifications.computeIfAbsent(constructSubjectIdWithRef(aclSubject.getSubject()),
                key -> new ACLSubjectOperationModification().subject(aclSubject.getSubject()));
        subjectModification
            .addToAddItem(new ACLOperationReference().operation(aclSubject.getOperation().getName())
                .comment(aclSubject.getOperation().getComment()).entityUri(contextEntity)
                .config(contextConfigCode));
      }
    }

    Set<String> toDelete = new HashSet<>();
    currentEntries.values().stream().forEach(e -> {
      // We have to remove the given operation from the entry. If it was the last operation then
      // remove the whole entry with the subject. There is no more relevant operation for the given
      // subject.
      if (contextEntity != null) {
        ACLSubjectOperationModification subjectModification =
            modifications.computeIfAbsent(constructSubjectIdWithRef(e.getSubject()),
                key -> new ACLSubjectOperationModification().subject(e.getSubject()));
        subjectModification
            .addToRemoveItem(constructOperationReferenceId(operation, contextEntity));
      }
      e.getOperations().remove(operation);
      e.getOperationObjects().removeIf(op -> operation.equals(op.getName()));
      if (e.getOperations().isEmpty() && e.getOperationObjects().isEmpty()) {
        toDelete.add(subjectManagementApi.toString(e.getSubject()));
      }
    });

    // At last we remove the unnecessary entries.
    if (!toDelete.isEmpty()) {
      acl.getRootEntry().getEntries()

          .removeIf(e -> e.getOperations().isEmpty() && e.getOperationObjects().isEmpty()
              && toDelete.contains(subjectManagementApi.toString(e.getSubject())));
    }

    executeSubjectModifications(modifications.values());

    return acl;
  }

  @Override
  public void executeSubjectModifications(
      Collection<ACLSubjectOperationModification> modifications) {
    // Now we save here subject by subject the changes.
    for (ACLSubjectOperationModification subjectModification : modifications) {
      StoredReference<ACLSubjectOperations> refSubjectOperations =
          collectionApi.reference(subjectModification.getSubject().getRef(),
              SubjectManagementApi.SCHEMA,
              constructSubjectId(subjectModification.getSubject()),
              ACLSubjectOperations.class);
      refSubjectOperations.update(so -> {
        if (so == null) {
          so = new ACLSubjectOperations();
          so.subject(subjectModification.getSubject());
        }
        so.getOperations().removeIf(or -> subjectModification.getToRemove()
            .contains(constructOperationReferenceId(or.getOperation(), or.getEntityUri())));
        so.getOperations().addAll(subjectModification.getToAdd());
        return so;
      });
    }
  }

  private final String constructOperationReferenceId(String operation, URI contextEntity) {
    return operation + StringConstant.DOT + contextEntity;
  }

  @Override
  public List<ACLSubjectOperations> getUserAllOperations(URI userUri,
      Collection<String> subjectModels) {
    Objects.requireNonNull(subjectModels);
    return subjectModels.stream()
        .flatMap(s -> subjectManagementApi.getSubjectsOfUser(s, userUri).stream())
        .map(
            s -> {
              StoredReference<ACLSubjectOperations> ref =
                  collectionApi.reference(s.getRef(), SubjectManagementApi.SCHEMA,
                      constructSubjectId(s), ACLSubjectOperations.class);
              if (ref.exists()) {
                return ref.get();
              }
              return null;
            })
        .filter(o -> o != null)
        .collect(toList());
  }

  @Override
  public List<ACLSubjectSubscription> getUserAllSubscriptions(URI userUri,
      Collection<String> subjectModels) {
    return getUserAllOperations(userUri, subjectModels).stream()
        .flatMap(
            o -> o.getOperations().stream().map(
                or -> new ACLSubjectSubscription().subject(o.getSubject()).operationReference(or)))
        .collect(toList());
  }

  @Override
  public ACL getAclFromObject(ACLObject aclObject, String name) {
    return aclObject.getMap().computeIfAbsent(
        name,
        (s) -> new ACL()
            .rootEntry(new ACLEntry()
                .entryKind(EntryKindEnum.SET)
                .setOperation(SetOperationEnum.UNION)));
  }

}
