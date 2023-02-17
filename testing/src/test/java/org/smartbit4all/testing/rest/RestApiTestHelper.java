package org.smartbit4all.testing.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.function.Consumer;
import org.smartbit4all.api.localauthentication.bean.LocalAuthenticationLoginRequest;
import org.smartbit4all.api.session.bean.SessionInfoData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

public class RestApiTestHelper {

  private final RestTemplate restTemplate;
  private final int serverPort;

  private String jwtToken;
  private String sessionApiPath = "api";
  private String localAuthApiPath = "api";

  public RestApiTestHelper(RestTemplate restTemplate, int serverPort) {
    this.restTemplate = restTemplate;
    this.serverPort = serverPort;
  }

  public SessionInfoData sessionStartSession() {
    SessionInfoData sid = callPut(sessionApiPath, "session", null, SessionInfoData.class);
    jwtToken = sid.getSid();
    return sid;
  }

  public SessionInfoData sessionGetSession() {
    return callGet(sessionApiPath, "session", SessionInfoData.class);
  }

  public void localLogin(String username, String password) {
    callPost(localAuthApiPath, "login",
        new LocalAuthenticationLoginRequest().username(username).password(password), Void.class);
  }

  public void localLogout() {
    callPost(localAuthApiPath, "logout", null, Void.class);
  }

  public <T> T callPut(String apiPath, String method, Object request, Class<T> resultClazz) {
    return callPut(apiPath, method, request, resultClazz, null);
  }

  public <T> T callPutWithAssert(String apiPath, String method, Object request,
      Class<T> resultClazz) {
    return callPut(apiPath, method, request, resultClazz,
        response -> assertTrue(response.getStatusCode().is2xxSuccessful()));
  }

  public <T> T callPut(String apiPath, String method, Object request, Class<T> resultClazz,
      Consumer<ResponseEntity<T>> responseHandler) {
    return callRest(HttpMethod.PUT, apiPath, method, request, resultClazz, responseHandler);
  }


  public <T> T callPost(String apiPath, String method, Object request, Class<T> resultClazz) {
    return callPost(apiPath, method, request, resultClazz, null);
  }

  public <T> T callPostWithAssert(String apiPath, String method, Object request,
      Class<T> resultClazz) {
    return callPost(apiPath, method, request, resultClazz,
        response -> assertTrue(response.getStatusCode().is2xxSuccessful()));
  }

  public <T> T callPost(String apiPath, String method, Object request, Class<T> resultClazz,
      Consumer<ResponseEntity<T>> responseHandler) {
    return callRest(HttpMethod.POST, apiPath, method, request, resultClazz, responseHandler);
  }

  public <T> T callGet(String apiPath, String method, Class<T> resultClazz) {
    return callGet(apiPath, method, resultClazz, null);
  }

  public <T> T callGetWithAssert(String apiPath, String method, Class<T> resultClazz) {
    return callGet(apiPath, method, resultClazz,
        response -> assertTrue(response.getStatusCode().is2xxSuccessful()));
  }

  public <T> T callGet(String apiPath, String method, Class<T> resultClazz,
      Consumer<ResponseEntity<T>> responseHandler) {
    return callRest(HttpMethod.GET, apiPath, method, null, resultClazz, responseHandler);
  }

  private <T> T callRest(HttpMethod httpmethod, String apiPath, String method, Object request,
      Class<T> resultClazz,
      Consumer<ResponseEntity<T>> responseHandler) {
    ResponseEntity<T> response = restTemplate.exchange(
        getApiUrl(apiPath, method),
        httpmethod,
        createRequestEntity(request),
        resultClazz);

    if (responseHandler != null) {
      responseHandler.accept(response);
    }
    if (Void.class == resultClazz) {
      return null;
    }

    return response.getBody();
  }

  private <T> HttpEntity<T> createRequestEntity(T body) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    if (!ObjectUtils.isEmpty(jwtToken)) {
      String token = "Bearer " + jwtToken;
      headers.set("Authorization", token);
    }
    return new HttpEntity<>(body, headers);
  }

  private String getApiUrl(String apiPath, String method) {
    StringBuilder sb = new StringBuilder();
    sb.append("http://localhost:")
        .append(serverPort)
        .append("/");
    if (!ObjectUtils.isEmpty(apiPath)) {
      sb.append(apiPath);
      sb.append("/");
    }
    sb.append(method);
    return sb.toString();
  }

  public String getJwtToken() {
    return jwtToken;
  }

  public void setJwtToken(String jwtToken) {
    this.jwtToken = jwtToken;
  }

  public String getSessionApiPath() {
    return sessionApiPath;
  }

  public void setSessionApiPath(String sessionApiPath) {
    this.sessionApiPath = sessionApiPath;
  }

  public String getLocalAuthApiPath() {
    return localAuthApiPath;
  }

  public void setLocalAuthApiPath(String localAuthApiPath) {
    this.localAuthApiPath = localAuthApiPath;
  }

}
