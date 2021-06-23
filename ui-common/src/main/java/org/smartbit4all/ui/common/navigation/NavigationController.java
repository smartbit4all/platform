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
package org.smartbit4all.ui.common.navigation;

import java.net.URI;
import java.util.stream.Stream;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.ui.common.action.NavigationActionListener;
import org.smartbit4all.ui.common.controller.UIController;
import org.smartbit4all.ui.common.view.UIViewShowCommand;

/**
 * The UI controller for a navigation view that contains all the business logic. Instantiated for
 * every navigation view since it's a stateful object.
 * 
 * @author Peter Boros
 */
public interface NavigationController extends UIController {

  /**
   * The view instance
   * 
   * @param view
   */
  void setUI(NavigationView view);

  void addRoot(URI entryMetaUri, URI rootObjectURI);

  int getChildCount(NavigationTreeNode node);

  Stream<NavigationTreeNode> getChildren(NavigationTreeNode parent);

  boolean hasChildren(NavigationTreeNode node);

  UIViewShowCommand getViewCommand(NavigationTreeNode node);

  void nodeSelected(NavigationTreeNode node);

  void refreshNode(NavigationTreeNode node);

  void registerNavigationActionListener(NavigationActionListener listener);

  NavigationTreeNode getSelectedNode();

  NavigationEntry getNavigationEntry(NavigationTreeNode navigationTreeNode);

  NavigationEntry getSelectedEntry();

  void navigateTo(UIViewShowCommand command);

}
