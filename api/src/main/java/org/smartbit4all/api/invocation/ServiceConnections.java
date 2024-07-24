package org.smartbit4all.api.invocation;

import org.smartbit4all.core.utility.StringConstant;

public class ServiceConnections {

  public static final String AUTHORIZATION = "Authorization";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String BEARER = "Bearer";

  public static final String getBearer(String token) {
    return BEARER + StringConstant.SPACE + token;
  }

  private ServiceConnections() {}
}
