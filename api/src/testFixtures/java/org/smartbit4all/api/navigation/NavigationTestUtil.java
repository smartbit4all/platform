package org.smartbit4all.api.navigation;


import java.io.IOException;
import java.util.List;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationReference;

public class NavigationTestUtil {

  public static String navigateAll(Navigation navigation,
      List<ApiItemChangeEvent<NavigationReference>> changeList, String indent, StringBuilder sb)
      throws IOException {
    if (changeList == null || changeList.isEmpty()) {
      return null;
    }
    for (ApiItemChangeEvent<NavigationReference> apiItemChangeEvent : changeList) {
      List<ApiItemChangeEvent<NavigationReference>> expandAll =
          navigation.expandAll(apiItemChangeEvent.item().getEndNode());
      NavigationEntry endNodeEntry = apiItemChangeEvent.item().getEndNode().getEntry();
      sb.append(indent + endNodeEntry.getName() + "\n");
      String subIndent = indent + "\t";
      navigateAll(navigation, expandAll, subIndent, sb);
    }
    return sb.toString();
  }


}
