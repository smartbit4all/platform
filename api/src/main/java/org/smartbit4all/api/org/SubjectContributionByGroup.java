package org.smartbit4all.api.org;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class SubjectContributionByGroup extends ContributionApiImpl
    implements SubjectContributionApi {

  public SubjectContributionByGroup() {
    super(SubjectContributionByGroup.class.getName());
  }

  @Autowired(required = false)
  private OrgApi orgApi;

  @Autowired(required = false)
  private ObjectApi objectApi;

  @Override
  public List<Subject> getUserSubjects(URI userUri) {
    if (orgApi == null) {
      return Collections.emptyList();
    }
    return orgApi.getGroupsOfUser(userUri).stream()
        .map(g -> new Subject().ref(g.getUri()).type(Group.class.getName())).collect(toList());
  }

  @Override
  public List<Subject> getAllSubjects() {
    if (orgApi == null) {
      return Collections.emptyList();
    }
    return orgApi.getAllGroups().stream()
        .map(g -> new Subject().ref(g.getUri()).type(Group.class.getName())).collect(toList());
  }

  @Override
  public List<URI> getUsersOf(List<URI> subjects) {
    if (subjects == null || objectApi == null || orgApi == null) {
      return Collections.emptyList();
    }
    return subjects.stream().filter(u -> objectApi.definition(u).instanceOf(Group.class))
        .flatMap(u -> orgApi.getUsersOfGroup(u).stream().map(user -> user.getUri()))
        .collect(toList());
  }

  @Override
  public List<Subject> getAllSubjects(List<URI> baseList) {
    if (baseList == null || objectApi == null || orgApi == null) {
      return Collections.emptyList();
    }
    return baseList.stream().filter(s -> objectApi.definition(s).instanceOf(Group.class))
        .flatMap(s -> Stream.concat(Stream.of(orgApi.getGroup(s)),
            orgApi.getSubGroups(s).stream()))
        .map(g -> new Subject().ref(g.getUri()).type(Group.class.getName())).collect(toList());
  }

}
