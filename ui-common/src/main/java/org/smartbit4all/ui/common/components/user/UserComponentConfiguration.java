/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
