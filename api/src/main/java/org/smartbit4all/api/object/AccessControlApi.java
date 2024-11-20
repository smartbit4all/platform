package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.org.bean.ACLOperation;
import org.smartbit4all.api.org.bean.ACLSubject;
import org.smartbit4all.api.org.bean.Subject;

public interface AccessControlApi {

  URI addOrUpdateSubjects(URI aclObjectUri, List<Subject> subjects, String aclName,
      List<ACLOperation> operations);

  URI addOrUpdateSubjects(URI aclObjectUri, List<Subject> subjects, String aclName,
      List<ACLOperation> operations, URI contextEntity, String contextConfigCode);

  URI deleteSubjects(URI aclObjectUri, List<URI> subjects, String aclName,
      List<String> operations);

  URI deleteSubjects(URI aclObjectUri, List<URI> subjects, String aclName,
      List<String> operations, URI contextEntity, String contextConfigCode);

  boolean isSubjectOfAcl(URI aclObjectUri, URI subject, String aclName,
      String operation);

  List<ACLSubject> getSubjects(URI aclObjectUri, String aclName, String operation);

}
