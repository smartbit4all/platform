package org.smartbit4all.ui.common.navigation;

import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.bean.NavigationAssociation;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;

/**
 * The tree node is a massively UI object created and managed by the {@link NavigationController}.
 * If the representation of a {@link Navigation} is a tree. The tree can be generated from the
 * different objects of the navigation. They could be {@link NavigationNode},
 * {@link NavigationAssociation} or even {@link NavigationReference}. It depends on the visual
 * settings of the controller and the tree.
 * 
 * @author Peter Boros
 */
public class NavigationTreeNode {

  public static enum Kind {

    ENTRY, ASSOCIATION, REFERENCE

  }

  /**
   * The kind of the given node.
   */
  private Kind kind;

  /**
   * The identifier of the given tree node.
   */
  private String identifier;

  /**
   * The caption of the given tree node.
   */
  private String caption;

  /**
   * The short description can be a few line text that describes the data of given node. If it's
   * null then it's not generated. It depends on the
   * {@link NavigationViewOption#isShowShortDescription()}
   */
  private String shortDescription;

  /**
   * The logical name of an icon, resolved on the UI.
   */
  private String icon;

  /**
   * The symbolic names of styles to apply on the UI.
   */
  private String styles[];

  public NavigationTreeNode(Kind kind, String identifier, String caption, String shortDescription,
      String icon, String[] styles) {
    super();
    this.kind = kind;
    this.identifier = identifier;
    this.caption = caption;
    this.shortDescription = shortDescription;
    this.icon = icon;
    this.styles = styles;
  }

  public final String getCaption() {
    return caption;
  }

  public final void setCaption(String caption) {
    this.caption = caption;
  }

  public final String getShortDescription() {
    return shortDescription;
  }

  public final void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public final String getIcon() {
    return icon;
  }

  public final void setIcon(String icon) {
    this.icon = icon;
  }

  public final String[] getStyles() {
    return styles;
  }

  public final void setStyles(String[] styles) {
    this.styles = styles;
  }

  public final Kind getKind() {
    return kind;
  }

  public final boolean isKind(Kind kind) {
    return this.kind == kind;
  }

  public final void setKind(Kind kind) {
    this.kind = kind;
  }

  public final String getIdentifier() {
    return identifier;
  }

  public final void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

}
