package org.smartbit4all.api.org;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class SubjectContributionByUser extends ContributionApiImpl
    implements SubjectContributionApi {

  @Autowired(required = false)
  private OrgApi orgApi;

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

}
