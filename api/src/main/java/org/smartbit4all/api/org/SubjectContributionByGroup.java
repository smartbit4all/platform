package org.smartbit4all.api.org;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class SubjectContributionByGroup extends ContributionApiImpl
    implements SubjectContributionApi {

  public SubjectContributionByGroup() {
    super(SubjectContributionByGroup.class.getName());
  }

  @Autowired(required = false)
  private OrgApi orgApi;

  @Override
  public List<Subject> getUserSubjects(URI userUri) {
    if (orgApi == null) {
      return Collections.emptyList();
    }
    return orgApi.getGroupsOfUser(userUri).stream()
        .map(g -> new Subject().ref(g.getUri()).type(Group.class.getName())).collect(toList());
  }

}
