package org.smartbit4all.ui.common.components.user;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class UserComponentConfiguration {

  final Supplier<String> labelFactory;
  
  final Supplier<InputStream> pictureFactory;
  
  final Map<String, NavigationActionItem> itemsByCode = new HashMap<>();
  
  public UserComponentConfiguration(Supplier<String> labelFactory,
      Supplier<InputStream> pictureFactory) {
    super();
    this.labelFactory = labelFactory;
    this.pictureFactory = pictureFactory;
  }

  public UserComponentConfiguration addItem(String code, String label, String iconCode, String navigationRoute) {
    NavigationActionItem item = new NavigationActionItem(code, label, iconCode, navigationRoute);
    itemsByCode.put(code, item);
    return this;
  }

  public Supplier<String> getLabelFactory() {
    return labelFactory;
  }

  public Supplier<InputStream> getPictureFactory() {
    return pictureFactory;
  }

  public Map<String, NavigationActionItem> getItemsByCode() {
    return itemsByCode;
  }

  public static class NavigationActionItem {
    
    private String code;
    private String label;
    private String iconCode;
    private String navigationRoute;
    
    public NavigationActionItem(String code, String label, String iconCode, String navigationRoute) {
      super();
      this.code = code;
      this.label = label;
      this.iconCode = iconCode;
      this.navigationRoute = navigationRoute;
    }
    
    public String getCode() {
      return code;
    }
    public String getLabel() {
      return label;
    }
    public String getIconCode() {
      return iconCode;
    }
    
    public String getNavigationRoute() {
      return navigationRoute;
    }
    
  }
  
}
