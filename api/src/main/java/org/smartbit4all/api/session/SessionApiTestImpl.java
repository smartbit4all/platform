package org.smartbit4all.api.session;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.UserActivityLog;

public class SessionApiTestImpl implements SessionApi {

  private OrgApi orgApi;

  URI userUri;

  public SessionApiTestImpl(OrgApi orgApi) {
    this.orgApi = orgApi;
  }

  public void setCurrentUser(URI userUri) {
    this.userUri = userUri;
  }


  @Override
  public User getUser() {
    if (userUri == null) {
      return null;
    }
    return orgApi.getUser(userUri);
  }

  @Override
  public URI getUserUri() {
    return userUri;
  }

  @Override
  public URI getSessionUri() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OffsetDateTime getExpiration() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Locale getLocale() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<AccountInfo> getAuthentications() {
    User user = getUser();
    if (user == null) {
      return Collections.emptyList();
    }

    return Arrays.asList(new AccountInfo()
        .userName(user.getUsername())
        .displayName(user.getName())
        .roles(orgApi.getGroupsOfUser(userUri).stream()
            .map(Group::getName)
            .collect(Collectors.toList()))
        .parameters(user.getAttributes()));
  }

  @Override
  public AccountInfo getAuthentication(String kind) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getParameter(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setParameter(String key, String value) {
    // TODO Auto-generated method stub

  }

  @Override
  public String removeParameter(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T getParameterObject(String key, Class<T> clazz) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> void setParameterObject(String key, T value) {
    // TODO Auto-generated method stub

  }

  @Override
  public Map<String, URI> getViewContexts() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T getParameterObject(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addViewContext(UUID viewContextUuid, URI viewContextUri) {
    // TODO Auto-generated method stub

  }

  @Override
  public UserActivityLog createActivityLog() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void subscribeForParameterChange(String key, UUID viewContextUuid, UUID viewUuid,
      InvocationRequest callback) {
    // TODO Auto-generated method stub

  }

}
