package org.smartbit4all.api.servlet.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class RequestResponseLoggingFilter implements Filter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    CustomHttpResponseWrapper responseWrapper = new CustomHttpResponseWrapper(httpResponse);

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    CustomHttpServletRequestWrapper requestWrapper =
        new CustomHttpServletRequestWrapper(httpRequest);

    LOGGER.info("Receive Http request: [path: {}, body: {}]", httpRequest.getRequestURI(),
        requestWrapper.getRequestBody());

    chain.doFilter(requestWrapper, responseWrapper);

    byte[] responseData = responseWrapper.getResponseData();
    String responseBody = new String(responseData, httpResponse.getCharacterEncoding());
    LOGGER.info("Response for {} is: [status: {}, body: {}]", httpRequest.getRequestURI(),
        httpResponse.getStatus(), responseBody);
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
}
