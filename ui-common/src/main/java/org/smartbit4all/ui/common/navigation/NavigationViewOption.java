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
