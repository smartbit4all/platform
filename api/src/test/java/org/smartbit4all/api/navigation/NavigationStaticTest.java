package org.smartbit4all.api.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.navigation.NavigationConfig.ConfigBuilder;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = NavigationTestConfig.class)
class NavigationStaticTest {

  @Autowired
  Navigation1 nav1;

  @Autowired
  Navigation2 nav2;

  @Autowired
  Navigation3 nav3;

  @Autowired
  NavigationApi navigationApi;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void test() {
    NavigationImplStatic navStatic = new NavigationImplStatic("static");
    NavigationView view = new NavigationView();
    view.setName("StaticView");
    String icon = "icon.png";
    URI rootJoe = navStatic.addRoot("Joe", "József", icon, view);
    URI addressOfJoe = navStatic.addChild(rootJoe, "address", "Home", "Otthoni cím", icon, view);
    NavigationConfig config = navStatic.config();
    System.out.println(config);
    Navigation navigation = new Navigation(config, navStatic);
    NavigationNode rootNode = navigation.addRootNode(rootJoe, rootJoe);
    navigation.expandAll(rootNode);
    System.out.println(navigation);
  }

  @Test
  void resolveValuesTestWithOneNavigation() {
    TestBean4 bean4 = TestBeans.bean4Of("testbean4name");
    TestBean3 bean3 = TestBeans.bean3Of("testbean3name", bean4);
    TestBean2 bean2 = TestBeans.bean2Of("testbean2name", Arrays.asList(bean3));
    TestBean1 bean1 = TestBeans.bean1Of("testbean1name", Arrays.asList(bean2));

    ConfigBuilder builder = NavigationConfig.builder();
    builder.addAssociationMeta(Navigation1.ASSOC_BEAN2S_OF_BEAN1_META);
    builder.addAssociationMeta(Navigation1.ASSOC_BEAN3S_OF_BEAN2_META);
    NavigationConfig navConfig = builder.build();

    nav1.setBeans(Arrays.asList(bean1));

    Navigation navigation = new Navigation(navConfig, navigationApi);
    NavigationNode rootNode =
        navigation.addRootNode(Navigation1.ENTRY_BEAN1_META.getUri(), bean1.getUri());

    List<String> qualifiedNames = Arrays.asList("/bean2s#name", "/bean2s#uri");
    Map<String, Object> resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean2name", resolveValues.get("/bean2s#name"));
    assertEquals(bean2.getUri(), resolveValues.get("/bean2s#uri"));

    qualifiedNames = Arrays.asList("/bean2s/bean3s#name", "/bean2s/bean3s#uri");
    resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean3name", resolveValues.get("/bean2s/bean3s#name"));
    assertEquals(bean3.getUri(), resolveValues.get("/bean2s/bean3s#uri"));

    qualifiedNames = Arrays.asList("/bean2s/bean3s#bean4/name");
    resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean4name", resolveValues.get("/bean2s/bean3s#bean4/name"));
  }

  @Test
  void resolveValuesTestWithTwoNavigations() {
    TestBean4 bean4 = TestBeans.bean4Of("testbean4name");
    TestBean3 bean3 = TestBeans.bean3Of("testbean3name", bean4);
    TestBean2 bean2 = TestBeans.bean2Of("testbean2name", Arrays.asList(bean3));
    TestBean1 bean1 = TestBeans.bean1Of("testbean1name", Arrays.asList(bean2));

    nav2.setBeans(Arrays.asList(bean1));
    nav3.setBeans(Arrays.asList(bean2));

    ConfigBuilder builder = NavigationConfig.builder();
    builder.addAssociationMeta(Navigation2.ASSOC_BEAN2S_OF_BEAN1_META);
    builder.addAssociationMeta(Navigation3.ASSOC_BEAN3S_OF_BEAN2_META);
    NavigationConfig navConfig = builder.build();

    Navigation navigation = new Navigation(navConfig, navigationApi);
    NavigationNode rootNode =
        navigation.addRootNode(Navigation2.ENTRY_BEAN1_META.getUri(), bean1.getUri());

    List<String> qualifiedNames = Arrays.asList("/bean2s#name", "/bean2s#uri");
    Map<String, Object> resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean2name", resolveValues.get("/bean2s#name"));
    assertEquals(bean2.getUri(), resolveValues.get("/bean2s#uri"));

    qualifiedNames = Arrays.asList("/bean2s/bean3s#name", "/bean2s/bean3s#uri");
    resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean3name", resolveValues.get("/bean2s/bean3s#name"));
    assertEquals(bean3.getUri(), resolveValues.get("/bean2s/bean3s#uri"));

    qualifiedNames = Arrays.asList("/bean2s/bean3s#bean4/name");
    resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean4name", resolveValues.get("/bean2s/bean3s#bean4/name"));
  }

}
