package org.smartbit4all.api.invocation.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

public class MapFormHttpMessageConverter extends AbstractHttpMessageConverter<Map<String, ?>> {

  public MapFormHttpMessageConverter() {
    super(MediaType.APPLICATION_FORM_URLENCODED);
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return Map.class.isAssignableFrom(clazz);
  }

  @Override
  protected Map<String, ?> readInternal(Class<? extends Map<String, ?>> clazz,
      HttpInputMessage inputMessage)
      throws IOException {

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(inputMessage.getBody(), StandardCharsets.UTF_8))) {

      String body = reader.lines().reduce("", (a, b) -> a + b);

      Map<String, String> map = new HashMap<>();
      if (!body.isEmpty()) {
        String[] pairs = body.split("&");
        for (String pair : pairs) {
          int idx = pair.indexOf("=");
          String key =
              idx > 0 ? URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8) : pair;
          String value = idx > 0 && pair.length() > idx + 1
              ? URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8)
              : "";
          map.put(key, value);
        }
      }
      return map;
    }
  }

  @Override
  protected void writeInternal(Map<String, ?> map, HttpOutputMessage outputMessage)
      throws IOException {
    StringBuilder body = new StringBuilder();
    for (Map.Entry<String, ?> entry : map.entrySet()) {
      if (body.length() > 0) {
        body.append("&");
      }
      body.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
      body.append("=");
      if (entry.getValue() != null) {
        body.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
      }
    }
    try (OutputStreamWriter writer =
        new OutputStreamWriter(outputMessage.getBody(), StandardCharsets.UTF_8)) {
      writer.write(body.toString());
    }
  }
}
