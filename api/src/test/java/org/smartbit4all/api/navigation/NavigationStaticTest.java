package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.ArrayList;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Autowired
  NavigationFeatureApi featureApi;

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
    TestBean4 bean4 = TestBeans.bean4Of(
        "testbean4name",
        Arrays.asList("address4", "address44"));

    TestBean3 bean3 = TestBeans.bean3Of(
        "testbean3name",
        Arrays.asList("address3", "address33"),
        bean4);

    TestBean2 bean2 = TestBeans.bean2Of(
        "testbean2name",
        Arrays.asList("address2", "address22"),
        Arrays.asList(bean3));

    TestBean1 bean1 = TestBeans.bean1Of(
        "testbean1name",
        Arrays.asList("address1", "address11"),
        Arrays.asList(bean2));

    nav1.setBeans(Arrays.asList(bean1));

    ConfigBuilder builder = NavigationConfig.builder();
    builder.addAssociationMeta(Navigation1.ASSOC_BEAN2S_OF_BEAN1_META);
    builder.addAssociationMeta(Navigation1.ASSOC_BEAN3S_OF_BEAN2_META);
    NavigationConfig navConfig = builder.build();

    Navigation navigation = new Navigation(navConfig, navigationApi);
    NavigationNode rootNode =
        navigation.addRootNode(Navigation1.ENTRY_BEAN1_META.getUri(), bean1.getUri());

    List<String> qualifiedNames = Arrays.asList("/bean2s#name", "/bean2s#uri", "/bean2s#addresses");
    Map<String, ResolvedValue> resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean2name", resolveValues.get("/bean2s#name").getObject());
    assertEquals(bean2.getUri(), resolveValues.get("/bean2s#uri").getObject());
    List<String> addresses = (List<String>) resolveValues.get("/bean2s#addresses").getObject();
    assertEquals(bean2.getAddresses().size(), addresses.size());

    qualifiedNames =
        Arrays.asList("/bean2s/bean3s#name", "/bean2s/bean3s#uri", "/bean2s/bean3s#addresses");
    resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean3name", resolveValues.get("/bean2s/bean3s#name").getObject());
    assertEquals(bean3.getUri(), resolveValues.get("/bean2s/bean3s#uri").getObject());
    addresses = (List<String>) resolveValues.get("/bean2s/bean3s#addresses").getObject();
    assertEquals(bean3.getAddresses().size(), addresses.size());

    qualifiedNames = Arrays.asList("/bean2s/bean3s#bean4/name", "/bean2s/bean3s#bean4/uri",
        "/bean2s/bean3s#bean4/addresses");
    resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean4name", resolveValues.get("/bean2s/bean3s#bean4/name").getObject());
    assertEquals(bean4.getUri(), resolveValues.get("/bean2s/bean3s#bean4/uri").getObject());
    addresses = (List<String>) resolveValues.get("/bean2s/bean3s#bean4/addresses").getObject();
    assertEquals(bean4.getAddresses().size(), addresses.size());
  }

  @Test
  void resolveNodesThenValuesTestWithOneNavigation() {
    TestBean3 bean3 = TestBeans.bean3Of(
        "testbean3name",
        Arrays.asList("address3", "address33"),
        null);

    TestBean3 bean32 = TestBeans.bean3Of(
        "testbean32name",
        Arrays.asList("address32", "address3232"),
        null);

    TestBean3 bean33 = TestBeans.bean3Of(
        "testbean33name",
        Arrays.asList("address33", "address3333"),
        null);

    TestBean2 bean2 = TestBeans.bean2Of(
        "testbean2name",
        Arrays.asList("address2", "address22"),
        Arrays.asList(bean3, bean32));

    TestBean2 bean22 = TestBeans.bean2Of(
        "testbean22name",
        Arrays.asList("address22", "address2222"),
        Arrays.asList(bean33));

    TestBean1 bean1 = TestBeans.bean1Of(
        "testbean1name",
        Arrays.asList("address1", "address11"),
        Arrays.asList(bean2, bean22));

    nav1.setBeans(Arrays.asList(bean1));

    ConfigBuilder builder = NavigationConfig.builder();
    builder.addAssociationMeta(Navigation1.ASSOC_BEAN2S_OF_BEAN1_META);
    builder.addAssociationMeta(Navigation1.ASSOC_BEAN3S_OF_BEAN2_META);
    NavigationConfig navConfig = builder.build();

    Navigation navigation = new Navigation(navConfig, navigationApi);
    NavigationNode rootNode =
        navigation.addRootNode(Navigation1.ENTRY_BEAN1_META.getUri(), bean1.getUri());

    List<NavigationNode> resolvedNodes = navigation.resolveNodes(rootNode, "/bean2s");
    List<String> qualifiedNames = Arrays.asList("/#name");
    List<Object> results = new ArrayList<>();
    for (NavigationNode resolvedNode : resolvedNodes) {
      Map<String, ResolvedValue> resolveValues =
          navigation.resolveValues(resolvedNode, qualifiedNames);
      results.add(resolveValues.get("/#name").getObject());
    }
    assertTrue(results.contains(bean2.getName()));
    assertTrue(results.contains(bean22.getName()));

    resolvedNodes = navigation.resolveNodes(rootNode, "/bean2s/bean3s");
    qualifiedNames = Arrays.asList("/#name");
    results = new ArrayList<>();
    for (NavigationNode resolvedNode : resolvedNodes) {
      Map<String, ResolvedValue> resolveValues =
          navigation.resolveValues(resolvedNode, qualifiedNames);
      results.add(resolveValues.get("/#name").getObject());
    }
    assertTrue(results.contains(bean3.getName()));
    assertTrue(results.contains(bean32.getName()));
    assertTrue(results.contains(bean33.getName()));
  }

  @Test
  void resolveValuesTestWithTwoNavigations() {
    TestBean4 bean4 = TestBeans.bean4Of(
        "testbean4name",
        Arrays.asList("address4", "address44"));

    TestBean3 bean3 = TestBeans.bean3Of(
        "testbean3name",
        Arrays.asList("address3", "address33"),
        bean4);

    TestBean2 bean2 = TestBeans.bean2Of(
        "testbean2name",
        Arrays.asList("address2", "address22"),
        Arrays.asList(bean3));

    TestBean1 bean1 = TestBeans.bean1Of(
        "testbean1name",
        Arrays.asList("address1", "address11"),
        Arrays.asList(bean2));

    nav2.setBeans(Arrays.asList(bean1));
    nav3.setBeans(Arrays.asList(bean2));

    ConfigBuilder builder = NavigationConfig.builder();
    builder.addAssociationMeta(Navigation2.ASSOC_BEAN2S_OF_BEAN1_META);
    builder.addAssociationMeta(Navigation3.ASSOC_BEAN3S_OF_BEAN2_META);
    NavigationConfig navConfig = builder.build();

    Navigation navigation = new Navigation(navConfig, navigationApi);
    NavigationNode rootNode =
        navigation.addRootNode(Navigation2.ENTRY_BEAN1_META.getUri(), bean1.getUri());

    List<String> qualifiedNames = Arrays.asList("/bean2s#name", "/bean2s#uri", "/bean2s#addresses");
    Map<String, ResolvedValue> resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean2name", resolveValues.get("/bean2s#name").getObject());
    assertEquals(bean2.getUri(), resolveValues.get("/bean2s#uri").getObject());
    List<String> addresses = (List<String>) resolveValues.get("/bean2s#addresses").getObject();
    assertEquals(bean2.getAddresses().size(), addresses.size());

    qualifiedNames =
        Arrays.asList("/bean2s/bean3s#name", "/bean2s/bean3s#uri", "/bean2s/bean3s#addresses");
    resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean3name", resolveValues.get("/bean2s/bean3s#name").getObject());
    assertEquals(bean3.getUri(), resolveValues.get("/bean2s/bean3s#uri").getObject());
    addresses = (List<String>) resolveValues.get("/bean2s/bean3s#addresses").getObject();
    assertEquals(bean3.getAddresses().size(), addresses.size());

    qualifiedNames = Arrays.asList("/bean2s/bean3s#bean4/name", "/bean2s/bean3s#bean4/uri",
        "/bean2s/bean3s#bean4/addresses");
    resolveValues = navigation.resolveValues(rootNode, qualifiedNames);
    assertEquals("testbean4name", resolveValues.get("/bean2s/bean3s#bean4/name").getObject());
    assertEquals(bean4.getUri(), resolveValues.get("/bean2s/bean3s#bean4/uri").getObject());
    addresses = (List<String>) resolveValues.get("/bean2s/bean3s#bean4/addresses").getObject();
    assertEquals(bean4.getAddresses().size(), addresses.size());
  }

  @Test
  void resolveNodesThenValuesTestWithTwoNavigations() {
    TestBean3 bean3 = TestBeans.bean3Of(
        "testbean3name",
        Arrays.asList("address3", "address33"),
        null);

    TestBean3 bean32 = TestBeans.bean3Of(
        "testbean32name",
        Arrays.asList("address32", "address3232"),
        null);

    TestBean3 bean33 = TestBeans.bean3Of(
        "testbean33name",
        Arrays.asList("address33", "address3333"),
        null);

    TestBean2 bean2 = TestBeans.bean2Of(
        "testbean2name",
        Arrays.asList("address2", "address22"),
        Arrays.asList(bean3, bean32));

    TestBean2 bean22 = TestBeans.bean2Of(
        "testbean22name",
        Arrays.asList("address22", "address2222"),
        Arrays.asList(bean33));

    TestBean1 bean1 = TestBeans.bean1Of(
        "testbean1name",
        Arrays.asList("address1", "address11"),
        Arrays.asList(bean2, bean22));

    nav2.setBeans(Arrays.asList(bean1));
    nav3.setBeans(Arrays.asList(bean2, bean22));

    ConfigBuilder builder = NavigationConfig.builder();
    builder.addAssociationMeta(Navigation2.ASSOC_BEAN2S_OF_BEAN1_META);
    builder.addAssociationMeta(Navigation3.ASSOC_BEAN3S_OF_BEAN2_META);
    NavigationConfig navConfig = builder.build();

    Navigation navigation = new Navigation(navConfig, navigationApi);
    NavigationNode rootNode =
        navigation.addRootNode(Navigation2.ENTRY_BEAN1_META.getUri(), bean1.getUri());

    List<NavigationNode> resolvedNodes = navigation.resolveNodes(rootNode, "/bean2s");
    List<String> qualifiedNames = Arrays.asList("/#name");
    List<Object> results = new ArrayList<>();
    for (NavigationNode resolvedNode : resolvedNodes) {
      Map<String, ResolvedValue> resolveValues =
          navigation.resolveValues(resolvedNode, qualifiedNames);
      results.add(resolveValues.get("/#name").getObject());
    }
    assertTrue(results.contains(bean2.getName()));
    assertTrue(results.contains(bean22.getName()));

    resolvedNodes = navigation.resolveNodes(rootNode, "/bean2s/bean3s");
    qualifiedNames = Arrays.asList("/#name");
    results = new ArrayList<>();
    for (NavigationNode resolvedNode : resolvedNodes) {
      Map<String, ResolvedValue> resolveValues =
          navigation.resolveValues(resolvedNode, qualifiedNames);
      results.add(resolveValues.get("/#name").getObject());
    }
    assertTrue(results.contains(bean3.getName()));
    assertTrue(results.contains(bean32.getName()));
    assertTrue(results.contains(bean33.getName()));
  }

  @Test
  void testFeaturesWithNavigation1() {
    TestBean4 bean4 = TestBeans.bean4Of(
        "testbean4name",
        Arrays.asList("address4", "address44"));

    TestBean3 bean3 = TestBeans.bean3Of(
        "testbean3name",
        Arrays.asList("address3", "address33"),
        bean4);

    TestBean2 bean2 = TestBeans.bean2Of(
        "testbean2name",
        Arrays.asList("address2", "address22"),
        Arrays.asList(bean3));

    TestBean1 bean1 = TestBeans.bean1Of(
        "testbean1name",
        Arrays.asList("address1", "address11"),
        Arrays.asList(bean2));

    nav1.setBeans(Arrays.asList(bean1));

    ConfigBuilder builder = NavigationConfig.builder();
    builder.addAssociationMeta(Navigation1.ASSOC_BEAN2S_OF_BEAN1_META);
    builder.addAssociationMeta(Navigation1.ASSOC_BEAN3S_OF_BEAN2_META);
    NavigationConfig navConfig = builder.build();

    Navigation navigation = new Navigation(navConfig, navigationApi);
    NavigationNode rootNode =
        navigation.addRootNode(Navigation1.ENTRY_BEAN1_META.getUri(), bean1.getUri());

    StringBuilder sb = new StringBuilder();
    traverseObjectWriter(navigation, rootNode, sb, 0);

    System.out.println(sb);

  }

  void traverseObjectWriter(Navigation navigation, NavigationNode currentNode,
      StringBuilder builder, int dept) {
    List<ObjectTitleWriter> api =
        featureApi.api(ObjectTitleWriter.class, currentNode.getEntry().getMetaUri());
    api.stream().forEach(a -> a.write(currentNode.getEntry().getName(), builder, dept));
    navigation.expandAll(currentNode);
    currentNode.getAssociations().stream()
        .flatMap(a -> a.getReferences().stream().map(r -> r.getEndNode()))
        .forEach(n -> traverseObjectWriter(navigation, n, builder, dept + 1));
  }

}
