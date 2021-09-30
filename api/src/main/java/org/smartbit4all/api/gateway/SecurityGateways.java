package org.smartbit4all.api.gateway;

import java.net.URI;

public class SecurityGateways {

  public static final String LOCAL_API_NAME = "local";

  public static URI generateURI(String apiName, String uuid) {
    return URI.create(apiName + ":/" + uuid);
  }

  public static URI generateURI(String uuid) {
    return generateURI(LOCAL_API_NAME, uuid);
  }

}
