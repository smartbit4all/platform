package org.smartbit4all.ui.api.navigation.restserver.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.CommandData;
import org.smartbit4all.ui.api.navigation.model.CommandResult;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.ViewModelData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
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
@TestMethodOrder(OrderAnnotation.class)
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
        .viewName(TestViewModel.TEST_VIEW)
        .viewClassName(TestViewModel.class.getName()));
    uiNavigationApi.registerView(new NavigableViewDescriptor()
        .viewName(TestViewModel.MODIFY_VIEW)
        .viewClassName(TestViewModel.class.getName()));
  }

  @Test
  @Order(0)
  void createViewModel() {
    NavigationTarget navigationTarget = new NavigationTarget()
        .viewName("test-view");
    ViewModelData result = restTemplate.postForObject(getUrl("createViewModel", null),
        navigationTarget, ViewModelData.class);
    assertEquals(navigationTarget.getViewName(), result.getNavigationTarget().getViewName());
    assertNull(navigationTarget.getUuid());
    assertNotNull(result.getUuid());
    assertNotNull(result.getNavigationTarget().getUuid());
    assertNotNull(result.getModel());
    this.viewModelUUID = result.getUuid();
  }

  @Test
  @Order(1)
  void handleData() {

    User model = getUser();

    assertEquals("Test User", model.getName());
    assertEquals("test@email.com", model.getEmail());

    model.setName("Modified User");

    restTemplate.postForObject(getUrl("setModel", viewModelUUID), model, Void.class);

    model = getUser();
    assertEquals("Modified User", model.getName());
    assertEquals("test@email.com", model.getEmail());

    model.setName("Modified User 123");

  }

  @Test
  @Order(2)
  void simpleCommand() {
    User model = getUser();
    String originalName = model.getName();

    CommandData commandData = new CommandData()
        .model(model)
        .commandPath(null)
        .commandCode(TestViewModel.TEST_COMMAND);

    CommandResult commandResult = restTemplate
        .postForObject(getUrl("executeCommand", viewModelUUID), commandData, CommandResult.class);
    assertNull(commandResult.getUiToOpen());
    model = objectMapper.convertValue(commandResult.getView().getModel(), User.class);
    assertEquals(originalName + "+command", model.getName());
    assertEquals("test@email.com", model.getEmail());


  }

  @Test
  @Order(3)
  void testNavigateCommand() {
    User model = getUser();
    String originalName = model.getName();

    CommandData commandData = new CommandData()
        .model(null)
        .commandPath(null)
        .commandCode(TestViewModel.MODIFY);

    CommandResult commandResult = restTemplate
        .postForObject(getUrl("executeCommand", viewModelUUID), commandData, CommandResult.class);
    NavigationTarget navigationTarget = commandResult.getUiToOpen();
    assertNotNull(navigationTarget);
    assertEquals(model.getUri(), navigationTarget.getObjectUri());
    assertEquals(TestViewModel.MODIFY_VIEW, navigationTarget.getViewName());

    // model unchanged
    ViewModelData view = commandResult.getView();
    model = objectMapper.convertValue(view.getModel(), User.class);
    assertEquals(originalName, model.getName());
    assertEquals("test@email.com", model.getEmail());

  }

  private User getUser() {
    return objectMapper.convertValue(getViewModelData().getModel(), User.class);
  }

  private ViewModelData getViewModelData() {
    Object dataMap = restTemplate.getForObject(getUrl("getModel", viewModelUUID), Object.class);
    assertNotNull(dataMap);
    ViewModelData data = objectMapper.convertValue(dataMap, ViewModelData.class);
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
