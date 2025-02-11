package org.smartbit4all.api.servlet.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Logging out incoming requests and outgoing responses
 * 
 */
public class RequestResponseLoggingFilter implements Filter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    long startTime = System.currentTimeMillis();
    String requestId = UUID.randomUUID().toString();

    HttpServletResponse httpResponse = (HttpServletResponse) response;
    CustomHttpResponseWrapper responseWrapper = new CustomHttpResponseWrapper(httpResponse);

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    CustomHttpServletRequestWrapper requestWrapper =
        new CustomHttpServletRequestWrapper(httpRequest);

    logRequest(httpRequest, requestWrapper, requestId);

    chain.doFilter(requestWrapper, responseWrapper);

    logResponse(requestWrapper, httpResponse, responseWrapper, requestId, startTime);
  }

  private void logRequest(HttpServletRequest httpRequest,
      CustomHttpServletRequestWrapper requestWrapper, String requestId) throws IOException {
    StringBuilder logBuilder = new StringBuilder();

    // Log method and URI
    logBuilder.append("Incoming HTTP Request - ").append(requestId).append("\n");
    logBuilder.append("Method: ").append(httpRequest.getMethod()).append(", ");
    logBuilder.append("URI: ").append(getURL(httpRequest)).append(", ");
    logBuilder.append("Headers: [");
    // Log headers
    Enumeration<String> headerNames = httpRequest.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      logBuilder.append(headerName).append(": ")
          .append(httpRequest.getHeader(headerName)).append(" ");
    }
    logBuilder.append("]");

    // Log body
    logBuilder.append("Body:\n");
    String body = requestWrapper.getRequestBody();
    logBuilder.append(body);
    if (isJsonRequest(httpRequest)) {
      logBody(body, logBuilder);
    } else {
      logBuilder.append(body);
    }

    LOGGER.info(logBuilder.toString());
  }

  private void logResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
      CustomHttpResponseWrapper responseWrapper, String requestId,
      long startTime) throws IOException {
    StringBuilder logBuilder = new StringBuilder();
    // End time
    long endTime = System.currentTimeMillis();
    // Calculate duration and log
    long duration = endTime - startTime;
    // Log method and URI
    logBuilder.append("Outgoing HTTP Response - ").append(requestId).append("\n");
    logBuilder.append("URI: ").append(getURL(httpRequest)).append(", ");
    logBuilder.append("Status: ").append(httpResponse.getStatus()).append(", ");
    logBuilder.append("Execution time (ms): ").append(duration).append(", ");

    // Log body
    logBuilder.append("Body:\n");
    byte[] responseData = responseWrapper.getResponseData();
    String body = new String(responseData, httpResponse.getCharacterEncoding());
    if (isJsonResponse(httpResponse)) {
      logBody(body, logBuilder);
    } else if (isOctetStreamResponse(httpResponse)) {
      // don't log out octet-stream response body
      logBuilder.append("");
    } else {
      logBuilder.append(body);
    }

    LOGGER.info(logBuilder.toString());
    httpResponse.getOutputStream().write(responseData);
  }

  public static String getURL(HttpServletRequest req) {

    String scheme = req.getScheme(); // http
    String serverName = req.getServerName(); // hostname.com
    int serverPort = req.getServerPort(); // 80
    String contextPath = req.getContextPath(); // /mywebapp
    String servletPath = req.getServletPath(); // /servlet/MyServlet
    String pathInfo = req.getPathInfo(); // /a/b;c=123
    String queryString = req.getQueryString(); // d=789

    // Reconstruct original requesting URL
    StringBuilder url = new StringBuilder();
    url.append(scheme).append("://").append(serverName);

    if (serverPort != 80 && serverPort != 443) {
      url.append(":").append(serverPort);
    }

    url.append(contextPath).append(servletPath);

    // if (pathInfo != null) {
    // url.append(pathInfo);
    // }
    // if (queryString != null) {
    // url.append("?").append(queryString);
    // }
    return url.toString();
  }

  /**
   * <p>
   * With this wrapper we can get the response body.
   * </p>
   * 
   * <a href="https://www.geeksforgeeks.org/get-the-response-body-in-spring-boot-filter/">source</a>
   */
  public static class CustomHttpResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintWriter printWriter = new PrintWriter(outputStream);

    public CustomHttpResponseWrapper(HttpServletResponse response) {
      super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      return new ServletOutputStream() {
        @Override
        public boolean isReady() {
          // Indicate whether the stream is ready to be written to.
          return true; // Changed to true for better compatibility
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
          // No-op for synchronous processing
        }

        @Override
        public void write(int b) throws IOException {
          outputStream.write(b); // Write data to buffer
        }
      };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
      return printWriter; // Use PrintWriter to capture text data
    }

    public byte[] getResponseData() throws IOException {
      printWriter.flush(); // Ensure all data is written to the buffer
      return outputStream.toByteArray(); // Return buffered response data
    }
  }

  /**
   * <p>
   * With this wrapper we can get the request body.
   * </p>
   * 
   * <a href=
   * "https://www.geeksforgeeks.org/modify-request-body-before-reaching-controller-in-spring-boot/">source</a>
   */
  public static class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private String requestBody;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
      super(request);
      this.requestBody = getBodyFromRequest(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
      final ByteArrayInputStream byteArrayInputStream =
          new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
      return new ServletInputStream() {
        @Override
        public boolean isFinished() {
          return byteArrayInputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
          return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
          // No implementation needed
        }

        @Override
        public int read() throws IOException {
          return byteArrayInputStream.read();
        }
      };
    }

    @Override
    public BufferedReader getReader() throws IOException {
      return new BufferedReader(
          new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
    }

    public String getRequestBody() {
      return this.requestBody;
    }

    public void setRequestBody(String body) {
      this.requestBody = body;
    }

    private String getBodyFromRequest(HttpServletRequest request) throws IOException {
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    }
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

  private boolean isJsonRequest(HttpServletRequest request) {
    return request.getContentType() != null
        && request.getContentType().startsWith("application/json");
  }

  private boolean isJsonResponse(HttpServletResponse response) {
    return response.getContentType() != null
        && response.getContentType().startsWith("application/json");
  }

  private boolean isOctetStreamResponse(HttpServletResponse response) {
    return response.getContentType() != null
        && response.getContentType().startsWith("application/octet-stream");
  }
}
