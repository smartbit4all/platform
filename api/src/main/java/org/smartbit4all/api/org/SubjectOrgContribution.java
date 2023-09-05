package org.smartbit4all.api.org;

import java.net.URI;
import java.util.List;
import javax.swing.GroupLayout.Group;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class SubjectOrgContribution extends ContributionApiImpl implements SubjectContributionApi {

  public static final String ORG_API = "org";

  protected SubjectOrgContribution() {
    super(ORG_API);
  }

  @Autowired
  private OrgApi orgApi;

  @Override
  public List<Subject> getUserSubjects(URI userUri) {
    List<Subject> result = orgApi.getGroupsOfUser(userUri).stream()
        .map(g -> new Subject().ref(g.getUri()).type(Group.class.getName())).collect(toList());
    result.add(new Subject().ref(userUri).type(User.class.getName()));
    return result;
  }

}
