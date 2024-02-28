package org.smartbit4all.api.org;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

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
  public List<Subject> getUserSubjects(String modelName, URI userUri) {
    if (orgApi == null) {
      return Collections.emptyList();
    }
    return orgApi.getGroupsOfUser(userUri).stream()
        .map(g -> new Subject()
            .model(modelName)
            .type(Group.class.getName())
            .ref(g.getUri()))
        .collect(toList());
  }

  @Override
  public List<Subject> getAllSubjects(String modelName) {
    if (orgApi == null) {
      return Collections.emptyList();
    }
    return orgApi.getAllGroups().stream()
        .map(g -> new Subject()
            .model(modelName)
            .type(Group.class.getName())
            .ref(g.getUri()))
        .collect(toList());
  }

  @Override
  public List<URI> getUsersOf(String modelName, List<URI> subjects) {
    if (subjects == null || objectApi == null || orgApi == null) {
      return Collections.emptyList();
    }
    return subjects.stream()
        .filter(u -> objectApi.definition(u).instanceOf(Group.class))
        .flatMap(u -> orgApi.getUsersOfGroup(u).stream().map(User::getUri))
        .collect(toList());
  }

  @Override
  public List<Subject> getAllSubjects(String modelName, List<URI> baseList) {
    if (baseList == null || objectApi == null || orgApi == null) {
      return Collections.emptyList();
    }
    return baseList.stream()
        .filter(s -> objectApi.definition(s).instanceOf(Group.class))
        .flatMap(s -> Stream.concat(
            Stream.of(orgApi.getGroup(s)),
            orgApi.getSubGroups(s).stream()))
        .map(g -> new Subject()
            .model(modelName)
            .type(Group.class.getName())
            .ref(g.getUri()))
        .collect(toList());
  }

  @Override
  public List<String> getDisplayValue(String modelName, List<URI> subjects) {
    return subjects.stream()
        .map(objectApi::loadLatest)
        .map(n -> {
          String displayvalue = n.getValueAsString(Group.TITLE);
          if (Strings.isNullOrEmpty(displayvalue)) {
            displayvalue = n.getValueAsString(Group.NAME);
          }
          return displayvalue;
        })
        .collect(toList());
  }
}
