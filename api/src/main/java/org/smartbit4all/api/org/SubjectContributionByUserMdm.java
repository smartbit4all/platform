package org.smartbit4all.api.org;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

public class SubjectContributionByUserMdm extends ContributionApiImpl
    implements SubjectContributionApi {

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  private Long lastCacheRerfeshTime = null;

  private List<Subject> allSubjects;

  private String definitionName;

  private String entryName;

  private Map<URI, User> usersByUri;

  private Map<String, User> usersById;

  public SubjectContributionByUserMdm(String apiName, String definitionName, String entryName) {
    super(apiName);
    this.definitionName = definitionName;
    this.entryName = entryName;
  }

  private final void refreshCache(String modelName) {
    MDMEntryApi mdmUserApi = masterDataManagementApi.getApi(definitionName, entryName);
    StoredList userList = mdmUserApi.getList();
    if (lastCacheRerfeshTime == null
        || (userList.exists() && !userList.getLastModified().equals(lastCacheRerfeshTime))) {
      allSubjects = userList.nodesFromCache()
          .map(n -> new Subject()
              .model(modelName)
              .type(User.class.getName())
              .ref(n.getObjectUri()))
          .collect(toList());

      usersByUri = new HashMap<>();
      usersById = new HashMap<>();
      userList.nodesFromCache().forEach(node -> {
        User user = node.getObject(User.class);
        usersByUri.put(objectApi.getLatestUri(node.getObjectUri()), user);
        usersById.put(user.getUsername(), user);
      });
      lastCacheRerfeshTime = userList.getLastModified();
    }
  }

  @Override
  public List<Subject> getUserSubjects(String modelName, URI userUri) {
    refreshCache(modelName);
    List<Subject> result = new ArrayList<>();
    User user = usersByUri == null ? null : usersByUri.get(userUri);
    if (user != null) {
      result.add(
          new Subject()
              .model(modelName)
              .type(User.class.getName())
              .ref(userUri));
    }
    return result;
  }

  @Override
  public List<Subject> getAllSubjects(String modelName) {
    refreshCache(modelName);
    return new ArrayList<>(allSubjects);
  }

  /**
   * Return the URI if it is a user because the result is itself in this case.
   */
  @Override
  public List<URI> getUsersOf(String modelName, List<URI> subjects) {
    refreshCache(modelName);
    if (subjects == null || objectApi == null) {
      return Collections.emptyList();
    }
    return subjects.stream()
        .filter(u -> objectApi.definition(u).instanceOf(User.class))
        .filter(u -> usersByUri.containsKey(u))
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
    refreshCache(modelName);
    // Here we add all the parent organizations because we are part of it. If we need to have
    // another approach then we need to register this contribution on other name.
    return baseList.stream()
        .filter(s -> objectApi.definition(s).instanceOf(User.class))
        .filter(u -> usersByUri.containsKey(u))
        .map(e -> new Subject()
            .model(modelName)
            .type(User.class.getName())
            .ref(e))
        .collect(toList());
  }

  @Override
  public List<String> getDisplayValue(String modelName, List<URI> subjects) {
    refreshCache(modelName);
    return subjects.stream()
        .filter(s -> objectApi.definition(s).instanceOf(User.class))
        .filter(u -> usersByUri.containsKey(u))
        .map(objectApi::loadLatest)
        .map(n -> n.getValueAsString(User.NAME))
        .collect(toList());
  }

}
