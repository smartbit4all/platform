package org.smartbit4all.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.navigation.NavigationURI;

class TestNavigationURI {

  private static final String ID4 = "id4";
  private static final String ID3 = "id3";
  private static final String ID2 = "id2";
  private static final String ID1 = "id1";
  private static final String MY_NAVIGATION = "myNavigation";

  @BeforeEach
  void setUp() throws Exception {}

  @AfterEach
  void tearDown() throws Exception {}

  @Test
  void testNavigationURIStringListOfString() {
    List<String> path = Arrays.asList(ID1, ID2, ID3);
    NavigationURI navigationURI = new NavigationURI(MY_NAVIGATION, path);
    String expected = MY_NAVIGATION + NavigationURI.NAVIGATION_SEPARATOR
        + String.join(NavigationURI.DELIMITER, path);
    assertEquals(expected, navigationURI.getUri());
    System.out.println(expected + " = " + navigationURI.getUri());
  }

  @Test
  void testNavigationURIString() {
    List<String> path = Arrays.asList(ID1, ID2, ID3);
    String uri = MY_NAVIGATION + NavigationURI.NAVIGATION_SEPARATOR
        + String.join(NavigationURI.DELIMITER, path);
    NavigationURI navigationURI = new NavigationURI(MY_NAVIGATION, path);

    String result = navigationURI.getNavigation() + NavigationURI.NAVIGATION_SEPARATOR
        + String.join(NavigationURI.DELIMITER, navigationURI.getPath());
    assertEquals(uri, result);
    System.out.println(uri + " = " + result);
  }

  @Test
  void testSetNavigation() {
    List<String> path = Arrays.asList(ID1, ID2, ID3);
    String uri = MY_NAVIGATION + NavigationURI.NAVIGATION_SEPARATOR
        + String.join(NavigationURI.DELIMITER, path);
    NavigationURI navigationURI = new NavigationURI(MY_NAVIGATION, path);
    navigationURI.setNavigation(MY_NAVIGATION + "1");
    String expected = MY_NAVIGATION + "1" + NavigationURI.NAVIGATION_SEPARATOR
        + String.join(NavigationURI.DELIMITER, path);
    assertEquals(expected, navigationURI.getUri());
    System.out.println(expected + " = " + navigationURI.getUri());
  }

  @Test
  void testSetPath() {
    List<String> path = new ArrayList<>(Arrays.asList(ID1, ID2, ID3));
    String uri = MY_NAVIGATION + NavigationURI.NAVIGATION_SEPARATOR
        + String.join(NavigationURI.DELIMITER, path);
    NavigationURI navigationURI = new NavigationURI(MY_NAVIGATION, path);
    path.add(ID4);
    navigationURI.setPath(path);
    String expected = MY_NAVIGATION + NavigationURI.NAVIGATION_SEPARATOR
        + String.join(NavigationURI.DELIMITER, path);
    assertEquals(expected, navigationURI.getUri());
    System.out.println(expected + " = " + navigationURI.getUri());
  }

}
