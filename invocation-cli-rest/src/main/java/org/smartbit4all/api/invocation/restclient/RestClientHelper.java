package org.smartbit4all.api.invocation.restclient;

import java.util.List;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

public class RestClientHelper {

  public static RestClient getRestClient(ServiceConnection connection) {
    Builder restClientBuilder = RestClient.builder()
        .messageConverters(list -> list
            .addAll(List.of(new FormHttpMessageConverter(), new MapFormHttpMessageConverter())));

    if (StringUtils.hasText(connection.getPassword())) {
      restClientBuilder.defaultHeaders(
          headers -> headers.setBasicAuth(connection.getUsername(), connection.getPassword()));
    }

    return restClientBuilder.build();
  }
}
