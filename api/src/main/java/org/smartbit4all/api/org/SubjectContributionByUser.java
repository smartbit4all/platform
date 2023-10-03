package org.smartbit4all.api.org;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

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
  public List<Subject> getUserSubjects(URI userUri) {
    List<Subject> result = new ArrayList<>();
    result.add(new Subject().ref(userUri).type(User.class.getName()));
    return result;
  }

  @Override
  public List<Subject> getAllSubjects() {
    if (orgApi == null) {
      return Collections.emptyList();
    }
    return orgApi.getAllUsers().stream()
        .map(u -> new Subject().ref(u.getUri()).type(User.class.getName())).collect(toList());
  }

  /**
   * Return the URI if it is a user because the result is itself in this case.
   */
  @Override
  public List<URI> getUsersOf(List<URI> subjects) {
    if (subjects == null || objectApi == null) {
      return Collections.emptyList();
    }
    return subjects.stream().filter(u -> objectApi.definition(u).instanceOf(User.class))
        .map(u -> objectApi.getLatestUri(u))
        .collect(toList());
  }

}
