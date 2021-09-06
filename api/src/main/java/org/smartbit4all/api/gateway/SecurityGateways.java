package org.smartbit4all.api.gateway;

import java.net.URI;

public class SecurityGateways {

  public static final String LOCAL_API_NAME = "local";

  public static URI userUri(String apiName, String userName) {
    return URI.create(apiName + ":/" + userName);
  }

  public static URI groupUri(String apiName, String groupName) {
    return URI.create(apiName + ":/" + groupName);
  }

  public static URI usersOfGroupUri(String apiName, String groupName) {
    return URI.create(apiName + ":/" + groupName);
  }

  public static URI groupsOfUserUri(String apiName, String userName) {
    return URI.create(apiName + ":/" + userName);
  }

  public static URI localUserUri(String userName) {
    return userUri(LOCAL_API_NAME, userName);
  }

  public static URI localGroupUri(String groupName) {
    return groupUri(LOCAL_API_NAME, groupName);
  }

  public static URI localUsersOfGroupUri(String groupName) {
    return usersOfGroupUri(LOCAL_API_NAME, groupName);
  }

  public static URI localGroupsOfUserUri(String userName) {
    return groupsOfUserUri(LOCAL_API_NAME, userName);
  }

}