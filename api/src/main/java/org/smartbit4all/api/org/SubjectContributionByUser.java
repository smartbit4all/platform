package org.smartbit4all.api.org;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.SubjectAssociationModificationModel;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

public class SubjectContributionByUser extends ContributionApiImpl
    implements SubjectContributionApi {

  @Autowired(required = false)
  private OrgApi orgApi;

  @Autowired(required = false)
  private ObjectApi objectApi;

  public SubjectContributionByUser() {
    super(SubjectContributionByUser.class.getName());
  }

  @Override
  public List<Subject> getUserSubjects(String modelName, URI userUri) {
    if (orgApi == null) {
      return Collections.emptyList();
    }
    List<Subject> result = new ArrayList<>();
    if (orgApi.getUser(userUri) != null) {
      result.add(new Subject()
          .model(modelName)
          .type(User.class.getName())
          .ref(userUri));
    }
    return result;
  }

  @Override
  public List<Subject> getAllSubjects(String modelName) {
    if (orgApi == null) {
      return Collections.emptyList();
    }
    return orgApi.getAllUsers().stream()
        .map(u -> new Subject()
            .model(modelName)
            .type(User.class.getName())
            .ref(u.getUri()))
        .collect(toList());
  }

  /**
   * Return the URI if it is a user because the result is itself in this case.
   */
  @Override
  public List<URI> getUsersOf(String modelName, List<URI> subjects) {
    if (subjects == null || objectApi == null) {
      return Collections.emptyList();
    }
    return subjects.stream()
        .filter(u -> objectApi.definition(u).instanceOf(User.class))
        .filter(u -> orgApi.getUser(u) != null)
        .map(u -> objectApi.getLatestUri(u))
        .collect(toList());
  }

  @Override
  public List<Subject> getAllSubjects(String modelName, List<URI> baseList) {
    return getSubjects(modelName, baseList);
  }

  @Override
  public List<Subject> getAllContainingSubjects(String modelName, List<URI> baseList) {
    return getSubjects(modelName, baseList);
  }

  protected List<Subject> getSubjects(String modelName, List<URI> baseList) {
    if (baseList == null || objectApi == null) {
      return Collections.emptyList();
    }
    // Here we add all the parent organizations because we are part of it. If we need to have
    // another approach then we need to register this contribution on other name.
    return baseList.stream()
        .filter(s -> objectApi.definition(s).instanceOf(User.class))
        .filter(u -> orgApi.getUser(u) != null)
        .map(e -> new Subject()
            .model(modelName)
            .type(User.class.getName())
            .ref(e))
        .collect(toList());
  }

  @Override
  public List<String> getDisplayValue(String modelName, List<URI> subjects) {
    return subjects.stream()
        .filter(s -> objectApi.definition(s).instanceOf(User.class))
        .filter(u -> orgApi.getUser(u) != null)
        .map(objectApi::loadLatest)
        .map(n -> n.getValueAsString(User.NAME))
        .collect(toList());
  }

  @Override
  public void processSubjectChanges(String modelName,
      SubjectAssociationModificationModel subjectAssociationModel) {
    // TODO Auto-generated method stub

  }
}
