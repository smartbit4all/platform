package org.smartbit4all.api.navigation;

import java.net.URI;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationView;

class NavigationStaticTest {

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

}
