package org.smartbit4all.api.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

  private static Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

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
    // End time
    long endTime = System.currentTimeMillis();
    // Calculate duration and log
    long duration = endTime - startTime;
    // Log method and URI
    logBuilder.append("Incoming HTTP Response - ").append(requestId).append("\n");
    logBuilder.append("URI: ").append(req.getURI()).append(", ");
    logBuilder.append("Status: ").append(response.getStatusCode().value()).append(", ");
    logBuilder.append("Execution time (ms): ").append(duration).append(", ");

    // Log body
    logBuilder.append("Body:\n");
    String body =
        new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    logBuilder.append(body);

    LOGGER.info(logBuilder.toString());

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

    LOGGER.info(logBuilder.toString());
  }
}
