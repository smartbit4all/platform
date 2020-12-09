package org.smartbit4all.api.org;

import java.net.URI;
import org.smartbit4all.api.UriUtils;

public class OrgUris extends UriUtils {

  public static final String ORG_SCHEME = "org";
  public static final String HOST_USER = "user";
  public static final String HOST_GROUP = "group";
  public static final String PATH_ID = "/id";
  
  private OrgUris() {
  }
  
  public static URI createUserUri(String userId) {
    return createUri(ORG_SCHEME, HOST_USER, PATH_ID, userId);
  }
  
  public static URI createGroupUri(String groupId) {
    return createUri(ORG_SCHEME, HOST_GROUP, PATH_ID, groupId);
  }
  
  public static String getUserId(URI userUri) {
    checkOrgURI(userUri, HOST_USER, PATH_ID);
    return userUri.getFragment();
  }
  
  public static String getGroupId(URI groupUri) {
    checkOrgURI(groupUri, HOST_GROUP, PATH_ID);
    return groupUri.getFragment();
  }
  
  private static void checkOrgURI(URI uriToCheck, String host, String path) {
    checkURI(uriToCheck, ORG_SCHEME, host, path);
  }
  
  
}
