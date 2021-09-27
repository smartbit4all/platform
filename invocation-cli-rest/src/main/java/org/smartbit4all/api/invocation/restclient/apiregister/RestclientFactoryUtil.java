package org.smartbit4all.api.invocation.restclient.apiregister;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.smartbit4all.api.apiregister.bean.ApiInfo;
import org.smartbit4all.api.invocation.restclientgen.util.ApiClient;
import org.springframework.web.client.RestTemplate;

public class RestclientFactoryUtil {

  public static final String REST_BASE_PATH = "REST_BASE_PATH";

  public static <T> T createNamedConfiguredApi(ApiInfo apiInfo, RestTemplate restTemplate,
      BiFunction<String, ApiClient, T> apiConstructor) {
    ApiClient apiClient = new ApiClient(restTemplate);

    String basePath = (String) apiInfo.getParameters().get(REST_BASE_PATH);

    apiClient.setBasePath(basePath);
    // TODO set apiClient based on apiInfo. path, authentication, header ...
    // it may be required to manage handlers registered in spring context

    T api = apiConstructor.apply(apiInfo.getApiIdentifier(), apiClient);
    return api;
  }

  public static <T> T createConfiguredApi(ApiInfo apiInfo, RestTemplate restTemplate,
      Function<ApiClient, T> apiConstructor) {
    ApiClient apiClient = new ApiClient(restTemplate);

    String basePath = (String) apiInfo.getParameters().get(REST_BASE_PATH);

    apiClient.setBasePath(basePath);
    // TODO set apiClient based on apiInfo. path, authentication, header ...
    // it may be required to manage handlers registered in spring context

    T api = apiConstructor.apply(apiClient);
    return api;
  }


}
