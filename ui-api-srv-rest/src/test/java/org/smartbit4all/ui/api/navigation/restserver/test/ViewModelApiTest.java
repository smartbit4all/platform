package org.smartbit4all.ui.api.navigation.restserver.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

  @Value("${openapi.navigation.base-path:/}")
  private String path;

  @Value("${viewModelApi.testFile:src/test/resources/lorem-ipsum.pdf}")
  private String testFile;

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
  @Order(10)
  void handleData() {

    TestModel model = getTestModel();

    assertEquals("Test User", model.getUser().getName());
    assertEquals("test@email.com", model.getUser().getEmail());

    model.getUser().setName("Modified User");

    restTemplate.postForObject(getUrl("setModel", viewModelUUID), model, Void.class);

    model = getTestModel();
    assertEquals("Modified User", model.getUser().getName());
    assertEquals("test@email.com", model.getUser().getEmail());

  }

  @Test
  @Order(20)
  void simpleCommand() {
    TestModel model = getTestModel();
    String originalName = model.getUser().getName();

    CommandData commandData = new CommandData()
        .model(model)
        .commandPath(null)
        .commandCode(TestViewModel.TEST_COMMAND);

    CommandResult commandResult = restTemplate
        .postForObject(getUrl("executeCommand", viewModelUUID), commandData, CommandResult.class);
    assertNull(commandResult.getUiToOpen());
    model = objectMapper.convertValue(commandResult.getView().getModel(), TestModel.class);
    assertEquals(originalName + "+command", model.getUser().getName());
    assertEquals("test@email.com", model.getUser().getEmail());

  }

  @Test
  @Order(30)
  void testNavigateCommand() {
    TestModel model = getTestModel();
    String originalName = model.getUser().getName();

    CommandData commandData = new CommandData()
        .model(null)
        .commandPath(null)
        .commandCode(TestViewModel.MODIFY);

    CommandResult commandResult = restTemplate
        .postForObject(getUrl("executeCommand", viewModelUUID), commandData, CommandResult.class);
    NavigationTarget navigationTarget = commandResult.getUiToOpen();
    assertNotNull(navigationTarget);
    assertEquals(model.getUser().getUri(), navigationTarget.getObjectUri());
    assertEquals(TestViewModel.MODIFY_VIEW, navigationTarget.getViewName());

    // model unchanged
    ViewModelData view = commandResult.getView();
    model = objectMapper.convertValue(view.getModel(), TestModel.class);
    assertEquals(originalName, model.getUser().getName());
    assertEquals("test@email.com", model.getUser().getEmail());

  }

  @Test
  @Order(40)
  void testUploadCommand() {
    TestModel model = getTestModel();
    String originalName = model.getUser().getName();

    CommandData commandData = new CommandData()
        .model(null)
        .commandPath(null)
        .commandCode(TestViewModel.UPLOAD);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    // headers.set(HttpHeaders.ACCEPT, "application/json, application/*+json");
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

    // testFile = "src/test/resources/lorem-ipsum.pdf";
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("uuid", viewModelUUID.toString());
    body.add("command", commandData);
    body.add("content", new FileSystemResource(testFile));
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    ResponseEntity<CommandResult> response = restTemplate
        .postForEntity(getUrl("upload", null), requestEntity, CommandResult.class);

    CommandResult commandResult = response.getBody();
    ViewModelData view = commandResult.getView();
    model = objectMapper.convertValue(view.getModel(), TestModel.class);
    long fileLength = new File(testFile).length();
    assertEquals(originalName + "-length:" + fileLength, model.getUser().getName());
    assertEquals("test@email.com", model.getUser().getEmail());

  }

  @Test
  @Order(50)
  void testDownload() {
    String downloadIdentifier = "001";
    CommandData commandData = new CommandData()
        .model(null)
        .commandPath(null)
        .commandCode(TestViewModel.DOWNLOAD)
        .addParamsItem(downloadIdentifier);

    restTemplate
        .postForObject(getUrl("executeCommand", viewModelUUID), commandData, CommandResult.class);

    String path = getUrl("download", viewModelUUID) + "/" + downloadIdentifier;
    ResponseEntity<Resource> response =
        restTemplate.exchange(path, HttpMethod.GET, HttpEntity.EMPTY, Resource.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());
    Resource content = response.getBody();
    assertNotNull(content);
    long fileLength = new File(testFile).length();
    assertDoesNotThrow(() -> {
      assertEquals(fileLength, content.contentLength());
    });

  }

  private TestModel getTestModel() {
    return objectMapper.convertValue(getViewModelData().getModel(), TestModel.class);
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
