/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.common.filter;

import java.net.URI;
import java.util.List;
import org.smartbit4all.ui.common.controller.UIController;

public interface DynamicFilterController extends UIController {

  public static final String ROOT_FILTER_GROUP = "root";

  void setUi(DynamicFilterView dynamicFilterView);

  void loadData();

  void addFilterField(String filterSelectorId);

  void addFilterGroup(String parentGroupId);

  void removeFilterField(String groupId, String filterId);

  void removeFilterGroup(String groupId);

  void filterOperationChanged(String filterId, String filterOperation);

  void activeFilterGroupChanged(String filterGroupId);

  void changeGroup(String oldGroupId, String newGroupId, String filterId);

  void filterSelectionChanged(String filterId, List<URI> values);

  void filterValueChanged(String filterId, String... values);

}
