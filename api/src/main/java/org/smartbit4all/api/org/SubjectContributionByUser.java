package org.smartbit4all.api.org;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.springframework.beans.factory.annotation.Autowired;

public class SubjectContributionByUser extends ContributionApiImpl
    implements SubjectContributionApi {

  public SubjectContributionByUser() {
    super(SubjectContributionByUser.class.getName());
  }

  @Autowired
  private OrgApi orgApi;

  @Override
  public List<Subject> getUserSubjects(URI userUri) {
    List<Subject> result = new ArrayList<>();
    result.add(new Subject().ref(userUri).type(User.class.getName()));
    return result;
  }

}
