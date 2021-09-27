package org.smartbit4all.api.invocation.registration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.smartbit4all.api.apiregister.bean.ApiInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class LocalApiInstantiator implements ProtocolSpecificApiInstantiator {

  public static final String LOCAL_API_IMPL = "LOCAL_API_IMPL";

  // FIXME we store it for a fix time
  private static final Cache<String, Object> localApiParameterHolder = CacheBuilder.newBuilder()
      .expireAfterWrite(5, TimeUnit.MINUTES)
      .build();

  @Override
  public String getProtocol() {
    return "local";
  }

  @Override
  public Object instantiate(ApiInfo apiInfo) throws Exception {
    String apiParameter = apiInfo.getParameters().get(LOCAL_API_IMPL);
    if (apiParameter == null) {
      throw new IllegalArgumentException("The apiInfo's '" + LOCAL_API_IMPL + "' parameter can not "
          + "be null!");
    }
    Object localApiObject = getLocalApiObjectFromParameter(apiParameter);
    if (localApiObject == null) {
      throw new IllegalStateException(
          "There is no local api object found for farameter " + apiParameter);
    }
    return localApiObject;
  }

  public static String createLocalApiParameter(Object localApiObject) {
    String parameter = UUID.randomUUID().toString();
    localApiParameterHolder.put(parameter, localApiObject);
    return parameter;
  }

  private static Object getLocalApiObjectFromParameter(String parameter) {
    return localApiParameterHolder.getIfPresent(parameter);
  }

}
