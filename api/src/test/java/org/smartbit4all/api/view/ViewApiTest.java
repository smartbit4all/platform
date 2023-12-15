package org.smartbit4all.api.view;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.object.DomainObjectTestBean;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategory.ColorEnum;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ViewApiTestConfig.class})
class ViewApiTest {

  public static final String USER_CATEGORY = "USER_CATEGORY";
  private static final String PASSWDCODE =
      "$2a$10$2LXntgURMBoixkUhddcnVuBPCfcPyB/ely5HkPXc45LmDpdR3nFcS";
  private static final String PASSWD =
      "asd";

  private static final String ADDED_WITHTYPECLASSNAME = "ADDED_WITHTYPECLASSNAME";
  private static final String ADDED_WITHTYPECLASS = "ADDED_WITHTYPECLASS";
  public static final String SCHEMA_ASPECTS = "aspectTest";

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private ActionManagementApi actionManagementApi;

  @Test
  void testPredefinedDefinition() throws IOException {
    ObjectDefinition<DomainObjectTestBean> definition =
        objectApi.definition(DomainObjectTestBean.class);
    assertTrue(definition.isExplicitUri());
    assertEquals(ObjectMapper.class.getName(), definition.getDefaultSerializer().getName());

    SampleCategory cat1 = new SampleCategory().name("MyBlackCategory").color(ColorEnum.BLACK);

    {
      List<UiAction> actions = actionManagementApi.calculateActions(cat1);

      Assertions.assertThat(actions.stream().map(a -> a.getCode()))
          .containsExactlyInAnyOrder(ColorEnum.BLACK.name());
    }

  }

  private URI createUser(String username, String fullname, URI... group) {
    URI uri = orgApi.saveUser(new User().username(username)
        .password(PASSWDCODE)
        .name(fullname));
    Arrays.asList(group).stream()
        .forEach(g -> orgApi.addUserToGroup(
            uri,
            g));
    return uri;
  }

}
