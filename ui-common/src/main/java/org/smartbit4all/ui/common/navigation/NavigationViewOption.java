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
package org.smartbit4all.ui.common.navigation;

/**
 * Responsible for storing the visual option of a {@link NavigationView} screen instance.
 * 
 * @author Peter Boros
 */
public class NavigationViewOption {

  /**
   * If it's true then the tree for example will show the associations itself to help the navigation
   * by grouping the sub nodes.
   */
  private boolean showAssociations = false;

  /**
   * The short description can be a few line of text to help in identifying the given navigation
   * node.
   */
  private boolean showShortDescription = false;

  public final boolean isShowAssociations() {
    return showAssociations;
  }

  public final void setShowAssociations(boolean showAssociations) {
    this.showAssociations = showAssociations;
  }

  public final boolean isShowShortDescription() {
    return showShortDescription;
  }

  public final void setShowShortDescription(boolean showShortDescription) {
    this.showShortDescription = showShortDescription;
  }

}
