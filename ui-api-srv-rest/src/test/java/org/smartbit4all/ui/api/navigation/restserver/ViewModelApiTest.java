package org.smartbit4all.ui.api.navigation.restserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.CommandData;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@SpringBootTest(
    classes = {
        ViewModelApiTestConfig.class
    },
    properties = {
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class ViewModelApiTest {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UINavigationApi uiNavigationApi;

  @LocalServerPort
  private int serverPort;

  @Value("${openapi.invocation.base-path:/}")
  private String path;

  private UUID viewModelUUID;

  @BeforeAll
  public void setup() {
    uiNavigationApi.registerView(new NavigableViewDescriptor()
        .viewName("test-view")
        .viewClassName("org.smartbit4all.ui.api.navigation.restserver.TestViewModel"));
  }

  @Test
  @Order(0)
  void createViewModel() {
    NavigationTarget navigationTarget = new NavigationTarget()
        .viewName("test-view");
    NavigationTarget result = restTemplate.postForObject(getUrl("createViewModel", null),
        navigationTarget, NavigationTarget.class);
    assertEquals(navigationTarget.getViewName(), result.getViewName());
    assertNull(navigationTarget.getUuid());
    assertNotNull(result.getUuid());
    this.viewModelUUID = result.getUuid();
  }

  @Test
  @Order(1)
  void handleData() {

    NavigationTarget data = getData();

    assertEquals("test title", data.getTitle());
    assertEquals("data-for-view", data.getViewName());

    data.setTitle("modified title");

    restTemplate.postForObject(getUrl("setData", viewModelUUID), data, Void.class);

    data = getData();
    assertEquals("modified title", data.getTitle());
    assertEquals("data-for-view", data.getViewName());

    CommandData commandData = new CommandData()
        .commandPath(null)
        .commandCode(TestViewModel.TEST_COMMAND);

    restTemplate.postForObject(getUrl("executeCommand", viewModelUUID), commandData, Void.class);

    data = getData();
    assertEquals("modified title+command", data.getTitle());
    assertEquals("data-for-view", data.getViewName());

  }

  private NavigationTarget getData() {
    Object dataMap = restTemplate.getForObject(getUrl("getData", viewModelUUID), Object.class);
    assertNotNull(dataMap);
    NavigationTarget data =
        objectMapper.convertValue(dataMap, new TypeReference<NavigationTarget>() {});
    return data;
  }

  /**
   * http://localhost:port/path/viewModelApi
   */
  private String getUrl(String method, UUID uuid) {
    StringBuilder sb = new StringBuilder();
    sb.append("http://localhost:")
        .append(serverPort)
        .append("/");
    if (!ObjectUtils.isEmpty(path)) {
      sb.append(path);
      sb.append("/");
    }
    sb.append(method);
    if (uuid != null) {
      sb.append("/");
      sb.append(uuid.toString());
    }
    return sb.toString();
  }
}
