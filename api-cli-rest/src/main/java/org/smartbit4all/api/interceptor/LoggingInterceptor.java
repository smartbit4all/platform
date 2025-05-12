package org.smartbit4all.api.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Log outgoing HTTP request and responses for it. It has to be registered to a {@link RestTemplate}
 * or through a {@link RestTemplateCustomizer}.
 * <p>
 * When registered the RequestFactory also has to be changed. Cause the body can only be read once
 * {@link BufferingClientHttpRequestFactory}
 * </p>
 * <p>
 * For example:
 * 
 * <pre>
 * {@code
 *
 * public RestTemplateCustomizer restTemplateCustomizer() {
 *   return restTemplate -> {
 *     restTemplate.getInterceptors().add(new LoggingInterceptor());
 *     restTemplate.setRequestFactory(
 *         new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
 *   };
 * }
 * }
 * </pre>
 * 
 * </p>
 */
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

  private static Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public ClientHttpResponse intercept(
      HttpRequest req, byte[] reqBody, ClientHttpRequestExecution ex) throws IOException {
    long startTime = System.currentTimeMillis();
    String requestId = UUID.randomUUID().toString();

    logRequest(req, reqBody, requestId);
    try {
      ClientHttpResponse response = ex.execute(req, reqBody);
      logResponse(req, response, requestId, startTime);
      return response;
    } catch (IOException | RuntimeException exception) {
      // Log the exception if needed
      throw exception; // Propagate the exception
    }

  }

  private void logResponse(HttpRequest req, ClientHttpResponse response, String requestId,
      long startTime) throws IOException {
    StringBuilder logBuilder = new StringBuilder();
    // Log method and URI
    logBuilder.append("Incoming HTTP Response - ").append(requestId).append("\n");
    logBuilder.append("URI: ").append(req.getURI()).append(", ");
    logBuilder.append("Status: ").append(response.getStatusCode().value()).append(", ");

    // End time
    long endTime = System.currentTimeMillis();
    // Calculate duration and log
    long duration = endTime - startTime;
    logBuilder.append("Execution time (ms): ").append(duration).append(", ");

    // Log body
    logBuilder.append("Body:\n");
    String body =
        new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    if (isJsonResponse(response)) {
      logBody(body, logBuilder);
    } else {
      logBuilder.append(body);
    }

    LOGGER.debug(logBuilder.toString());

  }

  private void logRequest(HttpRequest req, byte[] reqBody, String requestId) {
    StringBuilder logBuilder = new StringBuilder();

    // Log method and URI
    logBuilder.append("Outgoing HTTP Request - ").append(requestId).append("\n");
    logBuilder.append("Method: ").append(req.getMethod()).append(", ");
    logBuilder.append("URI: ").append(req.getURI()).append(", ");

    // Log headers
    logBuilder.append("Headers:").append(req.getHeaders().toString()).append(", ");

    // Log body
    logBuilder.append("Body:\n");
    String body = new String(reqBody, StandardCharsets.UTF_8);
    logBuilder.append(body);
    if (isJsonRequest(req)) {
      logBody(body, logBuilder);
    } else {
      logBuilder.append(body);
    }

    LOGGER.debug(logBuilder.toString());
  }

  private void logBody(String body, StringBuilder logBuilder) {
    try {
      Object json = objectMapper.readValue(body, Object.class);
      String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
      logBuilder.append(prettyJson);
    } catch (JsonProcessingException e) {
      logBuilder.append(body);
    }
  }

  private boolean isJsonRequest(HttpRequest request) {

    return request.getHeaders() != null
        && request.getHeaders().getContentType() != null
        && request.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON);
  }

  private boolean isJsonResponse(ClientHttpResponse response) {
    return response.getHeaders() != null
        && response.getHeaders().getContentType() != null
        && response.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON);
  }
}
