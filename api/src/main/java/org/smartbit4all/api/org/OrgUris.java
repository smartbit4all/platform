package org.smartbit4all.api.org;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Supplier;

public class OrgUris {

  public static final String ORG_SCHEME = "org";
  public static final String HOST_USER = "user";
  public static final String HOST_GROUP = "group";
  public static final String PATH_ID = "/id";
  
  public static URI createUserUri(String userId) {
    try {
      return new URI(ORG_SCHEME, HOST_USER, PATH_ID, userId);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static URI createGroupUri(String groupId) {
    try {
      return new URI(ORG_SCHEME, HOST_GROUP, PATH_ID, groupId);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static String getUserId(URI userUri) {
    checkURI(userUri, HOST_USER, PATH_ID);
    return userUri.getFragment();
  }
  
  public static String getGroupId(URI groupUri) {
    checkURI(groupUri, HOST_GROUP, PATH_ID);
    return groupUri.getFragment();
  }
  
  private static void checkURI(URI uriToCheck, String host, String path) {
    try {
      checkURIPart(ORG_SCHEME, uriToCheck::getScheme);
      checkURIPart(host, uriToCheck::getHost);
      checkURIPart(path, uriToCheck::getPath);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e.getMessage() +" URI: " + uriToCheck.toString(), e);
    }
  }
  
  private static void checkURIPart(String partValue, Supplier<String> partSupplier) throws IllegalAccessException {
    if(!partValue.equals(partSupplier.get())) {
      throw new IllegalAccessException("The given uri's format is not acceptable!");
    }
  }
  
}
