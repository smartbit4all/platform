package org.smartbit4all.api.invocation.registration;

public class LocalApiInstantiator implements ProtocolSpecificApiInstantiator {

  public static final String LOCAL_API_IMPL = "LOCAL_API_IMPL";
  
  @Override
  public String getProtocol() {
    return "local";
  }

  @Override
  public Object instantiate(ApiInfo apiInfo) throws Exception {
    Object apiImpl = apiInfo.getParameters().get(LOCAL_API_IMPL);
    if(apiImpl == null) {
      throw new IllegalArgumentException("The apiInfo's '" + LOCAL_API_IMPL + "' parameter can not "
          + "be null!");
    }
    return apiImpl;
  }

}