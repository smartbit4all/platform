package org.smartbit4all.api.object;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.List;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.org.bean.ACLObject;
import org.smartbit4all.api.org.bean.ACLOperation;
import org.smartbit4all.api.org.bean.ACLSubject;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class AccessControlApiImpl implements AccessControlApi {

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private AccessControlInternalApi accessControlInternalApi;

  @Override
  public URI addSubjects(URI aclObjectUri, List<Subject> subjects, String aclName,
      List<ACLOperation> operations) {

    return addSubjects(aclObjectUri, subjects, aclName, operations, null, null);
  }

  @Override
  public URI addSubjects(URI aclObjectUri, List<Subject> subjects, String aclName,
      List<ACLOperation> operations, URI contextEntity, String contextConfigCode) {
    ObjectNode aclObjectNode = objectApi.loadLatest(aclObjectUri);
    aclObjectNode.modify(ACLObject.class, aclObject -> {
      ACL acl = accessControlInternalApi.getAclFromObject(aclObject, aclName);
      for (ACLOperation operation : operations) {
        List<ACLSubject> currentSubjects =
            accessControlInternalApi.getSubjects(acl, operation.getName());

        for (Subject subject : subjects) {
          if (!checkSubjectIsAlreadyInAcl(acl, subject.getRef())) {
            currentSubjects.add(new ACLSubject().subject(subject).operation(operation));
          }
        }
        accessControlInternalApi.applySubjects(acl, currentSubjects, operation.getName(),
            contextEntity, contextConfigCode);
      }
      return aclObject;
    });


    return objectApi.save(aclObjectNode);
  }

  @Override
  public URI deleteSubjects(URI aclObjectUri, List<URI> subjects, String aclName,
      List<String> operations) {
    return deleteSubjects(aclObjectUri, subjects, aclName, operations, null, null);
  }

  @Override
  public URI deleteSubjects(URI aclObjectUri, List<URI> subjects, String aclName,
      List<String> operations, URI contextEntity, String contextConfigCode) {
    ObjectNode aclObjectNode = objectApi.loadLatest(aclObjectUri);
    aclObjectNode.modify(ACLObject.class, aclObject -> {
      ACL acl = accessControlInternalApi.getAclFromObject(aclObject, aclName);
      for (String operation : operations) {
        List<ACLSubject> currentSubjects = accessControlInternalApi.getSubjects(acl, operation);

        boolean anyChange = false;
        for (URI subject : subjects) {
          anyChange |= currentSubjects.removeIf(
              sub -> objectApi.equalsIgnoreVersion(sub.getSubject().getRef(),
                  subject));
        }
        if (anyChange) {
          accessControlInternalApi.applySubjects(acl, currentSubjects, operation, contextEntity,
              contextConfigCode);
        }
      }
      return aclObject;
    });
    return objectApi.save(aclObjectNode);
  }

  @Override
  public boolean isSubjectOfAcl(URI aclObjectUri, URI subject, String aclName, String operations) {
    ACL acl = getAclFromObject(aclObjectUri, aclName);
    return checkSubjectIsAlreadyInAcl(acl, subject);
  }

  @Override
  public List<ACLSubject> getSubjects(URI aclObjectUri, String aclName, String operation) {
    ACL acl = getAclFromObject(aclObjectUri, aclName);
    return accessControlInternalApi.getSubjects(acl, operation);
  }

  protected ACL getAclFromObject(URI aclObjectUri, String aclName) {
    ObjectNode aclObjectNode = objectApi.loadLatest(aclObjectUri);
    ACLObject aclObject = aclObjectNode.getObject(ACLObject.class);
    ACL acl = accessControlInternalApi.getAclFromObject(aclObject, aclName);
    return acl;
  }

  private boolean checkSubjectIsAlreadyInAcl(ACL acl, URI subjectUri) {
    if (acl == null || acl.getRootEntry() == null || acl.getRootEntry().getEntries() == null) {
      return false;
    }
    return acl.getRootEntry().getEntries().stream().map(entry -> entry.getSubject().getRef())
        .collect(toList())
        .contains(subjectUri);
  }


}
