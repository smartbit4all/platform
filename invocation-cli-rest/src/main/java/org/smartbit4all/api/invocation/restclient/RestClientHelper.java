package org.smartbit4all.api.invocation.restclient;

import java.time.Duration;
import java.util.List;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

public class RestClientHelper {

  public static RestClient createClient(ServiceConnection connection, Duration timeout) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(timeout);
    factory.setReadTimeout(timeout);

    RestClient.Builder builder = RestClient.builder()
        .requestFactory(factory)
        .messageConverters(list -> list.addAll(List.of(
            new FormHttpMessageConverter(),
            new MapFormHttpMessageConverter())));

    if (StringUtils.hasText(connection.getPassword())) {
      builder
          .defaultHeaders(h -> h.setBasicAuth(connection.getUsername(), connection.getPassword()));
    }

    return builder.build();
  }
}
